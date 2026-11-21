<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Something went wrong &middot; JakLoc</title>
    <link rel="icon" type="image/svg+xml" href="${ctx}/assets/images/favicon.svg">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${ctx}/assets/css/base.css">
</head>
<body>
<main class="status-page">
    <div class="status-card">
        <p class="status-code">500</p>
        <h1>Something went wrong</h1>
        <p>We could not complete that request. Please try again in a moment.</p>
        <a class="btn btn-primary" href="${ctx}/">Back to my area</a>
    </div>
</main>
</body>
</html>
