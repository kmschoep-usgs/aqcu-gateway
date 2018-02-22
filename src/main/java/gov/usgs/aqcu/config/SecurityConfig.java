package gov.usgs.aqcu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.netflix.zuul.ZuulFilter;

import feign.RequestInterceptor;
import gov.usgs.aqcu.security.CidaAuthRequestInterceptor;
import gov.usgs.aqcu.security.CidaAuthTokenRelayFilter;
import gov.usgs.aqcu.security.CidaAuthTokenSecurityFilter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private CidaAuthTokenSecurityFilter cidaAuthTokenSecurityFilter;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic().disable()
//			.anonymous().disable()
			.cors().and()
			.authorizeRequests()
//				.antMatchers("/swagger-resources/**", "/webjars/**", "/v2/**").permitAll()
//				.antMatchers("/info**", "/health/**", "/hystrix/**", "/hystrix.stream**", "/proxy.stream**", "/favicon.ico").permitAll()
//				.antMatchers("/swagger-ui.html").permitAll()
//				.anyRequest().fullyAuthenticated()
				.anyRequest().permitAll()
			.and()
				.logout().permitAll()
			.and()
				.csrf().disable()
				.addFilterBefore(cidaAuthTokenSecurityFilter, UsernamePasswordAuthenticationFilter.class)
		;
	}


	@Bean
	public RequestInterceptor cidaAuthRequestInterceptor() {
		return new CidaAuthRequestInterceptor();
	}

	@Bean
	public ZuulFilter cidaAuthTokenRelayFilter() {
		return new CidaAuthTokenRelayFilter();
	}

}
