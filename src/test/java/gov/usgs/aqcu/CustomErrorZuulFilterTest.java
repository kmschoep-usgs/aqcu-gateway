package gov.usgs.aqcu;

import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.junit.Before;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.ClientAuthorizationRequiredException;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

@RunWith(SpringRunner.class)
public class CustomErrorZuulFilterTest {

    CustomErrorZuulFilter filter;

    @Before
    public void setUp() {
        this.filter = new CustomErrorZuulFilter();
    }
    
    public boolean itShouldFilter(String path, int statusCode) {
        RequestContext context = new RequestContext();
        
        HttpServletResponse response = mock(HttpServletResponse.class);
        context.setResponse(response);
        context.setResponseStatusCode(statusCode);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        context.setRequest(request);
        
        RequestContext.testSetCurrentContext(context);
        return filter.shouldFilter();
    }
    
    @Test
    public void shouldNotFilterSuccesfulResponses() {
        assertFalse(itShouldFilter("/service/report/foo", 200));
        assertFalse(itShouldFilter("/service/report/foo", 201));
        assertFalse(itShouldFilter("/service/config/foo", 200));
        assertFalse(itShouldFilter("/service/config/foo", 201));
    }
    
    @Test
    public void shouldFilterUnauthorizedNonConfigResponses() {
        assertTrue(itShouldFilter("/service/report/foo", 401));
    }
        
    @Test
    public void shouldFilterForbiddenNonConfigResponses() {
        assertTrue(itShouldFilter("/service/report/foo", 403));
    }

    @Test
    public void shouldNotFilterUnauthorizedConfigResponses() {
        assertFalse(itShouldFilter("/service/config/foo", 401));
        assertFalse(itShouldFilter("/service/config/groups/me/folders/asdf", 401));
    }
    
    @Test
    public void shouldNotFilterForbiddenConfigResponses() {
        assertFalse(itShouldFilter("/service/config/foo", 403));
        assertFalse(itShouldFilter("/service/config/groups/me/folders/asdf", 403));
    }

}