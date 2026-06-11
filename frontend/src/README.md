# Frontend — структура

## Маршруты (React Router)

| Путь | Страница | Описание |
|------|----------|----------|
| `/login` | LoginPage | Вход |
| `/register` | RegisterPage | Регистрация |
| `/dashboard` | DashboardPage | Главная после входа |
| `/generate` | GeneratePage | Новый запрос (форма параметров + preview промпта) |
| `/generate/:id` | GenerationDetailPage | Статус, результат, повтор |
| `/history` | HistoryPage | История запросов с фильтрами |
| `/characters` | CharactersPage | Сохранённые персонажи |
| `/characters/:id` | CharacterDetailPage | Детали + «сгенерировать снова» |
| `/portraits/:id` | PortraitPage | Просмотр + в избранное |
| `/favorites` | FavoritesPage | Избранные портреты |

## Пакеты

```
api/           — HTTP-клиент (fetch + credentials: include)
components/    — UI-компоненты
  common/      — Button, Input, StatusBadge, Loader
  layout/      — AppShell, Navbar, ProtectedRoute
  generation/  — GenerationForm, PromptPreview, ParameterControls
  portrait/    — PortraitViewer, SavePortraitDialog
  character/   — CharacterCard, CharacterForm
pages/         — Страницы по маршрутам
hooks/         — useAuth, useGeneration, usePolling
store/         — Состояние (TanStack Query + контекст auth)
types/         — TypeScript-типы, зеркало API DTO
routes/        — Конфигурация React Router
styles/        — Глобальные стили, тема D&D
```

## Принципы UI

- Все параметры генерации — элементы управления, не ручной промпт.
- Блок «Что будет отправлено в модель» — read-only preview.
- Визуальное различие статусов: COMPLETED / SAVED / FAILED.
- `credentials: 'include'` для всех API-запросов (JWT в cookies).
