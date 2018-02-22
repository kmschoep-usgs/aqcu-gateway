package gov.usgs.aqcu.security;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class CidaAuthAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = -8417858529803671163L;
	private String token;

	public CidaAuthAuthenticationToken(Object principal, Object credentials,
			Collection<? extends GrantedAuthority> authorities, String token) {
		super(principal, credentials, authorities);
		this.token = token;
	}

	public String getToken() {
		return token;
	}

}
