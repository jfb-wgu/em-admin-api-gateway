package edu.wgu.dmadmin.audit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import edu.wgu.dmadmin.exception.UserIdNotFoundException;
import edu.wgu.dmadmin.model.audit.ActivityLogByUserModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.IdentityUtil;

public class AuditServiceTest {

	AuditService service = new AuditService();
	
	CassandraRepo repo = mock(CassandraRepo.class);
	IdentityUtil iUtil = mock(IdentityUtil.class);
	JoinPoint point = mock(JoinPoint.class);
	Audit auditable = mock(Audit.class);
	MethodSignature signature = mock(MethodSignature.class);
	
	MockHttpServletRequest request = new MockHttpServletRequest();
	
	UUID submissionId = UUID.randomUUID();
	String URI = "https://www.dmadmin.org/submission/" + this.submissionId.toString();
	
	@Before
	public void initialize() {		
		this.service.setCassandraRepo(repo);
		this.service.setIdentityUtil(iUtil);
		request.setRequestURI(URI);
		
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
		
		when(this.point.getSignature()).thenReturn(this.signature);
		when(this.signature.getName()).thenReturn("testing");
	}
	
	@Test
	public void testAudit() throws UserIdNotFoundException {
		this.service.audit(this.point, this.auditable);
		
		ArgumentCaptor<ActivityLogByUserModel> arg1 = ArgumentCaptor.forClass(ActivityLogByUserModel.class);
		
		verify(this.repo).saveActivityLogEntry(arg1.capture());
		assertEquals(this.submissionId, arg1.getValue().getItemId());
	}
	
	@Test
	public void testAuditNoItemID() throws UserIdNotFoundException {
		this.request.setRequestURI("http://testing");
		this.service.audit(this.point, this.auditable);
		
		ArgumentCaptor<ActivityLogByUserModel> arg1 = ArgumentCaptor.forClass(ActivityLogByUserModel.class);
		
		verify(this.repo).saveActivityLogEntry(arg1.capture());
		assertNull(arg1.getValue().getItemId());
	}
}
