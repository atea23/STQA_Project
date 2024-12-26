package Exceptions;

public class ManagerException extends RuntimeException {
    private int errorCode;

    public ManagerException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
