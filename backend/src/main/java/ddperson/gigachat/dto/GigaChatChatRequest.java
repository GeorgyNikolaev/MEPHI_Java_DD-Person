package ddperson.gigachat.dto;

/**
 * Запрос chat/completions для генерации изображения.
 *
 * @param function_call обязателен для text2image — см. документацию GigaChat:
 *                      https://developers.sber.ru/docs/ru/gigachat/guides/images-generation
 *                      Значение {@code auto} — модель сама вызывает встроенную функцию text2image.
 */
public record GigaChatChatRequest(
        String model,
        java.util.List<GigaChatMessage> messages,
        String function_call
) {
    public record GigaChatMessage(String role, String content) {
    }
}
