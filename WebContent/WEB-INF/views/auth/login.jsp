<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sign in &middot; JakLoc</title>
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
            <p class="eyebrow">Premium car rental</p>
            <h1>Drive the <span class="accent">extraordinary.</span></h1>
            <p class="brand-lede">
                A calm, considered way to choose your next car. Curated vehicles,
                transparent rental days and a booking experience built around your time.
            </p>
            <ul class="brand-points">
                <li>Curated fleet, ready when you are</li>
                <li>Simple reservation and return flow</li>
                <li>Your account, your rental history</li>
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
                <p class="eyebrow">Client &amp; admin access</p>
                <h2>Welcome back</h2>
                <p>Sign in to manage your rentals and reservations.</p>
            </header>

            <div class="auth-alerts" aria-live="polite">
                <c:if test="${param.registered eq '1'}">
                    <div class="alert alert-success" role="status">
                        Your account has been created. Sign in to continue.
                    </div>
                </c:if>
                <c:if test="${param.logout eq '1'}">
                    <div class="alert alert-success" role="status">
                        You have been signed out.
                    </div>
                </c:if>
                <c:if test="${param.authRequired eq '1'}">
                    <div class="alert alert-info" role="status">
                        Please sign in to continue.
                    </div>
                </c:if>

                <c:if test="${not empty errors}">
                    <div class="alert alert-error" role="alert">
                        <p>Please check the form:</p>
                        <ul>
                            <c:forEach var="error" items="${errors}">
                                <li><c:out value="${error.value}" /></li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
            </div>

            <form class="auth-form" action="${ctx}/login" method="post" novalidate>
                <div class="field">
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
                           autofocus
                           aria-invalid="${not empty errors.email ? 'true' : 'false'}"
                           aria-describedby="${not empty errors.email ? 'email-error' : ''}">
                    <c:if test="${not empty errors.email}">
                        <p class="field-error" id="email-error"><c:out value="${errors.email}" /></p>
                    </c:if>
                </div>

                <div class="field">
                    <label for="password">Password</label>
                    <div class="password-wrap">
                        <input class="input"
                               type="password"
                               id="password"
                               name="password"
                               placeholder="Enter your password"
                               autocomplete="current-password"
                               maxlength="64"
                               required
                               aria-invalid="${not empty errors.password ? 'true' : 'false'}"
                               aria-describedby="${not empty errors.password ? 'password-error' : ''}">
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
                    <c:if test="${not empty errors.password}">
                        <p class="field-error" id="password-error"><c:out value="${errors.password}" /></p>
                    </c:if>
                </div>

                <button class="btn btn-primary btn-block auth-submit" type="submit">Sign in</button>
            </form>

            <p class="auth-switch">
                New to JakLoc?
                <a href="${ctx}/register">Create an account</a>
            </p>
        </div>
    </section>

</main>
<script src="${ctx}/assets/js/auth.js" defer></script>
</body>
</html>
