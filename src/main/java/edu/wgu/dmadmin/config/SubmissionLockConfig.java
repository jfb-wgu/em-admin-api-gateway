package edu.wgu.dmadmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.wgu.dmadmin.factory.SubmissionLockFactory;

@Configuration
public class SubmissionLockConfig {
	
	private SubmissionLockFactory factory;
	
	@Bean
	public SubmissionLockFactory getSubmissionLockFactory() {
		if (factory == null) factory = new SubmissionLockFactory();
		return factory;
	}
}
