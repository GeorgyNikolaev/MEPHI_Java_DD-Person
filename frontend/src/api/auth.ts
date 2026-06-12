import { api } from '@/api/client';
import type { MessageResponse, User } from '@/types/api';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  displayName: string;
}

export const authApi = {
  me: () => api.get<User>('/api/v1/auth/me'),
  login: (body: LoginRequest) => api.post<User>('/api/v1/auth/login', body),
  register: (body: RegisterRequest) => api.post<User>('/api/v1/auth/register', body),
  logout: () => api.post<MessageResponse>('/api/v1/auth/logout'),
  refresh: () => api.post<User>('/api/v1/auth/refresh'),
};
