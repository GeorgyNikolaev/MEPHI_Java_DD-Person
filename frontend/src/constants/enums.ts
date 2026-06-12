import type { EnumOption, Mood, RoleArchetype, UniverseStyle } from '@/types/api';

export const ROLE_ARCHETYPES: EnumOption[] = [
  { code: 'BARBARIAN', labelRu: 'Варвар' },
  { code: 'BARD', labelRu: 'Бард' },
  { code: 'CLERIC', labelRu: 'Жрец' },
  { code: 'DRUID', labelRu: 'Друид' },
  { code: 'FIGHTER', labelRu: 'Воин' },
  { code: 'MONK', labelRu: 'Монах' },
  { code: 'PALADIN', labelRu: 'Паладин' },
  { code: 'RANGER', labelRu: 'Следопыт' },
  { code: 'ROGUE', labelRu: 'Плут' },
  { code: 'SORCERER', labelRu: 'Чародей' },
  { code: 'WARLOCK', labelRu: 'Колдун' },
  { code: 'WIZARD', labelRu: 'Волшебник' },
];

export const UNIVERSE_STYLES: EnumOption[] = [
  { code: 'FORGOTTEN_REALMS', labelRu: 'Забытые Королевства' },
  { code: 'EBERRON', labelRu: 'Эберрон' },
  { code: 'RAVENLOFT', labelRu: 'Равенлофт' },
  { code: 'DARK_SUN', labelRu: 'Тёмное Солнце' },
  { code: 'PLANESCAPE', labelRu: 'Планскап' },
  { code: 'CUSTOM', labelRu: 'Свой стиль' },
];

export const MOODS: EnumOption[] = [
  { code: 'HEROIC', labelRu: 'Героическое' },
  { code: 'BROODING', labelRu: 'Мрачное' },
  { code: 'WHIMSICAL', labelRu: 'Причудливое' },
  { code: 'MENACING', labelRu: 'Угрожающее' },
  { code: 'NOBLE', labelRu: 'Благородное' },
  { code: 'MISCHIEVOUS', labelRu: 'Озорное' },
];

export const GENERATION_STATUSES: EnumOption[] = [
  { code: 'PENDING', labelRu: 'В очереди' },
  { code: 'PROCESSING', labelRu: 'Выполняется' },
  { code: 'COMPLETED', labelRu: 'Завершено' },
  { code: 'FAILED', labelRu: 'Ошибка' },
];

export const DEFAULT_GENERATION_VALUES = {
  characterDescription: '',
  roleArchetype: 'RANGER' as RoleArchetype,
  universeStyle: 'FORGOTTEN_REALMS' as UniverseStyle,
  seriousnessLevel: 5,
  expressivenessLevel: 5,
  mood: '' as Mood | '',
};

export function labelFor(options: EnumOption[], code: string): string {
  return options.find((o) => o.code === code)?.labelRu ?? code;
}

export function statusClass(status: string): string {
  switch (status) {
    case 'COMPLETED':
      return 'status-completed';
    case 'FAILED':
      return 'status-failed';
    case 'PROCESSING':
      return 'status-processing';
    default:
      return 'status-pending';
  }
}
