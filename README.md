# JakLoc

JakLoc is a car rental and return management web application built as a
university project with Jakarta EE 10. The current milestone covers the shared
foundation, the **authentication module** and the **client dashboard and vehicle
catalogue**. Vehicle management, reservations and payment modules will be added
in later milestones.

**Students:** Omar Khecherem and Moomen Msaadi

## Current scope

Implemented in this milestone:

- Shared `User` model with `ADMIN` and `CLIENT` roles.
- Clean MVC foundation with model, DAO, service, servlet, filter and utility
  packages.
- Registration with server-side validation and duplicate email rejection.
- BCrypt password hashing (never plain text).
- Login with session regeneration (session fixation protection).
- Role-based redirects and route protection for `/client/*` and `/admin/*`.
- Logout with session invalidation.
- Login and registration pages based on `DESIGN.md`.
- Reusable CSS, password visibility toggle, responsive layouts.
- In-memory user repository behind a `UserRepository` interface, ready to be
  replaced by a JDBC implementation later.
- Client dashboard with fleet statistics and featured vehicles.
- Vehicle catalogue with brand/model search, category, gearbox, fuel and daily
  rate filters, plus price/name/seats/newest sorting.
- Canonical `Vehicle` model, `VehicleRepository`, `VehicleService` and in-memory
  demo vehicle data behind an interface.
- Responsive RentalX-inspired client catalogue using `client.css` and
  `client.js`, with an honest empty state and local placeholder artwork.

Not implemented yet (future tasks):

- Admin vehicle and category management.
- Vehicle details page, reservations, returns, payments and the full admin
  dashboard.
- Favorites and profile editing.
- MySQL/JDBC persistence and database configuration.

## Technology

| Layer | Choice |
| --- | --- |
| Language | Java 21 (compatible with JDK 17+) |
| Platform | Jakarta EE 10, Servlet 6.0 |
| Server | Apache TomEE 10 (also runs on Tomcat 10.1) |
| Views | JSP, JSTL 3.0, EL |
| Architecture | MVC with DAO and service layers |
| Password hashing | jBCrypt 0.4 |
| IDE | Eclipse IDE for Enterprise Java Developers |

No Spring, no Node.js, no Maven is required. The project is a classic Eclipse
Dynamic Web Project with its libraries bundled in `WEB-INF/lib`.

## Project structure

```
JakLoc-Jakrata/
├── src/main/java/tn/jakloc/
│   ├── model/                  User, Role, Vehicle, vehicle enums, VehicleFilter
│   ├── dao/                    InMemoryUserRepository, InMemoryVehicleRepository,
│   │                           VehicleSeeder
│   ├── dao/interfaces/         UserRepository, VehicleRepository
│   ├── service/                AuthService, VehicleService
│   ├── servlet/auth/           Login, Register, Logout
│   ├── servlet/client/         Client dashboard, vehicle catalogue, bookings,
│   │                           favorites, profile
│   ├── servlet/admin/          Admin dashboard placeholder
│   ├── filter/                 AuthenticationFilter
│   ├── util/                   AppConstants, PasswordUtil, SessionUtil,
│   │                           ValidationUtil, AppContextListener
│   └── exception/              DuplicateEmailException
├── WebContent/
│   ├── assets/css/             base.css, auth.css, dashboard.css, client.css
│   ├── assets/js/              auth.js, client.js
│   ├── assets/images/          favicon.svg, vehicles/vehicle-placeholder.svg
│   ├── WEB-INF/lib/            jBCrypt + JSTL jars
│   ├── WEB-INF/views/auth/     login.jsp, register.jsp
│   ├── WEB-INF/views/client/   dashboard.jsp, vehicles/list.jsp, includes/
│   ├── WEB-INF/views/admin/    dashboard.jsp
│   ├── WEB-INF/views/errors/   403.jsp, 404.jsp, 500.jsp
│   ├── WEB-INF/web.xml
│   └── index.jsp
├── docs/
│   ├── ARCHITECTURE.md
│   └── API_CONTRACT.md
├── DESIGN.md                   Visual design reference
├── CONTRIBUTING.md
└── README.md
```

## Prerequisites

- JDK 17 or newer (the project is configured for Java 21).
- Eclipse IDE for Enterprise Java Developers.
- Apache TomEE 10 (recommended) **or** Apache Tomcat 10.1.
- No database is needed for this milestone.

## Import the project in Eclipse

1. Open Eclipse.
2. `File > Import... > General > Existing Projects into Workspace`.
3. Select the `JakLoc-Jakrata` folder (the folder that contains `.project`).
4. Finish the import.
5. If Eclipse reports a missing runtime, configure one:
   - `Window > Preferences > Server > Runtime Environments > Add...`
   - Choose **Apache TomEE 10** (or Apache Tomcat v10.1).
   - Point it to your server installation directory.
6. Right-click the project:
   - `Properties > Targeted Runtimes` and tick your TomEE 10 runtime.
   - Apply and close.
7. Right-click the project again: `Maven` is not used, so do not convert it.
   The project uses the Eclipse Web App Libraries container.

### Run on Apache TomEE 10

1. In the Servers view, create a new server:
   `New > Server > Apache > TomEE 10` (or Tomcat v10.1).
