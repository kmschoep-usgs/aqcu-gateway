package gov.usgs.aqcu.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class CidaAuthTokenRelayFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof CidaAuthAuthenticationToken) {
			return true;
		}
		return false;
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.addZuulRequestHeader(CidaAuthTokenSecurityFilter.AUTHORIZATION_HEADER, CidaAuthTokenSecurityFilter.AUTH_BEARER_STRING + getAccessToken());
		return null;
	}

	private String getAccessToken() {
		CidaAuthAuthenticationToken auth = (CidaAuthAuthenticationToken) SecurityContextHolder
				.getContext().getAuthentication();
		return auth.getToken();
	}

	@Override
	public String filterType() {
		return "pre";
	}

	@Override
	public int filterOrder() {
		return 10;
	}

}
