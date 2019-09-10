package gov.usgs.aqcu.config;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.util.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LambdaConfig {
    @Value("${lambda.region}") 
    String region;

    @Value("${lambda.endpoint:}")
    String endpoint;

    @Bean
    public AWSLambdaClientBuilder awsLambdaClientBuilder() {
        AWSLambdaClientBuilder lambdaClientBuilder = AWSLambdaClientBuilder.standard();

        lambdaClientBuilder.setRegion(region);
        
        if(!StringUtils.isNullOrEmpty(endpoint)) {
            lambdaClientBuilder.setEndpointConfiguration(
                new EndpointConfiguration(endpoint, region)
            );
        }

        return lambdaClientBuilder;
    }
}