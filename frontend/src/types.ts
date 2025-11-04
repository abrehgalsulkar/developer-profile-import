export interface DeveloperProfile {
  id: number;
  firstName: string;
  lastName: string;
  designation: string | null;
  verified: boolean;
  hourlyRate: number | null;
  experienceYears: number | null;
  totalProjectCompletion: number | null;
  totalWorkedHours: number | null;
  averageRating: number | null;
  totalReview: number | null;
  jobTitle: string | null;
  profilePictureUrl: string | null;
  introductionVideoUrl: string | null;
  resumeUrl: string | null;
  about: string | null;
  permanentAddress: string | null;
  temporaryAddress: string | null;
  availabilities: string[];
  workLocations: string[];
  technologies: string[];
  languages: LanguageSkill[];
}

export interface LanguageSkill {
  language: string | null;
  proficiency: string | null;
}

export interface DeveloperListResponse {
  items: DeveloperProfile[];
  total: number;
  page: number;
  size: number;
}

export interface FilterOption {
  label: string;
  value: string;
}

export interface FilterMetadata {
  technologies: FilterOption[];
  experienceRanges: string[];
  projectCompletionRanges: string[];
  workLocations: FilterOption[];
  availabilities: FilterOption[];
  languages: FilterOption[];
  languageProficiencies: FilterOption[];
}

export type SortMetric = 'projects' | 'experience' | 'hourlyRate' | 'verified';
export type SortDirection = 'asc' | 'desc' | 'default';

export interface Filters {
  keyword: string;
  technologies: string[];
  experienceRanges: string[];
  projectCompletionRanges: string[];
  workLocations: string[];
  availabilities: string[];
  languages: string[];
  languageProficiency?: string;
  hourlyMin?: number;
  hourlyMax?: number;
}
