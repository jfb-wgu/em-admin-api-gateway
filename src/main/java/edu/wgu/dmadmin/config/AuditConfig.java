package edu.wgu.dmadmin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import edu.wgu.dmaudit.audit.AuditAdvice;
import edu.wgu.dmaudit.audit.AuditService;
import edu.wgu.dmaudit.audit.repository.AuditRepo;

import com.datastax.driver.core.Session;

@Configuration
@ComponentScan
@EnableAspectJAutoProxy
public class AuditConfig {
	
	@Autowired
	Session session;
	
	AuditRepo auditRepo;
	AuditAdvice auditAdvice;
	AuditService auditService;

	@Bean 
	public AuditRepo getAuditRepo() {
		if (this.auditRepo == null) {
			this.auditRepo = new AuditRepo(this.session);
		}
		
		return this.auditRepo;
	}
	
	@Bean
	public AuditService getAuditService() {
		if (this.auditService == null) {
			this.auditService = new AuditService();
			this.auditService.setAuditRepo(this.getAuditRepo());
		}
		
		return this.auditService;
	}
	
	@Bean
	public AuditAdvice getAuditAdvice() {
		if (this.auditAdvice == null) {
			this.auditAdvice = new AuditAdvice();
			this.auditAdvice.setAuditService(this.getAuditService());
		}
		
		return this.auditAdvice;
	}
}
