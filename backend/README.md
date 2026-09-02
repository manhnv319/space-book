# book-backend-hex

Book Store backend, hexagonal architecture (ports & adapters) + DDD on Spring Boot 4 / Java 21.

## Tech stack

| Layer            | Choice                                                         |
| ---------------- | -------------------------------------------------------------- |
| Runtime          | Java 21, Spring Boot 4.0.6                                     |
| Persistence      | MongoDB 7 (runtime mặc định), Spring Data MongoDB              |
| Compatibility    | Spring Data JPA + PostgreSQL adapter kept behind `postgres` profile |
| Cache / denylist | Redis 7 (Spring Data Redis, `StringRedisTemplate`)             |
| Auth             | Spring Security + JWT (jjwt 0.12), `@EnableMethodSecurity`     |
| Mail             | Spring Mail (Gmail SMTP by default)                            |
| Payment          | VNPay sandbox (IPN + return URL)                               |
| Testing          | JUnit 5, Mockito, AssertJ, ArchUnit 1.3, Spring Security Test |
| Build            | Maven (Maven Wrapper)                                          |

## Local development

### 1. Start dependencies

```bash
docker compose up -d
```

Brings up MongoDB as a single-node replica set (required for Mongo transactions) and Redis:

| Service    | Port | Notes                              |
| ---------- | ---- | ---------------------------------- |
| mongo      | 27017 | Database `book_store`, replica set `rs0` |
| redis      | 6379  | Used for token denylist + reset          |

Mongo data lives in the `mongo_data` volume, Redis in `redis_data`.

### 2. Configure secrets (optional)

Defaults are baked in for local dev, override via env vars:

```text
SPRING_PROFILES_ACTIVE=mongodb
MONGODB_URI=mongodb://localhost:27017/book_store?replicaSet=rs0&directConnection=true
SPRING_DATA_REDIS_HOST=localhost
SPRING_DATA_REDIS_PORT=6379
JWT_SECRET=velstrong-book-store-secret-key-2026-very-long-secret
MAIL_USERNAME=<gmail address>
MAIL_PASSWORD=<gmail app password>
VNPAY_TMN_CODE=<sandbox tmn code>
VNPAY_HASH_SECRET=<sandbox hash secret>
VNPAY_RETURN_URL=http://localhost:8080/api/v1/payment/vnpay/callback
VNPAY_IPN_URL=http://localhost:8080/api/v1/payment/vnpay/ipn
```

### 3. Run

```bash
./mvnw spring-boot:run
```

The default `mongodb` profile creates compatible collections and indexes on startup, then seeds the RBAC reference data when it is absent. Mongo transactions are enabled through the replica set.

The original PostgreSQL adapter and Flyway migrations remain in the repository for compatibility. To run that adapter explicitly, provide a PostgreSQL database and start with:

```bash
SPRING_PROFILES_ACTIVE=postgres ./mvnw spring-boot:run
```

Flyway then applies migrations from `src/main/resources/db/migration`:

- `V1__init_schema.sql` — 15 tables
- `V2__add_version_and_used_count.sql` — `@Version` columns + voucher usage counter

### 4. Tests

```bash
./mvnw test
```

Last run: **213 tests, 0 failures** (including context, Mongo versioning, service, domain, REST security, persistence, and ArchUnit tests).

### Optional local/staging showcase data

`scripts/seed-books-data.sql` is the legacy PostgreSQL showcase seed. For MongoDB, load equivalent catalogue documents with your deployment's seed process after the backend has created its collections; do not run the SQL file against MongoDB.

The SQL seed remains idempotent and can still be run manually against an external PostgreSQL instance when using the `postgres` profile.

Never run this script in production and never move its DML into `src/main/resources/db/migration`. Review cover-image rights and availability before any public deployment.

## Architecture

### Module layout

```text
com.velstrong.bookstore
├── BookBackendHexApplication.java
├── domain/                     ← framework-free business core
│   ├── model/                  ← aggregates, value objects, enums
│   ├── port/in/                ← use cases (driving ports)
│   ├── port/out/               ← repository / external service ports
│   └── exception/              ← BookstoreException + typed subclasses
├── application/                ← use case implementations
│   ├── service/<bc>/...        ← one @Service per use case
│   ├── command/                ← request → command records
│   └── response/               ← domain → response DTOs
└── infrastructure/             ← adapters (Spring, JPA, REST, JWT, Redis, …)
    ├── adapter/in/rest/<bc>/   ← @RestController + @Request DTOs
    ├── adapter/out/persistence/← JPA + Mongo entities/adapters, mappers
    ├── adapter/out/external/   ← JwtServiceImpl, BcryptPasswordEncoder
    ├── adapter/out/cache/      ← RedisTokenAdapter, RedisCacheAdapter
    └── config/                 ← SecurityConfig, ClockConfig
```

