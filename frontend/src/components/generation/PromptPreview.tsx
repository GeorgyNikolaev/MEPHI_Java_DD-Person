import type { GenerationFormValues } from '@/types/api';
import { labelFor, MOODS, ROLE_ARCHETYPES, UNIVERSE_STYLES } from '@/constants/enums';

interface ParametersPreviewProps {
  values: GenerationFormValues;
}

export function ParametersPreview({ values }: ParametersPreviewProps) {
  return (
    <div className="panel">
      <h2>Что будет отправлено в модель</h2>
      <p className="field-hint" style={{ marginBottom: '0.75rem' }}>
        Параметры из формы. Итоговый промпт появится после создания запроса.
      </p>
      <dl className="meta-list">
        <dt>Описание</dt>
        <dd>{values.characterDescription || '—'}</dd>
        <dt>Роль</dt>
        <dd>{labelFor(ROLE_ARCHETYPES, values.roleArchetype)}</dd>
        <dt>Вселенная</dt>
        <dd>{labelFor(UNIVERSE_STYLES, values.universeStyle)}</dd>
        <dt>Настроение</dt>
        <dd>{values.mood ? labelFor(MOODS, values.mood) : '—'}</dd>
        <dt>Серьёзность / выразительность</dt>
        <dd>
          {values.seriousnessLevel}/10 · {values.expressivenessLevel}/10
        </dd>
      </dl>
    </div>
  );
}

interface PromptPreviewProps {
  systemPrompt?: string | null;
  userPrompt?: string | null;
}

export function PromptPreview({ systemPrompt, userPrompt }: PromptPreviewProps) {
  if (!systemPrompt && !userPrompt) {
    return null;
  }

  return (
    <div className="panel">
      <h2>Собранный промпт</h2>
      {systemPrompt && (
        <>
          <p className="field-hint">System</p>
          <div className="prompt-block">{systemPrompt}</div>
        </>
      )}
      {userPrompt && (
        <>
          <p className="field-hint" style={{ marginTop: '0.75rem' }}>
            User
          </p>
          <div className="prompt-block">{userPrompt}</div>
        </>
      )}
    </div>
  );
}
