import { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { formatDate } from '@/api/client';
import { formatErrorMessage } from '@/api/errors';
import { favoritesApi } from '@/api/favorites';
import { Button } from '@/components/common/Button';
import { ErrorAlert } from '@/components/common/ErrorAlert';
import { Loader } from '@/components/common/Loader';
import { PageHeader } from '@/components/common/PageHeader';
import { PortraitViewer } from '@/components/portrait/PortraitViewer';
import type { FavoritePortrait } from '@/types/api';

export function FavoritesPage() {
  const navigate = useNavigate();
  const [items, setItems] = useState<FavoritePortrait[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [busyId, setBusyId] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await favoritesApi.list(page, 12);
      setItems(result.content);
      setTotalPages(result.totalPages);
    } catch (err) {
      setError(formatErrorMessage(err, 'Ошибка загрузки'));
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void load();
  }, [page]);

  const handleRemove = async (e: React.MouseEvent, portraitId: string) => {
    e.preventDefault();
    e.stopPropagation();
    setBusyId(portraitId);
    setError(null);
    try {
      await favoritesApi.remove(portraitId);
      await load();
    } catch (err) {
      setError(formatErrorMessage(err, 'Не удалось удалить'));
    } finally {
      setBusyId(null);
    }
  };

  const openGeneration = (item: FavoritePortrait) => {
    if (item.generationId) {
      navigate(`/generations/${item.generationId}`);
    }
  };

  return (
    <>
      <PageHeader title="Избранное" subtitle="Сохранённые понравившиеся портреты" />
      {error && <ErrorAlert message={error} />}

      {loading ? (
        <Loader />
      ) : items.length === 0 ? (
        <div className="panel empty-state">
          Избранных портретов пока нет. Добавьте понравившийся результат из{' '}
          <Link to="/history">истории генераций</Link>.
        </div>
      ) : (
        <>
          <div className="card-grid">
            {items.map((item) => (
              <div
                key={item.id}
                className={`character-card${item.generationId ? ' favorite-card-link' : ''}`}
                onClick={() => openGeneration(item)}
                onKeyDown={(e) => {
                  if (item.generationId && (e.key === 'Enter' || e.key === ' ')) {
                    e.preventDefault();
                    openGeneration(item);
                  }
                }}
                role={item.generationId ? 'link' : undefined}
                tabIndex={item.generationId ? 0 : undefined}
              >
                <PortraitViewer portraitId={item.portrait.id} alt={item.characterDescription ?? 'Портрет'} />
                <div style={{ marginTop: '0.75rem' }}>
                  <p style={{ marginBottom: '0.35rem' }}>
                    {item.characterDescription ?? 'Без описания'}
                  </p>
                  <div className="character-meta">
                    {item.roleArchetype?.labelRu}
                    {item.universeStyle ? ` · ${item.universeStyle.labelRu}` : ''}
                  </div>
                  {item.characterName && item.characterId && (
                    <div className="character-meta">
                      <Link
                        to={`/characters/${item.characterId}`}
                        onClick={(e) => e.stopPropagation()}
                      >
                        {item.characterName}
                      </Link>
                    </div>
                  )}
                  <div className="character-meta">Добавлено: {formatDate(item.favoritedAt)}</div>
                  <div className="favorite-card-actions">
                    {item.generationId && (
                      <Link
                        to={`/generations/${item.generationId}`}
                        onClick={(e) => e.stopPropagation()}
                      >
                        Открыть
                      </Link>
                    )}
                    <Button
                      type="button"
                      variant="ghost"
                      disabled={busyId === item.portrait.id}
                      onClick={(e) => void handleRemove(e, item.portrait.id)}
                    >
                      Убрать
                    </Button>
                  </div>
                </div>
              </div>
            ))}
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <Button
                type="button"
                variant="secondary"
                disabled={page === 0}
                onClick={() => setPage((p) => p - 1)}
              >
                Назад
              </Button>
              <span>
                Страница {page + 1} из {totalPages}
              </span>
              <Button
                type="button"
                variant="secondary"
                disabled={page >= totalPages - 1}
                onClick={() => setPage((p) => p + 1)}
              >
                Вперёд
              </Button>
            </div>
          )}
        </>
      )}
    </>
  );
}
