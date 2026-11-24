<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="${vehicle.displayName}" />
<c:set var="activeNav" value="vehicles" />
<%@ include file="/WEB-INF/views/client/includes/head.jspf" %>
<%@ include file="/WEB-INF/views/client/includes/header.jspf" %>

<main class="dashboard vehicle-details-page">
    <nav class="detail-breadcrumb" aria-label="Breadcrumb">
        <a href="${ctx}/client/vehicles">Vehicle catalogue</a>
        <span aria-hidden="true">/</span>
        <span aria-current="page"><c:out value="${vehicle.displayName}" /></span>
    </nav>

    <section class="vehicle-detail-hero" aria-labelledby="vehicle-title">
        <div class="vehicle-detail-media vehicle-card__media--${vehicle.category.cssClass}">
            <c:choose>
                <c:when test="${not empty vehicle.imageUrl}">
                    <img class="vehicle-detail-image"
                         src="${fn:escapeXml(vehicle.imageUrl)}"
                         alt="${fn:escapeXml(vehicle.displayName)}"
                         width="1200" height="800">
                </c:when>
                <c:otherwise>
                    <img class="vehicle-detail-image vehicle-detail-image--placeholder"
                         src="${ctx}/assets/images/vehicles/vehicle-placeholder.svg"
                         alt="" width="1200" height="800">
                </c:otherwise>
            </c:choose>

            <span class="vehicle-card__category"><c:out value="${vehicle.category.label}" /></span>
            <span class="vehicle-status vehicle-status--${vehicle.status.cssClass}">
                <c:out value="${vehicle.status.label}" />
            </span>
        </div>

        <aside class="vehicle-detail-summary">
            <p class="eyebrow"><c:out value="${vehicle.category.label}" /> &middot; <c:out value="${vehicle.year}" /></p>
            <p class="vehicle-detail-brand"><c:out value="${vehicle.brand}" /></p>
            <h1 id="vehicle-title"><c:out value="${vehicle.model}" /></h1>
            <p class="vehicle-detail-lead"><c:out value="${vehicle.description}" /></p>

            <div class="vehicle-detail-price">
                <strong>
                    <c:choose>
                        <c:when test="${not empty vehicle.dailyRate}">
                            TND <fmt:formatNumber value="${vehicle.dailyRate}" minFractionDigits="0"
                                                  maxFractionDigits="0" />
                        </c:when>
                        <c:otherwise>On request</c:otherwise>
                    </c:choose>
                </strong>
                <span>per day</span>
            </div>

            <div class="vehicle-detail-availability">
                <p>Current availability</p>
                <span class="availability-pill availability-pill--${vehicle.status.cssClass}">
                    <span class="availability-pill__dot" aria-hidden="true"></span>
                    <c:out value="${vehicle.status.label}" />
                </span>
            </div>

            <div class="vehicle-detail-actions">
                <c:choose>
                    <c:when test="${vehicle.available}">
                        <a class="btn btn-primary"
                           href="${ctx}/client/bookings?vehicleId=${vehicle.id}">Start booking</a>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-primary" type="button" disabled>Currently unavailable</button>
                    </c:otherwise>
                </c:choose>
                <a class="btn btn-ghost" href="${ctx}/client/vehicles">Back to catalogue</a>
            </div>

            <p class="vehicle-detail-note">
                <c:choose>
                    <c:when test="${vehicle.available}">
                        You will choose dates and confirm the rental details in the booking step.
                    </c:when>
                    <c:otherwise>
                        <c:out value="${vehicle.status.description}" />
                    </c:otherwise>
                </c:choose>
            </p>
        </aside>
    </section>

    <section class="vehicle-detail-body">
        <div class="vehicle-detail-overview">
            <p class="eyebrow">Overview</p>
            <h2>About this <c:out value="${vehicle.category.label}" /></h2>
            <p><c:out value="${vehicle.description}" /></p>
            <p class="vehicle-detail-location">
                <svg viewBox="0 0 24 24" aria-hidden="true" focusable="false">
                    <path d="M12 21s7-5.5 7-12a7 7 0 1 0-14 0c0 6.5 7 12 7 12Z" fill="none"
                          stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/>
                    <circle cx="12" cy="9" r="2.4" fill="none" stroke="currentColor" stroke-width="1.8"/>
                </svg>
                Pickup in <c:out value="${vehicle.location}" />
            </p>
        </div>

        <aside class="vehicle-detail-availability-card">
            <p class="eyebrow">Fleet status</p>
            <span class="availability-pill availability-pill--${vehicle.status.cssClass}">
                <span class="availability-pill__dot" aria-hidden="true"></span>
                <c:out value="${vehicle.status.label}" />
            </span>
            <h2><c:out value="${vehicle.status.description}" /></h2>
            <dl class="vehicle-detail-facts">
                <div>
                    <dt>Daily rate</dt>
                    <dd>
                        <c:choose>
                            <c:when test="${not empty vehicle.dailyRate}">
                                TND <fmt:formatNumber value="${vehicle.dailyRate}" minFractionDigits="0"
                                                      maxFractionDigits="0" />
                            </c:when>
                            <c:otherwise>On request</c:otherwise>
                        </c:choose>
                    </dd>
                </div>
                <div>
                    <dt>Pickup</dt>
                    <dd><c:out value="${vehicle.location}" /></dd>
                </div>
                <div>
                    <dt>Vehicle ID</dt>
                    <dd>#<c:out value="${vehicle.id}" /></dd>
                </div>
            </dl>
        </aside>
    </section>

    <section aria-labelledby="specifications-title">
        <div class="client-section-head">
            <div>
                <p class="eyebrow">Specifications</p>
                <h2 id="specifications-title">The details that matter</h2>
            </div>
        </div>

        <div class="detail-spec-grid">
            <article class="detail-spec">
                <span class="detail-spec__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" focusable="false">
                        <path d="M4 15h16l-2-5H6l-2 5Zm2 0v3m12-3v3M7 11h10" fill="none"
                              stroke="currentColor" stroke-width="1.7" stroke-linecap="round"
                              stroke-linejoin="round"/>
                    </svg>
                </span>
                <span class="detail-spec__copy">
                    <small>Category</small>
                    <strong><c:out value="${vehicle.category.label}" /></strong>
                </span>
            </article>

            <article class="detail-spec">
                <span class="detail-spec__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" focusable="false">
                        <circle cx="12" cy="8" r="3" fill="none" stroke="currentColor" stroke-width="1.7"/>
                        <path d="M5 20c.8-4 3.3-6 7-6s6.2 2 7 6" fill="none" stroke="currentColor"
                              stroke-width="1.7" stroke-linecap="round"/>
                    </svg>
                </span>
                <span class="detail-spec__copy">
                    <small>Seats</small>
                    <strong><c:out value="${vehicle.seats}" /></strong>
                </span>
            </article>

            <article class="detail-spec">
                <span class="detail-spec__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" focusable="false">
                        <path d="M12 4v16M7 7h10M7 17h10M5 12h14" fill="none" stroke="currentColor"
                              stroke-width="1.7" stroke-linecap="round"/>
                    </svg>
                </span>
                <span class="detail-spec__copy">
                    <small>Gearbox</small>
                    <strong><c:out value="${vehicle.transmission.label}" /></strong>
                </span>
            </article>

            <article class="detail-spec">
                <span class="detail-spec__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" focusable="false">
                        <path d="M12 3s5 5.2 5 9a5 5 0 0 1-10 0c0-3.8 5-9 5-9Z" fill="none"
                              stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/>
                    </svg>
                </span>
                <span class="detail-spec__copy">
                    <small>Fuel</small>
                    <strong><c:out value="${vehicle.fuelType.label}" /></strong>
                </span>
            </article>

            <article class="detail-spec">
                <span class="detail-spec__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" focusable="false">
                        <path d="M7 3v3M17 3v3M4 9h16M5 5h14a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1Z"
                              fill="none" stroke="currentColor" stroke-width="1.7" stroke-linecap="round"
                              stroke-linejoin="round"/>
                    </svg>
                </span>
                <span class="detail-spec__copy">
                    <small>Model year</small>
                    <strong><c:out value="${vehicle.year}" /></strong>
                </span>
            </article>

            <article class="detail-spec">
                <span class="detail-spec__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" focusable="false">
                        <path d="M5 4h11a3 3 0 0 1 3 3v13H8a3 3 0 0 1-3-3V4Zm3 16V8" fill="none"
                              stroke="currentColor" stroke-width="1.7" stroke-linecap="round"
                              stroke-linejoin="round"/>
                    </svg>
                </span>
                <span class="detail-spec__copy">
                    <small>Doors</small>
                    <strong><c:out value="${vehicle.doors}" /></strong>
                </span>
            </article>

            <article class="detail-spec">
                <span class="detail-spec__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" focusable="false">
                        <path d="M4 15a8 8 0 1 1 16 0M12 15l3-4" fill="none" stroke="currentColor"
                              stroke-width="1.7" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                </span>
                <span class="detail-spec__copy">
                    <small>Mileage</small>
                    <strong>
                        <fmt:formatNumber value="${vehicle.mileage}" type="number" maxFractionDigits="0" />
                        km
                    </strong>
                </span>
            </article>

            <article class="detail-spec">
                <span class="detail-spec__icon" aria-hidden="true">
                    <svg viewBox="0 0 24 24" focusable="false">
                        <path d="M12 21s7-5.5 7-12a7 7 0 1 0-14 0c0 6.5 7 12 7 12Z" fill="none"
                              stroke="currentColor" stroke-width="1.7" stroke-linejoin="round"/>
                        <circle cx="12" cy="9" r="2.4" fill="none" stroke="currentColor" stroke-width="1.7"/>
                    </svg>
                </span>
                <span class="detail-spec__copy">
                    <small>Pickup</small>
                    <strong><c:out value="${vehicle.location}" /></strong>
                </span>
            </article>
        </div>
    </section>

    <section class="vehicle-detail-cta">
        <div class="vehicle-detail-cta__copy">
            <p class="eyebrow">
                <c:choose>
                    <c:when test="${vehicle.available}">Ready when you are</c:when>
                    <c:otherwise>Next availability</c:otherwise>
                </c:choose>
            </p>
            <h2>
                <c:choose>
                    <c:when test="${vehicle.available}">
                        Take the <c:out value="${vehicle.model}" /> for a drive.
                    </c:when>
                    <c:otherwise>
                        The <c:out value="${vehicle.model}" /> is not bookable right now.
                    </c:otherwise>
                </c:choose>
            </h2>
            <p>
                <c:choose>
                    <c:when test="${vehicle.available}">
                        Start the booking flow to select your rental dates and confirm the car.
                    </c:when>
                    <c:otherwise>
                        Browse the rest of the fleet while this vehicle is off the road.
                    </c:otherwise>
                </c:choose>
            </p>
        </div>

        <div class="vehicle-detail-cta__actions">
            <div class="vehicle-detail-cta__price">
                <c:choose>
                    <c:when test="${not empty vehicle.dailyRate}">
                        <strong>TND <fmt:formatNumber value="${vehicle.dailyRate}" minFractionDigits="0"
                                                      maxFractionDigits="0" /></strong>
                    </c:when>
                    <c:otherwise><strong>On request</strong></c:otherwise>
                </c:choose>
                <span>per day</span>
            </div>
            <div class="vehicle-detail-cta__buttons">
                <c:choose>
                    <c:when test="${vehicle.available}">
                        <a class="btn btn-primary"
                           href="${ctx}/client/bookings?vehicleId=${vehicle.id}">Start booking</a>
                    </c:when>
                    <c:otherwise>
                        <button class="btn btn-primary" type="button" disabled>Currently unavailable</button>
                    </c:otherwise>
                </c:choose>
                <a class="btn btn-ghost" href="${ctx}/client/vehicles">Back to catalogue</a>
            </div>
        </div>
    </section>
</main>

<%@ include file="/WEB-INF/views/client/includes/footer.jspf" %>
