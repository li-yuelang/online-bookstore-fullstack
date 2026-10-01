# Online Bookstore — React + Spring Boot

A full-stack course project with a React/Vite storefront and admin interface, a Spring Boot REST API, and a MySQL database. This repository is a curated copy of the final iteration: generated build output, dependencies, course handouts, submission archives, and local database credentials are excluded.

## Features

- Storefront: browse/search books, view details and reviews, manage a cart, and place orders.
- Accounts: registration, login, profile, and enabled/disabled user status.
- Admin interface: manage books, users, and orders; filter orders.
- Statistics: sales totals, user spending, and personal purchase summaries.
- Backend structure: Controller → Service → Repository with DTOs and JPA entities for users, books, reviews, cart items, orders, and order items.

The backend source defines 27 HTTP endpoint mappings. The included test sources contain 38 `@Test` methods across controller, service, and application tests. These counts describe the code, not a claim that every test currently passes in every environment.

## Project layout

```text
backend/   Spring Boot application, REST controllers, JPA entities, SQL schema/data, tests
frontend/  React/Vite application, customer/admin routes, components, demo book data
```

## Run locally

Requirements: Java 8+, Maven, MySQL 8, Node.js and npm compatible with Vite 5.

1. Create an empty MySQL database named `online_library`.
2. Set `DB_URL`, `DB_USER`, and `DB_PASSWORD` for your own MySQL instance. `DB_URL` defaults to a local `online_library` database; `DB_USER` defaults to `root`. No database password is stored in this repository.
3. Start the backend from `backend/`:

   ```bash
   mvn spring-boot:run
   ```

4. In another terminal, start the frontend from `frontend/`:

   ```bash
   npm ci
   npm run dev
   ```

Vite proxies `/api` requests to `http://localhost:8080`. The backend initializes tables and demonstration book/review data from `backend/src/main/resources/schema.sql` and `data.sql`.

Run backend tests with `mvn test` in `backend/`; build the frontend with `npm run build` in `frontend/`.

## Security and provenance

This is an educational demo, **not a production-ready commerce service**. The current login checks plaintext passwords and the UI's role-based route guards are not a substitute for server-side authorization. Do not deploy it with real user data or expose it to the public internet without proper password hashing, authentication/authorization, input validation, and security review.

The work originated as a course assignment. The assignment PDFs and handout directories are not included; see [PROVENANCE.md](PROVENANCE.md). No license is asserted for course-provided material or third-party cover images.
