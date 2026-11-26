# JakLoc Architecture

This document explains how the first milestone is organized and why. It is the
reference for both developers before adding new modules.

## 1. Goals

- Keep a clean MVC separation that beginners can follow.
- Share one `User` model and one authentication layer.
- Make the persistence implementation replaceable without touching servlets or
  JSP pages.
- Protect private routes centrally, including routes added by future modules.
- Avoid unnecessary frameworks and patterns.

## 2. Layers

```
Browser
   |
   | HTTP (POST/GET)
   v
+---------------------------+
| Filters                   |  AuthenticationFilter: public paths, session,
| tn.jakloc.filter          |  role checks, no-store headers
+---------------------------+
   |
   v
+---------------------------+
| Servlets                  |  Read request, validate input format,
| tn.jakloc.servlet.*       |  call a service, forward or redirect
+---------------------------+
   |
   v
+---------------------------+
| Services                  |  Business rules: duplicate email, hashing,
| tn.jakloc.service         |  credential verification, role assignment
+---------------------------+
   |
   v
+---------------------------+
| DAO interfaces            |  UserRepository contract
| tn.jakloc.dao.interfaces  |
+---------------------------+
   |
   v
+---------------------------+
| DAO implementations       |  JdbcUserRepository,
| tn.jakloc.dao             |  JdbcVehicleRepository
|                           |  (in-memory fallback)
+---------------------------+

JSP + EL/JSTL render data only. No Java business logic.
```

## 3. Package responsibilities

| Package | Responsibility | Must not contain |
| --- | --- | --- |
| `tn.jakloc.model` | Shared domain objects (`User`, `Role`, `Vehicle` and vehicle enums) | SQL, HTTP, JSP logic |
| `tn.jakloc.dao.interfaces` | Persistence contracts (`UserRepository`, `VehicleRepository`) | Implementation details |
| `tn.jakloc.dao` | JDBC repositories (`JdbcUserRepository`, `JdbcVehicleRepository`), `Database` connection helper, in-memory fallbacks and vehicle seed data | HTTP or session logic |
| `tn.jakloc.service` | Business rules and use cases (`AuthService`, `VehicleService`) | Request/response objects |
| `tn.jakloc.servlet.auth` | Login, registration, logout endpoints | Password hashing, SQL |
| `tn.jakloc.servlet.client` | CLIENT dashboard and vehicle catalogue | Admin-only features |
| `tn.jakloc.servlet.admin` | ADMIN pages | Client-only features |
| `tn.jakloc.filter` | Cross-cutting request protection and UTF-8 encoding | Business rules |
| `tn.jakloc.util` | Constants, validation, hashing, session, startup wiring | Domain logic |
| `tn.jakloc.exception` | Application exceptions | Stack-trace exposure |

## 4. Startup wiring

`AppContextListener` runs once when the application starts:

1. Calls `Database.configure(ServletContext)` to resolve the JDBC URL, user and
   password from `web.xml` context parameters, environment variables or system
   properties.
2. Creates one `JdbcUserRepository` and one `JdbcVehicleRepository` and probes
   them (`findAll`). If the connection or schema is unavailable, it logs the
   cause and switches to `InMemoryUserRepository` / `InMemoryVehicleRepository`.
3. Creates one `AuthService` and one `VehicleService`.
4. Seeds the demo ADMIN and CLIENT accounts and, when the vehicle repository is
   empty, the demo vehicle fleet.
5. Stores the repositories and services in the `ServletContext` under their
   `AppConstants` names.

Servlets read the service in `init()`:

```java
authService = (AuthService) getServletContext().getAttribute(AppConstants.AUTH_SERVICE);
vehicleService = (VehicleService) getServletContext().getAttribute(AppConstants.VEHICLE_SERVICE);
```

This keeps the selected repositories alive between requests without using static
singletons in the servlet classes.

## 5. Authentication flows

### 5.1 Registration

```
POST /register
  -> AuthenticationFilter (public path, no session required)
  -> RegisterServlet
       - trim fields
       - validate full name, email, phone, password, confirmation
       - on format errors: forward to register.jsp with a field error map
       - on valid format: AuthService.register(...)
            - normalize email (lowercase)
            - check duplicate email
            - hash password with BCrypt (cost 12)
            - create User with Role.CLIENT and createdAt
            - repository.save(user)
       - success: redirect to /login?registered=1
       - duplicate: forward to register.jsp with an email error
```

Passwords are never put in the URL and never stored in plain text. The form
uses `method="post"`.

### 5.2 Login

```
POST /login
  -> AuthenticationFilter (public path)
  -> LoginServlet
       - validate non-empty and email format
       - AuthService.authenticate(email, password)
            - repository.findByEmail(normalized email)
            - PasswordUtil.matches(raw, storedHash)
       - failure: generic "Invalid email or password."
       - success: SessionUtil.login(request, user)
            - invalidate any existing session
            - create a new session (prevents session fixation)
            - store authenticatedUser and userRole
       - redirect to /client/dashboard or /admin/dashboard
```

The failure message is intentionally the same for an unknown email and a wrong
password so the page does not reveal which accounts exist.

### 5.3 Protected request

```
GET /client/dashboard
  -> AuthenticationFilter
       - public? no
       - session user? yes
       - role allowed for /client/*? CLIENT yes
       - add Cache-Control: no-store
  -> ClientDashboardServlet
       - forward to /WEB-INF/views/client/dashboard.jsp
```

If there is no session, the filter redirects to `/login?authRequired=1`. If the
role does not match, it sends HTTP 403, which maps to
`WEB-INF/views/errors/403.jsp`.

