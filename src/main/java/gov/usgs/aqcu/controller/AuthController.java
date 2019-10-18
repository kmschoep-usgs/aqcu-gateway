package gov.usgs.aqcu.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {
	@Value("${aqcu.ui.login.route}")
	private String uiLoginPage;

	@GetMapping("login")
	public void getToken(HttpServletResponse response) throws IOException {		
		if(SecurityContextHolder.getContext().getAuthentication() != null){
			response.sendRedirect(uiLoginPage + "?accessToken=" + ((OAuth2AuthenticationDetails) ((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication()).getDetails()).getTokenValue());
		} else {
            response.sendError(HttpStatus.SC_FORBIDDEN, "Login failed");
        }
	}

    @GetMapping("token")
    public String getToken() {
        if(SecurityContextHolder.getContext().getAuthentication() != null){
            return ((OAuth2AuthenticationDetails) ((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication()).getDetails()).getTokenValue();
        }

        return null;
    }
}