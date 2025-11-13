<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="fragments/head.jspf" %>
  <title>Your Cart</title>
</head>
<body class="d-flex flex-column min-vh-100">
  <main class="flex-grow-1 py-5">
    <div class="container">
      <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <div>
          <h1 class="display-6 fw-semibold mb-1">Cart</h1>
          <p class="text-secondary mb-0">
            <c:choose>
              <c:when test="${cartCount > 0}">
                You have <strong><c:out value="${cartCount}" /></strong> developer<c:if test="${cartCount != 1}">s</c:if> in your cart.
              </c:when>
              <c:otherwise>
                Your cart is empty.
              </c:otherwise>
            </c:choose>
          </p>
        </div>
        <div class="d-flex gap-2">
          <a class="btn btn-outline-secondary rounded-pill" href="<c:url value='/developers' />">&larr; Back to directory</a>
        </div>
      </div>

      <c:choose>
        <c:when test="${cartCount == 0}">
          <div class="bg-white rounded-4 shadow-soft p-5 text-center">
            <p class="text-secondary mb-3">No developers in your cart yet.</p>
            <a class="btn btn-primary rounded-pill px-4" href="<c:url value='/developers' />">Browse developers</a>
          </div>
        </c:when>
        <c:otherwise>
          <div class="row g-4">
            <div class="col-12 col-lg-8">
              <c:forEach var="developer" items="${cartDevelopers}">
                <c:url var="detailUrl" value="/developers/${developer.id}" />
                <c:url var="removeUrl" value="/cart/remove">
                  <c:param name="developerId" value="${developer.id}" />
                </c:url>
                <article class="card developer-card shadow-soft mb-3" data-profile-url="${detailUrl}">
                  <div class="card-body">
                    <div class="card-heading">
                      <div class="d-flex align-items-center gap-3">
                        <img
                          class="profile-avatar shadow-sm"
                          src="<c:url value='/developer-images/jane.png' />"
                          alt="Profile photo of ${developer.firstName} ${developer.lastName}"
                        />
                        <div>
                          <h2 class="h5 fw-semibold mb-1">
                            <c:out value="${developer.firstName}" /> <c:out value="${developer.lastName}" />
                          </h2>
                          <p class="mb-0 text-secondary small fw-medium">
                            <c:out value="${not empty developer.jobTitle ? developer.jobTitle : (not empty developer.designation ? developer.designation.designation : 'Experienced Developer')}" />
                          </p>
                        </div>
                      </div>
                      <div class="text-end">
                        <div class="rate-accent mb-2">
                          <c:choose>
                            <c:when test="${developer.hourlyRate != null}">
                              $<fmt:formatNumber value="${developer.hourlyRate}" pattern="#,##0.##" />
                            </c:when>
                            <c:otherwise>$ —</c:otherwise>
                          </c:choose>
                          <span class="text-secondary fs-6 fw-normal">/hr</span>
                        </div>
                        <a class="btn btn-danger btn-sm px-3" href="${removeUrl}">Remove</a>
                      </div>
                    </div>
                  </div>
                </article>
              </c:forEach>
            </div>
            <div class="col-12 col-lg-4">
              <div class="card shadow-soft">
                <div class="card-body">
                  <h2 class="h5 fw-semibold mb-3">Summary</h2>
                  <div class="d-flex justify-content-between mb-2">
                    <span class="text-secondary">Developers</span>
                    <span class="fw-semibold"><c:out value="${cartCount}" /></span>
                  </div>
                  <div class="d-flex justify-content-between mb-2">
                    <span class="text-secondary">Total hourly rate</span>
                    <span class="fw-semibold">$<fmt:formatNumber value="${totalHourlyRate}" pattern="#,##0.##" /></span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </main>
</body>
</html>
