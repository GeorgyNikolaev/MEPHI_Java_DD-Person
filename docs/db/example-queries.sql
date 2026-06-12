-- Примеры SELECT для демонстрации работы с данными.
-- Аналоги генерирует Spring Data JPA в runtime.

-- 1. Список персонажей пользователя (аналог CharacterRepository.findByUserId...)
SELECT c.id, c.name, c.role_archetype, c.updated_at
FROM characters c
JOIN users u ON u.id = c.user_id
WHERE u.email = 'hero@example.com'
ORDER BY c.updated_at DESC;

-- 2. История генераций со статусом
SELECT gr.id, gr.status, gr.created_at, gr.completed_at
FROM generation_requests gr
JOIN users u ON u.id = gr.user_id
WHERE u.email = 'hero@example.com'
  AND gr.status = 'COMPLETED'
ORDER BY gr.created_at DESC
LIMIT 20;

-- 3. Портрет с параметрами генерации
SELECT p.id AS portrait_id,
       p.storage_path,
       gp.character_description,
       gp.role_archetype
FROM portrait_artifacts p
JOIN generation_requests gr ON gr.id = p.request_id
JOIN generation_parameters gp ON gp.request_id = gr.id
WHERE p.id = '00000000-0000-0000-0000-000000000001';

-- 4. Избранные портреты пользователя
SELECT fp.id, p.id AS portrait_id, fp.created_at AS favorited_at
FROM favorite_portraits fp
JOIN portrait_artifacts p ON p.id = fp.portrait_id
JOIN users u ON u.id = fp.user_id
WHERE u.email = 'hero@example.com'
ORDER BY fp.created_at DESC;

-- 5. Аудит токенов GigaChat за сутки
SELECT call_type, model, total_tokens, created_at
FROM gigachat_api_calls
WHERE created_at >= NOW() - INTERVAL '1 day'
ORDER BY created_at DESC;
