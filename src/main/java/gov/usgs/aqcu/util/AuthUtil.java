package gov.usgs.aqcu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthUtil {
	public static final String UNKNOWN_USERNAME = "unknown";

	public String getRequestingUser() {
		String username = UNKNOWN_USERNAME;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();	
		if (null != authentication && !(authentication instanceof AnonymousAuthenticationToken)) {
			username= authentication.getName();
		}
		return username;
	}
	
	public List<String> getRoles() {
		List<String> roles = new ArrayList<>();
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		roles = authentication.getAuthorities().stream()
				.map(r -> r.toString())
				.collect(Collectors.toList());
		return roles;
	}
}