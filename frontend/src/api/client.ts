import type { ApiErrorResponse } from '@/types/api';
import { ApiError, NetworkError } from '@/api/errors';

export { ApiError, NetworkError } from '@/api/errors';

const JSON_HEADERS = { 'Content-Type': 'application/json', Accept: 'application/json' };

async function parseBody<T>(response: Response): Promise<T | undefined> {
  if (response.status === 204) {
    return undefined;
  }
  const text = await response.text();
  if (!text) {
    return undefined;
  }
  return JSON.parse(text) as T;
}

export async function apiRequest<T>(path: string, init: RequestInit = {}): Promise<T> {
  let response: Response;
  try {
    response = await fetch(path, {
      credentials: 'include',
      ...init,
      headers: {
        ...JSON_HEADERS,
        ...init.headers,
      },
    });
  } catch {
    throw new NetworkError(
      'Не удалось связаться с сервером. Проверьте, что backend запущен на порту 8080 (mvn spring-boot:run или ./run.sh).',
    );
  }

  if (!response.ok) {
    const body = await parseBody<ApiErrorResponse>(response).catch(() => null);
    throw new ApiError(response.status, body ?? null);
  }

  return (await parseBody<T>(response)) as T;
}

export const api = {
  get: <T>(path: string) => apiRequest<T>(path),
  post: <T>(path: string, body?: unknown) =>
    apiRequest<T>(path, { method: 'POST', body: body ? JSON.stringify(body) : undefined }),
  put: <T>(path: string, body: unknown) =>
    apiRequest<T>(path, { method: 'PUT', body: JSON.stringify(body) }),
  delete: <T>(path: string) => apiRequest<T>(path, { method: 'DELETE' }),
};

export function portraitImageUrl(portraitId: string): string {
  return `/api/v1/portraits/${portraitId}/image`;
}

export function formatDate(value: string | null | undefined): string {
  if (!value) {
    return '—';
  }
  return new Intl.DateTimeFormat('ru-RU', {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value));
}
