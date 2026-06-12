# Диаграмма классов (основные слои)

Упрощённая диаграмма ключевых классов backend.

```mermaid
classDiagram
    direction TB

  class GenerationController {
    +create()
    +list()
    +get()
    +retry()
  }
  class CharacterController {
    +create()
    +list()
    +get()
    +update()
    +delete()
    +generate()
  }
  class FavoriteController {
    +list()
  }
  class PortraitController {
    +image()
    +addFavorite()
    +removeFavorite()
  }
  class AuthController {
    +register()
    +login()
    +refresh()
    +logout()
    +me()
  }

  class GenerationService {
    +create()
    +createFromCharacter()
    +list()
    +getById()
    +retry()
  }
  class CharacterService {
    +create()
    +list()
    +getById()
    +update()
    +delete()
    +generatePortrait()
  }
  class FavoriteService {
    +add()
    +remove()
    +list()
  }
  class AuthService {
    +register()
    +login()
    +refresh()
    +logout()
    +me()
  }

  class PromptBuilder {
    +build()
  }
  class PromptStrategy {
    <<interface>>
    +systemFragment()
  }
  class ImageGenerationPipeline {
    +execute()
  }
  class ImageGenerationPort {
    <<interface>>
    +generate()
  }
  class GigaChatImageAdapter {
    +generate()
  }

  class GenerationRequestEntity
  class CharacterEntity
  class PortraitEntity
  class FavoritePortraitEntity
  class UserEntity

  class GenerationRequestRepository
  class CharacterRepository
  class FavoritePortraitRepository
  class UserRepository

  GenerationController --> GenerationService
  CharacterController --> CharacterService
  FavoriteController --> FavoriteService
  PortraitController --> FavoriteService
  PortraitController --> PortraitService
  AuthController --> AuthService

  CharacterService --> GenerationService
  GenerationService --> PromptBuilder
  GenerationService --> GenerationRequestRepository
  CharacterService --> CharacterRepository
  FavoriteService --> FavoritePortraitRepository

  PromptBuilder --> PromptStrategy
  GenerationService ..> GenerationRequestedEvent : publish
  ImageGenerationPipeline --> ImageGenerationPort
  ImageGenerationPipeline --> GenerationRequestRepository
  GigaChatImageAdapter ..|> ImageGenerationPort

  GenerationRequestRepository --> GenerationRequestEntity
  CharacterRepository --> CharacterEntity
  FavoritePortraitRepository --> FavoritePortraitEntity
  UserRepository --> UserEntity

  GenerationRequestEntity --> CharacterEntity
  GenerationRequestEntity --> PortraitEntity
  CharacterEntity --> PortraitEntity : lastPortrait
```

## Пакеты

```
ddperson.api          — контроллеры, DTO, мапперы
ddperson.service      — сценарии использования
ddperson.generation   — промпты и pipeline
ddperson.gigachat     — интеграция GigaChat
ddperson.persistence  — JPA-сущности и репозитории
ddperson.security     — JWT, cookies, фильтры
ddperson.redis        — rate limit, blacklist
ddperson.storage      — файлы JPG
```
