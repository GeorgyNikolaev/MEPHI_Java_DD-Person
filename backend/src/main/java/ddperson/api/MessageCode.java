package ddperson.api;

/**
 * Ключи сообщений в файлах {@code i18n/messages*.properties}.
 */
public final class MessageCode {

    private MessageCode() {
    }

    public static final String ERROR_VALIDATION = "error.validation";
    public static final String ERROR_UNAUTHORIZED = "error.unauthorized";
    public static final String ERROR_FORBIDDEN = "error.forbidden";
    public static final String ERROR_NOT_FOUND = "error.not_found";
    public static final String ERROR_CONFLICT = "error.conflict";
    public static final String ERROR_RATE_LIMIT = "error.rate_limit_exceeded";
    public static final String ERROR_EXTERNAL_SERVICE = "error.external_service";
    public static final String ERROR_INTERNAL = "error.internal";

    public static final String SUCCESS_HEALTH_OK = "success.health.ok";
    public static final String SUCCESS_PING = "success.ping";
}
