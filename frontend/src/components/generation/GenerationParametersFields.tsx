import type { GenerationFormValues } from '@/types/api';
import { MOODS, ROLE_ARCHETYPES, UNIVERSE_STYLES } from '@/constants/enums';
import { Select } from '@/components/common/Select';
import { Textarea } from '@/components/common/Textarea';

interface GenerationParametersFieldsProps {
  values: GenerationFormValues;
  onChange: (values: GenerationFormValues) => void;
}

export function GenerationParametersFields({ values, onChange }: GenerationParametersFieldsProps) {
  const update = <K extends keyof GenerationFormValues>(key: K, value: GenerationFormValues[K]) => {
    onChange({ ...values, [key]: value });
  };

  return (
    <div className="form-grid">
      <Textarea
        label="Описание персонажа"
        name="characterDescription"
        value={values.characterDescription}
        onChange={(e) => update('characterDescription', e.target.value)}
        placeholder="Например: эльфийский следопыт со шрамом через бровь, зелёные глаза, кожаная броня..."
        required
      />
      <div className="grid-2">
        <Select
          label="Роль / архетип"
          name="roleArchetype"
          options={ROLE_ARCHETYPES}
          value={values.roleArchetype}
          onChange={(e) => update('roleArchetype', e.target.value as GenerationFormValues['roleArchetype'])}
        />
        <Select
          label="Стиль вселенной"
          name="universeStyle"
          options={UNIVERSE_STYLES}
          value={values.universeStyle}
          onChange={(e) => update('universeStyle', e.target.value as GenerationFormValues['universeStyle'])}
        />
      </div>
      <Select
        label="Настроение портрета"
        name="mood"
        options={MOODS}
        emptyOption="Не выбрано"
        value={values.mood}
        onChange={(e) => update('mood', e.target.value as GenerationFormValues['mood'])}
      />
      <div className="grid-2">
        <div className="field">
          <label htmlFor="seriousnessLevel">Серьёзность: {values.seriousnessLevel}/10</label>
          <div className="range-row">
            <input
              id="seriousnessLevel"
              type="range"
              min={1}
              max={10}
              value={values.seriousnessLevel}
              onChange={(e) => update('seriousnessLevel', Number(e.target.value))}
            />
            <span className="range-value">{values.seriousnessLevel}</span>
          </div>
        </div>
        <div className="field">
          <label htmlFor="expressivenessLevel">Выразительность: {values.expressivenessLevel}/10</label>
          <div className="range-row">
            <input
              id="expressivenessLevel"
              type="range"
              min={1}
              max={10}
              value={values.expressivenessLevel}
              onChange={(e) => update('expressivenessLevel', Number(e.target.value))}
            />
            <span className="range-value">{values.expressivenessLevel}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
