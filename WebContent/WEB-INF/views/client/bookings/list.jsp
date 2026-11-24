<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="My bookings" />
<c:set var="activeNav" value="bookings" />
<%@ include file="/WEB-INF/views/client/includes/head.jspf" %>
<%@ include file="/WEB-INF/views/client/includes/header.jspf" %>

<main class="dashboard">
    <section class="client-page-head">
        <p class="eyebrow">Reservations</p>
        <h1>My bookings</h1>
        <p>
            Your rental requests will be listed here once the booking flow is
            connected to the reservation service.
        </p>
    </section>

    <section class="placeholder-card">
        <span class="status-pill">Coming next</span>
        <h2>No bookings yet</h2>
        <p>
            The reservation service will populate this page. No placeholder or
            misleading bookings are created.
        </p>
    </section>
</main>

<%@ include file="/WEB-INF/views/client/includes/footer.jspf" %>
