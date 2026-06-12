import { api } from '@/api/client';
import type {
  GenerationDetail,
  GenerationFormValues,
  GenerationStatus,
  GenerationSummary,
  PageResponse,
} from '@/types/api';

function toPayload(values: GenerationFormValues) {
  return {
    characterDescription: values.characterDescription,
    roleArchetype: values.roleArchetype,
    universeStyle: values.universeStyle,
    seriousnessLevel: values.seriousnessLevel,
    expressivenessLevel: values.expressivenessLevel,
    mood: values.mood || null,
  };
}

export const generationsApi = {
  create: (values: GenerationFormValues) =>
    api.post<GenerationSummary>('/api/v1/generations', toPayload(values)),

  list: (params: { status?: GenerationStatus; page?: number; size?: number } = {}) => {
    const search = new URLSearchParams();
    if (params.status) search.set('status', params.status);
    search.set('page', String(params.page ?? 0));
    search.set('size', String(params.size ?? 20));
    return api.get<PageResponse<GenerationSummary>>(`/api/v1/generations?${search}`);
  },

  get: (id: string) => api.get<GenerationDetail>(`/api/v1/generations/${id}`),

  retry: (id: string) => api.post<GenerationSummary>(`/api/v1/generations/${id}/retry`),
};
