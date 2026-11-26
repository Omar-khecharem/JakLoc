# JakLoc

JakLoc is a car rental and return management web application built as a
university project with Jakarta EE 10. The current milestone covers the shared
foundation, the **authentication module**, the **client dashboard and vehicle
catalogue**, and **MySQL persistence** through JDBC. Vehicle management,
reservations and payment modules will be added in later milestones.

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
- MySQL persistence with JDBC repositories (`JdbcUserRepository`,
  `JdbcVehicleRepository`) behind the existing repository interfaces.
- SQL schema and seed file (`database/jakloc.sql`) for the `users` and
  `vehicles` tables, including demo ADMIN and CLIENT accounts.
- Automatic in-memory fallback when MySQL is unreachable, so the app still
  starts during a demo.
- Client dashboard with fleet statistics and featured vehicles.
- Vehicle catalogue with brand/model search, category, gearbox, fuel and daily
  rate filters, plus price/name/seats/newest sorting.
- Canonical `Vehicle` model, `VehicleRepository`, `VehicleService` and demo
  vehicle data behind an interface.
- Responsive RentalX-inspired client catalogue using `client.css` and
  `client.js`, with an honest empty state and local placeholder artwork.

Not implemented yet (future tasks):

- Admin vehicle and category management.
- Vehicle details page, reservations, returns, payments and the full admin
  dashboard.
- Favorites and profile editing.

## Technology

| Layer | Choice |
| --- | --- |
| Language | Java 21 (compatible with JDK 17+) |
| Platform | Jakarta EE 10, Servlet 6.0 |
| Server | Apache TomEE 10 (also runs on Tomcat 10.1) |
| Database | MySQL 8.0+ (MariaDB 10.4+ also works) |
| Persistence | JDBC with MySQL Connector/J 8.3.0 |
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
│   ├── dao/                    Database, JdbcUserRepository, JdbcVehicleRepository,
│   │                           InMemoryUserRepository, InMemoryVehicleRepository,
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
│   ├── WEB-INF/lib/            jBCrypt + JSTL + MySQL Connector/J jars
│   ├── WEB-INF/views/auth/     login.jsp, register.jsp
│   ├── WEB-INF/views/client/   dashboard.jsp, vehicles/list.jsp, includes/
│   ├── WEB-INF/views/admin/    dashboard.jsp
│   ├── WEB-INF/views/errors/   403.jsp, 404.jsp, 500.jsp
│   ├── WEB-INF/web.xml
│   └── index.jsp
├── database/jakloc.sql         MySQL schema + seed (users, admin, client, vehicles)
├── lib/                        servlet-api.jar (compile only, not deployed)
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
- MySQL 8.0+ (or MariaDB 10.4+) running locally. The demo defaults assume
  `localhost:3306`, user `root` and an empty password (typical XAMPP setup).
  Change the credentials in `WEB-INF/web.xml` or with environment variables if
  your server differs.

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
4. Open the URL shown for the module. It is usually
   `http://localhost:8080/JakLoc/` but Eclipse sometimes uses the project name,
   so it can be `http://localhost:8080/JakLoc-Jakrata/`.

The context root is configured as `JakLoc` in
`.settings/org.eclipse.wst.common.component`. To see the exact value Eclipse
deployed, open the Servers view, double-click the server, open the `Modules`
tab and read the `Path` column. If it does not match, edit the module path
there or set the context root under project properties >
`Web Project Settings`. A wrong context root is the most common cause of a
Tomcat 404 because the app is reachable under a different prefix.

## Database setup (MySQL)

JakLoc stores users and vehicles in MySQL. The schema and seed data live in
`database/jakloc.sql`.

### 1. Start MySQL

Start your MySQL service (for example the `MySQL80` service, or XAMPP's MySQL
module). The default connection expects `localhost:3306` with user `root` and an
empty password.

### 2. Create the schema and seed data

Run the script once. It creates the `jakloc` database, the `users` and
`vehicles` tables, and inserts the demo accounts and fleet. The script is
idempotent, so it is safe to run again.