2. Add `JakLoc-Jakrata` to the server.
3. Start the server.
4. Open `http://localhost:8080/JakLoc/`.

The context root is configured as `JakLoc` in
`.settings/org.eclipse.wst.common.component`. If your server shows a different
context root, open the project properties under `Web Project Settings` and set
it to `JakLoc`.

### Demo admin account

The application seeds one ADMIN account at startup:

```
Email:    admin@jakloc.tn
Password: Admin@123
```

The password is stored only as a BCrypt hash in memory. Change or remove this
demo account before any real deployment.

### Demo vehicle fleet

Because MySQL/JDBC is not configured yet, `VehicleSeeder` adds eight demo
vehicles to the in-memory repository at startup. Six are available, one is rented
and one is in maintenance. Prices are displayed in Tunisian dinars (TND). This
seed data is temporary and must be removed when the JDBC repository is added.

### Registration

New accounts are always created with the `CLIENT` role. After registration the
user is redirected to the login page with a success message.

## Bundled libraries

The following jars are already in `WebContent/WEB-INF/lib`:

| Jar | Purpose |
| --- | --- |
| `jbcrypt-0.4.jar` | BCrypt password hashing |
| `jakarta.servlet.jsp.jstl-api-3.0.0.jar` | JSTL 3.0 API |
| `jakarta.servlet.jsp.jstl-3.0.1.jar` | JSTL 3.0 implementation (GlassFish) |

If you re-import the project on another machine, keep these jars. Do not add the
Servlet API to `WEB-INF/lib`; TomEE/Tomcat provides it.

If TomEE already provides JSTL and you see a duplicate tag library warning, the
bundled JSTL jars still keep the project portable to plain Tomcat 10.1.

## How authentication works (short version)

1. `POST /register` validates the form, checks the email, hashes the password
   with BCrypt and saves a `CLIENT` user through `AuthService`.
2. `POST /login` validates the credentials through `AuthService`.
3. On success, `SessionUtil.login()` invalidates the old session and creates a
   new one, then stores `authenticatedUser` and `userRole`.
4. The user is redirected to `/client/dashboard` or `/admin/dashboard` based on
   the role.
5. `AuthenticationFilter` protects every non-public path, checks roles for
   `/client/*` and `/admin/*`, and prevents authenticated users from returning
   to login/register.
6. `POST /logout` invalidates the session and redirects to the login page.

See `docs/ARCHITECTURE.md` for the full request flow.

## Manual test checklist

Use this after deploying:

- [ ] `GET /login` and `GET /register` render correctly.
- [ ] Register with valid data creates a CLIENT account.
- [ ] Registering the same email again shows "An account with this email already exists."
- [ ] Invalid fields show field-level messages and preserve the entered values.
- [ ] Login with a valid CLIENT redirects to `/client/dashboard`.
- [ ] Login with `admin@jakloc.tn` redirects to `/admin/dashboard`.
- [ ] Login with a wrong password shows "Invalid email or password."
- [ ] A CLIENT opening `/admin/dashboard` receives the 403 page.
- [ ] An ADMIN opening `/client/dashboard` receives the 403 page.
- [ ] Logout invalidates the session; the Back button does not reveal the dashboard.
- [ ] Opening `/client/dashboard` while signed out redirects to login with a notice.
- [ ] Password visibility toggles work on both forms.
- [ ] Client dashboard shows available car count, categories, starting rate and
      three featured vehicles.
- [ ] Catalogue search matches brand or model and preserves the query.
- [ ] Category, gearbox, fuel and maximum-rate filters narrow the results.
- [ ] Sorting by price, name, seats and newest changes the displayed order.
- [ ] The Reset filters link and the empty state work.
- [ ] Unavailable vehicles show their status and no Reserve action.
- [ ] Layout is usable on desktop and mobile widths.

## Troubleshooting

| Problem | Fix |
| --- | --- |
| 404 on `http://localhost:8080/JakLoc/` | Check the context root and that the project is added to the server. |
| JSP tag error about `jakarta.tags.core` | Confirm both JSTL jars are in `WEB-INF/lib` and the server is Tomcat/TomEE 10. |
| `ClassNotFoundException: org.mindrot.jbcrypt.BCrypt` | Confirm `jbcrypt-0.4.jar` is in `WEB-INF/lib` and redeploy. |
| `javax.servlet` errors | The code uses `jakarta.*`; use a Jakarta EE 10 server, not Tomcat 9. |
| Login always fails | In-memory data is lost on restart; register again after redeploying. |
| Eclipse shows a missing runtime | Add TomEE 10 (or Tomcat 10.1) under Server Runtime Environments. |

## Documentation

- `docs/ARCHITECTURE.md` - layers, request flows, security decisions.
- `docs/API_CONTRACT.md` - shared contracts for Omar's and Moomen's modules.
- `CONTRIBUTING.md` - branches, commits, conventions and integration rules.
- `DESIGN.md` - visual reference used for the authentication pages.

> Note: no `.mg` design file was present in the workspace when this milestone
> was implemented. `DESIGN.md` ("Atelier Grand Tour") was used as the visual
> reference.
