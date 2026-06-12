package ddperson.gigachat.dto;

public record GigaChatChatResult(
        String content,
        String model,
        GigaChatChatResponse.Usage usage
) {
}
