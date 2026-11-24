<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Client dashboard" />
<c:set var="activeNav" value="dashboard" />
<%@ include file="/WEB-INF/views/client/includes/head.jspf" %>
<%@ include file="/WEB-INF/views/client/includes/header.jspf" %>

<main class="dashboard client-dashboard">
    <section class="welcome-card client-hero">
        <div class="client-hero__copy">
            <p class="eyebrow">Client area</p>
            <h1>Welcome back, <c:out value="${sessionScope.authenticatedUser.fullName}" /></h1>
            <p>
                Your next drive starts here. Explore the JakLoc fleet, compare
                daily rates and find the car that fits your plans.
            </p>
            <div class="client-hero__actions">
                <a class="btn btn-primary" href="${ctx}/client/vehicles">Browse the fleet</a>
                <a class="btn btn-ghost client-hero__ghost" href="${ctx}/client/bookings">My bookings</a>
            </div>
        </div>

        <aside class="client-hero__panel" aria-label="Fleet availability">
            <p class="eyebrow">Ready now</p>
            <strong><c:out value="${availableVehicleCount}" /></strong>
            <span>cars available to reserve</span>
            <a href="${ctx}/client/vehicles">See availability</a>
        </aside>
    </section>

    <section class="client-stats" aria-label="Fleet at a glance">
        <article class="client-stat">
            <p class="eyebrow">Available cars</p>
            <strong><c:out value="${availableVehicleCount}" /></strong>
            <span>Ready for your next trip</span>
        </article>
        <article class="client-stat">
            <p class="eyebrow">Categories</p>
            <strong><c:out value="${fleetCategoryCount}" /></strong>
            <span>Different ways to drive</span>
        </article>
        <article class="client-stat">
            <p class="eyebrow">Starting from</p>
            <c:choose>
                <c:when test="${not empty lowestAvailableRate}">
                    <strong>
                        TND <fmt:formatNumber value="${lowestAvailableRate}" minFractionDigits="0"
                                               maxFractionDigits="0" />
                    </strong>
                    <span>Per day, selected cars</span>
                </c:when>
                <c:otherwise>
                    <strong>&mdash;</strong>
                    <span>No cars available right now</span>
                </c:otherwise>
            </c:choose>
        </article>
    </section>

    <section aria-labelledby="featured-title">
        <div class="client-section-head">
            <div>
                <p class="eyebrow">Featured fleet</p>
                <h2 id="featured-title">Handpicked for your next drive</h2>
            </div>
            <a class="btn btn-ghost btn-sm" href="${ctx}/client/vehicles">View all vehicles</a>
        </div>

        <c:choose>
            <c:when test="${empty featuredVehicles}">
                <div class="empty-state empty-state--compact">
                    <h2>The fleet is being prepared</h2>
                    <p>No vehicles are available to feature right now. Please check again shortly.</p>
                    <a class="btn btn-primary btn-sm" href="${ctx}/client/vehicles">Open the catalogue</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="vehicle-grid vehicle-grid--featured">
                    <c:forEach var="vehicle" items="${featuredVehicles}">
                        <%@ include file="/WEB-INF/views/client/includes/vehicle-card.jspf" %>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>

    <section aria-labelledby="workspace-title">
        <div class="client-section-head">
            <div>
                <p class="eyebrow">Your workspace</p>
                <h2 id="workspace-title">Everything in one place</h2>
            </div>
        </div>

        <div class="client-quick-grid">
            <a class="quick-card" href="${ctx}/client/bookings">
                <span class="quick-card__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" focusable="false">
                        <path d="M7 3v3M17 3v3M4 9h16M5 5h14a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1Z"
                              fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"
                              stroke-linejoin="round"/>
                    </svg>
                </span>
                <span class="quick-card__copy">
                    <strong>My bookings</strong>
                    <small>Track requests and upcoming pickups.</small>
                </span>
                <span class="quick-card__arrow" aria-hidden="true">&rarr;</span>
            </a>

            <a class="quick-card" href="${ctx}/client/favorites">
                <span class="quick-card__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" focusable="false">
                        <path d="M12 20s-7-4.35-7-10a4 4 0 0 1 7-2.65A4 4 0 0 1 19 10c0 5.65-7 10-7 10Z"
                              fill="none" stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/>
                    </svg>
                </span>
                <span class="quick-card__copy">
                    <strong>Favorites</strong>
                    <small>Keep the cars you love close at hand.</small>
                </span>
                <span class="quick-card__arrow" aria-hidden="true">&rarr;</span>
            </a>

            <a class="quick-card" href="${ctx}/client/profile">
                <span class="quick-card__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" focusable="false">
                        <circle cx="12" cy="8" r="3.2" fill="none" stroke="currentColor" stroke-width="1.7"/>
                        <path d="M5 20c.8-4 3.3-6 7-6s6.2 2 7 6" fill="none" stroke="currentColor"
                              stroke-width="1.7" stroke-linecap="round"/>
                    </svg>
                </span>
                <span class="quick-card__copy">
                    <strong>Your profile</strong>
                    <small>Review the account linked to this session.</small>
                </span>
                <span class="quick-card__arrow" aria-hidden="true">&rarr;</span>
            </a>
        </div>
    </section>

    <section class="cards">
        <article class="info-card">
            <p class="eyebrow">Account summary</p>
            <h2>Signed-in details</h2>
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
            <a class="btn btn-ghost btn-sm" href="${ctx}/client/profile">Open profile</a>
        </article>

        <article class="info-card info-card--accent">
            <p class="eyebrow">Ready to go?</p>
            <h2>Pick a car, set the pace</h2>
            <p>
                Compare categories, gearboxes and daily rates in the catalogue.
                Your next reservation starts with the right vehicle.
            </p>
            <a class="btn btn-primary btn-sm" href="${ctx}/client/vehicles">Explore the fleet</a>
        </article>
    </section>
</main>

<%@ include file="/WEB-INF/views/client/includes/footer.jspf" %>
