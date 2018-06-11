package gov.usgs.aqcu;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

@Component
public class CustomErrorZuulFilter extends ZuulFilter {

	@Override
	public boolean shouldFilter() {
		RequestContext ctx = RequestContext.getCurrentContext();
		return ctx.getResponseStatusCode() == 401 || ctx.getResponseStatusCode() == 403;
	}

	@Override
	public Object run() {
		//Note that this does not provide a nice page, the xml is displayed as is...
		RequestContext ctx = RequestContext.getCurrentContext();
		ctx.setResponseBody("<html><head/><body>You have been logged out due to inactivity. Please visit <a href=\"https://reporting.nwis.usgs.gov/login.jsp\">https://reporting.nwis.usgs.gov/login.jsp</a> to log in again</body></html>");
		ctx.getResponse().setContentType("text/html");
		ctx.setResponseStatusCode(200);
		return null;
	}

	@Override
	public String filterType() {
		return FilterConstants.POST_TYPE;
	}

	@Override
	public int filterOrder() {
		return FilterConstants.SEND_ERROR_FILTER_ORDER - 1;
	}

}
