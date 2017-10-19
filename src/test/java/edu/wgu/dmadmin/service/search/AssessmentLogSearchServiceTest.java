package edu.wgu.dmadmin.service.search;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.Test;

import edu.wgu.dmadmin.domain.audit.StatusLogEntry;
import edu.wgu.dmadmin.model.audit.StatusLogByAssessmentModel;
import edu.wgu.dmadmin.service.SearchServiceTest;

public class AssessmentLogSearchServiceTest extends SearchServiceTest {
	
	UUID assessmentId = UUID.randomUUID();

	@Test
	public void testGetStatusLogByAssessment() {
		StatusLogByAssessmentModel model1 = new StatusLogByAssessmentModel();
		model1.setAssessmentId(assessmentId);
		StatusLogByAssessmentModel model2 = new StatusLogByAssessmentModel();
		model2.setAssessmentId(assessmentId);
		
		List<StatusLogByAssessmentModel> models = Arrays.asList(model1, model2);

		when(this.repo.getStatusLogByAssessment(this.assessmentId)).thenReturn(models);
		
		List<StatusLogEntry> result = this.service.getStatusLogByAssessment(this.assessmentId);
		
		verify(this.repo).getStatusLogByAssessment(this.assessmentId);
		assertEquals(2, result.size());
	}
}
