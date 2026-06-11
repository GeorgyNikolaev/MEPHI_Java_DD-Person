package ddperson.domain.exception;

import ddperson.api.MessageCode;

/**
 * Коды ошибок API и связь с ключами i18n.
 */
public enum ErrorCode {

    VALIDATION_ERROR(MessageCode.ERROR_VALIDATION),
    UNAUTHORIZED(MessageCode.ERROR_UNAUTHORIZED),
    FORBIDDEN(MessageCode.ERROR_FORBIDDEN),
    NOT_FOUND(MessageCode.ERROR_NOT_FOUND),
    CONFLICT(MessageCode.ERROR_CONFLICT),
    RATE_LIMIT_EXCEEDED(MessageCode.ERROR_RATE_LIMIT),
    EXTERNAL_SERVICE_ERROR(MessageCode.ERROR_EXTERNAL_SERVICE),
    INTERNAL_ERROR(MessageCode.ERROR_INTERNAL),
    INVALID_CREDENTIALS(MessageCode.ERROR_INVALID_CREDENTIALS),
    EMAIL_TAKEN(MessageCode.ERROR_EMAIL_TAKEN),
    REFRESH_TOKEN_INVALID(MessageCode.ERROR_REFRESH_TOKEN_INVALID);

    private final String messageKey;

    ErrorCode(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
