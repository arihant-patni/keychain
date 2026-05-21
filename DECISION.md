# DECISIONS.md

## 1. Multi‑Module Framework with JAR Reuse
**Intent**  
Build a modular automation framework where each team owns its module, compiles it into a JAR, and can consume other teams’ JARs for integration testing.

**Impact**
- Promotes separation of concerns (core utilities vs. service‑specific tests).
- Enables cross‑team collaboration by sharing JARs.
- Keeps the framework extensible and maintainable.

---

## 2. Protocol‑Agnostic Client Layer
**Intent**  
Rejected RestAssured to avoid tight coupling with REST. Instead, used Java’s `HttpURLConnection` (or similar low‑level client).

**Impact**
- Framework stays protocol‑agnostic → can extend to SOAP or gRPC without rewriting.
- Provides fine‑grained control over headers, payloads, and response handling.
- Lightweight, with fewer external dependencies.

---

## 3. Helper Functions Exposed as Services
**Intent**  
Convert test helpers into REST controllers so they can be consumed by non‑technical users (manual testers, data creators).

**Impact**
- Bridges the gap between automation and manual QA.
- Allows data creation and validation without deep system knowledge.
- Encourages reuse of automation assets as internal services.
