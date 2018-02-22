package gov.usgs.aqcu.security;

import org.springframework.security.core.context.SecurityContextHolder;

import feign.RequestInterceptor;
import feign.RequestTemplate;

public class CidaAuthRequestInterceptor implements RequestInterceptor {

	@Override
	public void apply(RequestTemplate template) {
		CidaAuthAuthenticationToken auth = (CidaAuthAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
		template.header(CidaAuthTokenSecurityFilter.AUTHORIZATION_HEADER, CidaAuthTokenSecurityFilter.AUTH_BEARER_STRING + auth.getToken());
	}

}
