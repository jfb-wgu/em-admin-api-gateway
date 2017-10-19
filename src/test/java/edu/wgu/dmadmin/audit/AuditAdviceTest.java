package edu.wgu.dmadmin.audit;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.aspectj.lang.JoinPoint;
import org.junit.Before;
import org.junit.Test;

import edu.wgu.dmadmin.exception.UserIdNotFoundException;

public class AuditAdviceTest {
	
	AuditAdvice advice = new AuditAdvice();

	AuditService service = mock(AuditService.class);
	JoinPoint point = mock(JoinPoint.class);
	Audit auditable = mock(Audit.class);
	
	@Before
	public void initialize() {		
		this.advice.setAuditService(service);
	}
	
	@Test
	public void testAuditAction() throws UserIdNotFoundException {
		this.advice.auditAction(this.point, this.auditable);
		verify(this.service).audit(any(JoinPoint.class), any(Audit.class));
	}
}
