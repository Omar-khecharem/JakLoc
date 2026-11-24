<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Profile" />
<c:set var="activeNav" value="profile" />
<%@ include file="/WEB-INF/views/client/includes/head.jspf" %>
<%@ include file="/WEB-INF/views/client/includes/header.jspf" %>

<main class="dashboard">
    <section class="client-page-head">
        <p class="eyebrow">Account</p>
        <h1>Your profile</h1>
        <p>
            These details come from your authenticated session. Profile editing
            and password changes will be added in a later step.
        </p>
    </section>

    <section class="info-card">
        <p class="eyebrow">Account details</p>
        <h2>Signed-in information</h2>
        <dl class="account-list">
            <div>
                <dt>Full name</dt>
                <dd><c:out value="${sessionScope.authenticatedUser.fullName}" /></dd>
            </div>
            <div>
                <dt>Email</dt>
                <dd><c:out value="${sessionScope.authenticatedUser.email}" /></dd>
            </div>
            <div>
                <dt>Phone</dt>
                <dd><c:out value="${sessionScope.authenticatedUser.phone}" /></dd>
            </div>
            <div>
                <dt>Account type</dt>
                <dd><c:out value="${sessionScope.authenticatedUser.role}" /></dd>
            </div>
            <div>
                <dt>Member since</dt>
                <dd>
                    <c:choose>
                        <c:when test="${not empty sessionScope.authenticatedUser.createdAt}">
                            <c:out value="${fn:substring(sessionScope.authenticatedUser.createdAt, 0, 10)}" />
                        </c:when>
                        <c:otherwise>&mdash;</c:otherwise>
                    </c:choose>
                </dd>
            </div>
        </dl>
        <p class="field-hint">
            Profile updates and password changes are intentionally not
            implemented in this foundation step.
        </p>
    </section>
</main>

<%@ include file="/WEB-INF/views/client/includes/footer.jspf" %>
