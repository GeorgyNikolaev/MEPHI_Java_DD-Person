import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ApiError } from '@/api/client';
import { generationsApi } from '@/api/generations';
import { Button } from '@/components/common/Button';
import { ErrorAlert } from '@/components/common/ErrorAlert';
import { PageHeader } from '@/components/common/PageHeader';
import { GenerationParametersFields } from '@/components/generation/GenerationParametersFields';
import { ParametersPreview } from '@/components/generation/PromptPreview';
import { DEFAULT_GENERATION_VALUES } from '@/constants/enums';
import type { GenerationFormValues } from '@/types/api';

export function GeneratePage() {
  const navigate = useNavigate();
  const [values, setValues] = useState<GenerationFormValues>({ ...DEFAULT_GENERATION_VALUES });
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!values.characterDescription.trim()) {
      setError('Укажите описание персонажа');
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      const created = await generationsApi.create(values);
      navigate(`/generations/${created.id}`);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Не удалось создать запрос');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <PageHeader
        title="Новая генерация"
        subtitle="Заполните параметры — промпт соберётся автоматически на сервере"
      />
      {error && <ErrorAlert message={error} />}
      <div className="grid-2">
        <form onSubmit={handleSubmit} className="panel">
          <GenerationParametersFields values={values} onChange={setValues} />
          <div className="btn-row">
            <Button type="submit" disabled={submitting}>
              {submitting ? 'Отправка…' : 'Сгенерировать портрет'}
            </Button>
          </div>
        </form>
        <ParametersPreview values={values} />
      </div>
    </>
  );
}
