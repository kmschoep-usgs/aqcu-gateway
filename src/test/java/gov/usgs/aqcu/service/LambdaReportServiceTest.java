package gov.usgs.aqcu.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.services.lambda.model.InvokeResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import gov.usgs.aqcu.exception.LambdaExecutionException;
import gov.usgs.aqcu.exception.LambdaInvocationException;
import gov.usgs.aqcu.lambda.LambdaFunctionConfig;
import gov.usgs.aqcu.service.LambdaReportService;

@RunWith(SpringRunner.class)
@ActiveProfiles("test")
public class LambdaReportServiceTest {

    @MockBean
    AWSLambdaClientBuilder builder;

    @MockBean
    private AWSLambda client;

    private LambdaReportService lambdaReportService;

    @Before
    public void setup() {
        when(builder.withClientConfiguration(any(ClientConfiguration.class))).thenReturn(builder);
        when(builder.build()).thenReturn(client);

        lambdaReportService = new LambdaReportService(builder);
    }

    @Test
    public void getResponsePayloadStringSuccessTest() {
        InvokeResult invokeResult = new InvokeResult();
        invokeResult.setFunctionError(null);
        invokeResult.setPayload(ByteBuffer.wrap("test".getBytes(Charset.forName("UTF-8"))));

        String result = lambdaReportService.getResponsePayloadString(invokeResult);

        assertEquals(result, "test");
    }

    @Test
    public void getResponsePayloadStringErrorTest() {
        InvokeResult invokeResult = new InvokeResult();
        invokeResult.setFunctionError(null);
        invokeResult.setPayload(null);

        try {
            lambdaReportService.getResponsePayloadString(invokeResult);
            fail("Expected to catch exception of type LambdaInvocationException but no exception occurred.");
        } catch(LambdaInvocationException e) {
            // Success
        } catch(Exception e) {
            fail("Caught exception was of type " + e.getClass().getName() + " but was expected to be LambdaInvocationException.");
        }
    }

    @Test
    public void executeSuccessTest() {
        InvokeResult invokeResult = new InvokeResult();
        invokeResult.setFunctionError(null);
        invokeResult.setStatusCode(200);
        invokeResult.setPayload(ByteBuffer.wrap("test".getBytes(Charset.forName("UTF-8"))));
        
        when(client.invoke(any())).thenReturn(invokeResult);

        String result = lambdaReportService.execute(new LambdaFunctionConfig("test-function", 1), "test");

        assertEquals(result, "test");
    }

    @Test
    public void executeErrorTest1() {
        InvokeResult invokeResult = new InvokeResult();
        invokeResult.setFunctionError(null);
        invokeResult.setStatusCode(200);
        invokeResult.setPayload(ByteBuffer.wrap("test".getBytes(Charset.forName("UTF-8"))));
        
        when(client.invoke(any())).thenThrow(new RuntimeException("failure"));

        try {
            lambdaReportService.execute(new LambdaFunctionConfig("test-function", 1), "test");
            fail("Expected an exception to be thrown.");
        } catch(Exception e) {
            assertEquals(e.getMessage(), "failure");
        }
    }

    @Test
    public void executeErrorTest2() {
        InvokeResult invokeResult = new InvokeResult();
        invokeResult.setFunctionError("Unhandled");
        invokeResult.setStatusCode(200);
        invokeResult.setPayload(ByteBuffer.wrap("failure".getBytes(Charset.forName("UTF-8"))));
        
        when(client.invoke(any())).thenReturn(invokeResult);

        try {
            lambdaReportService.execute(new LambdaFunctionConfig("test-function", 1), "test");
            fail("Expected an exception of type LambdaExecutionException to be thrown. Got no exception.");
        } catch(LambdaExecutionException e) {
            assertEquals(e.getMessage(), "failure");
        } catch(Exception e) {
            fail("Expected an exception of type LambdaExecutionException to be thrown. Got: " + e.getClass().getName());
        }
    }

    @Test
    public void executeErrorTest3() {
        InvokeResult invokeResult = new InvokeResult();
        invokeResult.setFunctionError(null);
        invokeResult.setStatusCode(500);
        invokeResult.setPayload(ByteBuffer.wrap("failure".getBytes(Charset.forName("UTF-8"))));
        
        when(client.invoke(any())).thenReturn(invokeResult);

        try {
            lambdaReportService.execute(new LambdaFunctionConfig("test-function", 1), "test");
            fail("Expected an exception of type LambdaInvocationException to be thrown. Got no exception.");
        } catch(LambdaInvocationException e) {
            assertTrue(e.getMessage().contains("failure"));
            assertTrue(e.getMessage().contains("(500)"));
        } catch(Exception e) {
            fail("Expected an exception of type LambdaInvocationException to be thrown. Got: " + e.getClass().getName());
        }
    }
}