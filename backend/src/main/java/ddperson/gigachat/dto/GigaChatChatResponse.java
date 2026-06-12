package ddperson.gigachat.dto;

import java.util.List;

public record GigaChatChatResponse(List<Choice> choices) {

    public record Choice(Message message) {
    }

    public record Message(String content) {
    }

    public String firstContent() {
        if (choices == null || choices.isEmpty() || choices.get(0).message() == null) {
            return null;
        }
        return choices.get(0).message().content();
    }
}
