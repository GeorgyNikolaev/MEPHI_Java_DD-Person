import type { CharacterFormValues } from '@/types/api';
import { Input } from '@/components/common/Input';
import { GenerationParametersFields } from '@/components/generation/GenerationParametersFields';

interface CharacterFormProps {
  values: CharacterFormValues;
  onChange: (values: CharacterFormValues) => void;
}

export function CharacterForm({ values, onChange }: CharacterFormProps) {
  return (
    <div className="form-grid">
      <Input
        label="Имя персонажа"
        name="name"
        value={values.name}
        onChange={(e) => onChange({ ...values, name: e.target.value })}
        placeholder="Аэlarion"
        required
        maxLength={150}
      />
      <GenerationParametersFields
        values={values}
        onChange={(generationValues) => onChange({ ...values, ...generationValues })}
      />
    </div>
  );
}
