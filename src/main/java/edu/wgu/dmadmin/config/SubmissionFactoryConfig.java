package edu.wgu.dmadmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.wgu.dmadmin.factory.SubmissionFactory;

@Configuration
public class SubmissionFactoryConfig {
	
	SubmissionFactory factory = null;
	
	@Bean
	public SubmissionFactory getSubmissionFactory() {
		if (factory == null) factory = new SubmissionFactory();
		return factory;
	}
}
