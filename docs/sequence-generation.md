# Sequence: генерация портрета

Асинхронный сценарий от формы до JPG.

```mermaid
sequenceDiagram
    actor User as Пользователь
    participant UI as React Frontend
    participant API as GenerationController
    participant Svc as GenerationService
    participant RL as GenerationRateLimitService
    participant PB as PromptBuilder
    participant DB as PostgreSQL
    participant Bus as ApplicationEvent
    participant Pipe as ImageGenerationPipeline
    participant GC as GigaChat API
    participant FS as PortraitStorage

    User->>UI: Заполняет параметры, «Сгенерировать»
    UI->>API: POST /api/v1/generations (cookies)
    API->>Svc: create(request)
    Svc->>RL: checkAndIncrement(userId)
    RL-->>Svc: OK / 429
    Svc->>PB: build(input)
    PB-->>Svc: systemPrompt + userPrompt
    Svc->>DB: INSERT generation_requests (PENDING)
    Svc-->>API: 202 + id
    API-->>UI: GenerationSummary
    Svc->>Bus: GenerationRequestedEvent

    Note over Bus,Pipe: @Async после commit транзакции

    Bus->>Pipe: execute(requestId)
    Pipe->>DB: UPDATE status = PROCESSING
    Pipe->>GC: OAuth + chat/completions
    GC-->>Pipe: image file id
    Pipe->>GC: download JPG
    GC-->>Pipe: bytes
    Pipe->>FS: save(userId, requestId, bytes)
    Pipe->>DB: INSERT portrait_artifacts, UPDATE COMPLETED

    loop Polling каждые 2.5 с
        UI->>API: GET /generations/{id}
        API->>DB: SELECT request + portrait
        API-->>UI: status, imageUrl
    end
```

## Участники

| Компонент | Роль |
|-----------|------|
| `PromptBuilder` | Strategy + Builder: system/user промпт |
| `GenerationRateLimitService` | Redis: лимит запросов в день/час |
| `GigaChatImageAdapter` | Port/Adapter к GigaChat |
| `PortraitStorageService` | JPG на диск |

## Связанные диаграммы

- [sequence-favorites.md](sequence-favorites.md) — избранное
- [sequence-characters.md](sequence-characters.md) — персонажи и генерация по шаблону
