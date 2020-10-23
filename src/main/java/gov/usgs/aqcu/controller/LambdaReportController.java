package gov.usgs.aqcu.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.http.timers.client.ClientExecutionTimeoutException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.aqcu.exception.LambdaExecutionException;
import gov.usgs.aqcu.lambda.LambdaFunctionConfig;
import gov.usgs.aqcu.service.LambdaReportService;
import gov.usgs.aqcu.util.AuthUtil;

@RestController
@RequestMapping("lambda/reports")
@ConfigurationProperties(prefix = "lambda")
public class LambdaReportController {
	protected static final Logger LOG = LoggerFactory.getLogger(LambdaReportController.class);
	private final String GENERIC_ERROR_MESSAGE = "An error occurred while executing the report function.";

	/*
	 * This is populated automatically from "lambda.region" in the application.yml
	 * because of the @ConfigurationProperties annotation on the class.
	 */
	private String region;

	/*
	 * This is populated automatically from "lambda.functions" in the
	 * application.yml because of the @ConfigurationProperties annotation on the
	 * class.
	 */
	private HashMap<String, LambdaFunctionConfig> functions;
	private LambdaReportService lambdaReportService;
	private AuthUtil authUtil;
	private ObjectMapper mapper;

	@Autowired
	public LambdaReportController(LambdaReportService lambdaReportService, AuthUtil authUtil) {
		this.lambdaReportService = lambdaReportService;
		this.authUtil = authUtil;
		this.mapper = new ObjectMapper();
	}

	@GetMapping(path = "/{report}", produces = "application/json")
	public ResponseEntity<String> getReportLambda(
		@PathVariable("report") String report, 
		@RequestParam MultiValueMap<String, String> allRequestParams
	) {
		String detailErrorMessage = "";
		if (report != null && functions.containsKey(report.toLowerCase())) {
			allRequestParams.add("requestingUser", authUtil.getRequestingUser());
			String lambdaRequestJson;
			try {
				lambdaRequestJson = queryParamsToLambdaJson(allRequestParams);
			} catch (Exception e) {
				LOG.error("Failed to convert lambda report request parameters to JSON.\nParams: "
					+ allRequestParams.toString() + "\nError: ", e);
				return new ResponseEntity<String>("Failed to parse provided report request parameters.",
					new HttpHeaders(), HttpStatus.BAD_REQUEST);
			}

			try {
				String result = lambdaReportService.execute(functions.get(report), lambdaRequestJson);
				return new ResponseEntity<String>(result, new HttpHeaders(), HttpStatus.OK);
			} catch (Exception e) {
				if(e instanceof LambdaExecutionException) {
					LOG.info("Lambda function '{}' errored during its execution. " +
						"Error details can be found in the logs of the function.", functions.get(report));
					detailErrorMessage = parseDetailMessage(e.getMessage()).get("errorMessage").toString();
				// catch the report timeout exception to be able to return the message to the user
				} else if (e instanceof ClientExecutionTimeoutException) {
					detailErrorMessage = e.getLocalizedMessage();
					LOG.error("Lambda function '{}' timed out. Error: {}", functions.get(report), e);
				} else {
					LOG.error("Lambda function '{}' failed to execute. Error: {}", functions.get(report), e);
				}
				
				return new ResponseEntity<String>(GENERIC_ERROR_MESSAGE + " " + detailErrorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<String>("Report " + report + " not found.", new HttpHeaders(), HttpStatus.NOT_FOUND);
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public HashMap<String, LambdaFunctionConfig>  getFunctions() {
		return functions;
	}

	public void setFunctions(HashMap<String, LambdaFunctionConfig>  functions) {
		this.functions = functions;
	}

	public ObjectMapper getMapper() {
		return mapper;
	}

	public void setMapper(ObjectMapper mapper) {
		this.mapper = mapper;
	}

	protected String queryParamsToLambdaJson(Map<String, List<String>> queryParams) throws IOException {
		Map<String, Object> lambdaRequestParams = new HashMap<>();

		if(queryParams != null && !queryParams.isEmpty()) {
			for(String queryKey : queryParams.keySet()) {
				if(queryParams.get(queryKey) != null && !queryParams.get(queryKey).isEmpty()) {
					if(queryParams.get(queryKey).size() > 1) {
						lambdaRequestParams.put(queryKey, queryParams.get(queryKey));
					} else {
						lambdaRequestParams.put(queryKey, queryParams.get(queryKey).get(0));
					}
				}
			}
		}		

		return mapper.writeValueAsString(lambdaRequestParams);
	}
	
	protected Map<String, Object> parseDetailMessage(String message) {
		TypeReference<Map<String, Object>> mapType = new TypeReference<Map<String, Object>>() {};
		Map<String, Object> reportErrorMessage = new HashMap<>();
		try {
			reportErrorMessage = mapper.readValue(message, mapType);
		} catch (JsonProcessingException e) {
			reportErrorMessage.put("errorMessage", "Could not parse error response.");
		} 
		return reportErrorMessage;
	}
}