- MySQL Workbench / phpMyAdmin: open `database/jakloc.sql` and execute it.
- Command line (cmd or Git Bash):

  ```bash
  mysql -u root -p < database/jakloc.sql
  ```

- PowerShell (no `<` redirection):

  ```powershell
  Get-Content database\jakloc.sql | mysql -u root -p
  ```

- XAMPP:

  ```powershell
  Get-Content database\jakloc.sql | C:\xampp\mysql\bin\mysql.exe -u root
  ```

### 3. Configure the connection

The connection is configured in `WebContent/WEB-INF/web.xml` with three context
parameters. Edit them if your MySQL user or password is different:

```xml
<context-param>
    <param-name>jakloc.db.url</param-name>
    <param-value>jdbc:mysql://localhost:3306/jakloc?...</param-value>
</context-param>
<context-param>
    <param-name>jakloc.db.user</param-name>
    <param-value>root</param-value>
</context-param>
<context-param>
    <param-name>jakloc.db.password</param-name>
    <param-value></param-value>
</context-param>
```

You can also override them without editing files by setting the environment
variables `JAKLOC_DB_URL`, `JAKLOC_DB_USER` and `JAKLOC_DB_PASSWORD` (or the
system properties `jakloc.db.url`, `jakloc.db.user`, `jakloc.db.password`). The
resolution order is: `web.xml` value, environment variable, system property,
then the built-in default.

### 4. Redeploy and verify

Restart/redeploy the web application. In the server log you should see:

```
JakLoc started: MySQL persistence is active.
```

If MySQL is down or the schema is missing, the app logs the reason and falls
back to in-memory storage for that run (it still starts). In that mode data is
lost on restart and only the two demo accounts are recreated.

### Demo accounts and how to access each area

| Area | Entry URL | Email | Password |
| --- | --- | --- | --- |
| Admin | `/admin/dashboard` | `admin@jakloc.tn` | `Admin@123` |
| Client | `/client/dashboard` | `client@jakloc.tn` | `Client@` |

How to sign in:

1. Open the application root (for example `http://localhost:8080/JakLoc/` or
   `http://localhost:8080/JakLoc-Jakrata/`). It redirects to `/login`.
2. Log in as the admin to reach `/admin/dashboard`.
3. Log in as the client to reach `/client/dashboard` and the vehicle catalogue
   at `/client/vehicles`.
4. New registrations always create a `CLIENT` account and land on the client
   area after login.
5. Route protection is enforced: an ADMIN opening `/client/*` gets 403, and a
   CLIENT opening `/admin/*` gets 403.

Both demo accounts are in `database/jakloc.sql` and are also recreated at
startup when missing, so the admin and client areas are always reachable.
Passwords are stored only as BCrypt hashes. Change or remove these demo accounts
before any real deployment.

### Demo vehicle fleet

The SQL seed inserts eight demo vehicles (six available, one rented, one in
maintenance). If the `vehicles` table is empty at startup, `VehicleSeeder`
inserts the same fleet automatically. Prices are displayed in Tunisian dinars
(TND).

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
| `mysql-connector-j-8.3.0.jar` | MySQL JDBC driver used by the repositories |

If you re-import the project on another machine, keep these jars. Do not add the
Servlet API to `WEB-INF/lib`; TomEE/Tomcat provides it.

The project also ships `lib/servlet-api.jar` (Tomcat 10.1). It is a **compile
only** dependency: it lets Eclipse and VS Code's Java extension resolve
`jakarta.servlet.*` while building. It is referenced by `.classpath` but is not
part of the deployment, so it is never copied into `WEB-INF/lib` or the WAR.

If TomEE already provides JSTL and you see a duplicate tag library warning, the
bundled JSTL jars still keep the project portable to plain Tomcat 10.1.

## How authentication works (short version)

1. `POST /register` validates the form, checks the email, hashes the password
   with BCrypt and saves a `CLIENT` user through `AuthService`.
2. `POST /login` validates the credentials through `AuthService`.
3. On success, `SessionUtil.login()` invalidates the old session and creates a
   new one, then stores `authenticatedUser` and `userRol123e`.
