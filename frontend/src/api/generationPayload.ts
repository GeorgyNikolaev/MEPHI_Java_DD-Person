import type { GenerationFormValues } from '@/types/api';

export function toGenerationPayload(values: GenerationFormValues) {
  return {
    characterDescription: values.characterDescription,
    roleArchetype: values.roleArchetype,
    universeStyle: values.universeStyle,
    seriousnessLevel: values.seriousnessLevel,
    expressivenessLevel: values.expressivenessLevel,
    mood: values.mood || null,
  };
}
