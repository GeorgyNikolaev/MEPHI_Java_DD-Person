import type { ApiErrorResponse } from '@/types/api';

export class ApiError extends Error {
  readonly status: number;
  readonly body: ApiErrorResponse | null;

  constructor(status: number, body: ApiErrorResponse | null) {
    super(body?.message ?? `HTTP ${status}`);
    this.name = 'ApiError';
    this.status = status;
    this.body = body;
  }
}

export class NetworkError extends Error {
  constructor(message: string) {
    super(message);
    this.name = 'NetworkError';
  }
}

const SERVER_UNAVAILABLE =
  'Сервер недоступен. Запустите backend (порт 8080) и инфраструктуру: docker compose up -d';

function isProxyOrServerError(status: number, body: unknown): boolean {
  if (status === 502 || status === 503 || status === 504) {
    return true;
  }
  return status >= 500 && body == null;
}

export function formatErrorMessage(err: unknown, fallback: string): string {
  if (err instanceof NetworkError) {
    return err.message;
  }

  if (err instanceof ApiError) {
    if (isProxyOrServerError(err.status, err.body)) {
      return SERVER_UNAVAILABLE;
    }
    if (err.body?.message) {
      return err.body.message;
    }
    if (err.status === 401) {
      return 'Неверный email или пароль';
    }
    return `${fallback} (код ${err.status})`;
  }

  if (err instanceof TypeError) {
    return SERVER_UNAVAILABLE;
  }

  if (err instanceof Error && err.message) {
    return err.message;
  }

  return fallback;
}

export { SERVER_UNAVAILABLE };
