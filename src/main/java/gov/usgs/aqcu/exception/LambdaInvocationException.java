package gov.usgs.aqcu.exception;

public class LambdaInvocationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LambdaInvocationException() {
        super();
    }

    public LambdaInvocationException(String message) {
        super(message);
    }

    public LambdaInvocationException(Throwable cause) {
        super(cause);
    }
}