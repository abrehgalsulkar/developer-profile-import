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
              <c:set var="techPreviewCount" value="6" />
              <c:set var="hasAdditionalTechnologies"
                     value="${filterTechnologies != null and filterTechnologies.size() > techPreviewCount}" />
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
                  <c:if test="${hasAdditionalTechnologies}">
                    <button class="btn btn-link btn-sm p-0 small" type="button" data-toggle="show-more" data-target="#techMoreGroup">Show more</button>
                  </c:if>
                </div>
                <c:choose>
                  <c:when test="${empty filterTechnologies}">
                    <p class="text-secondary small mb-0">No technologies available yet.</p>
                  </c:when>
                  <c:otherwise>
                    <div class="chip-group" data-filter-options>
                      <c:forEach var="technology" items="${filterTechnologies}" varStatus="status">
                        <c:if test="${status.index < techPreviewCount}">
                          <div class="chip-option">
                            <input type="checkbox" class="chip-input" name="technologies" value="${technology}" id="tech-${status.index}"<c:if test="${filters.technologies != null and filters.technologies.contains(technology)}"> checked</c:if>>
                            <label for="tech-${status.index}" class="chip chip-action">
                              <c:out value="${technology}" />
                            </label>
                          </div>
                        </c:if>
                      </c:forEach>
                    </div>
                    <c:if test="${hasAdditionalTechnologies}">
                      <div class="chip-group mt-2 d-none" id="techMoreGroup" data-filter-options>
                        <c:forEach var="technology" items="${filterTechnologies}" varStatus="status">
                          <c:if test="${status.index >= techPreviewCount}">
                            <div class="chip-option">
                              <input type="checkbox" class="chip-input" name="technologies" value="${technology}" id="tech-${status.index}"<c:if test="${filters.technologies != null and filters.technologies.contains(technology)}"> checked</c:if>>
                              <label for="tech-${status.index}" class="chip chip-action">
                                <c:out value="${technology}" />
                              </label>
                            </div>
                          </c:if>
                        </c:forEach>
                      </div>
                    </c:if>
                  </c:otherwise>
                </c:choose>
              </section>
              <section class="filter-section">
                <span class="fw-semibold small text-uppercase text-secondary d-block mb-2">Experience Range</span>
                <c:choose>
                  <c:when test="${empty filterExperienceRanges}">
                    <p class="text-secondary small mb-0">No experience ranges available.</p>
                  </c:when>
                  <c:otherwise>
                    <div class="chip-group" data-filter-options>
                      <c:forEach var="rangeLabel" items="${filterExperienceRanges}" varStatus="status">
                        <div class="chip-option">
                          <input type="checkbox" class="chip-input" name="experienceRanges" value="${rangeLabel}" id="exp-${status.index}"<c:if test="${filters.experienceRanges != null and filters.experienceRanges.contains(rangeLabel)}"> checked</c:if>>
                          <label for="exp-${status.index}" class="chip chip-action">
                            <c:out value="${rangeLabel}" />
                          </label>
                        </div>
                      </c:forEach>
                    </div>
                  </c:otherwise>
                </c:choose>
              </section>
              <section class="filter-section">
                <span class="fw-semibold small text-uppercase text-secondary d-block mb-2">Project Completion</span>
                <c:choose>
                  <c:when test="${empty filterProjectCompletions}">
                    <p class="text-secondary small mb-0">No project completion ranges available.</p>
                  </c:when>
                  <c:otherwise>
                    <div class="chip-group" data-filter-options>
                      <c:forEach var="projectLabel" items="${filterProjectCompletions}" varStatus="status">
                        <div class="chip-option">
                          <input type="checkbox" class="chip-input" name="projectCompletions" value="${projectLabel}" id="proj-${status.index}"<c:if test="${filters.projectCompletions != null and filters.projectCompletions.contains(projectLabel)}"> checked</c:if>>
                          <label for="proj-${status.index}" class="chip chip-action">
                            <c:out value="${projectLabel}" />
                          </label>
                        </div>
                      </c:forEach>
                    </div>
                  </c:otherwise>
                </c:choose>
              </section>
              <section class="filter-section">
                <span class="fw-semibold small text-uppercase text-secondary d-block mb-2">Work Locations</span>
                <c:choose>
                  <c:when test="${empty filterWorkLocations}">
                    <p class="text-secondary small mb-0">No work location filters available.</p>
                  </c:when>
                  <c:otherwise>
                    <div class="chip-group" data-filter-options>
                      <c:forEach var="workLocation" items="${filterWorkLocations}" varStatus="status">
                        <div class="chip-option">
                          <input type="checkbox" class="chip-input" name="workLocations" value="${workLocation}" id="loc-${status.index}"<c:if test="${filters.workLocations != null and filters.workLocations.contains(workLocation)}"> checked</c:if>>
                          <label for="loc-${status.index}" class="chip chip-action">
                            <c:out value="${workLocation}" />
                          </label>
                        </div>
                      </c:forEach>
                    </div>
                  </c:otherwise>
                </c:choose>
              </section>
              <section class="filter-section">
                <span class="fw-semibold small text-uppercase text-secondary d-block mb-2">Availabilities</span>
                <c:choose>
                  <c:when test="${empty filterAvailabilities}">
                    <p class="text-secondary small mb-0">No availability filters available.</p>
                  </c:when>
                  <c:otherwise>
                    <div class="chip-group" data-filter-options>
                      <c:forEach var="availability" items="${filterAvailabilities}" varStatus="status">
                        <div class="chip-option">
                          <input type="checkbox" class="chip-input" name="availabilities" value="${availability}" id="avail-${status.index}"<c:if test="${filters.availabilities != null and filters.availabilities.contains(availability)}"> checked</c:if>>
                          <label for="avail-${status.index}" class="chip chip-action">
                            <c:out value="${availability}" />
                          </label>
                        </div>
                      </c:forEach>
                    </div>
                  </c:otherwise>
                </c:choose>
              </section>
              <section class="filter-section">
                <span class="fw-semibold small text-uppercase text-secondary d-block mb-2">Language</span>
                <c:choose>
                  <c:when test="${empty filterLanguages}">
                    <p class="text-secondary small mb-3">No language filters available.</p>
                  </c:when>
                  <c:otherwise>
                    <div class="chip-group mb-3" data-filter-options>
                      <c:forEach var="language" items="${filterLanguages}" varStatus="status">
                        <div class="chip-option">
                          <input type="checkbox" class="chip-input" name="languages" value="${language}" id="lang-${status.index}"<c:if test="${filters.languages != null and filters.languages.contains(language)}"> checked</c:if>>
                          <label for="lang-${status.index}" class="chip chip-action">
                            <c:out value="${language}" />
                          </label>
                        </div>
                      </c:forEach>
                    </div>
                  </c:otherwise>
                </c:choose>
                <select class="form-select form-select-sm" name="languageProficiency" data-filter-select>
                  <option value="">Select proficiency</option>
                  <c:forEach var="proficiency" items="${filterLanguageProficiencies}">
                    <option value="${proficiency}"<c:if test="${filters.languageProficiency eq proficiency}"> selected</c:if>>
                      <c:out value="${proficiency}" />
                    </option>
                  </c:forEach>
                </select>
              </section>
              <c:set var="minRateValue" value="${filters.minHourlyRate != null ? filters.minHourlyRate.intValue() : 15}" />
              <c:set var="maxRateValue" value="${filters.maxHourlyRate != null ? filters.maxHourlyRate.intValue() : 120}" />
              <section class="filter-section rate-highlight">
                <div class="d-flex justify-content-between mb-2">
                  <span class="fw-semibold small text-uppercase text-secondary">Hourly Rate</span>
                  <span class="small text-secondary">$<span id="rateMinValue">${minRateValue}</span> - $<span id="rateMaxValue">${maxRateValue}</span></span>
                </div>
                <div class="range-slider-group">
                  <div class="range-control">
                    <label class="small fw-semibold text-secondary mb-1" for="rateMin">Min hourly rate</label>
                    <input type="range" id="rateMin" name="minHourlyRate" min="1" max="150" value="${minRateValue}" data-default="15">
                  </div>
                  <div class="range-control">
                    <label class="small fw-semibold text-secondary mb-1" for="rateMax">Max hourly rate</label>
                    <input type="range" id="rateMax" name="maxHourlyRate" min="1" max="150" value="${maxRateValue}" data-default="120">
                  </div>
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
              <div
                  id="developerGrid"
                  class="developer-grid"
                  data-current-page="${developersPage.number}"
                  data-total-pages="${developersPage.totalPages}"
                  data-page-size="${developersPage.size}">
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
                  <article class="card developer-card shadow-soft h-100" data-profile-url="<c:url value='/developers/${developer.id}' />">
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
              <div class="text-center text-secondary small py-3 mt-2" data-infinite-sentinel>
                <span>Scroll to load more developers</span>
              </div>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </main>
  <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
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
      const developerGrid = document.getElementById('developerGrid');
      const infiniteScrollSentinel = document.querySelector('[data-infinite-sentinel]');
      let submitTimeout;
      let isLoadingNextPage = false;

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

      function wireDeveloperCard(card) {
        if (!card || card.dataset.cardClickable === 'true') {
          return;
        }
        card.dataset.cardClickable = 'true';
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
      }

      document.querySelectorAll('.developer-card[data-profile-url]').forEach(wireDeveloperCard);

      function hasMorePages() {
        if (!developerGrid) {
          return false;
        }
        const currentPage = Number(developerGrid.dataset.currentPage || '0');
        const totalPages = Number(developerGrid.dataset.totalPages || '1');
        return currentPage < totalPages - 1;
      }

      function setSentinelMessage(message) {
        if (infiniteScrollSentinel) {
          infiniteScrollSentinel.textContent = message;
        }
      }

      function updateSentinelState() {
        if (!infiniteScrollSentinel) {
          return;
        }
        if (!developerGrid || developerGrid.children.length === 0) {
          setSentinelMessage('No developers match your current filters.');
          return;
        }
        if (isLoadingNextPage) {
          setSentinelMessage('Loading more developers…');
          return;
        }
        if (hasMorePages()) {
          setSentinelMessage('Scroll to load more developers');
        } else {
          setSentinelMessage('You have reached the end of the list.');
        }
      }

      updateSentinelState();

      async function loadNextPage() {
        if (!developerGrid || !infiniteScrollSentinel || isLoadingNextPage || !hasMorePages()) {
          return;
        }

        const nextPage = Number(developerGrid.dataset.currentPage || '0') + 1;
        const pageSize = developerGrid.dataset.pageSize || (pageInput ? pageInput.value : '12');
        isLoadingNextPage = true;
        updateSentinelState();

        if (pageInput) {
          pageInput.value = String(nextPage);
        }

        const formData = new FormData(filterForm);
        formData.set('page', String(nextPage));
        formData.set('size', pageSize);
        const params = new URLSearchParams();
        formData.forEach(function (value, key) {
          params.append(key, value);
        });

        try {
          const response = await fetch(filterForm.action + '?' + params.toString(), {
            headers: {
              'X-Requested-With': 'XMLHttpRequest'
            }
          });
          if (!response.ok) {
            throw new Error('Failed to load more developers');
          }
          const html = await response.text();
          const parser = new DOMParser();
          const doc = parser.parseFromString(html, 'text/html');
          const newGrid = doc.getElementById('developerGrid');
          if (!newGrid) {
            throw new Error('Unable to find developer grid in response');
          }
          const newCards = Array.from(newGrid.children);
          newCards.forEach(function (card) {
            developerGrid.appendChild(card);
            wireDeveloperCard(card);
          });
          developerGrid.dataset.currentPage = newGrid.dataset.currentPage || String(nextPage);
          developerGrid.dataset.totalPages = newGrid.dataset.totalPages || developerGrid.dataset.totalPages;
          developerGrid.dataset.pageSize = newGrid.dataset.pageSize || developerGrid.dataset.pageSize;
        } catch (error) {
          console.error(error);
          setSentinelMessage('Unable to load more developers right now.');
        } finally {
          isLoadingNextPage = false;
          updateSentinelState();
        }
      }

      if (developerGrid && infiniteScrollSentinel && 'IntersectionObserver' in window) {
        const observer = new IntersectionObserver(function (entries) {
          entries.forEach(function (entry) {
            if (entry.isIntersecting) {
              loadNextPage();
            }
          });
        }, {
          rootMargin: '200px'
        });
        observer.observe(infiniteScrollSentinel);
      }

      updateSortDirectionState();

    });
  </script>
</body>
</html>

