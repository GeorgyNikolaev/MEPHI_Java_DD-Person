package ddperson.gigachat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GigaChatChatResponse(
        List<Choice> choices,
        String model,
        Usage usage
) {

    public record Choice(Message message) {
    }

    public record Message(String content) {
    }

    public record Usage(
            @JsonProperty("prompt_tokens") Integer promptTokens,
            @JsonProperty("completion_tokens") Integer completionTokens,
            @JsonProperty("system_tokens") Integer systemTokens,
            @JsonProperty("total_tokens") Integer totalTokens
    ) {
    }

    public String firstContent() {
        if (choices == null || choices.isEmpty() || choices.get(0).message() == null) {
            return null;
        }
        return choices.get(0).message().content();
    }

    public Usage usageOrEmpty() {
        return usage != null ? usage : new Usage(null, null, null, null);
    }
}