### Hexagonal rules (enforced by ArchUnit — 9 rules, see `HexagonalArchitectureTest`)

- `domain.*` has **zero** Spring / JPA / Jackson / Lombok dependencies
- Domain models never carry `@Service` / `@Entity` / `@Component`
- Controllers depend only on use case ports (`domain.port.in.*`)
- Application services depend on ports and the domain, never on infrastructure
- Each driven port has at least one `*PersistenceAdapter` / `*Impl` implementation
- One `*PersistenceAdapter` per port (1 port ↔ 1 adapter)

## Architecture decisions

The following are the conventions captured in code; they are the **why** behind the structure above.

### Money is `Long`, not `BigDecimal`

All monetary fields (`unitPrice`, `depositAmount`, `totalAmount`, `subtotal`, voucher discount values, …) are stored and passed as `Long` representing VND.

- **Reason.** VND has no sub-unit. `Long` keeps arithmetic trivial, formatting uniform, and JPA column types predictable (`BIGINT`).
- **Scope.** Everywhere except the JSON wire for outgoing responses, where we still send VND as a number.
- **Migration path.** If we ever support multi-currency or fractional units, switch to `BigDecimal` and a `Money` value object. Don't do it speculatively.

### Domain is time-free; services inject `Clock`

Domain methods that depend on "today" take an explicit `LocalDate` argument instead of calling `LocalDate.now()`:

```java
// domain
rental.returnBook(LocalDate today);
rental.isOverdue(LocalDate today);
customerSubscription.isActive(LocalDate today);

// service
public ReturnRentalService(..., Clock clock) {
    this.clock = clock;
}
...
LocalDate today = LocalDate.now(clock);
rental.returnBook(today);
```

A single `Clock` bean lives in `infrastructure.config.ClockConfig`. This makes the domain pure and easy to test, and the application layer decides what "now" means (system clock in production, fixed clock in tests).

### API envelopes don't leak `Optional`

Use cases return `null` instead of `Optional<T>` so the REST envelope is a clean `ApiResponse<T>` with `data: null` when nothing is found. Example: `GET /api/v1/subscriptions/me/active` returns `{"data": null, ...}` when the user has no active subscription. The controller does not need to flatten `Optional` at the boundary.

### One port, one adapter

Each driven port has its own `*PersistenceAdapter` (e.g. `OrderRepository` → `OrderPersistenceAdapter`, `OrderItemRepository` → `OrderItemPersistenceAdapter`, `IamTokenRepository` → `RedisTokenAdapter`). Adapter classes are never shared between ports.

### Real authorization

JWT carries `roles` and `perms` claims. The auth filter maps permissions directly to `SimpleGrantedAuthority`s (`voucher:manage`) and maps roles to `ROLE_<role>` authorities for compatibility. Static endpoint authorization lives in `src/main/resources/security-endpoints.yml`, while ownership checks stay in application/domain services. `@EnableMethodSecurity` remains enabled for future parameter-sensitive method security.

`security-endpoints.yml` has three explicit groups:

- `public`: no token required
- `authenticated`: valid JWT required, no specific permission
- `permissions`: valid JWT plus the configured authority

Endpoint policy must not use roles. Roles map to permissions through the database-backed RBAC tables.

### Token denylist on Redis

Logout is real: `LogoutService` writes the access token to a Redis denylist with a TTL equal to the token's remaining lifetime. The JWT auth filter checks the denylist before accepting a token.

### Password reset is real

`ForgotPasswordService` generates a 32-byte SecureRandom Base64-URL token, stores `reset:<token> → userId` in Redis with a 15-minute TTL, and emails the token. `ResetPasswordService` validates the token from Redis, encodes the new password, updates the user, and deletes the key.

## Security

- Stateless, JWT bearer token, no session
- Public routes: `/api/v1/auth/**`, `/api/v1/users/register`, `/api/v1/users/forgot-password`, `/api/v1/users/reset-password`, `/api/v1/books/**`, `/api/v1/payment/vnpay/**`
- Everything else requires a valid `Authorization: Bearer <token>` header
- The `currentUserId` is propagated to controllers via a request attribute (no Spring `Principal` coupling in the domain)
- Roles and permissions come from JWT claims loaded from database-backed RBAC tables
- Static endpoint permission checks are configured in `security-endpoints.yml`; services keep ownership/business-condition checks
- The JWT filter is wired once as a `private final` field, not a `@Bean`, to prevent Spring from registering it as a global filter in addition to the security chain

## Persistence model

MongoDB database `book_store` uses collection names corresponding to the PostgreSQL tables:

