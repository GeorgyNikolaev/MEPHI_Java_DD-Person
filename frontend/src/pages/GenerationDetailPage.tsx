import { useCallback, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { ApiError, formatDate } from '@/api/client';
import { favoritesApi } from '@/api/favorites';
import { generationsApi } from '@/api/generations';
import { Button } from '@/components/common/Button';
import { ErrorAlert } from '@/components/common/ErrorAlert';
import { Loader } from '@/components/common/Loader';
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
  const [busy, setBusy] = useState(false);

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
      setActionError(err instanceof ApiError ? err.message : 'Не удалось повторить');
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
      setActionError(err instanceof ApiError ? err.message : 'Не удалось добавить в избранное');
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

  return (
    <>
      <PageHeader
        title="Результат генерации"
        subtitle={`Создан ${formatDate(data.createdAt)}`}
        actions={<StatusBadge status={data.status} label={data.statusLabel} />}
      />

      {actionError && <ErrorAlert message={actionError} />}
      {favoriteMessage && <div className="alert alert-info">{favoriteMessage}</div>}
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
            <Button type="button" variant="ghost" onClick={() => void refresh()} disabled={busy}>
              Обновить
            </Button>
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
    </>
  );
}
