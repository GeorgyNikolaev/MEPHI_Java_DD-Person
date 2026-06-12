import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { formatDate } from '@/api/client';
import { generationsApi } from '@/api/generations';
import { Loader } from '@/components/common/Loader';
import { PageHeader } from '@/components/common/PageHeader';
import { StatusBadge } from '@/components/common/StatusBadge';
import { useAuth } from '@/hooks/useAuth';
import type { GenerationSummary } from '@/types/api';

export function DashboardPage() {
  const { user } = useAuth();
  const [recent, setRecent] = useState<GenerationSummary[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    void (async () => {
      try {
        const page = await generationsApi.list({ page: 0, size: 5 });
        setRecent(page.content);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  return (
    <>
      <PageHeader
        title={`Добро пожаловать, ${user?.displayName}`}
        subtitle="Создавайте портреты персонажей D&amp;D через GigaChat"
      />

      <div className="dashboard-links">
        <Link to="/generate" className="dashboard-link">
          <strong>Новая генерация</strong>
          <span>Опишите персонажа и запустите создание портрета</span>
        </Link>
        <Link to="/characters" className="dashboard-link">
          <strong>Мои персонажи</strong>
          <span>Шаблоны для быстрой повторной генерации</span>
        </Link>
        <Link to="/favorites" className="dashboard-link">
          <strong>Избранное</strong>
          <span>Сохранённые понравившиеся портреты</span>
        </Link>
        <Link to="/history" className="dashboard-link">
          <strong>История</strong>
          <span>Все запросы генерации и их статусы</span>
        </Link>
      </div>

      <div className="panel" style={{ marginTop: '1.25rem' }}>
        <h2>Последние генерации</h2>
        {loading ? (
          <Loader />
        ) : recent.length === 0 ? (
          <div className="empty-state">Запросов пока нет. Начните с новой генерации.</div>
        ) : (
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Дата</th>
                  <th>Статус</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {recent.map((item) => (
                  <tr key={item.id}>
                    <td>{formatDate(item.createdAt)}</td>
                    <td>
                      <StatusBadge status={item.status} label={item.statusLabel} />
                    </td>
                    <td>
                      <Link to={`/generations/${item.id}`}>Открыть</Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  );
}
