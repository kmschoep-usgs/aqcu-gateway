package gov.usgs.aqcu.controller;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.aqcu.exception.LambdaExecutionException;
import gov.usgs.aqcu.service.LambdaReportService;

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
	 * This is populated automatically from "lambda.functions" in the application.yml
	 * because of the @ConfigurationProperties annotation on the class.
	 */
	private HashMap<String, String> functions;

	private LambdaReportService lambdaReportService;

	@Autowired
	public LambdaReportController(LambdaReportService lambdaReportService) {
		this.lambdaReportService = lambdaReportService;
	}

	@GetMapping(path = "/{report}", produces = "application/json")
	public ResponseEntity<String> getReportLambda(
		@PathVariable("report") String report, 
		@RequestParam Map<String, String> allRequestParams
	) {
		if (report != null && functions.containsKey(report.toLowerCase())) {
			ObjectMapper mapper = new ObjectMapper();
			String lambdaRequestJson;

			try {
				lambdaRequestJson = mapper.writeValueAsString(allRequestParams);
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
					LOG.info("Lambda function '{}}' errored during its execution. " +
						"Error details can be found in the logs of the function.", functions.get(report));
				} else {
					LOG.error("Lambda function '{}' failed to execute. Error: {}", functions.get(report), e);
				}
				
				return new ResponseEntity<String>(GENERIC_ERROR_MESSAGE, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
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

	public HashMap<String, String> getFunctions() {
		return functions;
	}

	public void setFunctions(HashMap<String, String> functions) {
		this.functions = functions;
	}
}