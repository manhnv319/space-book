# Space Book — Sách Nhà (HaUI Master: Advanced Databases)

Full-stack Bookstore and Rental platform developed for the **Advanced Databases** (Dữ liệu nâng cao) Master's curriculum at Hanoi University of Industry (HaUI):
- **Backend** (`backend/`): Spring Boot 4 (Java 21), Hexagonal Architecture (Ports & Adapters) + Domain-Driven Design (DDD). Default runtime persistence is **MongoDB 7** (Spring Data MongoDB) per Advanced Databases curriculum requirements (NoSQL / Document store with replica set multi-document ACID transactions). A secondary **PostgreSQL 17** adapter (Spring Data JPA + Flyway) is maintained behind the `postgres` profile. Token denylist and caching are powered by Redis 7.
- **Frontend** (`frontend/`): Next.js 15 (React 19, App Router) + TypeScript + Tailwind CSS, communicating with the backend via REST API (`/api/v1/...`).

---

## Architecture

### Backend: Hexagonal Architecture (Ports & Adapters) + DDD
```text
com.velstrong.bookstore
├── domain/                      ← Framework-free business core (ZERO Spring/JPA/Mongo dependencies)
│   ├── model/                   ← Entities, Aggregates, Value Objects, Enums
│   ├── port/in/                 ← Driving ports (Use case interfaces)
│   ├── port/out/                ← Driven ports (Repository & SPI interfaces)
│   └── exception/               ← BookstoreException + typed domain exceptions
├── application/                 ← Application use cases
│   ├── service/<bc>/...         ← @Service implementations orchestrating domain models & ports
│   ├── command/                 ← Command records (Input DTOs for use cases)
│   └── response/                ← Response DTOs
└── infrastructure/              ← Technical adapters
    ├── adapter/in/rest/<bc>/    ← @RestController + Request DTOs
    ├── adapter/out/persistence/ ← Persistence adapters:
    │   ├── mongo/               ← Mongo documents, repositories, adapters (Default runtime)
    │   ├── adapter/             ← JPA persistence adapters (Postgres profile)
    │   └── entity/              ← JPA entities (Postgres profile)
    ├── adapter/out/external/    ← EmailAdapter, JwtServiceImpl, BcryptPasswordEncoder
    ├── adapter/out/cache/       ← RedisTokenAdapter, RedisCacheAdapter
    └── config/                  ← SecurityConfig, MongoPersistenceConfig, ClockConfig
```

#### Inviolable Hexagonal Rules (Enforced by ArchUnit)
1. `domain.*` must have **zero** dependencies on Spring, JPA, Jackson, or MongoDB annotations.
2. Controllers depend only on Driving Ports (`domain.port.in.*`), never directly on Service implementation classes.
3. Services depend only on Ports and Domain, never on Infrastructure classes.
4. Time is always parameter-injected via `java.time.Clock`, never hardcoded `LocalDateTime.now()` in domain/services.
5. Money is stored as `Long` (VND), never floating-point `float`/`double`/`BigDecimal`.

---

## Git Flow & Release Process

We follow GitHub Flow with strict CI gates (learned from Tarotvio):

1. **Branching Model**:
   - Main branch: `main` (production-ready).
   - Feature branches: `feat/<feature-name>` or `feature/<feature-name>`.
   - Bugfix branches: `fix/<bug-name>`.
   - Refactor branches: `refactor/<scope>`.
2. **Pull Request & CI Gate**:
   - All changes must target `main` via a Pull Request.
   - CI (`.github/workflows/ci.yml`) must be **GREEN** before merging:
     - Frontend: Lint (`eslint`), Typecheck (`tsc --noEmit`), Unit tests (`vitest`), Build (`next build`).
     - Backend: Unit tests, Mockito mocks, ArchUnit architecture rules (`mvn test`), Application package.
3. **Flow Diagrams & Review Gate**:
   - Flow-affecting PRs **MUST** include an Archify interactive diagram and desktop preview in `docs/architecture/<scope>.<type>.png` following `docs/architecture/pr-flow-diagrams.md`.
   - Changes without behavior-flow impact must explicitly state `Diagram: N/A — no behavior-flow impact`.
4. **Merge & Deploy**:
   - Merge to `main` via PR (Squash and merge or Rebase).
   - Deploy workflow (`.github/workflows/deploy.yml`) builds immutable container images to GitHub Container Registry (GHCR) and deploys to VPS host `100.102.202.99:/opt/velstrong-book`.

## On-Demand Playbooks

- Flow-affecting PR diagrams: `docs/architecture/pr-flow-diagrams.md`
- Diagram toolchain: `.claude/skills/archify/SKILL.md`
---

## Local Development Commands

### Backend (`backend/`)
```bash
# 1. Start MongoDB (replica set rs0) + Redis
docker compose up -d

cd backend

# 2. Run test suite (Unit tests + ArchUnit hexagonal tests)
./mvnw test

# 3. Start application (defaults to MongoDB profile)
./mvnw spring-boot:run

# 4. Start with PostgreSQL profile (legacy/fallback)
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Frontend (`frontend/`)
```bash
cd frontend

# 1. Install dependencies
npm install

# 2. Start development server (http://localhost:3000)
npm run dev

# 3. Quality & test checks
npm run lint          # ESLint
npm run typecheck     # TypeScript compiler check
npm run test:unit     # Vitest unit tests (105 tests)
npm run build         # Next.js production build
```

---

## Conventions
- **One fix, one commit**: Follow Conventional Commits (`feat: ...`, `fix: ...`, `refactor: ...`, `docs: ...`).
- **HaUI Master: Advanced Databases Curriculum**: Any schema change, aggregation pipeline, or multi-document transaction on MongoDB must comply with schemas and indexes configured in `MongoSchemaInitializer` and `MongoPersistenceConfig`.
