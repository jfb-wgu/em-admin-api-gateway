package edu.wgu.dmadmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

import edu.wgu.dmadmin.domain.security.RequestBean;

@Configuration
public class RequestConfig {

	@Bean
	@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
	public RequestBean requestBean() {
	    return new RequestBean();
	}
}
