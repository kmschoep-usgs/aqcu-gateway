package gov.usgs.aqcu.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class CidaAuthTokenSecurityFilter extends GenericFilterBean {
	private final String username = "Authorized";
	private final Collection<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("AUTHENTICATED")) ;

	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String AUTH_BEARER_STRING = "Bearer ";

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpReq = (HttpServletRequest) req; 

		String token = getTokenFromHeader(httpReq);

		if (null != token) {
			CidaAuthAuthenticationToken authentication = new CidaAuthAuthenticationToken(username, null, authorities, token);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}

		filterChain.doFilter(req, resp); 
	}

	public static String getTokenFromHeader(HttpServletRequest httpRequest) {
		String token = null;

		String authHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);
		if(authHeader != null &&
				authHeader.toLowerCase().startsWith(AUTH_BEARER_STRING.toLowerCase())) {
			token = authHeader;
			token = token.replaceAll(AUTH_BEARER_STRING + "\\s+", "");
			token = token.replaceAll(AUTH_BEARER_STRING.toLowerCase() + "\\s+", "");
			token = token.replaceAll(AUTH_BEARER_STRING.toUpperCase() + "\\s+", "");
		}

		return token;
	}

}
