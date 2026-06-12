package ddperson.api.exception;

import ddperson.api.ApiMessages;
import ddperson.api.dto.ApiErrorResponse;
import ddperson.domain.exception.BusinessException;
import ddperson.domain.exception.ErrorCode;
import ddperson.gigachat.exception.GigaChatException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ApiMessages messages;

    public GlobalExceptionHandler(ApiMessages messages) {
        this.messages = messages;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(
            BusinessException ex, HttpServletRequest request) {
        ErrorCode code = ex.getErrorCode();
        HttpStatus status = mapStatus(code);
        String message = messages.get(code.getMessageKey());
        return ResponseEntity.status(status).body(buildError(status, code.name(), message, request));
    }

    @ExceptionHandler(GigaChatException.class)
    public ResponseEntity<ApiErrorResponse> handleGigaChat(GigaChatException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(
                buildError(HttpStatus.BAD_GATEWAY, ErrorCode.EXTERNAL_SERVICE_ERROR.name(),
                        messages.get(ErrorCode.EXTERNAL_SERVICE_ERROR.getMessageKey()), request));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        return ResponseEntity.badRequest().body(
                buildError(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR.name(),
                        messages.get(ErrorCode.VALIDATION_ERROR.getMessageKey()), request));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest().body(
                buildError(HttpStatus.BAD_REQUEST, ErrorCode.VALIDATION_ERROR.name(), details, request));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Необработанная ошибка", ex);
        return ResponseEntity.internalServerError().body(
                buildError(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_ERROR.name(),
                        messages.get(ErrorCode.INTERNAL_ERROR.getMessageKey()), request));
    }

    private String formatFieldError(FieldError error) {
        String key = error.getDefaultMessage();
        if (key != null && key.startsWith("validation.")) {
            return messages.get(key, error.getArguments());
        }
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private HttpStatus mapStatus(ErrorCode code) {
        return switch (code) {
            case VALIDATION_ERROR -> HttpStatus.BAD_REQUEST;
            case UNAUTHORIZED, INVALID_CREDENTIALS, REFRESH_TOKEN_INVALID -> HttpStatus.UNAUTHORIZED;
            case FORBIDDEN -> HttpStatus.FORBIDDEN;
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT, EMAIL_TAKEN -> HttpStatus.CONFLICT;
            case RATE_LIMIT_EXCEEDED -> HttpStatus.TOO_MANY_REQUESTS;
            case EXTERNAL_SERVICE_ERROR -> HttpStatus.BAD_GATEWAY;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }

    private ApiErrorResponse buildError(
            HttpStatus status, String code, String message, HttpServletRequest request) {
        return new ApiErrorResponse(Instant.now(), status.value(), code, message, request.getRequestURI());
    }
}
