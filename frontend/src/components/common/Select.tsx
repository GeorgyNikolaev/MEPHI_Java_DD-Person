import type { SelectHTMLAttributes } from 'react';
import type { EnumOption } from '@/types/api';

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label: string;
  options: EnumOption[];
  emptyOption?: string;
}

export function Select({ label, options, emptyOption, id, ...props }: SelectProps) {
  const fieldId = id ?? props.name;
  return (
    <div className="field">
      <label htmlFor={fieldId}>{label}</label>
      <select id={fieldId} {...props}>
        {emptyOption && <option value="">{emptyOption}</option>}
        {options.map((option) => (
          <option key={option.code} value={option.code}>
            {option.labelRu}
          </option>
        ))}
      </select>
    </div>
  );
}
