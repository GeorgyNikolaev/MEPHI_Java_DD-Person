package ddperson.gigachat.parser;

import ddperson.gigachat.exception.GigaChatException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class GigaChatImageParser {

    private static final Pattern IMG_PATTERN = Pattern.compile("<img\\s+src=\"([^\"]+)\"");

    public String extractFileId(String content) {
        if (content == null || content.isBlank()) {
            throw new GigaChatException("Пустой ответ GigaChat при генерации изображения", null, "NO_IMAGE_IN_RESPONSE");
        }
        Matcher matcher = IMG_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new GigaChatException("В ответе GigaChat не найден идентификатор изображения", null, "NO_IMAGE_IN_RESPONSE");
    }
}
