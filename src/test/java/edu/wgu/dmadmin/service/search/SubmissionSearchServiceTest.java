package edu.wgu.dmadmin.service.search;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.Test;

import edu.wgu.dmadmin.service.SearchServiceTest;
import edu.wgu.dreammachine.domain.submission.DashboardSubmission;
import edu.wgu.dreammachine.model.submission.SubmissionByIdModel;

public class SubmissionSearchServiceTest extends SearchServiceTest {
	
	@Test
	public void testSearchBySubmissionId() {
		this.criteria.setSubmissionId(this.submission.getSubmissionId().toString());
		when(this.repo.getSubmissionById(this.submission.getSubmissionId()))
			.thenReturn(Optional.of(new SubmissionByIdModel(this.submission)));
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		verify(this.repo).getSubmissionById(this.submission.getSubmissionId());
		assertEquals(this.submission.getSubmissionId(), result.get(0).getSubmissionId());
	}
	
	@Test
	public void testSearchBySubmissionIdNoSub() {
		this.criteria.setSubmissionId(this.submission.getSubmissionId().toString());
		when(this.repo.getSubmissionById(this.submission.getSubmissionId()))
			.thenReturn(Optional.empty());
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		verify(this.repo).getSubmissionById(this.submission.getSubmissionId());
		assertEquals(0, result.size());
	}
	
	@Test
	public void testSearchByBadSubmissionId() {
		this.criteria.setSubmissionId("testing");
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		assertEquals(0, result.size());
	}
}
