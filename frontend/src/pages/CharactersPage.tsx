import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ApiError } from '@/api/client';
import { charactersApi } from '@/api/characters';
import { Button } from '@/components/common/Button';
import { CharacterCard } from '@/components/character/CharacterCard';
import { CharacterForm } from '@/components/character/CharacterForm';
import { ErrorAlert } from '@/components/common/ErrorAlert';
import { Loader } from '@/components/common/Loader';
import { PageHeader } from '@/components/common/PageHeader';
import { DEFAULT_GENERATION_VALUES } from '@/constants/enums';
import type { CharacterFormValues, CharacterSummary } from '@/types/api';

export function CharactersPage() {
  const [characters, setCharacters] = useState<CharacterSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showForm, setShowForm] = useState(false);
  const [values, setValues] = useState<CharacterFormValues>({
    name: '',
    ...DEFAULT_GENERATION_VALUES,
  });
  const [submitting, setSubmitting] = useState(false);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const page = await charactersApi.list();
      setCharacters(page.content);
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка загрузки');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
  }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!values.name.trim() || !values.characterDescription.trim()) {
      setError('Заполните имя и описание персонажа');
      return;
    }
    setSubmitting(true);
    setError(null);
    try {
      await charactersApi.create(values);
      setShowForm(false);
      setValues({ name: '', ...DEFAULT_GENERATION_VALUES });
      await load();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Не удалось создать персонажа');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <PageHeader
        title="Мои персонажи"
        subtitle="Шаблоны для быстрой повторной генерации портретов"
        actions={
          <Button type="button" onClick={() => setShowForm((v) => !v)}>
            {showForm ? 'Отмена' : 'Новый персонаж'}
          </Button>
        }
      />

      {error && <ErrorAlert message={error} />}

      {showForm && (
        <form onSubmit={handleCreate} className="panel" style={{ marginBottom: '1rem' }}>
          <CharacterForm values={values} onChange={setValues} />
          <div className="btn-row">
            <Button type="submit" disabled={submitting}>
              {submitting ? 'Сохранение…' : 'Сохранить персонажа'}
            </Button>
          </div>
        </form>
      )}

      {loading ? (
        <Loader />
      ) : characters.length === 0 ? (
        <div className="panel empty-state">
          Персонажей пока нет. Создайте шаблон или{' '}
          <Link to="/generate">сгенерируйте портрет</Link> и сохраните параметры вручную.
        </div>
      ) : (
        <div className="card-grid">
          {characters.map((character) => (
            <CharacterCard key={character.id} character={character} />
          ))}
        </div>
      )}
    </>
  );
}
