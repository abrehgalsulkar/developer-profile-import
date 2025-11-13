<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <%@ include file="fragments/head.jspf" %>
  <title>Developer Directory</title>
</head>
<body class="d-flex flex-column min-vh-100">
  <main class="flex-grow-1 py-5">
    <div class="container">
      <c:set var="pageStart" value="${developerCount == 0 ? 0 : developersPage.number * developersPage.size + 1}" />
      <c:set var="pageEnd" value="${developersPage.number * developersPage.size + developersPage.numberOfElements}" />
      <c:set var="sortOptionName" value="${filters.sortOption != null ? filters.sortOption.name() : 'DEFAULT'}" />
      <c:set var="sortDirectionName" value="${filters.sortDirection != null ? filters.sortDirection.name() : 'DESC'}" />
      <div class="row g-4">
        <aside class="col-12 col-lg-4 col-xl-3 align-self-start">
          <div class="filter-panel shadow-soft bg-white p-4">
            <div class="d-flex align-items-center justify-content-between mb-4">
              <h2 class="h5 fw-semibold mb-0">Filters</h2>
              <button class="btn btn-link btn-sm text-decoration-none px-0" type="button" data-action="reset-filters">Reset</button>
            </div>
            <form id="filterForm" class="d-flex flex-column gap-4" method="get" action="/developers">
              <input type="hidden" name="page" value="${developersPage.number}" data-page-input>
              <input type="hidden" name="size" value="${developersPage.size}">
              <input type="hidden" name="sortOption" value="${sortOptionName}" data-sort-option-input>
              <input type="hidden" name="sortDirection" value="${sortDirectionName}" data-sort-direction-input>
              <section>
                <label for="filterSearch" class="form-label fw-medium small text-uppercase text-secondary">Search</label>
                <div class="input-group rounded-pill filter-search">
                  <span class="input-group-text ps-0">&#128269;</span>
                  <input
                    type="search"
                    id="filterSearch"
                    name="searchTerm"
                    class="form-control border-0"
                    placeholder="Search developer name..."
                    value="${filters.searchTerm}"
                  >
                </div>
              </section>
              <section class="filter-section">
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <span class="fw-semibold small text-uppercase text-secondary">Technologies</span>
                  <button class="btn btn-link btn-sm p-0 small" type="button" data-toggle="show-more" data-target="#techMoreGroup">Show more</button>
                </div>
                <div class="chip-group" data-filter-options>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="technologies" value="JavaScript" id="tech-0"<c:if test="${filters.technologies != null and filters.technologies.contains('JavaScript')}"> checked</c:if>>
                    <label for="tech-0" class="chip chip-action">JavaScript</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="technologies" value="Python" id="tech-1"<c:if test="${filters.technologies != null and filters.technologies.contains('Python')}"> checked</c:if>>
                    <label for="tech-1" class="chip chip-action">Python</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="technologies" value="Angular" id="tech-2"<c:if test="${filters.technologies != null and filters.technologies.contains('Angular')}"> checked</c:if>>
                    <label for="tech-2" class="chip chip-action">Angular</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="technologies" value="Node.js" id="tech-3"<c:if test="${filters.technologies != null and filters.technologies.contains('Node.js')}"> checked</c:if>>
                    <label for="tech-3" class="chip chip-action">Node.js</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="technologies" value="React" id="tech-4"<c:if test="${filters.technologies != null and filters.technologies.contains('React')}"> checked</c:if>>
                    <label for="tech-4" class="chip chip-action">React</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="technologies" value="Vue.js" id="tech-5"<c:if test="${filters.technologies != null and filters.technologies.contains('Vue.js')}"> checked</c:if>>
                    <label for="tech-5" class="chip chip-action">Vue.js</label>
                  </div>
                </div>
                <div class="chip-group mt-2 d-none" id="techMoreGroup" data-filter-options>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="technologies" value="TypeScript" id="tech-6"<c:if test="${filters.technologies != null and filters.technologies.contains('TypeScript')}"> checked</c:if>>
                    <label for="tech-6" class="chip chip-action">TypeScript</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="technologies" value="Java" id="tech-7"<c:if test="${filters.technologies != null and filters.technologies.contains('Java')}"> checked</c:if>>
                    <label for="tech-7" class="chip chip-action">Java</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="technologies" value="Go" id="tech-8"<c:if test="${filters.technologies != null and filters.technologies.contains('Go')}"> checked</c:if>>
                    <label for="tech-8" class="chip chip-action">Go</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="technologies" value="Ruby" id="tech-9"<c:if test="${filters.technologies != null and filters.technologies.contains('Ruby')}"> checked</c:if>>
                    <label for="tech-9" class="chip chip-action">Ruby</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="technologies" value="AWS" id="tech-10"<c:if test="${filters.technologies != null and filters.technologies.contains('AWS')}"> checked</c:if>>
                    <label for="tech-10" class="chip chip-action">AWS</label>
                  </div>
                </div>
              </section>
              <section class="filter-section">
                <span class="fw-semibold small text-uppercase text-secondary d-block mb-2">Experience Range</span>
                <div class="chip-group" data-filter-options>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="experienceRanges" value="0-5" id="exp-0"<c:if test="${filters.experienceRanges != null and filters.experienceRanges.contains('0-5')}"> checked</c:if>>
                    <label for="exp-0" class="chip chip-action">0-5</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="experienceRanges" value="6-10" id="exp-1"<c:if test="${filters.experienceRanges != null and filters.experienceRanges.contains('6-10')}"> checked</c:if>>
                    <label for="exp-1" class="chip chip-action">6-10</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="experienceRanges" value="11-15" id="exp-2"<c:if test="${filters.experienceRanges != null and filters.experienceRanges.contains('11-15')}"> checked</c:if>>
                    <label for="exp-2" class="chip chip-action">11-15</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="experienceRanges" value="16-20" id="exp-3"<c:if test="${filters.experienceRanges != null and filters.experienceRanges.contains('16-20')}"> checked</c:if>>
                    <label for="exp-3" class="chip chip-action">16-20</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="experienceRanges" value="21+" id="exp-4"<c:if test="${filters.experienceRanges != null and filters.experienceRanges.contains('21+')}"> checked</c:if>>
                    <label for="exp-4" class="chip chip-action">21+</label>
                  </div>
                </div>
              </section>
              <section class="filter-section">
                <span class="fw-semibold small text-uppercase text-secondary d-block mb-2">Project Completion</span>
                <div class="chip-group" data-filter-options>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="projectCompletions" value="0-5" id="proj-0"<c:if test="${filters.projectCompletions != null and filters.projectCompletions.contains('0-5')}"> checked</c:if>>
                    <label for="proj-0" class="chip chip-action">0-5</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="projectCompletions" value="6-10" id="proj-1"<c:if test="${filters.projectCompletions != null and filters.projectCompletions.contains('6-10')}"> checked</c:if>>
                    <label for="proj-1" class="chip chip-action">6-10</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="projectCompletions" value="11-15" id="proj-2"<c:if test="${filters.projectCompletions != null and filters.projectCompletions.contains('11-15')}"> checked</c:if>>
                    <label for="proj-2" class="chip chip-action">11-15</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="projectCompletions" value="16-20" id="proj-3"<c:if test="${filters.projectCompletions != null and filters.projectCompletions.contains('16-20')}"> checked</c:if>>
                    <label for="proj-3" class="chip chip-action">16-20</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="projectCompletions" value="21+" id="proj-4"<c:if test="${filters.projectCompletions != null and filters.projectCompletions.contains('21+')}"> checked</c:if>>
                    <label for="proj-4" class="chip chip-action">21+</label>
                  </div>
                </div>
              </section>
              <section class="filter-section">
                <span class="fw-semibold small text-uppercase text-secondary d-block mb-2">Work Locations</span>
                <div class="chip-group" data-filter-options>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="workLocations" value="Onsite" id="loc-0"<c:if test="${filters.workLocations != null and filters.workLocations.contains('Onsite')}"> checked</c:if>>
                    <label for="loc-0" class="chip chip-action">Onsite</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="workLocations" value="Remote" id="loc-1"<c:if test="${filters.workLocations != null and filters.workLocations.contains('Remote')}"> checked</c:if>>
                    <label for="loc-1" class="chip chip-action">Remote</label>
                  </div>
                </div>
              </section>
              <section class="filter-section">
                <span class="fw-semibold small text-uppercase text-secondary d-block mb-2">Availabilities</span>
                <div class="chip-group" data-filter-options>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="availabilities" value="Part Time" id="avail-0"<c:if test="${filters.availabilities != null and filters.availabilities.contains('Part Time')}"> checked</c:if>>
                    <label for="avail-0" class="chip chip-action">Part Time</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="availabilities" value="Full Time" id="avail-1"<c:if test="${filters.availabilities != null and filters.availabilities.contains('Full Time')}"> checked</c:if>>
                    <label for="avail-1" class="chip chip-action">Full Time</label>
                  </div>
                </div>
              </section>
              <section class="filter-section">
                <span class="fw-semibold small text-uppercase text-secondary d-block mb-2">Language</span>
                <div class="chip-group mb-3" data-filter-options>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="languages" value="English" id="lang-0"<c:if test="${filters.languages != null and filters.languages.contains('English')}"> checked</c:if>>
                    <label for="lang-0" class="chip chip-action">English</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="languages" value="Hindi" id="lang-1"<c:if test="${filters.languages != null and filters.languages.contains('Hindi')}"> checked</c:if>>
                    <label for="lang-1" class="chip chip-action">Hindi</label>
                  </div>
                  <div class="chip-option">
                    <input type="checkbox" class="chip-input" name="languages" value="Gujarati" id="lang-2"<c:if test="${filters.languages != null and filters.languages.contains('Gujarati')}"> checked</c:if>>
                    <label for="lang-2" class="chip chip-action">Gujarati</label>
                  </div>
                </div>
                <select class="form-select form-select-sm" name="languageProficiency" data-filter-select>
                  <option value="">Select proficiency</option>
                  <option value="Expert"<c:if test="${filters.languageProficiency eq 'Expert'}"> selected</c:if>>Expert</option>
                  <option value="Intermediate"<c:if test="${filters.languageProficiency eq 'Intermediate'}"> selected</c:if>>Intermediate</option>
                  <option value="Beginner"<c:if test="${filters.languageProficiency eq 'Beginner'}"> selected</c:if>>Beginner</option>
                </select>
              </section>
              <c:set var="minRateValue" value="${filters.minHourlyRate != null ? filters.minHourlyRate.intValue() : 15}" />
              <c:set var="maxRateValue" value="${filters.maxHourlyRate != null ? filters.maxHourlyRate.intValue() : 120}" />
              <section class="filter-section rate-highlight">
                <div class="d-flex justify-content-between mb-2">
                  <span class="fw-semibold small text-uppercase text-secondary">Hourly Rate</span>
                  <span class="small text-secondary">$<span id="rateMinValue">${minRateValue}</span> - $<span id="rateMaxValue">${maxRateValue}</span></span>
                </div>
                <div class="range-slider" data-range>
                  <input type="range" id="rateMin" name="minHourlyRate" min="1" max="150" value="${minRateValue}" data-default="15">
                  <input type="range" id="rateMax" name="maxHourlyRate" min="1" max="150" value="${maxRateValue}" data-default="120">
                </div>
                <div class="range-display">
                  <span>Min: $<span id="rateMinLabel">${minRateValue}</span></span>
                  <span>Max: $<span id="rateMaxLabel">${maxRateValue}</span></span>
                </div>
              </section>
            </form>
          </div>
        </aside>
        <div class="col-12 col-lg-8 col-xl-9 align-self-start">
          <section class="directory-header mb-4">
            <div class="d-flex flex-column flex-lg-row align-items-lg-center justify-content-between gap-3">
              <div class="directory-headline">
                <h1 class="display-6 fw-semibold mb-0">Showing <c:out value="${developerCount}" /> Developers</h1>
                <div class="d-flex flex-wrap align-items-center gap-2 text-secondary small">
                  <c:if test="${developerCount > 0}">
                    <span>Viewing ${pageStart} - ${pageEnd}</span>
                  </c:if>
                </div>
              </div>
              <div class="d-flex flex-wrap align-items-center gap-2">
                <div class="sort-control">
                  <label class="visually-hidden" for="sortKeySelect">Sort field</label>
                  <select class="form-select" id="sortKeySelect" data-sort-key-select>
                    <option value="DEFAULT"<c:if test="${sortOptionName eq 'DEFAULT'}"> selected</c:if>>Default order</option>
                    <option value="PROJECTS"<c:if test="${sortOptionName eq 'PROJECTS'}"> selected</c:if>>Number of projects</option>
                    <option value="EXPERIENCE"<c:if test="${sortOptionName eq 'EXPERIENCE'}"> selected</c:if>>Total experience</option>
                    <option value="HOURLY_RATE"<c:if test="${sortOptionName eq 'HOURLY_RATE'}"> selected</c:if>>Hourly price</option>
                    <option value="VERIFIED"<c:if test="${sortOptionName eq 'VERIFIED'}"> selected</c:if>>Verified</option>
                  </select>
                </div>
                <div class="sort-control">
                  <label class="visually-hidden" for="sortDirSelect">Sort direction</label>
                  <select class="form-select" id="sortDirSelect" data-sort-direction-select>
                    <option value="DESC"<c:if test="${sortDirectionName eq 'DESC'}"> selected</c:if>>Descending</option>
                    <option value="ASC"<c:if test="${sortDirectionName eq 'ASC'}"> selected</c:if>>Ascending</option>
                  </select>
                </div>
                <a class="btn btn-outline-secondary rounded-pill" href="<c:url value='/cart' />">
                  View cart
                  <c:if test="${cartCount > 0}">
                    (<c:out value="${cartCount}" />)
                  </c:if>
                </a>
              </div>
            </div>
          </section>

          <c:choose>
            <c:when test="${empty developers}">
              <div class="bg-white rounded-4 shadow-soft p-5 text-center">
                <h2 class="h5 fw-semibold mb-2">No developers found yet</h2>
                <p class="text-secondary mb-0">
                  Import developer profiles or adjust your filters once they are available to see matching talent here.
                </p>
              </div>
            </c:when>
            <c:otherwise>
              <div class="d-flex flex-column gap-4">
                <c:set var="defaultAvatar" value="/developer-images/jane.png" />
                <c:forEach var="developer" items="${developers}" varStatus="loop">
                  <c:set var="inCart" value="${cartDeveloperIds != null and cartDeveloperIds.contains(developer.id)}" />
                  <c:url var="addToCartUrl" value="/cart/add">
                    <c:param name="developerId" value="${developer.id}" />
                  </c:url>
                  <c:url var="removeFromCartUrl" value="/cart/remove">
                    <c:param name="developerId" value="${developer.id}" />
                  </c:url>
                  <c:set var="primaryAvailability" value="${not empty developer.availabilities ? developer.availabilities[0].availabilityLabel : null}" />
                  <c:set var="displayTitle" value="${not empty developer.jobTitle ? developer.jobTitle : (not empty developer.designation ? developer.designation.designation : 'Experienced Developer')}" />
                  <article class="card developer-card shadow-soft" data-profile-url="<c:url value='/developers/${developer.id}' />">
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
                            <p class="mb-2 text-secondary small fw-medium">
                              <c:out value="${displayTitle}" />
                            </p>
                            <c:if test="${developer.isVerified}">
                              <span class="badge-verified">Skill Test Verified</span>
                            </c:if>
                          </div>
                        </div>
                        <div class="text-end">
                          <div class="rate-accent">
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
                          <c:if test="${primaryAvailability != null}">
                            <span class="info-pill mt-2 d-inline-flex">
                              <c:out value="${primaryAvailability}" />
                            </span>
                          </c:if>
                        </div>
                      </div>

                      <div class="stats-row mb-4">
                        <c:choose>
                          <c:when test="${primaryAvailability != null}">
                            <span class="stats-card">
                              <c:out value="${primaryAvailability}" />
                            </span>
                          </c:when>
                          <c:otherwise>
                            <span class="stats-card">Availability TBD</span>
                          </c:otherwise>
                        </c:choose>
                        <span class="stats-card">
                          <c:choose>
                            <c:when test="${developer.numberOfExperience != null}">
                              <fmt:formatNumber value="${developer.numberOfExperience}" /> Years
                            </c:when>
                            <c:otherwise>
                              Experience N/A
                            </c:otherwise>
                          </c:choose>
                        </span>
                        <span class="stats-card">
                          <c:choose>
                            <c:when test="${developer.totalProjectCompletion != null}">
                              <fmt:formatNumber value="${developer.totalProjectCompletion}" /> Projects
                            </c:when>
                            <c:otherwise>
                              Projects N/A
                            </c:otherwise>
                          </c:choose>
                        </span>
                        <c:if test="${not empty developer.workLocations}">
                          <span class="stats-card">
                            <c:out value="${developer.workLocations[0].workLocationLabel}" />
                            <c:if test="${developer.workLocations.size() > 1}"> +</c:if>
                          </span>
                        </c:if>
                      </div>

                      <div class="mb-3">
                        <span class="text-uppercase small fw-semibold text-secondary d-block mb-2">About</span>
                        <p class="mb-3 text-secondary">
                          <c:choose>
                            <c:when test="${not empty developer.about}">
                              <c:out value="${developer.about}" />
                            </c:when>
                            <c:otherwise>
                              This developer has not provided a summary yet. Invite them to share more details about their experience.
                            </c:otherwise>
                          </c:choose>
                        </p>
                      </div>

                      <c:if test="${not empty developer.overallExperienceSkills}">
                        <div class="d-flex flex-wrap gap-2 mb-4">
                          <c:forEach var="tech" items="${developer.overallExperienceSkills}" varStatus="techStatus">
                            <c:if test="${techStatus.index < 6}">
                              <span class="skill-tag">
                                <c:out value="${tech.technology}" />
                              </span>
                            </c:if>
                          </c:forEach>
                          <c:if test="${developer.overallExperienceSkills.size() > 6}">
                            <span class="skill-tag">+<c:out value="${developer.overallExperienceSkills.size() - 6}" /> more</span>
                          </c:if>
                        </div>
                      </c:if>

                      <div class="card-footer-actions">
                        <div class="d-flex gap-2">
                          <c:choose>
                            <c:when test="${inCart}">
                              <a class="btn btn-danger btn-sm px-4" href="${removeFromCartUrl}">Remove from cart</a>
                            </c:when>
                            <c:otherwise>
                              <a class="btn btn-primary btn-sm px-4" href="${addToCartUrl}">Hire Developer</a>
                            </c:otherwise>
                          </c:choose>
                        </div>
                      </div>
                    </div>
                  </article>
                </c:forEach>
              </div>
              <c:if test="${developersPage.totalPages > 1}">
                <div class="d-flex flex-column flex-md-row align-items-center justify-content-between gap-3 mt-4">
                  <span class="text-secondary small">
                    Showing ${pageStart} - ${pageEnd} of ${developerCount}
                  </span>
                  <div class="d-flex align-items-center gap-2">
                    <button type="button" class="btn btn-outline-secondary btn-sm" data-page-action="prev"<c:if test="${developersPage.first}"> disabled</c:if>>
                      Previous
                    </button>
                    <span class="small fw-medium">Page ${developersPage.number + 1} of ${developersPage.totalPages}</span>
                    <button type="button" class="btn btn-outline-secondary btn-sm" data-page-action="next"<c:if test="${developersPage.last}"> disabled</c:if>>
                      Next
                    </button>
                  </div>
                </div>
              </c:if>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </main>
  <script>
    document.addEventListener('DOMContentLoaded', function () {
      const filterForm = document.getElementById('filterForm');
      if (!filterForm) {
        return;
      }

      const filterPanel = filterForm.closest('.filter-panel') || filterForm;
      const resetButton = filterPanel.querySelector('[data-action="reset-filters"]');
      const chipInputs = Array.from(filterForm.querySelectorAll('.chip-input'));
      const showMoreButtons = Array.from(filterForm.querySelectorAll('[data-toggle="show-more"]'));
      const searchInput = document.getElementById('filterSearch');
      const proficiencySelects = Array.from(filterForm.querySelectorAll('[data-filter-select]'));
      const rangeMin = filterForm.querySelector('#rateMin');
      const rangeMax = filterForm.querySelector('#rateMax');
      const minValue = document.getElementById('rateMinValue');
      const maxValue = document.getElementById('rateMaxValue');
      const minLabel = document.getElementById('rateMinLabel');
      const maxLabel = document.getElementById('rateMaxLabel');
      const pageInput = filterForm.querySelector('[data-page-input]');
      const sortOptionInput = filterForm.querySelector('[data-sort-option-input]');
      const sortDirectionInput = filterForm.querySelector('[data-sort-direction-input]');
      const sortKeySelect = document.querySelector('[data-sort-key-select]');
      const sortDirectionSelect = document.querySelector('[data-sort-direction-select]');
      const paginationButtons = Array.from(document.querySelectorAll('[data-page-action]'));
      let submitTimeout;

      function scheduleSubmit(options) {
        const settings = Object.assign({ resetPage: true, delay: 250 }, options || {});
        if (settings.resetPage) {
          resetPage();
        }
        clearTimeout(submitTimeout);
        submitTimeout = setTimeout(function () {
          syncSortHiddenFields();
          filterForm.submit();
        }, Math.max(settings.delay, 0));
      }

      function resetPage() {
        if (pageInput) {
          pageInput.value = '0';
        }
      }

      function syncSortHiddenFields() {
        if (sortKeySelect && sortOptionInput) {
          sortOptionInput.value = sortKeySelect.value;
        }
        if (sortDirectionSelect && sortDirectionInput) {
          sortDirectionInput.value = sortDirectionSelect.value;
        }
      }

      function updateSortDirectionState() {
        if (!sortKeySelect || !sortDirectionSelect) {
          return;
        }
        const isDefault = sortKeySelect.value === 'DEFAULT';
        sortDirectionSelect.disabled = isDefault;
      }

      showMoreButtons.forEach(function (button) {
        const targetSelector = button.getAttribute('data-target');
        const target = targetSelector ? filterForm.querySelector(targetSelector) : null;
        if (!target) {
          return;
        }

        const hasCheckedChild = target.querySelector('.chip-input:checked');
        if (hasCheckedChild) {
          target.classList.remove('d-none');
          target.dataset.collapsed = 'false';
          button.textContent = 'Show less';
          button.setAttribute('aria-expanded', 'true');
        } else {
          const initiallyCollapsed = target.classList.contains('d-none');
          target.dataset.collapsed = initiallyCollapsed ? 'true' : 'false';
          button.textContent = initiallyCollapsed ? 'Show more' : 'Show less';
          button.setAttribute('aria-expanded', initiallyCollapsed ? 'false' : 'true');
        }

        button.addEventListener('click', function () {
          const isCollapsed = target.classList.toggle('d-none');
          target.dataset.collapsed = isCollapsed ? 'true' : 'false';
          button.textContent = isCollapsed ? 'Show more' : 'Show less';
          button.setAttribute('aria-expanded', isCollapsed ? 'false' : 'true');
        });
      });

      function syncRangeValues() {
        if (!rangeMin || !rangeMax || !minValue || !maxValue || !minLabel || !maxLabel) {
          return;
        }

        let minVal = Number(rangeMin.value);
        let maxVal = Number(rangeMax.value);

        if (minVal >= maxVal) {
          if (this === rangeMin) {
            minVal = Math.max(rangeMin.min ? Number(rangeMin.min) : 1, maxVal - 1);
            rangeMin.value = String(minVal);
          } else {
            maxVal = Math.min(rangeMax.max ? Number(rangeMax.max) : 150, minVal + 1);
            rangeMax.value = String(maxVal);
          }
        }

        minValue.textContent = String(minVal);
        maxValue.textContent = String(maxVal);
        minLabel.textContent = String(minVal);
        maxLabel.textContent = String(maxVal);
      }

      [rangeMin, rangeMax].forEach(function (rangeInput) {
        if (!rangeInput) {
          return;
        }
        rangeInput.addEventListener('input', syncRangeValues);
        rangeInput.addEventListener('change', function () {
          scheduleSubmit();
        });
      });
      syncRangeValues();

      chipInputs.forEach(function (input) {
        input.addEventListener('change', function () {
          scheduleSubmit();
        });
      });

      proficiencySelects.forEach(function (select) {
        select.addEventListener('change', function () {
          scheduleSubmit();
        });
      });

      if (searchInput) {
        searchInput.addEventListener('input', function () {
          scheduleSubmit({ delay: 500 });
        });
      }

      if (sortKeySelect) {
        sortKeySelect.addEventListener('change', function () {
          updateSortDirectionState();
          scheduleSubmit();
        });
      }

      if (sortDirectionSelect) {
        sortDirectionSelect.addEventListener('change', function () {
          scheduleSubmit();
        });
      }

      paginationButtons.forEach(function (button) {
        button.addEventListener('click', function () {
          if (!pageInput) return;
          const action = button.getAttribute('data-page-action');
          let currentPage = Number(pageInput.value || '0');
          if (action === 'next') {
            pageInput.value = String(currentPage + 1);
          } else if (action === 'prev') {
            pageInput.value = String(Math.max(0, currentPage - 1));
          }
          scheduleSubmit({ resetPage: false, delay: 0 });
        });
      });

      if (resetButton) {
        resetButton.addEventListener('click', function (e) {
          e.preventDefault();

          if (searchInput) {
            searchInput.value = '';
          }
          chipInputs.forEach(function (input) {
            input.checked = false;
          });
          proficiencySelects.forEach(function (select) {
            select.selectedIndex = 0;
          });

          if (rangeMin) {
            rangeMin.value = rangeMin.getAttribute('data-default') || '15';
          }
          if (rangeMax) {
            rangeMax.value = rangeMax.getAttribute('data-default') || '120';
          }
          syncRangeValues();

          if (sortKeySelect) {
            sortKeySelect.value = 'DEFAULT';
          }
          if (sortDirectionSelect) {
            sortDirectionSelect.value = 'DESC';
          }
          updateSortDirectionState();

          resetPage();
          scheduleSubmit({ delay: 0 });
        });
      }

      const developerCards = Array.from(document.querySelectorAll('.developer-card[data-profile-url]'));
      developerCards.forEach(function (card) {
        card.style.cursor = 'pointer';
        card.addEventListener('click', function (event) {
          if (event.target.closest('button') || event.target.closest('a') || event.target.closest('input')) {
            return;
          }
          const url = card.getAttribute('data-profile-url');
          if (url) {
            window.location.href = url;
          }
        });
      });

      updateSortDirectionState();

    });
  </script>
</body>
</html>

