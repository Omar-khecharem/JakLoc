# Contributing to JakLoc

JakLoc is a two-person university project. Keep changes small, readable and
inside your module.

## Ownership

| Area | Owner |
| --- | --- |
| Authentication, login, registration, logout, sessions, auth filter, user service | Omar |
| Vehicle catalogue, vehicle management, categories, search and filtering | Moomen |
| Shared foundation (`model`, `util`, CSS tokens, docs) | Both, with coordination |

## Branches

Use short, descriptive branch names:

```
feature/auth-register
feature/auth-login
feature/vehicles-catalogue
fix/login-error-message
docs/api-contract
```

Keep `main` deployable. Merge only after the project compiles and the affected
flows have been tested.

## Commit messages

Use a short imperative summary and, when useful, a body:

```
Add duplicate email validation on registration

- Check email through AuthService
- Show the error on the email field
```

Avoid messages such as "update", "fix", "final" or "work".

## Code conventions

- Use `jakarta.*`, never `javax.*`.
- Java 17+ language features are allowed, but keep code understandable for
  FIGL2 students.
- Keep classes and methods small. Prefer clear names over clever code.
- No business logic in JSP files. JSPs only display data with EL/JSTL.
- Servlets handle HTTP only: read parameters, validate format, call a service,
  choose a view or redirect.
- Business rules belong in `tn.jakloc.service`.
- Persistence belongs in `tn.jakloc.dao` behind an interface in
  `tn.jakloc.dao.interfaces`.
- Never store or log plain-text passwords.
- Never send passwords through GET.
- Use `POST` for state-changing operations.
- Use `AppConstants` for session/context attribute names and shared routes.
- Use `SessionUtil` to read or write the authenticated user.
- Escape user data in JSPs with `<c:out>` or `fn:escapeXml`.
- Add comments only where they explain intent, not obvious code.

## Front-end conventions

- Shared tokens and components live in `assets/css/base.css`.
- Module-specific CSS goes in its own file (`auth.css`, future
  `vehicles.css`).
- Do not hard-code colors. Use the CSS variables from `base.css`.
- Reuse the existing classes: `.btn`, `.btn-primary`, `.input`, `.field`,
  `.alert`, `.auth-card`, `.info-card`, `.status-pill`.
- Keep JavaScript in `assets/js/`, not inline in JSPs.
- All URLs must include the context path (`${ctx}` in JSPs).
- Every form field needs a label and accessible error text.

## Integration rules

1. Never duplicate `User.java` or create a second `Role` enum.
2. Never create a second web application or a second database configuration.
3. Do not modify another developer's module without coordination.
4. Keep module-specific servlets, JSPs and CSS separated.
5. Reuse `AuthenticationFilter` instead of writing another auth filter.
6. New private routes under `/client/*` or `/admin/*` are protected
   automatically.
7. If a route must be public, add it to `AuthenticationFilter.PUBLIC_PATHS`
   deliberately and document why.
8. Any new shared contract change must be reflected in `docs/API_CONTRACT.md`.

## Pull request checklist

- [ ] The project compiles in Eclipse.
- [ ] The application deploys on TomEE 10 / Tomcat 10.1.
- [ ] Authentication flows still work after the change.
- [ ] No plain-text passwords, secrets or database credentials are committed.
- [ ] JSPs contain no Java business logic.
- [ ] CSS uses shared variables and does not break mobile layout.
- [ ] `docs/API_CONTRACT.md` is updated if a shared contract changed.
- [ ] Tested on a clean server restart (in-memory users are reset).
