# ARCHITECTURE

## 1) Overview

This service is a **version-controlled key-value store** with HTTP APIs.  
For each key, every write creates a new immutable version (`1..N`) with a UTC UNIX timestamp.

Core goals:
- Deterministic versioning under concurrent writes
- Point-in-time lookup by timestamp
- Production-grade structure (validation, retries, exception handling, docs, CI/CD)

---

## 2) High-Level Design

```mermaid
flowchart LR
    C[Client / Postman / curl] -->|HTTP| API[Spring Boot REST API]
    API --> SVC[KeyValueService]
    SVC --> CNT[KeyCounter Repository]
    SVC --> REC[Record Repository]
    CNT --> MDB[(MongoDB Atlas)]
    REC --> MDB
    API --> ACT[Actuator / Health]
    API --> SWG[Swagger/OpenAPI]
