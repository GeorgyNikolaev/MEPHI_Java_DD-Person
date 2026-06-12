import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { formatDate } from '@/api/client';
import { formatErrorMessage } from '@/api/errors';
import { generationsApi } from '@/api/generations';
import { Button } from '@/components/common/Button';
import { ErrorAlert } from '@/components/common/ErrorAlert';
import { Loader } from '@/components/common/Loader';
import { PageHeader } from '@/components/common/PageHeader';
import { StatusBadge } from '@/components/common/StatusBadge';
import { GENERATION_STATUSES } from '@/constants/enums';
import type { GenerationStatus, GenerationSummary } from '@/types/api';

export function HistoryPage() {
  const [items, setItems] = useState<GenerationSummary[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [status, setStatus] = useState<GenerationStatus | ''>('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [deletingId, setDeletingId] = useState<string | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await generationsApi.list({
        page,
        size: 15,
        status: status || undefined,
      });
      setItems(result.content);
      setTotalPages(result.totalPages);
    } catch (err) {
      setError(formatErrorMessage(err, 'Ошибка загрузки'));
    } finally {
      setLoading(false);
    }
  }, [page, status]);

  useEffect(() => {
    void load();
  }, [load]);

  const handleDelete = async (item: GenerationSummary) => {
    if (item.status === 'PENDING' || item.status === 'PROCESSING') {
      setError('Нельзя удалить запрос, пока выполняется генерация');
      return;
    }
    if (!window.confirm('Удалить этот запрос генерации? Портрет и запись в избранном также будут удалены.')) {
      return;
    }
    setDeletingId(item.id);
    setError(null);
    try {
      await generationsApi.delete(item.id);
      await load();
    } catch (err) {
      setError(formatErrorMessage(err, 'Не удалось удалить'));
    } finally {
      setDeletingId(null);
    }
  };

  return (
    <>
      <PageHeader title="История генераций" subtitle="Все запросы и их статусы" />

      <div className="filter-row">
        <select
          value={status}
          onChange={(e) => {
            setPage(0);
            setStatus(e.target.value as GenerationStatus | '');
          }}
        >
          <option value="">Все статусы</option>
          {GENERATION_STATUSES.map((s) => (
            <option key={s.code} value={s.code}>
              {s.labelRu}
            </option>
          ))}
        </select>
      </div>

      {error && <ErrorAlert message={error} />}

      <div className="panel">
        {loading ? (
          <Loader />
        ) : items.length === 0 ? (
          <div className="empty-state">Запросов не найдено</div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Создан</th>
                  <th>Завершён</th>
                  <th>Статус</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {items.map((item) => {
                  const inProgress = item.status === 'PENDING' || item.status === 'PROCESSING';
                  return (
                    <tr key={item.id}>
                      <td>{formatDate(item.createdAt)}</td>
                      <td>{formatDate(item.completedAt)}</td>
                      <td>
                        <StatusBadge status={item.status} label={item.statusLabel} />
                      </td>
                      <td>
                        <div className="favorite-card-actions">
                          <Link to={`/generations/${item.id}`}>Открыть</Link>
                          {!inProgress && (
                            <Button
                              type="button"
                              variant="ghost"
                              disabled={deletingId === item.id}
                              onClick={() => void handleDelete(item)}
                            >
                              Удалить
                            </Button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        )}

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
      </div>
    </>
  );
}
