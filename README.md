# Space Book

<p align="center">
  <a href="https://github.com/manhnv319/space-book/actions/workflows/deploy.yml">
    <img src="https://img.shields.io/badge/%F0%9F%9A%80%20SHIP%20IT-DEPLOY%20TO%20PROD-238636?style=for-the-badge&logo=githubactions&logoColor=white" alt="Ship It - Deploy to Prod" />
  </a>
  &nbsp;
  <a href="https://github.com/manhnv319/space-book/actions/workflows/ci.yml">
    <img src="https://img.shields.io/badge/CI-Pipeline-15803d?style=for-the-badge&logo=githubactions&logoColor=white" alt="CI Pipeline" />
  </a>
  &nbsp;
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
  &nbsp;
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.6-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot 4" />
  &nbsp;
  <img src="https://img.shields.io/badge/Next.js-15%20(React%2019)-black?style=for-the-badge&logo=next.js&logoColor=white" alt="Next.js 15" />
  &nbsp;
  <img src="https://img.shields.io/badge/MongoDB-7%20(Replica%20Set)-47A248?style=for-the-badge&logo=mongodb&logoColor=white" alt="MongoDB 7" />
  &nbsp;
  <img src="https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis 7" />
</p>

A modern full-stack Bookstore & Book Rental platform developed as part of the **Advanced Databases** (Dữ liệu nâng cao) Master's curriculum at Hanoi University of Industry (HaUI).

The platform features a **Hexagonal Architecture (Ports & Adapters) + Domain-Driven Design (DDD)** backend running on Spring Boot 4 with MongoDB 7 replica set as primary persistence, coupled with a high-performance **Next.js 15 App Router** frontend.

---

## Highlights & Features

- **Storefront & Catalog**: Browse books by category, curated collections, full-text search, and detailed book specifications.
- **Book Rental System**: Flexible rental subscriptions, rental copy tracking, fulfillment, return workflows, and overdue handling.
- **Cart & Orders**: Cart management, voucher reservation, and atomic order creation with transactional consistency.
- **Payment Integration**: VNPay sandbox integration with instant payment notification (IPN) and callback verification.
- **Realtime Support & Notifications**: Support chat threads with attachments, inbox management, and Web Push notifications.
- **Dual Persistence Architecture**: Native MongoDB 7 Document persistence (default for Advanced Databases curriculum) with a secondary PostgreSQL 17 JPA adapter profile (`postgres`).
- **Security & RBAC**: Stateless JWT authentication (`jjwt 0.12`), refresh token rotation, Redis-backed token denylist, and granular role/permission enforcement.

---

## System Architecture

```text
space-book/
├── backend/                       # Spring Boot 4 (Java 21, Maven)
│   ├── src/main/java/.../bookstore/
│   │   ├── domain/                # Framework-free business core (Entities, Aggregates, Ports)
│   │   │   ├── model/             # Domain models, value objects, enums
│   │   │   ├── port/in/           # Driving ports (Use case interfaces)
│   │   │   ├── port/out/          # Driven ports (Repository & SPI interfaces)
│   │   │   └── exception/         # Domain exceptions
│   │   ├── application/           # Application layer (Use case implementations)
│   │   │   ├── service/           # @Service implementations of driving ports
│   │   │   ├── command/           # Request command records
│   │   │   └── response/          # Response DTOs
│   │   └── infrastructure/        # Adapters layer (Spring Boot, Mongo, JPA, Redis, Web)
│   │       ├── adapter/in/rest/   # REST Controllers & Request DTOs
│   │       ├── adapter/out/persistence/
│   │       │   ├── mongo/         # MongoDB documents, repositories, adapters (Default)
│   │       │   ├── adapter/       # JPA persistence adapters (PostgreSQL fallback)
│   │       │   └── entity/        # JPA entities
│   │       ├── adapter/out/cache/ # Redis token & cache adapters
│   │       └── config/            # Security, Mongo replica set & Clock configs
│   ├── pom.xml
│   └── Dockerfile
├── frontend/                      # Next.js 15 (React 19, TypeScript, Tailwind CSS)
│   ├── src/
│   │   ├── app/                   # App Router pages & API routes
│   │   ├── components/            # UI components (storefront, cart, rental, support)
│   │   └── lib/                   # API client, hooks, and types
│   ├── package.json
│   └── Dockerfile
├── .github/                       # GitHub Actions CI/CD workflows
│   └── workflows/
│       └── ci.yml                 # Unified monorepo CI pipeline
├── docker-compose.yml             # Local MongoDB (replica set rs0) + Redis 7
├── CLAUDE.md                      # AI Agent & engineering guidelines
└── AGENTS.md                      # Symlink to CLAUDE.md for multi-agent support
```

### Inviolable Hexagonal Rules (ArchUnit Enforced)

1. `domain.*` has **zero** dependencies on Spring, JPA, Jackson, or MongoDB.
2. Controllers depend strictly on Driving Ports (`domain.port.in.*`), never on concrete Service implementations.
3. Application services depend only on Ports and Domain, never on Infrastructure adapters.
4. Time is always parameter-injected via `java.time.Clock`, never hardcoded `LocalDateTime.now()`.
5. Currency is stored as `Long` (VND) without fractional floating-point values.

---

## Getting Started

### Prerequisites

- **Docker & Docker Compose** (v2+)
- **JDK 21** (Temurin recommended)
- **Node.js 20+** and **npm**

### 1. Start Backing Services

From the root directory, start MongoDB and Redis:

```bash
docker compose up -d
```

This starts:
- **MongoDB 7**: Running as a single-node replica set `rs0` on port `27017` (required for MongoDB multi-document ACID transactions).
- **Redis 7**: Running on port `6379` for token denylist and caching.

### 2. Run the Backend

```bash
cd backend

# Copy sample environment configuration if needed
cp .env.example .env

# Run the Spring Boot application (defaults to MongoDB profile)
./mvnw spring-boot:run
```

The backend server starts on `http://localhost:8080`.

> **Note**: To run with PostgreSQL instead of MongoDB:
> ```bash
> ./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
> ```

### 3. Run the Frontend

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

---

## Testing & Quality Assurance

### Backend Verification

```bash
cd backend

# Run all unit tests, Mockito mocks, and ArchUnit architecture rules
./mvnw test

# Package application without running tests
./mvnw package -DskipTests
```

### Frontend Verification

```bash
cd frontend

# Run ESLint check
npm run lint

# Run TypeScript compiler check
npm run typecheck

# Run Vitest unit tests (105 tests)
npm run test:unit

# Verify Next.js production build
npm run build
```

---

## Git Flow & Collaboration

We follow GitHub Flow with strict CI verification:

1. **Branching**:
   - `main`: Production-ready branch.
   - `feat/<feature-name>`: New feature work.
   - `fix/<issue-name>`: Bug fixes.
   - `refactor/<scope>`: Code refactoring.
2. **Pull Requests**:
   - Every change targets `main` via a Pull Request.
   - CI Pipeline (`.github/workflows/ci.yml`) must be completely green before merging.
3. **Commit Convention**:
   - Follow [Conventional Commits](https://www.conventionalcommits.org/): `feat: ...`, `fix: ...`, `refactor: ...`, `docs: ...`.

---

## License

This project is developed for academic and demonstration purposes.
