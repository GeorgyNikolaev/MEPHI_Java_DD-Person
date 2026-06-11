package ddperson.api;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * Доступ к текстам HTTP-ответов из файлов i18n.
 */
@Component
public class ApiMessages {

    private final MessageSource messageSource;

    public ApiMessages(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public String get(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
