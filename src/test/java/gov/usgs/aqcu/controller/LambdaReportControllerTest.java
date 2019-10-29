package gov.usgs.aqcu.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import gov.usgs.aqcu.exception.LambdaExecutionException;
import gov.usgs.aqcu.exception.LambdaInvocationException;
import gov.usgs.aqcu.service.LambdaReportService;

@RunWith(SpringRunner.class)
@Configuration
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@ContextConfiguration(classes=LambdaReportController.class, initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("test")
public class LambdaReportControllerTest {

    @MockBean
    private LambdaReportService lambdaReportService;
    
    @Autowired
    private LambdaReportController lambdaReportController;

    @Test
    public void getReportLambdaSuccessTest() {
        given(lambdaReportService.execute(eq("test-function"), any(String.class))).willReturn("test");
        
        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("test"));

        ResponseEntity<String> result = lambdaReportController.getReportLambda("test", args);

        assertEquals(200, result.getStatusCode().value());
        assertTrue(result.getBody().contains("test"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void getReportLambdaErrorTest1() {
        /* This spy causes an error to be thrown when `isEmpty()` is called on the map. This causes the
         * jackson MapSerializer to encounter an error, which gets propagated back up the chain as a
         * JSON processing exception.
         */
        LinkedMultiValueMap<String,String> args = Mockito.spy(LinkedMultiValueMap.class);
        Mockito.when(args.isEmpty()).thenThrow(new RuntimeException());

        ResponseEntity<String> result = lambdaReportController.getReportLambda("test", args);

        assertEquals(400, result.getStatusCode().value());
        assertTrue(result.getBody().contains("Failed to parse"));
    }

    @Test
    public void getReportLambdaErrorTest2() {
        ResponseEntity<String> result = lambdaReportController.getReportLambda("invalid", new LinkedMultiValueMap<>());

        assertEquals(404, result.getStatusCode().value());
        assertTrue(result.getBody().contains("not found"));
    }

    @Test
    public void getReportLambdaErrorTest3() {
        given(lambdaReportService.execute(eq("test-function"), any(String.class))).willThrow(
            new LambdaExecutionException("failed")
        );
        
        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("test"));

        ResponseEntity<String> result = lambdaReportController.getReportLambda("test", args);

        assertEquals(500, result.getStatusCode().value());
        assertTrue(result.getBody().contains("An error occurred"));
    }

    @Test
    public void getReportLambdaErrorTest4() {
        given(lambdaReportService.execute(eq("test-function"), any(String.class))).willThrow(
            new LambdaInvocationException("failed")
        );

        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("test"));

        ResponseEntity<String> result = lambdaReportController.getReportLambda("test", args);

        assertEquals(500, result.getStatusCode().value());
        assertTrue(result.getBody().contains("An error occurred"));
    }

    @Test
    public void getReportLambdaErrorTest5() {
        given(lambdaReportService.execute(eq("test-function"), any(String.class))).willThrow(
            new RuntimeException("failed")
        );

        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("test"));

        ResponseEntity<String> result = lambdaReportController.getReportLambda("test", args);

        assertEquals(500, result.getStatusCode().value());
        assertTrue(result.getBody().contains("An error occurred"));
    }

    @Test
    public void queryParamsToLambdaJsonTest() throws Exception {
        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("test"));

        String result = lambdaReportController.queryParamsToLambdaJson(args);
        assertEquals(result, "{\"test1\":\"test\"}");

        args.put("test1", Arrays.asList("testa", "testb"));
        result = lambdaReportController.queryParamsToLambdaJson(args);
        assertEquals(result, "{\"test1\":[\"testa\",\"testb\"]}");

        args.put("test1", new ArrayList<>());
        result = lambdaReportController.queryParamsToLambdaJson(args);
        assertEquals(result, "{}");

        args.put("test1", null);
        result = lambdaReportController.queryParamsToLambdaJson(args);
        assertEquals(result, "{}");

        args.remove("test1");
        result = lambdaReportController.queryParamsToLambdaJson(args);
        assertEquals(result, "{}");

        result = lambdaReportController.queryParamsToLambdaJson(null);
        assertEquals(result, "{}");

        args.put("test1", Arrays.asList("testa", "testb"));
        args.put("test2", Arrays.asList("testc"));
        args.put("test3", Arrays.asList("testd", "testf"));
        args.put("test4", new ArrayList<>());
        args.put("test5", Arrays.asList("testg"));
        args.put("test6", null);
        result = lambdaReportController.queryParamsToLambdaJson(args);
        assertTrue(result.contains("\"test1\":[\"testa\",\"testb\"]"));
        assertTrue(result.contains("\"test2\":\"testc\""));
        assertTrue(result.contains("\"test3\":[\"testd\",\"testf\"]"));
        assertTrue(result.contains("\"test5\":\"testg\""));
        assertFalse(result.contains("test4"));
        assertFalse(result.contains("test6"));
    }
}