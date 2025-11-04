import './FiltersPanel.css';
import type { FilterOption, Filters } from '../types';
import { useMemo, type ChangeEvent } from 'react';

interface FiltersPanelProps {
  filters: Filters;
  onChange: (filters: Filters) => void;
  technologyOptions: FilterOption[];
  experienceRanges: string[];
  projectCompletionRanges: string[];
  workLocationOptions: FilterOption[];
  availabilityOptions: FilterOption[];
  languageOptions: FilterOption[];
  languageProficiencyOptions: FilterOption[];
}

export function FiltersPanel({
  filters,
  onChange,
  technologyOptions,
  experienceRanges,
  projectCompletionRanges,
  workLocationOptions,
  availabilityOptions,
  languageOptions,
  languageProficiencyOptions,
}: FiltersPanelProps) {
  const keyword = filters.keyword;

  const selectedTechnologies = useMemo(() => new Set(filters.technologies), [filters.technologies]);
  const selectedExperience = useMemo(() => new Set(filters.experienceRanges), [filters.experienceRanges]);
  const selectedProjects = useMemo(() => new Set(filters.projectCompletionRanges), [filters.projectCompletionRanges]);
  const selectedLocations = useMemo(() => new Set(filters.workLocations), [filters.workLocations]);
  const selectedAvailabilities = useMemo(() => new Set(filters.availabilities), [filters.availabilities]);
  const selectedLanguages = useMemo(() => new Set(filters.languages), [filters.languages]);

  function toggleChip(field: keyof Filters, value: string) {
    const current = new Set(filters[field] as string[]);
    if (current.has(value)) {
      current.delete(value);
    } else {
      current.add(value);
    }
    onChange({ ...filters, [field]: Array.from(current) });
  }

  function handleKeywordChange(e: ChangeEvent<HTMLInputElement>) {
    onChange({ ...filters, keyword: e.target.value });
  }

  function handleHourlyChange(field: 'hourlyMin' | 'hourlyMax', value: string) {
    const parsed = value === '' ? undefined : Number(value);
    onChange({ ...filters, [field]: Number.isNaN(parsed) ? undefined : parsed });
  }

  function handleLanguageProficiencyChange(e: ChangeEvent<HTMLSelectElement>) {
    const value = e.target.value || undefined;
    onChange({ ...filters, languageProficiency: value });
  }

  return (
    <aside className="filters-panel">
      <div className="filters-panel__section">
        <label className="filters-panel__label">Search</label>
        <input
          type="search"
          className="filters-panel__input"
          placeholder="Search developers..."
          value={keyword}
          onChange={handleKeywordChange}
        />
      </div>

      <ChipGroup
        title="Technologies"
        options={technologyOptions}
        selected={selectedTechnologies}
        onToggle={(value) => toggleChip('technologies', value)}
      />

      <ChipGroup
        title="Experience Range"
        options={experienceRanges}
        selected={selectedExperience}
        onToggle={(value) => toggleChip('experienceRanges', value)}
      />

      <ChipGroup
        title="Project Completion Range"
        options={projectCompletionRanges}
        selected={selectedProjects}
        onToggle={(value) => toggleChip('projectCompletionRanges', value)}
      />

      <ChipGroup
        title="Work Locations"
        options={workLocationOptions}
        selected={selectedLocations}
        onToggle={(value) => toggleChip('workLocations', value)}
      />

      <ChipGroup
        title="Availabilities"
        options={availabilityOptions}
        selected={selectedAvailabilities}
        onToggle={(value) => toggleChip('availabilities', value)}
      />

      <ChipGroup
        title="Languages"
        options={languageOptions}
        selected={selectedLanguages}
        onToggle={(value) => toggleChip('languages', value)}
      />

      <div className="filters-panel__section">
        <label className="filters-panel__label">Language Proficiency</label>
        <select
          className="filters-panel__select"
          value={filters.languageProficiency || ''}
          onChange={handleLanguageProficiencyChange}
        >
          <option value="">Any</option>
          {languageProficiencyOptions.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      </div>

      <div className="filters-panel__section">
        <label className="filters-panel__label">Hourly Rate ($1 - $150)</label>
        <div className="filters-panel__range">
          <input
            type="number"
            min={1}
            max={150}
            placeholder="Min"
            className="filters-panel__input"
            value={filters.hourlyMin ?? ''}
            onChange={(e) => handleHourlyChange('hourlyMin', e.target.value)}
          />
          <span>-</span>
          <input
            type="number"
            min={1}
            max={150}
            placeholder="Max"
            className="filters-panel__input"
            value={filters.hourlyMax ?? ''}
            onChange={(e) => handleHourlyChange('hourlyMax', e.target.value)}
          />
        </div>
      </div>
    </aside>
  );
}

interface ChipGroupProps {
  title: string;
  options: string[] | FilterOption[];
  selected: Set<string>;
  onToggle: (value: string) => void;
}

function ChipGroup({ title, options, selected, onToggle }: ChipGroupProps) {
  return (
    <div className="filters-panel__section">
      <span className="filters-panel__label">{title}</span>
      <div className="filters-panel__chips">
        {options.map((option) => {
          const label = typeof option === 'string' ? option : option.label;
          const value = typeof option === 'string' ? option.toLowerCase() : option.value;
          const isActive = selected.has(value);
          return (
            <button
              key={value}
              type="button"
              className={`filters-panel__chip ${isActive ? 'filters-panel__chip--active' : ''}`}
              onClick={() => onToggle(value)}
            >
              {label}
            </button>
          );
        })}
      </div>
    </div>
  );
}
