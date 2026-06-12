import { useCallback, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { formatDate } from '@/api/client';
import { formatErrorMessage } from '@/api/errors';
import { favoritesApi } from '@/api/favorites';
import { generationsApi } from '@/api/generations';
import { Button } from '@/components/common/Button';
import { ErrorAlert } from '@/components/common/ErrorAlert';
import { Input } from '@/components/common/Input';
import { Loader } from '@/components/common/Loader';
import { Modal } from '@/components/common/Modal';
import { PageHeader } from '@/components/common/PageHeader';
import { StatusBadge } from '@/components/common/StatusBadge';
import { PromptPreview } from '@/components/generation/PromptPreview';
import { PortraitViewer } from '@/components/portrait/PortraitViewer';
import { usePolling } from '@/hooks/usePolling';
import type { GenerationDetail } from '@/types/api';

export function GenerationDetailPage() {
  const { id = '' } = useParams();
  const navigate = useNavigate();
  const [actionError, setActionError] = useState<string | null>(null);
  const [favoriteMessage, setFavoriteMessage] = useState<string | null>(null);
  const [characterMessage, setCharacterMessage] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [characterName, setCharacterName] = useState('');

  const fetcher = useCallback(() => generationsApi.get(id), [id]);
  const shouldPoll = useCallback(
    (data: GenerationDetail) => data.status === 'PENDING' || data.status === 'PROCESSING',
    [],
  );

  const { data, loading, error, refresh } = usePolling(fetcher, shouldPoll);

  const handleRetry = async () => {
    setBusy(true);
    setActionError(null);
    try {
      const retried = await generationsApi.retry(id);
      navigate(`/generations/${retried.id}`);
    } catch (err) {
      setActionError(formatErrorMessage(err, 'Не удалось повторить'));
    } finally {
      setBusy(false);
    }
  };

  const handleFavorite = async () => {
    if (!data?.portrait) return;
    setBusy(true);
    setActionError(null);
    setFavoriteMessage(null);
    try {
      const result = await favoritesApi.add(data.portrait.id);
      setFavoriteMessage(result.message);
    } catch (err) {
      setActionError(formatErrorMessage(err, 'Не удалось добавить в избранное'));
    } finally {
      setBusy(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Удалить этот запрос генерации? Портрет и запись в избранном также будут удалены.')) {
      return;
    }
    setBusy(true);
    setActionError(null);
    try {
      await generationsApi.delete(id);
      navigate('/history');
    } catch (err) {
      setActionError(formatErrorMessage(err, 'Не удалось удалить'));
      setBusy(false);
    }
  };

  const handleCreateCharacter = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!characterName.trim()) {
      setActionError('Укажите имя персонажа');
      return;
    }
    if (!data?.parameters) {
      setActionError('Нет параметров для сохранения персонажа');
      return;
    }

    setBusy(true);
    setActionError(null);
    setCharacterMessage(null);
    try {
      const created = await generationsApi.createCharacter(id, characterName.trim());
      setCreateModalOpen(false);
      setCharacterName('');
      setCharacterMessage(`Персонаж «${created.name}» сохранён${created.lastPortrait ? ' с портретом' : ''}`);
      navigate(`/characters/${created.id}`);
    } catch (err) {
      setActionError(formatErrorMessage(err, 'Не удалось создать персонажа'));
    } finally {
      setBusy(false);
    }
  };

  if (loading && !data) {
    return <Loader />;
  }

  if (error && !data) {
    return <ErrorAlert message={error} />;
  }

  if (!data) {
    return <ErrorAlert message="Запрос не найден" />;
  }

  const isInProgress = data.status === 'PENDING' || data.status === 'PROCESSING';
  const canCreateCharacter = data.parameters != null && !data.characterId;

  return (
    <>
      <PageHeader
        title="Результат генерации"
        subtitle={`Создан ${formatDate(data.createdAt)}`}
        actions={<StatusBadge status={data.status} label={data.statusLabel} />}
      />

      {actionError && <ErrorAlert message={actionError} />}
      {favoriteMessage && <div className="alert alert-info">{favoriteMessage}</div>}
      {characterMessage && <div className="alert alert-info">{characterMessage}</div>}
      {isInProgress && (
        <div className="alert alert-info">
          Генерация выполняется… Страница обновляется автоматически.
        </div>
      )}

      <div className="grid-2">
        <div className="panel">
          {data.portrait ? (
            <PortraitViewer portraitId={data.portrait.id} />
          ) : (
            <div className="portrait-placeholder">
              {isInProgress ? 'Ожидание изображения от GigaChat…' : 'Портрет не создан'}
            </div>
          )}

          <div className="btn-row">
            {data.status === 'FAILED' && (
              <Button type="button" onClick={() => void handleRetry()} disabled={busy}>
                Повторить с теми же параметрами
              </Button>
            )}
            {data.portrait && (
              <Button type="button" variant="secondary" onClick={() => void handleFavorite()} disabled={busy}>
                В избранное
              </Button>
            )}
            {canCreateCharacter && (
              <Button
                type="button"
                variant="secondary"
                onClick={() => setCreateModalOpen(true)}
                disabled={busy}
              >
                Сохранить как персонажа
              </Button>
            )}
            <Button type="button" variant="ghost" onClick={() => void refresh()} disabled={busy}>
              Обновить
            </Button>
            {!isInProgress && (
              <Button type="button" variant="danger" onClick={() => void handleDelete()} disabled={busy}>
                Удалить
              </Button>
            )}
          </div>

          {data.error && (
            <div style={{ marginTop: '1rem' }}>
              <ErrorAlert message={`${data.error.code}: ${data.error.message}`} />
            </div>
          )}
        </div>

        <div>
          {data.characterName && (
            <div className="panel">
              <h2>Персонаж</h2>
              <p>
                <Link to={`/characters/${data.characterId}`}>{data.characterName}</Link>
              </p>
            </div>
          )}

          {data.parameters && (
            <div className="panel">
              <h2>Параметры</h2>
              <dl className="meta-list">
                <dt>Описание</dt>
                <dd>{data.parameters.characterDescription}</dd>
                <dt>Роль</dt>
                <dd>{data.parameters.roleArchetype.labelRu}</dd>
                <dt>Вселенная</dt>
                <dd>{data.parameters.universeStyle.labelRu}</dd>
                <dt>Настроение</dt>
                <dd>{data.parameters.mood?.labelRu ?? '—'}</dd>
                <dt>Серьёзность / выразительность</dt>
                <dd>
                  {data.parameters.seriousnessLevel}/10 · {data.parameters.expressivenessLevel}/10
                </dd>
              </dl>
            </div>
          )}

          <PromptPreview
            systemPrompt={data.builtPrompt?.systemPrompt}
            userPrompt={data.builtPrompt?.userPrompt}
          />
        </div>
      </div>

      <Modal
        title="Сохранить как персонажа"
        open={createModalOpen}
        onClose={() => {
          setCreateModalOpen(false);
          setCharacterName('');
        }}
        footer={
          <div className="btn-row">
            <Button type="submit" form="create-character-form" disabled={busy || !characterName.trim()}>
              {busy ? 'Сохранение…' : 'Создать'}
            </Button>
            <Button
              type="button"
              variant="ghost"
              onClick={() => {
                setCreateModalOpen(false);
                setCharacterName('');
              }}
            >
              Отмена
            </Button>
          </div>
        }
      >
        <form id="create-character-form" onSubmit={handleCreateCharacter} className="form-grid">
          <p className="field-hint">
            Параметры и портрет этой генерации будут сохранены в шаблон. Укажите имя персонажа.
          </p>
          <Input
            label="Имя персонажа"
            name="characterName"
            value={characterName}
            onChange={(e) => setCharacterName(e.target.value)}
            placeholder="Аэlarion"
            required
            autoFocus
            maxLength={150}
          />
        </form>
      </Modal>
    </>
  );
}
