export type RoleArchetype =
  | 'BARBARIAN'
  | 'BARD'
  | 'CLERIC'
  | 'DRUID'
  | 'FIGHTER'
  | 'MONK'
  | 'PALADIN'
  | 'RANGER'
  | 'ROGUE'
  | 'SORCERER'
  | 'WARLOCK'
  | 'WIZARD';

export type UniverseStyle =
  | 'FORGOTTEN_REALMS'
  | 'EBERRON'
  | 'RAVENLOFT'
  | 'DARK_SUN'
  | 'PLANESCAPE'
  | 'CUSTOM';

export type Mood =
  | 'HEROIC'
  | 'BROODING'
  | 'WHIMSICAL'
  | 'MENACING'
  | 'NOBLE'
  | 'MISCHIEVOUS';

export type GenerationStatus = 'PENDING' | 'PROCESSING' | 'COMPLETED' | 'FAILED';

export interface EnumOption {
  code: string;
  labelRu: string;
}

export interface User {
  id: string;
  email: string;
  displayName: string;
  message?: string;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  code: string;
  message: string;
  path: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface EnumLabelDto {
  code: string;
  labelRu: string;
}

export interface PortraitSummary {
  id: string;
  imageUrl: string;
  createdAt: string;
}

export interface GenerationParameters {
  characterDescription: string;
  roleArchetype: EnumLabelDto;
  universeStyle: EnumLabelDto;
  seriousnessLevel: number;
  expressivenessLevel: number;
  mood: EnumLabelDto | null;
}

export interface BuiltPrompt {
  systemPrompt: string;
  userPrompt: string;
}

export interface GenerationError {
  code: string;
  message: string;
}

export interface GenerationSummary {
  id: string;
  status: GenerationStatus;
  statusLabel: string;
  createdAt: string;
  completedAt: string | null;
}

export interface GenerationDetail extends GenerationSummary {
  parameters: GenerationParameters | null;
  builtPrompt: BuiltPrompt | null;
  portrait: PortraitSummary | null;
  error: GenerationError | null;
  startedAt: string | null;
}

export interface CharacterSummary {
  id: string;
  name: string;
  roleArchetype: EnumLabelDto;
  universeStyle: EnumLabelDto;
  lastPortrait: PortraitSummary | null;
  updatedAt: string;
}

export interface CharacterDetail extends CharacterSummary {
  description: string;
  seriousnessLevel: number;
  expressivenessLevel: number;
  mood: EnumLabelDto | null;
  createdAt: string;
}

export interface FavoritePortrait {
  id: string;
  portrait: PortraitSummary;
  generationId: string | null;
  characterDescription: string | null;
  roleArchetype: EnumLabelDto | null;
  universeStyle: EnumLabelDto | null;
  favoritedAt: string;
}

export interface GenerationFormValues {
  characterDescription: string;
  roleArchetype: RoleArchetype;
  universeStyle: UniverseStyle;
  seriousnessLevel: number;
  expressivenessLevel: number;
  mood: Mood | '';
}

export interface CharacterFormValues extends GenerationFormValues {
  name: string;
}

export interface MessageResponse {
  message: string;
}
