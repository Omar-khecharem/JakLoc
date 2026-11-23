<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Admin dashboard &middot; JakLoc</title>
<link rel="icon" type="image/svg+xml" href="${ctx}/assets/images/favicon.svg">
<link rel="preconnect" href="https://fonts.googleapis.com">
<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
<link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
<link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@20..48,100..700,0..1,-50..200" rel="stylesheet">
<link rel="stylesheet" href="${ctx}/assets/css/base.css">
<link rel="stylesheet" href="${ctx}/assets/css/admin-console.css">
</head>
<body class="app-page admin-console">

<div class="ac-shell">

  <aside class="ac-sidebar">
    <a class="brand" href="${ctx}/admin/dashboard">
      <span class="brand-mark" aria-hidden="true">J</span>
      <span class="brand-name">JakLoc</span>
    </a>
    <nav class="ac-nav" aria-label="Administration">
      <a class="ac-nav-item is-active" href="${ctx}/admin/dashboard"><span class="material-symbols-outlined">dashboard</span>Dashboard</a>
      <span class="ac-nav-item is-disabled" title="Reserved for the vehicle module"><span class="material-symbols-outlined">car_rental</span>Vehicle Registry<em class="ac-soon">soon</em></span>
      <span class="ac-nav-item is-disabled" title="Reserved for the vehicle module"><span class="material-symbols-outlined">calendar_month</span>Reservations<em class="ac-soon">soon</em></span>
    </nav>
  </aside>

  <div class="ac-main">

    <header class="topbar">
      <h1 class="ac-title">Admin DashBoard - Requests &amp; Validation</h1>
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

    <section class="ac-stats" aria-label="Queue summary">
      <div class="ac-stat ac-stat-pending"><span class="ac-stat-num"><c:out value="${stats.pending}" /></span><span class="ac-stat-label">Pending Approvals</span></div>
      <div class="ac-stat ac-stat-validated"><span class="ac-stat-num"><c:out value="${stats.validated}" /></span><span class="ac-stat-label">Validated &amp; Active</span></div>
      <div class="ac-stat ac-stat-flagged"><span class="ac-stat-num"><c:out value="${stats.flagged}" /></span><span class="ac-stat-label">Flagged</span></div>
    </section>

    <div class="ac-filters" role="tablist" aria-label="Filter requests">
      <button type="button" class="ac-pill ${activeFilter eq 'all' ? 'is-active' : ''}" data-filter="all">All (<c:out value="${stats.total}" />)</button>
      <button type="button" class="ac-pill ${activeFilter eq 'pending' ? 'is-active' : ''}" data-filter="pending">Pending (<c:out value="${stats.pending}" />)</button>
      <button type="button" class="ac-pill ${activeFilter eq 'validated' ? 'is-active' : ''}" data-filter="validated">Validated (<c:out value="${stats.validated}" />)</button>
      <button type="button" class="ac-pill ${activeFilter eq 'flagged' ? 'is-active' : ''}" data-filter="flagged">Flagged (<c:out value="${stats.flagged}" />)</button>
    </div>

    <section class="ac-grid" aria-label="Rental requests">
      <c:choose>
        <c:when test="${empty requests}">
          <div class="ac-empty">
            <span class="material-symbols-outlined" aria-hidden="true">inbox</span>
            <h2>No pending approvals</h2>
            <p>Rental requests will appear here once the vehicle and reservation modules are connected.</p>
          </div>
        </c:when>
        <c:otherwise>
          <c:forEach var="row" items="${requests}">
            <article class="ac-card" data-bucket="${fn:escapeXml(row.statusState)}">
              <div class="ac-card-head">
                <span class="ac-avatar" aria-hidden="true"><c:out value="${row.initials}" /></span>
                <div class="ac-id">
                  <p class="ac-name"><c:out value="${row.name}" /></p>
                </div>
                <span class="ac-status" data-tone="${fn:escapeXml(row.statusState)}"><c:out value="${row.statusLabel}" /></span>
              </div>

              <hr class="ac-divider">

              <div class="ac-vehicle">
                <p class="ac-vehicle-name"><c:out value="${row.vehicle}" /></p>
                <p class="ac-vehicle-meta"><c:out value="${row.vehicleMeta}" /></p>
              </div>

              <div class="ac-terms">
                <span class="ac-term"><span class="material-symbols-outlined">calendar_month</span><c:out value="${row.dateRange}" /> (<c:out value="${row.days}" />)</span>
                <span class="ac-price"><c:out value="${row.price}" /> <small>total</small></span>
                <span class="ac-hold" data-tone="${fn:escapeXml(row.holdState)}"><span class="material-symbols-outlined">lock</span><c:out value="${row.holdLabel}" /></span>
              </div>

              <ul class="ac-docs">
                <c:forEach var="doc" items="${row.docs}">
                  <li class="ac-doc" data-tone="${fn:escapeXml(doc.state)}"><span class="ac-doc-label"><c:out value="${doc.label}" />:</span> <c:out value="${doc.value}" /></li>
                </c:forEach>
              </ul>

              <div class="ac-actions">
                <c:forEach var="act" items="${row.actions}">
                  <button type="button" class="btn" data-kind="${fn:escapeXml(act.kind)}"><span class="material-symbols-outlined"><c:out value="${act.icon}" /></span><c:out value="${act.label}" /></button>
                </c:forEach>
              </div>
            </article>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </section>

    <p class="ac-note">Sample data &mdash; wired to the vehicle and reservation modules when they land.</p>

  </div>
</div>

<script src="${ctx}/assets/js/admin-console.js" defer></script>
</body>
</html>