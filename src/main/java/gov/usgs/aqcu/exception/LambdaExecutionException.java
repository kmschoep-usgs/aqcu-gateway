package gov.usgs.aqcu.exception;

/**
 * This Exception represents the situation where an error occurred within the Lambda
 * function itself. When this error is thrown it means that the workflow for executing
 * the Lambda function worked correctly (we were able to call it successfully from the
 * SDK and we got a response), but the Lambda itself encountered an error while running
 * and returned an error response.
 * 
 * When this exception is encountered it means that the Gateway service did what it was
 * supposed to do correctly, but something happened in the downstream Lambda that caused
 * it to fail. This means we should not consider this exception as an error that occured
 * within the Gateway service itself.
 */
public class LambdaExecutionException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LambdaExecutionException() {
        super();
    }

    public LambdaExecutionException(String message) {
        super(message);
    }
}