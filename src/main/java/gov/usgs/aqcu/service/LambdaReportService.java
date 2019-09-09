package gov.usgs.aqcu.service;

import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.usgs.aqcu.exception.LambdaExecutionException;
import gov.usgs.aqcu.exception.LambdaInvocationException;

@Service
public class LambdaReportService {
    private final Logger LOG = LoggerFactory.getLogger(LambdaReportService.class);

    private AWSLambdaClientBuilder awsLambdaClientBuilder;

    @Autowired
    public LambdaReportService(AWSLambdaClientBuilder awsLambdaClientBuilder) {
        this.awsLambdaClientBuilder = awsLambdaClientBuilder;
    }

    public String execute(String functionName, String payload) {
        InvokeResult result;
        AWSLambda client = awsLambdaClientBuilder.build();
        InvokeRequest request = new InvokeRequest()
            .withFunctionName(functionName)
            .withPayload(payload);

        result = client.invoke(request);

        String resultString = getResponsePayloadString(result);

        if(result.getFunctionError() != null && !result.getFunctionError().isEmpty()) {
            // An error occurred in the Lambda itself
            throw new LambdaExecutionException(resultString);
        } else if(result.getStatusCode() < 200 || result.getStatusCode() >= 300) {
            // An error occurred trying to hit the Lamba API
            throw new LambdaInvocationException("Lambda API Error: (" + result.getStatusCode() + "):\n" + resultString);
        } else {
            // The Lambda succeeded
            LOG.debug("Lambda '" + functionName + "' succeeded.");
            return resultString;
        }
    }

    protected String getResponsePayloadString(InvokeResult result) {
        try {
            return new String(result.getPayload().array(), "UTF-8");
        } catch(Exception e) {
            throw new LambdaInvocationException(e);
        }
    }
}