import { useEffect, useMemo, useState } from 'react';
import type {
  DeveloperListResponse,
  FilterMetadata,
  Filters,
  SortDirection,
  SortMetric,
} from './types';
import { fetchDevelopers, fetchFilterMetadata } from './api';
import { DeveloperCard } from './components/DeveloperCard';
import { FiltersPanel } from './components/FiltersPanel';
import './App.css';

const DEFAULT_FILTERS: Filters = {
  keyword: '',
  technologies: [],
  experienceRanges: [],
  projectCompletionRanges: [],
  workLocations: [],
  availabilities: [],
  languages: [],
  languageProficiency: undefined,
  hourlyMin: undefined,
  hourlyMax: undefined,
};

function App() {
  const [filters, setFilters] = useState<Filters>(DEFAULT_FILTERS);
  const [sortMetric, setSortMetric] = useState<SortMetric>('projects');
  const [sortDirection, setSortDirection] = useState<SortDirection>('default');
  const [page, setPage] = useState(0);
  const [size] = useState(10);

  const [data, setData] = useState<DeveloperListResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [metadata, setMetadata] = useState<FilterMetadata | null>(null);
  const [metadataError, setMetadataError] = useState<string | null>(null);
  const [metadataLoading, setMetadataLoading] = useState(true);

  useEffect(() => {
    let cancelled = false;
    async function loadMetadata() {
      setMetadataLoading(true);
      setMetadataError(null);
      try {
        const response = await fetchFilterMetadata();
        if (!cancelled) {
          setMetadata(response);
        }
      } catch (err) {
        if (!cancelled) {
          setMetadataError(err instanceof Error ? err.message : 'Failed to load filters');
        }
      } finally {
        if (!cancelled) {
          setMetadataLoading(false);
        }
      }
    }
    loadMetadata();
    return () => {
      cancelled = true;
    };
  }, []);

  const query = useMemo(() => ({
    keyword: filters.keyword,
    technology: filters.technologies,
    experienceRange: filters.experienceRanges,
    projectCompletionRange: filters.projectCompletionRanges,
    workLocation: filters.workLocations,
    availability: filters.availabilities,
    language: filters.languages,
    languageProficiency: filters.languageProficiency,
    hourlyMin: filters.hourlyMin,
    hourlyMax: filters.hourlyMax,
    page,
    size,
    sort: sortMetric,
    direction: sortDirection,
  }), [filters, page, size, sortMetric, sortDirection]);

  useEffect(() => {
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError(null);
      try {
        const response = await fetchDevelopers(query);
        if (!cancelled) {
          setData(response);
        }
      } catch (err) {
        if (!cancelled) {
          setError(err instanceof Error ? err.message : 'Unknown error');
        }
      } finally {
        if (!cancelled) {
          setLoading(false);
        }
      }
    }

    load();

    return () => {
      cancelled = true;
    };
  }, [query]);

  function handleFiltersChange(next: Filters) {
    setFilters(next);
    setPage(0);
  }

  function handlePrev() {
    setPage((p) => Math.max(0, p - 1));
  }

  function handleNext() {
    setPage((p) => (data && (p + 1) * size >= data.total ? p : p + 1));
  }

  const filterPanel = metadataLoading ? (
    <aside className="filters-panel">
      <div>Loading filters�</div>
    </aside>
  ) : metadataError ? (
    <aside className="filters-panel">
      <div className="filters-panel__error">{metadataError}</div>
    </aside>
  ) : metadata ? (
    <FiltersPanel
      filters={filters}
      onChange={handleFiltersChange}
      technologyOptions={metadata.technologies}
      experienceRanges={metadata.experienceRanges}
      projectCompletionRanges={metadata.projectCompletionRanges}
      workLocationOptions={metadata.workLocations}
      availabilityOptions={metadata.availabilities}
      languageOptions={metadata.languages}
      languageProficiencyOptions={metadata.languageProficiencies}
    />
  ) : null;

  const totalPages = data ? Math.max(1, Math.ceil(data.total / size)) : 1;

  return (
    <div className="layout">
      {filterPanel}

      <main className="main-content">
        <header className="main-header">
          <div className="main-header__title">
            <h1>Showing {data?.total ?? 0} Developers</h1>
            <p>{loading ? 'Loading�' : error ? error : ''}</p>
          </div>
          <div className="sort-controls">
            <label htmlFor="sortMetric">Sort by</label>
            <div className="sort-controls__inputs">
              <select
                id="sortMetric"
                value={sortMetric}
                onChange={(e) => setSortMetric(e.target.value as SortMetric)}
              >
                <option value="projects">Number of projects</option>
                <option value="experience">Total experience</option>
                <option value="hourlyRate">Hourly price</option>
                <option value="verified">Verified</option>
              </select>
              <select
                value={sortDirection}
                onChange={(e) => setSortDirection(e.target.value as SortDirection)}
              >
                <option value="default">Default</option>
                <option value="asc">Ascending</option>
                <option value="desc">Descending</option>
              </select>
            </div>
          </div>
        </header>

        <section className="developers-grid">
          {data?.items.map((developer) => (
            <DeveloperCard key={developer.id} developer={developer} />
          ))}
          {!loading && data?.items.length === 0 && (
            <div className="empty-state">No developers match the current filters.</div>
          )}
        </section>

        <footer className="pagination">
          <button type="button" onClick={handlePrev} disabled={page === 0 || loading}>
            Previous
          </button>
          <span>
            Page {page + 1} of {totalPages}
          </span>
          <button
            type="button"
            onClick={handleNext}
            disabled={loading || (data ? (page + 1) * size >= data.total : true)}
          >
            Next
          </button>
        </footer>
      </main>
    </div>
  );
}

export default App;
