package edu.wgu.dmadmin.audit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import edu.wgu.dmadmin.exception.UserIdNotFoundException;

@Aspect
@Component
public class AuditAdvice {

	@Autowired
	private AuditService auditService;

	/**
	 * The Audit annotation results in saving a record to the activity_log_by_user
	 * table with the user_id, timestamp and method name.
	 * 
	 * @param auditAnnotation
	 *            Audit annotation indicates a method should be audited.
	 * @throws UserIdNotFoundException 
	 */
	@AfterReturning("@annotation(audit))")
	public void auditAction(JoinPoint joinPoint, Audit audit) throws UserIdNotFoundException {
		auditService.audit(joinPoint, audit);
	}
	
	public void setAuditService(AuditService service) {
		this.auditService = service;
	}
}