4. The user is redirected to `/client/dashboard` or `/admin/dashboard` based on
   the role.
5. `AuthenticationFilter` protects every non-public path, checks roles for
   `/client/*` and `/admin/*`, and prevents authenticated users from returning
   to login/register.
6. `POST /logout` invalidates the session and redirects to the login page.

Accounts and vehicles are stored in MySQL through `JdbcUserRepository` and
`JdbcVehicleRepository`. `AppContextListener` configures the connection from
`web.xml`, verifies both tables, and falls back to the in-memory repositories
with a clear log message when MySQL is unreachable.

See `docs/ARCHITECTURE.md` for the full request flow.

## Manual test checklist

Use this after deploying:

- [ ] `GET /login` and `GET /register` render correctly.
- [ ] Register with valid data creates a CLIENT account.
- [ ] Registering the same email again shows "An account with this email already exists."
- [ ] Invalid fields show field-level messages and preserve the entered values.
- [ ] Login with a valid CLIENT redirects to `/client/dashboard`.
- [ ] Login with `admin@jakloc.tn` / `Admin@123` redirects to `/admin/dashboard`.
- [ ] Login with `client@jakloc.tn` / `Client@123` redirects to `/client/dashboard`.
- [ ] The server log says "MySQL persistence is active." when MySQL is running.
- [ ] A newly registered account survives a server restart (data is in MySQL).
- [ ] Stopping MySQL and redeploying still starts the app in in-memory fallback mode.
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
| 404 on `http://localhost:8080/JakLoc/` | Check the context root and that the project is added to the server. Eclipse may deploy it as `/JakLoc-Jakrata`; read the module `Path` in the server editor. |
| Every route shows the app's 404 page | The deployed classes were compiled without the Servlet API, so servlets never load. This happens when VS Code's Java extension rebuilds the project and cannot resolve Eclipse's Tomcat runtime. The project now has `lib/servlet-api.jar` on the build path for that reason. Do `Project > Clean`, republish and restart; in VS Code run `Java: Clean Java Language Server Workspace` / reload the window. |
| Classes contain "Unresolved compilation problems: The import jakarta cannot be resolved" | Same as above: clean and rebuild with `lib/servlet-api.jar` (or a valid Tomcat 10.1 runtime) on the build path. |
| JSP tag error about `jakarta.tags.core` | Confirm both JSTL jars are in `WEB-INF/lib` and the server is Tomcat/TomEE 10. |
| `ClassNotFoundException: org.mindrot.jbcrypt.BCrypt` | Confirm `jbcrypt-0.4.jar` is in `WEB-INF/lib` and redeploy. |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Confirm `mysql-connector-j-8.3.0.jar` is in `WEB-INF/lib` and redeploy. |
| `Communications link failure` / `Connection refused` | MySQL is not running on `localhost:3306`. Start the service or fix `jakloc.db.url`; the app falls back to in-memory meanwhile. |
| `Access denied for user 'root'@'localhost'` | Set the real MySQL user/password in `jakloc.db.user` / `jakloc.db.password` (or the `JAKLOC_DB_*` environment variables). |
| `Unknown database 'jakloc'` or `Table 'jakloc.users' doesn't exist` | Run `database/jakloc.sql` once, then restart the application. |
| `javax.servlet` errors | The code uses `jakarta.*`; use a Jakarta EE 10 server, not Tomcat 9. |
| Login always fails | Check the server log for the storage mode. If in-memory, data is lost on restart; if MySQL, confirm the seed ran and the credentials match. |
| Eclipse shows a missing runtime | Add TomEE 10 (or Tomcat 10.1) under Server Runtime Environments. |

## Documentation

- `docs/ARCHITECTURE.md` - layers, request flows, security decisions.
- `docs/API_CONTRACT.md` - shared contracts for Omar's and Moomen's modules.
- `CONTRIBUTING.md` - branches, commits, conventions and integration rules.
- `DESIGN.md` - visual reference used for the authentication pages.

> Note: no `.mg` design file was present in the workspace when this milestone
> was implemented. `DESIGN.md` ("Atelier Grand Tour") was used as the visual
> reference.
