package edu.wgu.dm.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.wgu.dm.service.AuditorAwareImpl;
import edu.wgu.dm.util.IdentityUtil;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuditAwareImplTest {


    @Spy
    @InjectMocks
    AuditorAwareImpl auditImpl;

    @Mock
    IdentityUtil iUtil;


    @Test
    public void test_WhenNoRequestBoundToThread() {
        // arrange
        doReturn(false).when(this.auditImpl)
                       .checkIfHttpServletRequestBoundToThread();
        // act
        Optional<String> val = auditImpl.getCurrentAuditor();

        // assert
        assertEquals(Optional.of("NA"), val);
        verify(iUtil, never()).getUserId();
    }

    @Test
    public void test_WhenRequestIsPresent() {
        // arrange
        doReturn(true).when(this.auditImpl)
                      .checkIfHttpServletRequestBoundToThread();

        String name = "TESTNAME";
        when(iUtil.getUserId()).thenReturn(name);

        // act
        Optional<String> val = auditImpl.getCurrentAuditor();

        // assert
        assertEquals(Optional.of(name.toUpperCase()), val);
    }

}
