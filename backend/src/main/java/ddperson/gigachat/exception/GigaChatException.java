package ddperson.gigachat.exception;

public class GigaChatException extends RuntimeException {

    private final Integer httpStatus;
    private final String errorCode;

    public GigaChatException(String message, Integer httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public GigaChatException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = null;
        this.errorCode = "GIGACHAT_ERROR";
    }

    public Integer getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
