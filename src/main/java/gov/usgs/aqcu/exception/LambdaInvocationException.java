package gov.usgs.aqcu.exception;

/**
 * This Exception represents the situation where an error occurred while the Gateway
 * service was attempting to call a Lambda function, and the Lambda was not successfully
 * invoked. This could mean several things, including that the SDK had trouble hitting
 * the AWS Lambda API, or that the response that the Gateway service got back from the
 * Lambda function was unreadable.
 * 
 * When this exception is encountered it means that the Gateway service encountered an
 * issue when trying to call the Lambda function, but the Lambda function itself worked
 * as it was supposed to (at least, as far as AWS Lambda is aware), or was not invoked
 * at all. This should be considered an error within the Gateway service because there
 * will not be any error logs within the Lambda function itself as it was either not
 * called or ran successfully but something happened to the response in-transit. 
 */
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