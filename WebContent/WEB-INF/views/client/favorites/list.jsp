<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Favorites" />
<c:set var="activeNav" value="favorites" />
<%@ include file="/WEB-INF/views/client/includes/head.jspf" %>
<%@ include file="/WEB-INF/views/client/includes/header.jspf" %>

<main class="dashboard">
    <section class="client-page-head">
        <p class="eyebrow">Saved cars</p>
        <h1>Favorites</h1>
        <p>
            Vehicles you save from the catalogue will be collected here for quick
            access.
        </p>
    </section>

    <section class="placeholder-card">
        <span class="status-pill">Coming next</span>
        <h2>No favorites yet</h2>
        <p>
            The favorites feature will be connected to the vehicle catalogue and
            the authenticated client session in a later step.
        </p>
    </section>
</main>

<%@ include file="/WEB-INF/views/client/includes/footer.jspf" %>
