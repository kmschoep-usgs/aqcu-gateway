package gov.usgs.aqcu.exception;

public class LambdaExecutionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LambdaExecutionException() {
        super();
    }

    public LambdaExecutionException(String message) {
        super(message);
    }
}