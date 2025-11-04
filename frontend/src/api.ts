import type { DeveloperListResponse, FilterMetadata } from './types';

const baseUrl = '/api/developer-profiles';

export async function fetchDevelopers(
  params: Record<string, string | number | undefined | null | string[]>
): Promise<DeveloperListResponse> {
  const query = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value === undefined || value === null || value === '') return;
    if (Array.isArray(value)) {
      value.forEach((v) => v != null && query.append(key, String(v)));
    } else {
      query.append(key, String(value));
    }
  });

  const response = await fetch(`${baseUrl}?${query.toString()}`);
  if (!response.ok) {
    throw new Error(`Failed to fetch developers: ${response.status}`);
  }
  return response.json();
}

export async function fetchFilterMetadata(): Promise<FilterMetadata> {
  const response = await fetch(`${baseUrl}/filters`);
  if (!response.ok) {
    throw new Error(`Failed to fetch filter metadata: ${response.status}`);
  }
  return response.json();
}
