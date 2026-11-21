# JakLoc API Contract

Version: 1.0
Scope: shared foundation and authentication module
Owners: Omar (authentication), Moomen (vehicles), shared foundation by both

This document is the agreement between the two modules. Change it before
changing a shared class, route or naming convention.

## 1. Shared domain model

### 1.1 `tn.jakloc.model.User`

| Field | Type | Notes |
| --- | --- | --- |
| `id` | `long` | Assigned by the repository; `0` means unsaved |
| `fullName` | `String` | 3-80 characters |
| `email` | `String` | Unique, normalized to lowercase by `AuthService` |
| `passwordHash` | `String` | BCrypt hash; never display or log |
| `phone` | `String` | 8-15 digits, may contain `+`, spaces, `()`, `-` |
| `role` | `tn.jakloc.model.Role` | `ADMIN` or `CLIENT` |
| `createdAt` | `java.time.LocalDateTime` | Set at registration |

Rules:

- There is exactly one `User` class in the application.
- Do not add vehicle-specific fields to `User`. Use relationships in the
  vehicle/reservation modules later.
- Getters and setters follow the JavaBean convention so EL can read them.

### 1.2 `tn.jakloc.model.Role`

```java
public enum Role {
    ADMIN,
    CLIENT
}
```

Do not add string role constants elsewhere. Use the enum.

## 2. Persistence contract

### 2.1 `tn.jakloc.dao.interfaces.UserRepository`

```java
Optional<User> findById(long id);
Optional<User> findByEmail(String email);
List<User> findAll();
boolean existsByEmail(String email);
User save(User user) throws DuplicateEmailException;
```

Agreements:

- `findByEmail` is case-insensitive.
- `save` assigns an id when `user.getId() == 0`.
- `save` throws `DuplicateEmailException` when another user has the same email.
- The current implementation is `tn.jakloc.dao.InMemoryUserRepository`.
- A future `JdbcUserRepository` must implement the same interface.
- No servlet or JSP may call the repository directly for authentication.

## 3. Authentication service

### 3.1 `tn.jakloc.service.AuthService`

```java
public AuthService(UserRepository userRepository)

public User register(String fullName, String email, String phone, String rawPassword)
        throws DuplicateEmailException

public Optional<User> authenticate(String email, String rawPassword)
```

Agreements:

- `register` always creates a `CLIENT` account.
- `register` hashes the password with BCrypt and never stores the raw value.
- `register` normalizes the email before checking duplicates.
- `authenticate` returns an empty `Optional` for an unknown email or a wrong
  password.
- Callers must show one generic login failure message, not distinguish the two
  cases.

### 3.2 `tn.jakloc.exception.DuplicateEmailException`

- Message is user-safe: "An account with this email already exists."
- The servlet displays it on the email field.
- A future JDBC implementation maps SQL unique violations to this exception.

## 4. Session contract

| Attribute | Constant | Type |
| --- | --- | --- |
| `authenticatedUser` | `AppConstants.SESSION_USER` | `User` |
| `userRole` | `AppConstants.SESSION_ROLE` | `Role` |

Rules:

- Only `SessionUtil.login()` writes these attributes.
- Only `SessionUtil.logout()` invalidates the session.
- Other modules read the user with `SessionUtil.getUser(request)` or EL
  `sessionScope.authenticatedUser`.
- Never introduce a second session attribute for the current user.
- Never store the raw password or password hash in a new session attribute.

`SessionUtil` public methods:

```java
static void login(HttpServletRequest request, User user)
static void logout(HttpServletRequest request)
static User getUser(HttpServletRequest request)
static Role getRole(HttpServletRequest request)
static boolean isLoggedIn(HttpServletRequest request)
static String dashboardPath(User user)
```

## 5. Context attributes

| Attribute | Constant | Type |
| --- | --- | --- |
| `authService` | `AppConstants.AUTH_SERVICE` | `AuthService` |
| `userRepository` | `AppConstants.USER_REPOSITORY` | `UserRepository` |

These are created once in `AppContextListener`. Do not create additional
application-wide instances.

## 6. Route contract

| Method | Path | Access | Servlet | View / redirect |
| --- | --- | --- | --- | --- |
| GET | `/` | public | welcome file | redirect `/login` |
| GET | `/login` | public | `LoginServlet` | `auth/login.jsp` |
| POST | `/login` | public | `LoginServlet` | redirect by role |
| GET | `/register` | public | `RegisterServlet` | `auth/register.jsp` |
| POST | `/register` | public | `RegisterServlet` | redirect `/login?registered=1` |
| POST | `/logout` | authenticated | `LogoutServlet` | redirect `/login?logout=1` |
| GET | `/client/dashboard` | CLIENT | `ClientDashboardServlet` | `client/dashboard.jsp` |
| GET | `/admin/dashboard` | ADMIN | `AdminDashboardServlet` | `admin/dashboard.jsp` |

