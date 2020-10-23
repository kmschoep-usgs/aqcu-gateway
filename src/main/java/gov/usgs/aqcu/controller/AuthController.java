package gov.usgs.aqcu.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import gov.usgs.aqcu.util.AuthUtil;

@RestController
@RequestMapping("auth")
public class AuthController {
	@Value("${aqcu.ui.login.route}")
	private String uiLoginPage;
	private AuthUtil authUtil;
	
	@Autowired
	public AuthController(AuthUtil authUtil) {
		this.authUtil = authUtil;
	}
	
	@GetMapping("login")
	public void getToken(HttpServletResponse response) throws IOException {		
		if(SecurityContextHolder.getContext().getAuthentication() != null){
			response.sendRedirect(uiLoginPage + "?accessToken=" + ((OAuth2AuthenticationDetails) ((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication()).getDetails()).getTokenValue());
		} else {
			response.sendError(HttpStatus.FORBIDDEN.value(), "Login failed");
		}
	}

	@GetMapping("token")
	public String getToken() {
		if(SecurityContextHolder.getContext().getAuthentication() != null){
			return ((OAuth2AuthenticationDetails) ((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication()).getDetails()).getTokenValue();
		}
		return null;
	}

	@GetMapping(path = "userDetails", produces = "application/json")
	public ResponseEntity<Map<String, Object>> getUserDetails() {
		Map<String, Object> userDetails = new HashMap<>();
		userDetails.put("username", authUtil.getRequestingUser());
		userDetails.put("userRoles", authUtil.getRoles());
		return new ResponseEntity<Map<String, Object>>(userDetails, new HttpHeaders(), HttpStatus.OK);
	}
}