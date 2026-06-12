import { api } from '@/api/client';
import type { FavoritePortrait, MessageResponse, PageResponse } from '@/types/api';

export const favoritesApi = {
  list: (page = 0, size = 20) =>
    api.get<PageResponse<FavoritePortrait>>(`/api/v1/favorites?page=${page}&size=${size}`),

  add: (portraitId: string) =>
    api.post<MessageResponse>(`/api/v1/portraits/${portraitId}/favorite`),

  remove: (portraitId: string) =>
    api.delete<MessageResponse>(`/api/v1/portraits/${portraitId}/favorite`),
};