### 5.4 Logout

```
POST /logout
  -> AuthenticationFilter (requires a session)
  -> LogoutServlet
       - SessionUtil.logout(request): invalidate session
       - redirect to /login?logout=1
```

Only POST is implemented. A GET request to `/logout` receives 405 from the
container. Private responses are marked `no-store`, so the browser Back button
does not render a cached dashboard after logout.

## 6. Route protection matrix

| Path | Anonymous | CLIENT | ADMIN |
| --- | --- | --- | --- |
| `/`, `/index.jsp` | redirect to login | redirect to client dashboard | redirect to admin dashboard |
| `/login`, `/register` | allowed | redirect to client dashboard | redirect to admin dashboard |
| `/assets/*` | allowed | allowed | allowed |
| `/client/*` | redirect to login | allowed | 403 |
| `/admin/*` | redirect to login | 403 | allowed |
| `/logout` | redirect to login | allowed (POST) | allowed (POST) |
| any other path | redirect to login | 404 if unknown | 404 if unknown |

The filter is deny-by-default: a new path is protected unless it is explicitly
listed in `PUBLIC_PATHS`.

## 7. Session model

| Attribute | Type | Constant |
| --- | --- | --- |
| `authenticatedUser` | `tn.jakloc.model.User` | `AppConstants.SESSION_USER` |
| `userRole` | `tn.jakloc.model.Role` | `AppConstants.SESSION_ROLE` |

- Session timeout: 30 minutes (`web.xml`).
- Session cookie: HttpOnly.
- URL rewriting is disabled (`<tracking-mode>COOKIE</tracking-mode>`).
- The session is invalidated and recreated at login.
- JSP pages read the user through `sessionScope`; they never query a repository.

`User.passwordHash` remains server-side in the session object. JSPs display only
`fullName`, `email` and `role`; they must never render `passwordHash`.

## 8. Persistence strategy

The default implementation is JDBC against MySQL:

- `Database` resolves `jakloc.db.url`, `jakloc.db.user` and `jakloc.db.password`
  from `web.xml`, then environment variables (`JAKLOC_DB_*`), then system
  properties, then built-in defaults (`localhost:3306/jakloc`, `root`, empty).
- `JdbcUserRepository` and `JdbcVehicleRepository` open a connection per
  operation with try-with-resources and map rows to domain objects.
- The SQL unique index on `users.email` is mapped to `DuplicateEmailException`,
  complementing the service-level check.
- Email lookup is case-insensitive.
- Schema and seed data are in `database/jakloc.sql`.
- `VehicleSeeder` inserts the demo fleet only when the `vehicles` table is empty.

If the connection or schema is unavailable at startup, `AppContextListener`
logs the cause and falls back to `InMemoryUserRepository` and
`InMemoryVehicleRepository`, which use `ConcurrentHashMap` and `AtomicLong`.
In that mode data is lost on restart.

### Schema

The canonical schema lives in `database/jakloc.sql`. The `users` table stores a
BCrypt hash (never a plain password), and `vehicles` stores the catalogue fields
plus the demo seed rows.

## 9. Security decisions

| Concern | Decision |
| --- | --- |
| Password storage | BCrypt, cost 12, random salt per password |
| Password transport | POST only; never in a query string |
| Session fixation | Invalidate the old session and create a new one after login |
| Route access | Central filter, deny-by-default, role checks |
| Error messages | Generic login failure; no stack traces in JSPs |
| Caching | `Cache-Control: no-store` on authenticated responses |
| Output escaping | `<c:out>` and `fn:escapeXml` in JSPs |
| Admin demo account | Seeded in MySQL and in memory; documented; must be removed/changed later |

Not implemented yet (acceptable for this milestone, document before release):

- CSRF tokens for state-changing forms.
- Rate limiting / login throttling.
- Email verification and password reset.
- HTTPS enforcement (`Secure` cookie flag).
- Audit logging.

## 10. UI architecture

- `base.css` contains design tokens and shared components.
- `auth.css` contains the split-screen authentication layout.
- `dashboard.css` contains the shared dashboard shell.
- `client.css` contains the client header, dashboard, catalogue and vehicle
  cards.
- `auth.js` contains only two behaviors: password visibility and live
  password-match feedback.
- `client.js` auto-submits catalogue filters and shows a loading label. The
  search form remains usable without JavaScript.
- Login and register pages use a dark editorial brand panel and a white auth
  card, following `DESIGN.md` (warm cream, charcoal, amber gold, Plus Jakarta
  Sans, pill buttons, diffused shadows).
- The client dashboard and catalogue reuse the same design language, with
  compact vehicle cards, filter capsules and a dark fleet hero.
- Layouts collapse to a single column below 960px and to a single-column form
  below 560px.
- Fonts load from Google Fonts with a system-font fallback.

## 11. Known limitations

- Persistence requires MySQL to be running and `database/jakloc.sql` to be
  applied; otherwise the app silently falls back to in-memory storage for that
  run.
- The JDBC layer opens a connection per operation; a pool (HikariCP) is a later
  improvement.
- Vehicle data is demo seed data, not admin-managed production data.
- The admin dashboard is a protected placeholder, not a feature.
- Vehicle details, reservations, cancellation, favorites, payments and profile
  editing are not implemented.
- No automated tests are included yet; verification is manual (see README).
- The local Eclipse configuration references Tomcat 10.1; TomEE 10 must be added
  as a runtime in Eclipse before running on TomEE.
