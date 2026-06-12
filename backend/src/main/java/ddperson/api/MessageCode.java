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
    public static final String ERROR_INVALID_CREDENTIALS = "error.invalid_credentials";
    public static final String ERROR_EMAIL_TAKEN = "error.email_taken";
    public static final String ERROR_REFRESH_TOKEN_INVALID = "error.refresh_token_invalid";
    public static final String ERROR_FAVORITE_ALREADY_EXISTS = "error.favorite_already_exists";
    public static final String ERROR_GENERATION_IN_PROGRESS = "error.generation_in_progress";

    public static final String SUCCESS_HEALTH_OK = "success.health.ok";
    public static final String SUCCESS_REGISTER = "success.register";
    public static final String SUCCESS_LOGIN = "success.login";
    public static final String SUCCESS_LOGOUT = "success.logout";
    public static final String SUCCESS_TOKEN_REFRESHED = "success.token_refreshed";
    public static final String SUCCESS_PING = "success.ping";
    public static final String SUCCESS_FAVORITE_ADDED = "success.favorite.added";
    public static final String SUCCESS_FAVORITE_REMOVED = "success.favorite.removed";
}
