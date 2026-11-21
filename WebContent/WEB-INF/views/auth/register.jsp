<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Create account &middot; JakLoc</title>
    <link rel="icon" type="image/svg+xml" href="${ctx}/assets/images/favicon.svg">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/auth.css">
</head>
<body class="auth-page">
<main class="auth-shell">

    <section class="brand-panel" aria-label="About JakLoc">
        <a class="brand" href="${ctx}/login">
            <span class="brand-mark" aria-hidden="true">J</span>
            <span class="brand-name">JakLoc</span>
        </a>

        <div class="brand-copy">
            <p class="eyebrow">Join the concourse</p>
            <h1>Your next drive <span class="accent">starts here.</span></h1>
            <p class="brand-lede">
                Create your JakLoc account once, then reserve vehicles, follow your
                rental days and keep every return in one place.
            </p>
            <ul class="brand-points">
                <li>One account for every reservation</li>
                <li>Clear rental and return tracking</li>
                <li>Designed for desktop and mobile</li>
            </ul>
        </div>

        <p class="brand-foot">
            <span>JakLoc &middot; Tunis</span>
            <span>Rental management</span>
        </p>
    </section>

    <section class="auth-panel">
        <div class="auth-card">
            <header>
                <p class="eyebrow">New client account</p>
                <h2>Create your account</h2>
                <p>It takes less than a minute. All fields are required.</p>
            </header>

            <div class="auth-alerts" aria-live="polite">
                <c:if test="${not empty errors}">
                    <div class="alert alert-error" role="alert">
                        <p>Please fix the following:</p>
                        <ul>
                            <c:forEach var="error" items="${errors}">
                                <li><c:out value="${error.value}" /></li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
            </div>

            <form class="auth-form" action="${ctx}/register" method="post" data-register-form novalidate>
                <div class="field">
                    <label for="fullName">Full name</label>
                    <input class="input"
                           type="text"
                           id="fullName"
                           name="fullName"
                           value="${fn:escapeXml(fullName)}"
                           placeholder="Omar Khecherem"
                           autocomplete="name"
                           maxlength="80"
                           required
                           autofocus
                           aria-invalid="${not empty errors.fullName ? 'true' : 'false'}"
                           aria-describedby="${not empty errors.fullName ? 'fullName-error' : ''}">
                    <c:if test="${not empty errors.fullName}">
                        <p class="field-error" id="fullName-error"><c:out value="${errors.fullName}" /></p>
                    </c:if>
                </div>

                <div class="form-grid">
                    <div class="field field-full">
                        <label for="email">Email address</label>
                        <input class="input"
                               type="email"
                               id="email"
                               name="email"
                               value="${fn:escapeXml(email)}"
                               placeholder="you@example.com"
                               autocomplete="email"
                               maxlength="254"
                               required
                               aria-invalid="${not empty errors.email ? 'true' : 'false'}"
                               aria-describedby="${not empty errors.email ? 'email-error' : ''}">
                        <c:if test="${not empty errors.email}">
                            <p class="field-error" id="email-error"><c:out value="${errors.email}" /></p>
                        </c:if>
                    </div>

                    <div class="field field-full">
                        <label for="phone">Phone number</label>
                        <input class="input"
                               type="tel"
                               id="phone"
                               name="phone"
                               value="${fn:escapeXml(phone)}"
                               placeholder="+216 20 123 456"
                               autocomplete="tel"
                               maxlength="20"
                               required
                               aria-invalid="${not empty errors.phone ? 'true' : 'false'}"
                               aria-describedby="${not empty errors.phone ? 'phone-error' : ''}">
                        <c:if test="${not empty errors.phone}">
                            <p class="field-error" id="phone-error"><c:out value="${errors.phone}" /></p>
                        </c:if>
                    </div>

                    <div class="field">
                        <label for="password">Password</label>
                        <div class="password-wrap">
                            <input class="input"
                                   type="password"
                                   id="password"
                                   name="password"
                                   placeholder="At least 8 characters"
                                   autocomplete="new-password"
                                   maxlength="64"
                                   required
                                   aria-invalid="${not empty errors.password ? 'true' : 'false'}"
                                   aria-describedby="password-hint${not empty errors.password ? ' password-error' : ''}">
                            <button class="password-toggle"
                                    type="button"
                                    data-toggle-password="password"
                                    aria-label="Show password"
                                    aria-pressed="false">
                                <svg class="icon-eye" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                     stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8S1 12 1 12z" />
                                    <circle cx="12" cy="12" r="3" />
                                </svg>
                                <svg class="icon-eye-off" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                     stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94" />
                                    <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19" />
                                    <path d="M14.12 14.12a3 3 0 1 1-4.24-4.24" />
                                    <line x1="1" y1="1" x2="23" y2="23" />
                                </svg>
                            </button>
                        </div>
                        <p class="field-hint" id="password-hint">8 to 64 characters, with a letter and a number.</p>
                        <c:if test="${not empty errors.password}">
                            <p class="field-error" id="password-error"><c:out value="${errors.password}" /></p>
                        </c:if>
                    </div>

                    <div class="field">
                        <label for="confirmPassword">Confirm password</label>
                        <div class="password-wrap">
                            <input class="input"
                                   type="password"
                                   id="confirmPassword"
                                   name="confirmPassword"
                                   placeholder="Repeat your password"
                                   autocomplete="new-password"
                                   maxlength="64"
                                   required
                                   aria-invalid="${not empty errors.confirmPassword ? 'true' : 'false'}"
                                   aria-describedby="password-match${not empty errors.confirmPassword ? ' confirmPassword-error' : ''}">
                            <button class="password-toggle"
                                    type="button"
                                    data-toggle-password="confirmPassword"
                                    aria-label="Show password"
                                    aria-pressed="false">
                                <svg class="icon-eye" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                     stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                    <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8S1 12 1 12z" />
                                    <circle cx="12" cy="12" r="3" />
                                </svg>
                                <svg class="icon-eye-off" viewBox="0 0 24 24" fill="none" stroke="currentColor"
                                     stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                                    <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94" />
                                    <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19" />
                                    <path d="M14.12 14.12a3 3 0 1 1-4.24-4.24" />
                                    <line x1="1" y1="1" x2="23" y2="23" />
                                </svg>
                            </button>
                        </div>
                        <p class="field-hint" id="password-match" data-password-match aria-live="polite"></p>
                        <c:if test="${not empty errors.confirmPassword}">
                            <p class="field-error" id="confirmPassword-error"><c:out value="${errors.confirmPassword}" /></p>
                        </c:if>
                    </div>
                </div>

                <button class="btn btn-primary btn-block auth-submit" type="submit">Create account</button>
            </form>

            <p class="auth-switch">
                Already have an account?
                <a href="${ctx}/login">Sign in</a>
            </p>
        </div>
    </section>

</main>
<script src="${ctx}/assets/js/auth.js" defer></script>
</body>
</html>
