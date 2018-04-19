package gov.usgs.aqcu;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORWARD_TO_KEY;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class CustomZuulFilter extends ZuulFilter {

	public static final String AQCU_SERVICE_ID_PREFIX = "aqcu/";

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		return !(ctx.containsKey(FORWARD_TO_KEY) && !ctx.get(FORWARD_TO_KEY).toString().isEmpty())
				&& ctx.getOrDefault(SERVICE_ID_KEY, "").toString().startsWith(AQCU_SERVICE_ID_PREFIX);
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.set(REQUEST_URI_KEY, ctx.get(SERVICE_ID_KEY).toString().substring(AQCU_SERVICE_ID_PREFIX.length()) + ctx.get(REQUEST_URI_KEY).toString());
		return null;
	}

	@Override
	public String filterType() {
		return PRE_TYPE;
	}

	@Override
	public int filterOrder() {
		return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
	}

}