Views live under `WEB-INF/views/` and are reached only by forward, never by a
direct browser URL.

## 7. Authentication filter contract

`tn.jakloc.filter.AuthenticationFilter` is mapped to `/*`.

Public paths:

```
/
/index.jsp
/login
/register
/favicon.ico
/favicon.svg
/assets/*
```

Behavior:

1. Anonymous request to a non-public path -> redirect to
   `/login?authRequired=1`.
2. Authenticated request to `/`, `/index.jsp`, `/login` or `/register` ->
   redirect to the dashboard for the role.
3. `/admin/*` requires `ADMIN`; otherwise HTTP 403.
4. `/client/*` requires `CLIENT`; otherwise HTTP 403.
5. Authenticated responses receive `Cache-Control: no-store`.

How Moomen should add routes:

- Client-facing vehicle pages: `/client/vehicles`, `/client/vehicles/*`.
- Admin vehicle/category pages: `/admin/vehicles`, `/admin/categories`.
- These are protected automatically; no new filter is needed.
- If a public catalogue page is required, propose the exact path and add it to
  `PUBLIC_PATHS` with a comment explaining why.
- Do not create a second authentication filter.

## 8. Naming conventions for the vehicle module

Use the same layers as authentication:

| Layer | Authentication example | Vehicle module example |
| --- | --- | --- |
| Model | `tn.jakloc.model.User` | `tn.jakloc.model.Vehicle` |
| DAO interface | `UserRepository` | `VehicleRepository` |
| DAO implementation | `InMemoryUserRepository` | `InMemoryVehicleRepository` |
| Service | `AuthService` | `VehicleService` |
| Servlet | `servlet/auth/LoginServlet` | `servlet/client/VehicleListServlet`, `servlet/admin/VehicleAdminServlet` |
| View | `WEB-INF/views/auth/login.jsp` | `WEB-INF/views/client/vehicles/list.jsp`, `WEB-INF/views/admin/vehicles/list.jsp` |
| CSS | `assets/css/auth.css` | `assets/css/vehicles.css` |
| JS | `assets/js/auth.js` | `assets/js/vehicles.js` |

Additional rules:

- Use `@WebServlet` for new servlets.
- Use `@WebFilter` only if a new concern is genuinely cross-cutting; coordinate
  first.
- Keep vehicle business rules in `VehicleService`, not in JSPs or servlets.
- Keep database access behind `VehicleRepository`.
- Do not duplicate the startup listener. Add the vehicle repository/service to
  `AppContextListener` when the JDBC/in-memory wiring is needed.
- Do not create a second database configuration. When MySQL is introduced,
  there must be exactly one shared configuration used by all repositories.

## 9. View and CSS contract

Shared CSS variables are defined in `assets/css/base.css`. Use them instead of
hard-coded colors:

```
--surface, --surface-container, --surface-container-high
--on-surface, --on-surface-variant, --muted
--primary, --primary-strong, --primary-ink
--charcoal, --charcoal-deep
--error, --error-bg, --success, --success-bg
--radius, --radius-md, --radius-lg, --radius-pill
--shadow-card, --shadow-float
--font
```

Reusable classes:

- Buttons: `.btn`, `.btn-primary`, `.btn-secondary`, `.btn-ghost`, `.btn-sm`,
  `.btn-block`.
- Forms: `.field`, `.input`, `.field-error`, `.field-hint`.
- Alerts: `.alert`, `.alert-error`, `.alert-success`, `.alert-info`.
- Brand: `.brand`, `.brand-mark`, `.brand-name`.
- Dashboard shell: `.app-page`, `.topbar`, `.dashboard`, `.info-card`,
  `.welcome-card`, `.status-pill`, `.user-chip`, `.avatar`.

JSP rules:

- Start pages with UTF-8 page directives and the JSTL taglibs.
- Use `<c:set var="ctx" value="${pageContext.request.contextPath}" />` and
  prefix every URL with `${ctx}`.
- Use `<c:out>` for text and `fn:escapeXml` for attribute values.
- Do not put Java scriptlets in JSPs.
- Do not add inline `<style>` or `<script>` blocks; use module CSS/JS files.
- Keep private views under `WEB-INF/views/<area>/`.

## 10. Integration rules

1. One application, one `User` model, one `Role` enum, one auth service.
2. Do not modify another developer's module without coordination.
3. Do not rename shared routes, session attributes or CSS variables without
   updating this document and both modules.
4. New private routes inherit the filter automatically. Verify the role prefix.
5. Use the same error pages for 403/404/500.
6. Keep authentication JSPs and vehicle JSPs in their own folders.
7. Commit only your module's files plus intentional shared changes.
8. Update this contract before merging a shared change.

## 11. Change log

| Version | Date | Change |
| --- | --- | --- |
| 1.0 | Initial milestone | Shared User model, repository contract, AuthService, session contract, routes, filter rules, UI conventions |
