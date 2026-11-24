# JakLoc API Contract

Version: 1.2
Scope: shared foundation, authentication and client vehicle catalogue
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

### 1.3 `tn.jakloc.model.Vehicle`

| Field | Type | Notes |
| --- | --- | --- |
| `id` | `long` | Assigned by the repository; `0` means unsaved |
| `brand` | `String` | Manufacturer name |
| `model` | `String` | Model name |
| `category` | `VehicleCategory` | See section 1.4 |
| `transmission` | `Transmission` | `AUTOMATIC` or `MANUAL` |
| `fuelType` | `FuelType` | `PETROL`, `DIESEL`, `HYBRID`, `ELECTRIC` |
| `year` | `int` | Model year |
| `seats` | `int` | Passenger capacity |
| `doors` | `int` | Door count |
| `dailyRate` | `java.math.BigDecimal` | Daily rental price; never use `double` for money |
| `mileage` | `int` | Odometer reading in kilometres |
| `location` | `String` | Pickup location label |
| `imageUrl` | `String` | Optional image URL; the views fall back to a local placeholder |
| `description` | `String` | Short catalogue description |
| `status` | `VehicleStatus` | `AVAILABLE`, `RENTED` or `MAINTENANCE` |
| `createdAt` | `java.time.LocalDateTime` | Record creation timestamp |

Rules:

- This is the single canonical vehicle model for both client and admin modules.
- Use the `isAvailable()` helper for availability checks in Java and EL.
- Display money through `<fmt:formatNumber>` using the `TND` currency symbol.
- The current seeder is demo data, not production data. It exists because no
  database exists yet and must be removed when a JDBC repository is added.

### 1.4 Vehicle enums

```java
public enum VehicleCategory {
    SUV, SEDAN, HATCHBACK, LUXURY, VAN, PICKUP, SPORTS, ELECTRIC
}

public enum Transmission {
    AUTOMATIC, MANUAL
}

public enum FuelType {
    PETROL, DIESEL, HYBRID, ELECTRIC
}

public enum VehicleStatus {
    AVAILABLE, RENTED, MAINTENANCE
}
```

Enums expose user-facing `label` values and CSS helpers where needed. Filter
query parameters use the enum constant names (`SUV`, `AUTOMATIC`, `ELECTRIC`).

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

### 2.2 `tn.jakloc.dao.interfaces.VehicleRepository`

```java
List<Vehicle> findAll();
Optional<Vehicle> findById(long id);
List<Vehicle> findByStatus(VehicleStatus status);
Vehicle save(Vehicle vehicle);
```

Agreements:

- `findAll` returns vehicles ordered by id.
- `save` assigns an id when `vehicle.getId() == 0`.
- The current implementation is `tn.jakloc.dao.InMemoryVehicleRepository`.
- A future `JdbcVehicleRepository` must implement the same interface and reuse
  the single shared database configuration.
- Servlets and JSPs must use `VehicleService`, not the repository directly.

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

### 3.3 `tn.jakloc.service.VehicleService`

```java
public VehicleService(VehicleRepository vehicleRepository)

public List<Vehicle> search(VehicleFilter filter)
public List<Vehicle> getFeaturedVehicles(int limit)
public long getAvailableCount()
public long getAvailableCategoryCount()
public BigDecimal getLowestAvailableRate()
```

`VehicleFilter` fields:

```java
String query;
VehicleCategory category;
Transmission transmission;
FuelType fuelType;
BigDecimal maxPrice;
VehicleSort sort = VehicleSort.RECOMMENDED;
```

Agreements:

- `query` matches brand, model, location and category label, case-insensitively.
- Category, transmission, fuel and maximum daily rate filters are inclusive.
- `VehicleSort` values are `RECOMMENDED`, `PRICE_ASC`, `PRICE_DESC`, `NAME_ASC`,
  `SEATS_DESC` and `NEWEST`; an unknown value falls back to `RECOMMENDED`.
