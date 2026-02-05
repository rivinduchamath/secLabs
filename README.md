![Build](https://github.com/rivinduchamath/secLabs/actions/workflows/ci.yml/badge.svg)


# Versioned Key-Value Store (Secretlab Tech Exercise)

Production-ready Java service that stores versioned values per key and supports:
- create/update with strict version increments,
- latest value lookup,
- point-in-time lookup by UNIX timestamp (UTC),
- list all current records.

---

## Live Links

- **Base URL:** `https://seclabs-production.up.railway.app`
- **Swagger UI:** `https://seclabs-production.up.railway.app/swagger-ui/index.html`
- **OpenAPI JSON:** `https://seclabs-production.up.railway.app/v3/api-docs`
- **Health:** `https://seclabs-production.up.railway.app/actuator/health`

> Replace with your final repo URL below:
- **GitHub Repo:** `<YOUR_PUBLIC_GITHUB_REPO_URL>`

---

## Tech Stack

- **Java 17**
- **Spring Boot 3.3.5** (Web MVC, Validation, Actuator)
- **MongoDB Atlas** (Spring Data MongoDB)
- **Spring Retry + AOP** (transient failure retry)
- **Lombok**
- **Swagger/OpenAPI** (`springdoc-openapi`)
- **JUnit 5 + Spring Boot Test**
- **Testcontainers (MongoDB)** for integration tests
- **JaCoCo** for coverage
- **GitHub Actions** for CI
- **Railway** for deployment
- **Docker** for containerized runtime

---

## Requirements Coverage

### 1) Accept key + value and store versioned record
- If key does not exist -> version `1`
- If key exists -> version increments by exactly `1`
- Handles concurrent updates so versions remain contiguous (`1..N`)

### 2) Get latest value by key
- `GET /object/{key}`

### 3) Get value by key and timestamp
- `GET /object/{key}?timestamp=<unix_seconds>`

### 4) Display all current records
- `GET /object/get_all_records`

---

## API Endpoints
# Swagger
https://seclabs-production.up.railway.app/swagger-ui/index.html#/

# DOC
https://seclabs-production.up.railway.app/v3/api-docs

# 1) Create/Update (v1)
curl --location --request POST 'https://seclabs-production.up.railway.app/object' \
--header 'Content-Type: application/json' \
--data-raw '{"mykey":"value1"}'

# 2) Get latest
curl --location --request GET 'https://seclabs-production.up.railway.app/object/mykey'

# 3) Get value at timestamp
curl --location --request GET 'https://seclabs-production.up.railway.app/object/mykey?timestamp=1738737000'

# 4) Get all current records
curl --location --request GET 'https://seclabs-production.up.railway.app/object/get_all_records'

