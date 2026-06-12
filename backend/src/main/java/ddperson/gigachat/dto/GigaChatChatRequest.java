package ddperson.gigachat.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GigaChatChatRequest(
        String model,
        List<GigaChatMessage> messages,
        @JsonProperty("function_call") String functionCall
) {
    public record GigaChatMessage(String role, String content) {
    }
}