- `users`, `user_addresses`
- `books`, `book_copies`
- `carts`, `cart_items`
- `orders`, `order_items`
- `payments`
- `rentals`
- `vouchers`, `voucher_usages`
- `subscriptions`, `customer_subscriptions`
- `roles`, `permissions`, `user_roles`, `role_permissions`, `user_permissions`
- `categories`, `book_categories`, `blog_posts`, `book_reviews`
- `support_conversations`, `support_messages`, `support_message_attachments`
- `user_notifications`, `push_subscriptions`, `processed_bank_messages`, `unmatched_transfers`

The Mongo adapter keeps the same domain ports, identifiers, enum values, monetary `Long` values, relationship fields, and collection/table names as the PostgreSQL implementation. Versioned aggregates retain the JPA `@Version` field and use explicit Mongo `_id + version` compare-and-replace semantics, while order/book-copy lock-sensitive reads use transactional write markers; voucher usage is incremented atomically. PostgreSQL entities, repositories, migrations, and adapters are intentionally retained and activated only by the `postgres` profile. The profiles are mutually exclusive, so enabling both fails fast instead of choosing a datastore implicitly.

## API surface (high-level)

| Base path                          | Purpose                                      |
| ---------------------------------- | -------------------------------------------- |
| `POST /api/v1/auth/token`          | Login (username + password) → JWT            |
| `POST /api/v1/auth/refresh`        | Exchange refresh token for new pair          |
| `POST /api/v1/auth/logout`         | Denylist the current access token            |
| `POST /api/v1/users/register`      | Public registration                          |
| `GET/PUT /api/v1/users/me`         | Current user profile                         |
| `PUT /api/v1/users/me/password`    | Change password (authed)                     |
| `POST /api/v1/users/forgot-password` | Request a reset email (no-op on unknown)  |
| `POST /api/v1/users/reset-password`  | Apply a reset token                      |
| `GET /api/v1/books/...`            | Public book catalog                          |
| `GET /api/v1/cart`                 | Get the current user's cart (or empty)       |
| `POST /api/v1/cart/items`          | Add a cart item                              |
| `PUT /api/v1/cart/items/{id}`      | Update quantity                              |
| `DELETE /api/v1/cart/items/{id}`   | Remove                                       |
| `POST /api/v1/orders`              | Create an order from the cart                |
| `GET /api/v1/orders/me`            | My orders                                    |
| `GET /api/v1/orders/{id}`          | Order detail                                 |
| `POST /api/v1/orders/{id}/cancel`  | Cancel                                       |
| `PUT /api/v1/orders/{id}/status`   | Change status (`order:update-status`)        |
| `GET /api/v1/orders`               | List with filters (`order:read:all`)         |
| `POST /api/v1/payment/vnpay/...`   | VNPay create / return / IPN                  |
| `GET /api/v1/rentals/me`           | My rentals                                   |
| `POST /api/v1/rentals/{id}/return` | Return a rental                              |
| `GET /api/v1/rentals`              | List rentals (`rental:read:all`)             |
| `GET /api/v1/rentals/overdue`      | Overdue list (`rental:read:all`)             |
| `POST /api/v1/rentals/{id}/force-return` | Force return (`rental:checkin`)       |
| `POST /api/v1/rentals/start/{orderId}` | Start rentals from order (`rental:checkin`) |
| `POST /api/v1/subscriptions/purchase` | Buy a subscription                       |
| `GET /api/v1/subscriptions/me/active` | My active subscription (data: null if none) |
| `GET /api/v1/subscriptions`        | List subscription plans (`subscription:manage`) |
| `POST /api/v1/subscriptions`       | Create a subscription plan (`subscription:manage`) |
| `POST /api/v1/vouchers/quote`      | Preview a voucher's discount                 |
| `* /api/v1/vouchers...`            | Voucher management (`voucher:manage`)        |

## Conventions in this repo

- **One fix, one commit**, branch per phase using `feature/...`, `fix/...`, or `refactor/...` (`feature/subscription-management`, `fix/business-correctness`, `refactor/convention-cleanup`).
- **Domain stays pure**: no Spring, no JPA, no Jackson, no Lombok annotations on domain types.
- **Controllers bind a Request DTO**, not the Command record, when there's any validation/mapping work to do.
- **Adapters live in `infrastructure.adapter.out.*`**, grouped by technology (persistence, external, cache).
- **Use cases are interfaces in `domain.port.in.*`**, implemented as `@Service` classes in `application.service.*`.
- **Time is a parameter**, never `LocalDate.now()` in the domain. Inject `Clock` at the service boundary.
- **Money is `Long` VND**, not `BigDecimal`. Don't introduce fractional units without a `Money` value object.
- **No `Optional` in API responses.** Use cases return `null` when nothing is found.
- **Test everything that has business logic:** domain models with pure JUnit + AssertJ, services with Mockito, architecture with ArchUnit.
