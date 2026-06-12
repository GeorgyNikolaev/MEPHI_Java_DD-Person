# Sequence: персонажи

Шаблоны персонажей отделены от истории генераций: из генерации можно создать персонажа в любой момент; повторная генерация по шаблону обновляет `lastPortrait`.

```mermaid
sequenceDiagram
    actor User as Пользователь
    participant UI as React Frontend
    participant CharAPI as CharacterController
    participant GenAPI as GenerationController
    participant CharSvc as CharacterService
    participant GenSvc as GenerationService
    participant Pipe as ImageGenerationPipeline
    participant DB as PostgreSQL

    Note over User,DB: Сохранить генерацию как персонажа

    User->>UI: «Сохранить как персонажа», вводит имя
    UI->>GenAPI: POST /generations/{id}/character { name }
    GenAPI->>CharSvc: createFromGeneration(id, name)
    CharSvc->>DB: SELECT generation + parameters + portrait
    CharSvc->>DB: INSERT characters (params из генерации)
    CharSvc->>DB: UPDATE characters.last_portrait_id
    Note right of CharSvc: Генерация не привязывается к персонажу
    CharSvc-->>GenAPI: CharacterResponse
    GenAPI-->>UI: 201
    UI-->>User: Переход на /characters/{id}

    Note over User,DB: Повторная генерация по шаблону

    User->>UI: На странице персонажа — «Сгенерировать портрет»
    UI->>CharAPI: POST /characters/{id}/generations
    CharAPI->>CharSvc: generatePortrait(id)
    CharSvc->>GenSvc: createFromCharacter(character)
    GenSvc->>DB: INSERT generation_requests (character_id для pipeline)
    GenSvc-->>CharAPI: 202 + generationId
    CharAPI-->>UI: GenerationSummary
    UI->>GenAPI: polling GET /generations/{id}

    Pipe->>DB: COMPLETED + portrait
    Pipe->>DB: UPDATE characters.last_portrait_id
    Note right of Pipe: character_id на запросе — только для обновления портрета
```

## Участники

| Компонент | Роль |
|-----------|------|
| `CharacterService.createFromGeneration` | Копирует параметры и портрет в новый шаблон |
| `GenerationService.createFromCharacter` | Создаёт запрос с `character_id` для async pipeline |
| `ImageGenerationPipeline` | После успеха обновляет `lastPortrait` персонажа |

## Связанные диаграммы

- [sequence-generation.md](sequence-generation.md) — async pipeline GigaChat
- [sequence-favorites.md](sequence-favorites.md) — избранное
