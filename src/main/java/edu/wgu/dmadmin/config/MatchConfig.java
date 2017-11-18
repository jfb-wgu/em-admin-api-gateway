package edu.wgu.dmadmin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MatchConfig extends WebMvcConfigurerAdapter {

	@Override
	public void configurePathMatch(final PathMatchConfigurer matcher) {
		matcher.setUseRegisteredSuffixPatternMatch(Boolean.TRUE);
	}

	@Override
	public void configureContentNegotiation(final ContentNegotiationConfigurer configurer) {
		configurer.favorParameter(true);
	}
}
