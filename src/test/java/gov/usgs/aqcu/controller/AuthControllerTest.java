package gov.usgs.aqcu.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import gov.usgs.aqcu.util.AuthUtil;

@RunWith(SpringRunner.class)
@Configuration
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
@ContextConfiguration(classes=AuthController.class, initializers = ConfigFileApplicationContextInitializer.class)
@ActiveProfiles("test")
public class AuthControllerTest {
	
	private static final String TEST_USERNAME = "testUser";
	private List<String> userRoles = new ArrayList<>();
    
    @MockBean
    private AuthUtil authUtil;
    
    @Autowired
    private AuthController authController;
    
    @Before
    public void setup() {
    	userRoles = Arrays.asList("userRoles");
    }

    @Test
    public void getUserDetailsSuccessTest() {
        given(authUtil.getRequestingUser()).willReturn(TEST_USERNAME);
        given(authUtil.getRoles()).willReturn(userRoles);

        ResponseEntity<Map<String, Object>> result = authController.getUserDetails();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(TEST_USERNAME, result.getBody().get("username"));
        assertEquals(userRoles, result.getBody().get("userRoles"));
    }
    
    @Test
    public void getUserDetailsNoRolesTest() {
        given(authUtil.getRequestingUser()).willReturn(TEST_USERNAME);
        given(authUtil.getRoles()).willReturn(new ArrayList<>());

        ResponseEntity<Map<String, Object>> result = authController.getUserDetails();

        assertEquals(200, result.getStatusCode().value());
        assertEquals(TEST_USERNAME, result.getBody().get("username"));
        assertEquals(new ArrayList<>(), result.getBody().get("userRoles"));
    }
}