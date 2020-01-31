package gov.usgs.aqcu.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
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
import gov.usgs.aqcu.lambda.LambdaFunctionConfig;
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

    @Before
    public void setup() {
        lambdaReportController.setMapper(new ObjectMapper());
    }
    

    @Test
    public void getReportLambdaSuccessTest() {
        given(lambdaReportService.execute(any(LambdaFunctionConfig.class), any(String.class))).willReturn("test");
        
        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("test"));

        ResponseEntity<String> result = lambdaReportController.getReportLambda("test", args);

        assertEquals(200, result.getStatusCode().value());
        assertTrue(result.getBody().contains("test"));
    }

    @Test
    public void getReportLambdaErrorTest1() throws JsonProcessingException {
        ObjectMapper mapper = Mockito.spy(ObjectMapper.class);

        when(mapper.writeValueAsString(any(HashMap.class))).thenThrow(new RuntimeException("error"));

        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("test"));

        lambdaReportController.setMapper(mapper);

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
    public void getReportLambdaExecutionErrorTest() {
        given(lambdaReportService.execute(any(LambdaFunctionConfig.class), any(String.class))).willThrow(
            new LambdaExecutionException("failed")
        );
        
        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("test"));

        ResponseEntity<String> result = lambdaReportController.getReportLambda("test", args);

        assertEquals(500, result.getStatusCode().value());
        assertTrue(result.getBody().contains("An error occurred"));
    }

    @Test
    public void getReportLambdaInvocationErrorTest() {
        given(lambdaReportService.execute(any(LambdaFunctionConfig.class), any(String.class))).willThrow(
            new LambdaInvocationException("failed")
        );

        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("test"));

        ResponseEntity<String> result = lambdaReportController.getReportLambda("test", args);

        assertEquals(500, result.getStatusCode().value());
        assertTrue(result.getBody().contains("An error occurred"));
    }

    @Test
    public void getReportLambdaRuntimeErrorTest() {
        given(lambdaReportService.execute(any(LambdaFunctionConfig.class), any(String.class))).willThrow(
            new RuntimeException("failed")
        );

        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("test"));

        ResponseEntity<String> result = lambdaReportController.getReportLambda("test", args);

        assertEquals(500, result.getStatusCode().value());
        assertTrue(result.getBody().contains("An error occurred"));
    }

    @Test
    public void queryParamsToLambdaJsonStringParamTest() throws Exception {
        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.add("test1", "testa");

        String result = lambdaReportController.queryParamsToLambdaJson(args);
        assertEquals("{\"test1\":\"testa\"}", result);
    }

    @Test
    public void queryParamsToLambdaJsonListParamTest() throws IOException {
        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("testa", "testb"));

        String result = lambdaReportController.queryParamsToLambdaJson(args);
        assertEquals("{\"test1\":[\"testa\",\"testb\"]}", result);
    }

    @Test
    public void queryParamsToLambdaJsonMultiParamsTest() throws IOException {
        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", Arrays.asList("testa", "testb"));
        args.add("test2", "testc");
        args.put("test3", Arrays.asList("testd", "testf"));
        args.put("test4", new ArrayList<>());
        args.add("test5", "testg");
        args.put("test6", null);

        String result = lambdaReportController.queryParamsToLambdaJson(args);
        assertTrue(result.contains("\"test1\":[\"testa\",\"testb\"]"));
        assertTrue(result.contains("\"test2\":\"testc\""));
        assertTrue(result.contains("\"test3\":[\"testd\",\"testf\"]"));
        assertTrue(result.contains("\"test5\":\"testg\""));
        assertFalse(result.contains("test4"));
        assertFalse(result.contains("test6"));
    }

    @Test
    public void queryParamsToLambdaJsonNullEmptyTest() throws IOException {
        LinkedMultiValueMap<String,String> args = new LinkedMultiValueMap<>();
        args.put("test1", new ArrayList<>());
        String result = lambdaReportController.queryParamsToLambdaJson(args);
        assertEquals("{}", result);

        args.put("test1", null);
        result = lambdaReportController.queryParamsToLambdaJson(args);
        assertEquals("{}", result);

        args.remove("test1", result);
        result = lambdaReportController.queryParamsToLambdaJson(args);
        assertEquals("{}", result);

        result = lambdaReportController.queryParamsToLambdaJson(null);
        assertEquals("{}", result);
    }
}