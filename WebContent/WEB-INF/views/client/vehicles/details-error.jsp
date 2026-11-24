<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="${errorTitle}" />
<c:set var="activeNav" value="vehicles" />
<%@ include file="/WEB-INF/views/client/includes/head.jspf" %>
<%@ include file="/WEB-INF/views/client/includes/header.jspf" %>

<main class="dashboard vehicle-details-page">
    <section class="detail-error">
        <p class="eyebrow">Vehicle details</p>
        <span class="detail-error__code"><c:out value="${errorCode}" /></span>
        <h1><c:out value="${errorTitle}" /></h1>
        <p><c:out value="${errorMessage}" /></p>
        <div class="detail-error__actions">
            <a class="btn btn-primary" href="${ctx}/client/vehicles">Back to catalogue</a>
            <a class="btn btn-ghost" href="${ctx}/client/dashboard">Go to dashboard</a>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/client/includes/footer.jspf" %>