- `RECOMMENDED` puts available vehicles first, then newest model year, then the
  lowest daily rate.
- Featured vehicles are available only and respect the requested limit.

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
| `vehicleService` | `AppConstants.VEHICLE_SERVICE` | `VehicleService` |
| `vehicleRepository` | `AppConstants.VEHICLE_REPOSITORY` | `VehicleRepository` |

These are created once in `AppContextListener`. Do not create additional
application-wide instances. `VehicleSeeder` populates the in-memory vehicle
repository only when it is empty.

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
| GET | `/client/vehicles` | CLIENT | `VehicleCatalogServlet` | `client/vehicles/list.jsp` |
| GET | `/client/bookings` | CLIENT | `MyBookingsServlet` | `client/bookings/list.jsp` (placeholder) |
| GET | `/client/favorites` | CLIENT | `FavoritesServlet` | `client/favorites/list.jsp` (placeholder) |
| GET | `/client/profile` | CLIENT | `ClientProfileServlet` | `client/profile.jsp` (read-only) |
| GET | `/admin/dashboard` | ADMIN | `AdminDashboardServlet` | `admin/dashboard.jsp` |

Views live under `WEB-INF/views/` and are reached only by forward, never by a
direct browser URL.

## 7. Authentication filter contract

`tn.jakloc.filter.AuthenticationFilter` is mapped to `/*`.

`tn.jakloc.filter.CharacterEncodingFilter` is also mapped to `/*` and only sets
UTF-8 request/response encoding. It performs no access control. `web.xml` no
longer declares the Servlet 6.0 encoding elements because the Eclipse WTP
descriptor model used by the project rejects them; the filter provides the same
behaviour at runtime.

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
| CSS | `assets/css/auth.css` | `assets/css/client.css` for client pages |
| JS | `assets/js/auth.js` | `assets/js/client.js` for catalogue behaviour |

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
- Client layout: `client.css`, `.client-page`, `.client-header`,
  `.client-nav`, `.client-nav-link`, `.client-account`, `.client-footer`,
  `.client-page-head`, `.placeholder-card`, `.account-list`.
- Client catalogue: `.catalog-hero`, `.catalog-toolbar`, `.search-capsule`,
  `.filter-grid`, `.active-filters`, `.filter-chip`, `.vehicle-grid`,
  `.vehicle-card`, `.vehicle-card__media`, `.vehicle-status`, `.vehicle-specs`,
  `.empty-state`.
- Client dashboard: `.client-hero`, `.client-stats`, `.client-stat`,
  `.client-quick-grid`, `.quick-card`.

Client JSP layout fragments live in
`WEB-INF/views/client/includes/`:

- `head.jspf` - document head, fonts and stylesheet links.
- `header.jspf` - brand, client navigation and account actions.
- `nav.jspf` - the five client navigation links with the active state.
- `vehicle-card.jspf` - reusable vehicle card; expects a `vehicle` variable.
- `footer.jspf` - footer, shared `client.js` and closing document tags.

The catalogue form uses GET parameters `query`, `category`, `transmission`,
`fuelType`, `maxPrice` and `sort`. `client.js` submits the form when a select
changes and shows a loading label while the request is in flight. The form still
works without JavaScript through the Search button.

Each client page sets `ctx`, `pageTitle` and `activeNav` before including
`head.jspf` and `header.jspf`, then closes with `footer.jspf`. The client
navigation is rendered only for authenticated CLIENT sessions because every
`/client/*` route is protected by `AuthenticationFilter`.

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
| 1.1 | Client foundation | Client layout fragments, navigation, client CSS and protected placeholder routes for vehicles, bookings, favorites and profile |
| 1.2 | Client catalogue | Canonical `Vehicle` model, enums, `VehicleFilter`, in-memory vehicle repository and seeder, `VehicleService`, filtered/sorted catalogue, featured dashboard vehicles, `vehicle-card.jspf` and `client.js` |
