import { api } from '@/api/client';
import { toGenerationPayload } from '@/api/generationPayload';
import type {
  CharacterDetail,
  CharacterFormValues,
  CharacterSummary,
  GenerationSummary,
  PageResponse,
} from '@/types/api';

export const charactersApi = {
  create: (values: CharacterFormValues) =>
    api.post<CharacterDetail>('/api/v1/characters', {
      name: values.name,
      description: values.characterDescription,
      ...toGenerationPayload(values),
    }),

  list: (page = 0, size = 20) =>
    api.get<PageResponse<CharacterSummary>>(`/api/v1/characters?page=${page}&size=${size}`),

  get: (id: string) => api.get<CharacterDetail>(`/api/v1/characters/${id}`),

  update: (id: string, values: CharacterFormValues) =>
    api.put<CharacterDetail>(`/api/v1/characters/${id}`, {
      name: values.name,
      description: values.characterDescription,
      ...toGenerationPayload(values),
    }),

  delete: (id: string) => api.delete<void>(`/api/v1/characters/${id}`),

  generate: (id: string) =>
    api.post<GenerationSummary>(`/api/v1/characters/${id}/generations`),
};
