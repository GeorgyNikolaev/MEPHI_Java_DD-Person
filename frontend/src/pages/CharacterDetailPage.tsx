import { useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { ApiError, formatDate } from '@/api/client';
import { charactersApi } from '@/api/characters';
import { Button } from '@/components/common/Button';
import { CharacterForm } from '@/components/character/CharacterForm';
import { ErrorAlert } from '@/components/common/ErrorAlert';
import { Loader } from '@/components/common/Loader';
import { PageHeader } from '@/components/common/PageHeader';
import { PortraitViewer } from '@/components/portrait/PortraitViewer';
import type { CharacterDetail, CharacterFormValues } from '@/types/api';

function toFormValues(character: CharacterDetail): CharacterFormValues {
  return {
    name: character.name,
    characterDescription: character.description,
    roleArchetype: character.roleArchetype.code as CharacterFormValues['roleArchetype'],
    universeStyle: character.universeStyle.code as CharacterFormValues['universeStyle'],
    seriousnessLevel: character.seriousnessLevel,
    expressivenessLevel: character.expressivenessLevel,
    mood: (character.mood?.code ?? '') as CharacterFormValues['mood'],
  };
}

export function CharacterDetailPage() {
  const { id = '' } = useParams();
  const navigate = useNavigate();
  const [character, setCharacter] = useState<CharacterDetail | null>(null);
  const [values, setValues] = useState<CharacterFormValues | null>(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await charactersApi.get(id);
      setCharacter(data);
      setValues(toFormValues(data));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Персонаж не найден');
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    void load();
  }, [load]);

  const handleGenerate = async () => {
    setBusy(true);
    setError(null);
    try {
      const created = await charactersApi.generate(id);
      navigate(`/generations/${created.id}`);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Не удалось запустить генерацию');
    } finally {
      setBusy(false);
    }
  };

  const handleSave = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!values) return;
    setBusy(true);
    setError(null);
    try {
      const updated = await charactersApi.update(id, values);
      setCharacter(updated);
      setValues(toFormValues(updated));
      setEditing(false);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Не удалось сохранить');
    } finally {
      setBusy(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Удалить персонажа? Шаблон будет удалён, история генераций сохранится.')) {
      return;
    }
    setBusy(true);
    try {
      await charactersApi.delete(id);
      navigate('/characters');
    } catch (err) {
      setError(err instanceof ApiError ? err.message : 'Не удалось удалить');
      setBusy(false);
    }
  };

  if (loading) return <Loader />;
  if (!character || !values) return <ErrorAlert message={error ?? 'Персонаж не найден'} />;

  return (
    <>
      <PageHeader
        title={character.name}
        subtitle={`${character.roleArchetype.labelRu} · ${character.universeStyle.labelRu}`}
        actions={
          <div className="btn-row" style={{ marginTop: 0 }}>
            <Button type="button" onClick={() => void handleGenerate()} disabled={busy}>
              Сгенерировать портрет
            </Button>
            <Button
              type="button"
              variant="secondary"
              onClick={() => setEditing((v) => !v)}
              disabled={busy}
            >
              {editing ? 'Отмена' : 'Редактировать'}
            </Button>
          </div>
        }
      />

      {error && <ErrorAlert message={error} />}

      <div className="grid-2">
        <div className="panel">
          {character.lastPortrait ? (
            <PortraitViewer portraitId={character.lastPortrait.id} alt={character.name} />
          ) : (
            <div className="portrait-placeholder">Последний портрет ещё не создан</div>
          )}
          <dl className="meta-list" style={{ marginTop: '1rem' }}>
            <dt>Создан</dt>
            <dd>{formatDate(character.createdAt)}</dd>
            <dt>Обновлён</dt>
            <dd>{formatDate(character.updatedAt)}</dd>
          </dl>
        </div>

        <div>
          {editing ? (
            <form onSubmit={handleSave} className="panel">
              <CharacterForm values={values} onChange={setValues} />
              <div className="btn-row">
                <Button type="submit" disabled={busy}>
                  Сохранить изменения
                </Button>
                <Button type="button" variant="danger" onClick={() => void handleDelete()} disabled={busy}>
                  Удалить
                </Button>
              </div>
            </form>
          ) : (
            <div className="panel">
              <h2>Параметры шаблона</h2>
              <dl className="meta-list">
                <dt>Описание</dt>
                <dd>{character.description}</dd>
                <dt>Настроение</dt>
                <dd>{character.mood?.labelRu ?? '—'}</dd>
                <dt>Серьёзность / выразительность</dt>
                <dd>
                  {character.seriousnessLevel}/10 · {character.expressivenessLevel}/10
                </dd>
              </dl>
              <p className="field-hint">
                <Link to="/history">История генераций</Link> этого персонажа доступна в общем списке.
              </p>
            </div>
          )}
        </div>
      </div>
    </>
  );
}
