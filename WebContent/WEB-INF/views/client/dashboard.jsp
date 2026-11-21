<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Client dashboard &middot; JakLoc</title>
    <link rel="icon" type="image/svg+xml" href="${ctx}/assets/images/favicon.svg">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
    <link rel="stylesheet" href="${ctx}/assets/css/dashboard.css">
</head>
<body class="app-page">
<header class="topbar">
    <a class="brand" href="${ctx}/client/dashboard">
        <span class="brand-mark" aria-hidden="true">J</span>
        <span class="brand-name">JakLoc</span>
    </a>

    <div class="topbar-actions">
        <div class="user-chip">
            <span class="avatar" aria-hidden="true"><c:out value="${fn:substring(sessionScope.authenticatedUser.fullName, 0, 1)}" /></span>
            <span class="user-meta">
                <strong><c:out value="${sessionScope.authenticatedUser.fullName}" /></strong>
                <small><c:out value="${sessionScope.authenticatedUser.email}" /></small>
            </span>
        </div>
        <form class="logout-form" action="${ctx}/logout" method="post">
            <button class="btn btn-ghost btn-sm" type="submit">Sign out</button>
        </form>
    </div>
</header>

<main class="dashboard">
    <section class="welcome-card">
        <p class="eyebrow">Client area</p>
        <h1>Welcome, <c:out value="${sessionScope.authenticatedUser.fullName}" /></h1>
        <p>
            Your JakLoc account is active. This dashboard will host the vehicle
            catalogue, reservations and rental returns as the project grows.
        </p>
        <span class="status-pill">
            Signed in as <c:out value="${sessionScope.userRole == 'ADMIN' ? 'Administrator' : 'Client'}" />
        </span>
    </section>

    <div class="cards">
        <section class="info-card">
            <p class="eyebrow">Coming next</p>
            <h2>Vehicle catalogue</h2>
            <p>
                Browse available cars, filter by category and open a vehicle
                details page. This area belongs to Moomen's module and will be
                connected to the same authenticated session.
            </p>
            <span class="tag">Vehicle module</span>
        </section>

        <section class="info-card">
            <p class="eyebrow">Authentication ready</p>
            <h2>Session &amp; route protection</h2>
            <p>
                This page is only reachable with a CLIENT session. Logging out
                invalidates the session, and private pages are not cached by the
                browser.
            </p>
            <span class="tag">Authentication module</span>
        </section>
    </div>
</main>
</body>
</html>
