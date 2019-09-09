package gov.usgs.aqcu.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthController {

    @GetMapping("token")
    public String getToken() {
        if(SecurityContextHolder.getContext().getAuthentication() != null){
            String jwtToken = ((OAuth2AuthenticationDetails) ((OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication()).getDetails()).getTokenValue();
            return jwtToken;
        }

        return null;
    }
}