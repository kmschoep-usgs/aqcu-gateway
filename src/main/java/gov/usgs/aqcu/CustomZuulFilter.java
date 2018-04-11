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

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		return !(ctx.containsKey(FORWARD_TO_KEY) && !ctx.get(FORWARD_TO_KEY).toString().isEmpty())
				&& ctx.getOrDefault(SERVICE_ID_KEY, "").toString().matches("^aqcu-.*/.*$");
	}

	@Override
	public Object run() {
		RequestContext ctx = RequestContext.getCurrentContext();
		String routedUri;

		//For endpoints with sub-paths pull out the proper sub-path
		if(ctx.get(SERVICE_ID_KEY).toString().endsWith("/**")) {
			int lastSlash = ctx.get(SERVICE_ID_KEY).toString().lastIndexOf('/');
			int secondToLastSlash = ctx.get(SERVICE_ID_KEY).toString().substring(0,lastSlash).lastIndexOf('/');
			String matchPath = ctx.get(SERVICE_ID_KEY).toString().substring(secondToLastSlash,lastSlash);
			String subPath = ctx.get(REQUEST_URI_KEY).toString().substring(ctx.get(REQUEST_URI_KEY).toString().indexOf(matchPath) + matchPath.length());
			routedUri = ctx.get(SERVICE_ID_KEY).toString().substring(0,lastSlash) + subPath;
		} else {
			routedUri = ctx.get(SERVICE_ID_KEY).toString(); 
		}
		ctx.set(REQUEST_URI_KEY, routedUri);
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
