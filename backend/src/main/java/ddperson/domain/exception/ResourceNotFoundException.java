package ddperson.domain.exception;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException() {
        super(ErrorCode.NOT_FOUND);
    }
}
