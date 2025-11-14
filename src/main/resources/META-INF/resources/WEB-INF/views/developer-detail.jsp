<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="fragments/head.jspf" %>
  <title><c:out value="${developer.firstName}" /> <c:out value="${developer.lastName}" /> | Developer Profile</title>
</head>
<body class="d-flex flex-column min-vh-100">
  <main class="flex-grow-1 py-5">
    <div class="container">
      <div class="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <a href="<c:url value='/developers' />" class="btn btn-link px-0">&larr; Back to directory</a>
        <a class="btn btn-outline-secondary rounded-pill ms-md-auto" href="<c:url value='/cart' />">
          View cart
          <c:if test="${cartCount > 0}">
            (<c:out value="${cartCount}" />)
          </c:if>
        </a>
      </div>
      <c:set var="detailAvatar" value="${empty developer.profilePictureUrl ? '/developer-images/jane.png' : developer.profilePictureUrl}" />
      <section class="card developer-card shadow-soft mb-4">
        <div class="card-body">
          <div class="card-heading mb-4">
            <div class="d-flex align-items-center gap-4">
              <img
                class="profile-avatar shadow-sm"
                src="<c:url value='${detailAvatar}' />"
                alt="Profile photo of ${developer.firstName} ${developer.lastName}"
              />
              <div>
                <h1 class="h3 fw-semibold mb-1">
                  <c:out value="${developer.firstName}" /> <c:out value="${developer.lastName}" />
                </h1>
                <p class="mb-2 text-secondary fw-medium">
                  <c:choose>
                    <c:when test="${not empty developer.jobTitle}">
                      <c:out value="${developer.jobTitle}" />
                    </c:when>
                    <c:when test="${not empty developer.designation}">
                      <c:out value="${developer.designation.designation}" />
                    </c:when>
                    <c:otherwise>
                      Experienced Developer
                    </c:otherwise>
                  </c:choose>
                </p>
                <c:if test="${developer.isVerified}">
                  <span class="badge-verified">
                    <svg viewBox="0 0 16 16" fill="currentColor" aria-hidden="true" focusable="false">
                      <path d="M8 0l1.763 1.114 2.059-.002 1.456 1.456.001 2.059L14.886 6.8 16 8l-1.114 1.2-.001 2.059-1.456 1.456-2.059-.001L8 16l-1.763-1.114-2.059.001-1.456-1.456-.001-2.059L1.114 9.2 0 8l1.114-1.2.001-2.059 1.456-1.456 2.059.002L8 0zm0 2.118l-1.21.764-1.658-.001L4.03 3.984l-.001 1.658-.764 1.21.764 1.21.001 1.658 1.102 1.103 1.658-.002L8 13.882l1.21-.764 1.658.002 1.102-1.103.001-1.658.764-1.21-.764-1.21-.001-1.658-1.102-1.103-1.658.001L8 2.118zm2.146 3.528a.5.5 0 0 1 .708.708l-3.182 3.182a.5.5 0 0 1-.708 0L5.146 8.414a.5.5 0 0 1 .708-.708L7.5 9.354z"/>
                    </svg>
                    Verified
                  </span>
                </c:if>
              </div>
            </div>
            <div class="text-end">
              <div class="rate-accent mb-2">
                <c:choose>
                  <c:when test="${developer.hourlyRate != null}">
                    $<fmt:formatNumber value="${developer.hourlyRate}" pattern="#,##0.##" />
                  </c:when>
                  <c:otherwise>
                    $ —
                  </c:otherwise>
                </c:choose>
                <span class="text-secondary fs-6 fw-normal">/hr</span>
              </div>
              <c:if test="${not empty developer.availabilities}">
                <span class="info-pill">
                  <c:out value="${developer.availabilities[0].availabilityLabel}" />
                </span>
              </c:if>
            </div>
          </div>
          <div class="stats-row mb-4">
            <span class="stats-card">
              <c:choose>
                <c:when test="${developer.numberOfExperience != null}">
                  <fmt:formatNumber value="${developer.numberOfExperience}" /> Years Experience
                </c:when>
                <c:otherwise>Experience N/A</c:otherwise>
              </c:choose>
            </span>
            <span class="stats-card">
              <c:choose>
                <c:when test="${developer.totalProjectCompletion != null}">
                  <fmt:formatNumber value="${developer.totalProjectCompletion}" /> Projects
                </c:when>
                <c:otherwise>Projects N/A</c:otherwise>
              </c:choose>
            </span>
            <c:if test="${not empty developer.workLocations}">
              <span class="stats-card">
                <c:out value="${developer.workLocations[0].workLocationLabel}" />
              </span>
            </c:if>
            <c:if test="${developer.averageRating != null}">
              <span class="stats-card">
                <fmt:formatNumber value="${developer.averageRating}" /> ★ Avg Rating
              </span>
            </c:if>
          </div>
          <div class="mb-4">
            <span class="text-uppercase small fw-semibold text-secondary d-block mb-2">About</span>
            <p class="text-secondary mb-0">
              <c:choose>
                <c:when test="${not empty developer.about}">
                  <c:out value="${developer.about}" />
                </c:when>
                <c:otherwise>
                  This developer has not provided a summary yet.
                </c:otherwise>
              </c:choose>
            </p>
          </div>
          <c:if test="${not empty developer.overallExperienceSkills}">
            <div class="mb-4">
              <span class="text-uppercase small fw-semibold text-secondary d-block mb-2">Key Skills</span>
              <div class="d-flex flex-wrap gap-2">
                <c:forEach var="tech" items="${developer.overallExperienceSkills}">
                  <span class="skill-tag"><c:out value="${tech.technology}" /></span>
                </c:forEach>
              </div>
            </div>
          </c:if>
          <div class="card-footer-actions pt-3">
            <div class="text-secondary small">
              <div>Email: <c:out value="${developer.email != null ? developer.email : 'Not shared'}" /></div>
              <div>Phone: <c:out value="${developer.phoneNumber != null ? developer.phoneNumber : 'Not shared'}" /></div>
            </div>
            <div>
              <c:url var="detailAddUrl" value="/cart/add">
                <c:param name="developerId" value="${developer.id}" />
              </c:url>
              <c:url var="detailRemoveUrl" value="/cart/remove">
                <c:param name="developerId" value="${developer.id}" />
              </c:url>
              <c:choose>
                <c:when test="${inCart}">
                  <a class="btn btn-danger btn-sm px-4" href="${detailRemoveUrl}">Remove from cart</a>
                </c:when>
                <c:otherwise>
                  <a class="btn btn-primary btn-sm px-4" href="${detailAddUrl}">Hire Developer</a>
                </c:otherwise>
              </c:choose>
            </div>
          </div>
        </div>
      </section>
    </div>
  </main>
</body>
</html>
