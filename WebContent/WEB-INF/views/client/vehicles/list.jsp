<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="pageTitle" value="Vehicle catalogue" />
<c:set var="activeNav" value="vehicles" />
<%@ include file="/WEB-INF/views/client/includes/head.jspf" %>
<%@ include file="/WEB-INF/views/client/includes/header.jspf" %>

<main class="dashboard catalog-page">
    <section class="catalog-hero">
        <div class="catalog-hero__copy">
            <p class="eyebrow">The JakLoc fleet</p>
            <h1>Find the car that fits the moment.</h1>
            <p>
                City runabouts, family SUVs and weekend grand tourers, curated in
                one place. Search the fleet, narrow it down and choose your next
                drive.
            </p>
        </div>
        <div class="catalog-hero__panel">
            <p class="eyebrow">Ready now</p>
            <strong><c:out value="${availableCount}" /></strong>
            <span>cars available to reserve</span>
            <c:if test="${not empty lowestAvailableRate}">
                <small>
                    From TND
                    <fmt:formatNumber value="${lowestAvailableRate}" minFractionDigits="0" maxFractionDigits="0" />
                    per day
                </small>
            </c:if>
        </div>
    </section>

    <section class="catalog-toolbar" aria-labelledby="filters-title">
        <div class="catalog-toolbar__head">
            <div>
                <p class="eyebrow">Refine</p>
                <h2 id="filters-title">Search the fleet</h2>
            </div>
            <p class="catalog-toolbar__count">
                <strong><c:out value="${resultCount}" /></strong>
                <c:choose>
                    <c:when test="${resultCount eq 1}">vehicle matches</c:when>
                    <c:otherwise>vehicles match</c:otherwise>
                </c:choose>
            </p>
        </div>

        <c:if test="${not empty errors}">
            <div class="alert alert-error" role="alert">
                <p>Please check the filters.</p>
                <ul>
                    <c:forEach var="error" items="${errors}">
                        <li><c:out value="${error}" /></li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <form class="catalog-form" method="get" action="${ctx}/client/vehicles" data-vehicle-filters>
            <div class="search-capsule">
                <label class="sr-only" for="query">Search by brand or model</label>
                <svg class="search-capsule__icon" viewBox="0 0 24 24" aria-hidden="true" focusable="false">
                    <circle cx="11" cy="11" r="6.5" fill="none" stroke="currentColor" stroke-width="1.8"/>
                    <path d="m16 16 4 4" fill="none" stroke="currentColor" stroke-width="1.8"
                          stroke-linecap="round"/>
                </svg>
                <input class="search-capsule__input" id="query" name="query" type="search"
                       value="${fn:escapeXml(filter.query)}"
                       placeholder="Search by brand or model, e.g. Porsche or GLE"
                       autocomplete="off">
                <button class="btn btn-primary search-capsule__submit" type="submit"
                        data-submit-button>Search</button>
            </div>

            <div class="filter-grid">
                <div class="field">
                    <label for="category">Category</label>
                    <select class="input" id="category" name="category" data-auto-submit>
                        <option value="">All categories</option>
                        <c:forEach var="category" items="${categories}">
                            <option value="${category}" ${filter.category eq category ? 'selected' : ''}>
                                <c:out value="${category.label}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="field">
                    <label for="transmission">Gearbox</label>
                    <select class="input" id="transmission" name="transmission" data-auto-submit>
                        <option value="">Any gearbox</option>
                        <c:forEach var="transmission" items="${transmissions}">
                            <option value="${transmission}" ${filter.transmission eq transmission ? 'selected' : ''}>
                                <c:out value="${transmission.label}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="field">
                    <label for="fuelType">Fuel</label>
                    <select class="input" id="fuelType" name="fuelType" data-auto-submit>
                        <option value="">Any fuel</option>
                        <c:forEach var="fuelType" items="${fuelTypes}">
                            <option value="${fuelType}" ${filter.fuelType eq fuelType ? 'selected' : ''}>
                                <c:out value="${fuelType.label}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="field">
                    <label for="maxPrice">Max daily rate</label>
                    <select class="input" id="maxPrice" name="maxPrice" data-auto-submit>
                        <option value="">Any price</option>
                        <option value="200" ${filter.maxPrice eq 200 ? 'selected' : ''}>Up to TND 200</option>
                        <option value="350" ${filter.maxPrice eq 350 ? 'selected' : ''}>Up to TND 350</option>
                        <option value="500" ${filter.maxPrice eq 500 ? 'selected' : ''}>Up to TND 500</option>
                        <option value="800" ${filter.maxPrice eq 800 ? 'selected' : ''}>Up to TND 800</option>
                    </select>
                </div>

                <div class="field">
                    <label for="sort">Sort by</label>
                    <select class="input" id="sort" name="sort" data-auto-submit>
                        <c:forEach var="sort" items="${sortOptions}">
                            <option value="${sort}" ${filter.sort eq sort ? 'selected' : ''}>
                                <c:out value="${sort.label}" />
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="filter-actions">
                    <a class="btn btn-ghost btn-sm" href="${ctx}/client/vehicles">Reset filters</a>
                </div>
            </div>

            <p class="field-hint">Select a filter to update the results instantly, or press Search.</p>
        </form>

        <c:if test="${filter.filtered}">
            <div class="active-filters">
                <span class="active-filters__label">Active filters</span>

                <c:if test="${not empty filter.query}">
                    <span class="filter-chip">
                        Search: <strong><c:out value="${filter.query}" /></strong>
                    </span>
                </c:if>
                <c:if test="${not empty filter.category}">
                    <span class="filter-chip"><c:out value="${filter.category.label}" /></span>
                </c:if>
                <c:if test="${not empty filter.transmission}">
                    <span class="filter-chip"><c:out value="${filter.transmission.label}" /></span>
                </c:if>
                <c:if test="${not empty filter.fuelType}">
                    <span class="filter-chip"><c:out value="${filter.fuelType.label}" /></span>
                </c:if>
                <c:if test="${not empty filter.maxPrice}">
                    <span class="filter-chip">
                        Up to TND <fmt:formatNumber value="${filter.maxPrice}" minFractionDigits="0"
                                                     maxFractionDigits="0" />
                    </span>
                </c:if>

                <a class="active-filters__reset" href="${ctx}/client/vehicles">Clear all</a>
            </div>
        </c:if>
    </section>

    <section class="catalog-results" aria-labelledby="results-title">
        <div class="client-section-head">
            <div>
                <p class="eyebrow">Fleet results</p>
                <h2 id="results-title">
                    <c:choose>
                        <c:when test="${resultCount eq 0}">No cars found</c:when>
                        <c:when test="${resultCount eq 1}">1 car for you</c:when>
                        <c:otherwise>${resultCount} cars for you</c:otherwise>
                    </c:choose>
                </h2>
            </div>
            <p class="catalog-results__sort">
                Sorted by <c:out value="${filter.sort.label}" />
            </p>
        </div>

        <c:choose>
            <c:when test="${empty vehicles}">
                <div class="empty-state">
                    <span class="empty-state__icon" aria-hidden="true">
                        <svg viewBox="0 0 24 24" focusable="false">
                            <circle cx="11" cy="11" r="6.5" fill="none" stroke="currentColor" stroke-width="1.6"/>
                            <path d="m16 16 4 4" fill="none" stroke="currentColor" stroke-width="1.6"
                                  stroke-linecap="round"/>
                        </svg>
                    </span>
                    <h2>No vehicles match those filters</h2>
                    <p>
                        Try a different brand, remove one of the filters, or reset the
                        search to see the whole fleet again.
                    </p>
                    <a class="btn btn-primary btn-sm" href="${ctx}/client/vehicles">Show all vehicles</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="vehicle-grid">
                    <c:forEach var="vehicle" items="${vehicles}">
                        <%@ include file="/WEB-INF/views/client/includes/vehicle-card.jspf" %>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </section>
</main>

<%@ include file="/WEB-INF/views/client/includes/footer.jspf" %>
