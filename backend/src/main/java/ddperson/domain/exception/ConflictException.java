package ddperson.domain.exception;

public class ConflictException extends BusinessException {

    public ConflictException() {
        super(ErrorCode.CONFLICT);
    }

    public ConflictException(ErrorCode errorCode) {
        super(errorCode);
    }
}
