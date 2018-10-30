package edu.wgu.dmadmin.service;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import java.util.Date;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.admin.service.ReportService;
import edu.wgu.dm.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class ReportServiceTest {

    @InjectMocks
    ReportService service;

    @Mock
    AdminRepository repo;

    @Before
    public void initialize() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetTaskCompetencies() {
        Date date = DateUtil.startOfDay(new Date());
        this.service.getTaskCompetencies(date);
        verify(this.repo).getTaskCompetencies(date);
    }

    @Test(expected = NullPointerException.class)
    public void testGetTaskCompetenciesNullDate() {
        this.service.getTaskCompetencies(null);
        verify(this.repo, never()).getTaskCompetencies(null);
    }

    @Test
    public void testGetEvaluationAspects() {
        Date start = DateUtil.startOfDay(new Date());
        Date end = DateUtil.endOfDay(new Date());
        this.service.getEvaluationAspects(start, end);
        verify(this.repo).getEvaluationAspects(start, end);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetEvaluationAspectsEndBeforeStart() {
        Date end = DateUtil.startOfDay(new Date());
        Date start = DateUtil.endOfDay(new Date());
        this.service.getEvaluationAspects(start, end);
        verify(this.repo, never()).getEvaluationAspects(start, end);
    }

    @Test
    public void testGetRubrics() {
        Date date = DateUtil.startOfDay(new Date());
        this.service.getRubrics(date);
        verify(this.repo).getTaskRecords(date);
    }

    @Test(expected = NullPointerException.class)
    public void testGetRubricsNullDate() {
        this.service.getRubrics(null);
        verify(this.repo, never()).getTaskRecords(null);
    }
}
