# Velstrong Bookstore — HaUI Master: Dữ liệu nâng cao

Full-stack Bookstore platform phục vụ đề tài môn **Dữ liệu nâng cao** (Cao học CNTT - HaUI):
- **Backend** (`backend/`): Spring Boot 4 (Java 21), Hexagonal Architecture (Ports & Adapters) + DDD. Runtime persistence mặc định là **MongoDB 7** (Spring Data MongoDB) theo yêu cầu môn Dữ liệu nâng cao (NoSQL/Document store), có adapter **PostgreSQL 17** (Spring Data JPA + Flyway) qua profile `postgres`. Cache và token denylist dùng Redis 7.
- **Frontend** (`frontend/`): Next.js 15 (React 19, App Router) + TypeScript + Tailwind CSS, giao tiếp với backend qua REST API `/api/v1/...`.

---

## Architecture

### Backend: Hexagonal Architecture (Ports & Adapters) + DDD
```text
com.velstrong.bookstore
├── domain/                      ← Core nghiệp vụ thuần túy (ZERO framework/Spring/JPA)
│   ├── model/                   ← Entities, Aggregates, Value Objects, Enums
│   ├── port/in/                 ← Use case interfaces (Driving ports)
│   ├── port/out/                ← Repository / External service interfaces (Driven ports)
│   └── exception/               ← BookstoreException + typed domain exceptions
├── application/                 ← Implementations của use cases
│   ├── service/<bc>/...         ← @Service thực hiện nghiệp vụ qua domain & ports
│   ├── command/                 ← Command records (input DTO của use case)
│   └── response/                ← Response DTOs
└── infrastructure/              ← Adapters công nghệ cụ thể
    ├── adapter/in/rest/<bc>/    ← @RestController + Request DTOs
    ├── adapter/out/persistence/ ← Persistence adapters:
    │   ├── mongo/               ← Mongo documents, Mongo repositories, Mongo adapters (Default)
    │   ├── adapter/             ← JPA persistence adapters (Postgres profile)
    │   └── entity/              ← JPA entities (Postgres profile)
    ├── adapter/out/external/    ← EmailAdapter, JwtServiceImpl, BcryptPasswordEncoder
    ├── adapter/out/cache/       ← RedisTokenAdapter, RedisCacheAdapter
    └── config/                  ← SecurityConfig, MongoPersistenceConfig, ClockConfig
```

#### Quy tắc Hexagonal bất biến (ArchUnit kiểm soát tự động)
1. `domain.*` **tuyệt đối không** phụ thuộc Spring, JPA, Jackson, MongoDB annotations.
2. Controller chỉ gọi Driving Ports (`domain.port.in.*`), không gọi trực tiếp Service implementation.
3. Service chỉ phụ thuộc Ports và Domain, không phụ thuộc Infrastructure.
4. Thời gian dùng `java.time.Clock` inject từ ngoài, không gọi `LocalDateTime.now()` trực tiếp trong domain/service.
5. Tiền tệ là `Long` (VND), không dùng `float` / `double` / `BigDecimal`.

---

## Git Flow & Release Process

Áp dụng chuẩn GitHub Flow / PR-based Git Flow (học hỏi từ Tarotvio):

1. **Branching Model**:
   - Nhánh chính: `main` (production-ready).
   - Nhánh tính năng: `feat/<tên-tính-năng>` hoặc `feature/<tên-tính-năng>`.
   - Nhánh sửa lỗi: `fix/<tên-lỗi>`.
   - Nhánh tái cấu trúc: `refactor/<tên-refactor>`.
2. **Pull Request & CI Gate**:
   - Mọi thay đổi đều tạo PR trỏ về `main`.
   - Bắt buộc CI (`.github/workflows/ci.yml`) phải **XANH** trước khi merge:
     - Frontend: Lint (`eslint`), Typecheck (`tsc --noEmit`), Unit test (`vitest`), Build (`next build`).
     - Backend: Unit tests, Mockito tests, ArchUnit verification (`mvn test`), Package build.
3. **Merge & Deploy**:
   - Merge vào `main` qua PR (Squash and merge hoặc Rebase).
   - Workflow `.github/workflows/deploy.yml` tự động build Docker image ARM64/AMD64 đẩy lên GitHub Container Registry (GHCR) và deploy qua Tailscale/SSH tới VPS host `100.102.202.99:/opt/velstrong-book`.

---

## Local Development Commands

### Backend (`backend/`)
```bash
cd backend

# 1. Khởi động MongoDB (replica set rs0) + Redis
docker compose up -d

# 2. Chạy test suite (Unit tests + ArchUnit hexagonal tests)
./mvnw test

# 3. Chạy ứng dụng (mặc định profile MongoDB)
./mvnw spring-boot:run

# 4. Chạy với PostgreSQL (profile legacy/fallback)
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Frontend (`frontend/`)
```bash
cd frontend

# 1. Cài đặt dependencies
npm install

# 2. Khởi chạy dev server (http://localhost:3000)
npm run dev

# 3. Kiểm tra code & tests
npm run lint          # ESLint
npm run typecheck     # TypeScript compiler check
npm run test:unit     # Vitest unit tests (105 tests)
npm run build         # Next.js production build
```

---

## Conventions
- **One fix, one commit**: Commit rõ ràng theo Conventional Commits (`feat: ...`, `fix: ...`, `refactor: ...`).
- **Khóa học Master Dữ liệu nâng cao**: Mọi thay đổi về dữ liệu, query aggregation, transaction trên MongoDB cần tuân thủ schema và indexes cấu hình tại `MongoSchemaInitializer` / `MongoPersistenceConfig`.
