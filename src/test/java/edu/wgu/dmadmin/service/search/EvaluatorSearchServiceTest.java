package edu.wgu.dmadmin.service.search;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wgu.dmadmin.domain.search.DateRange;
import edu.wgu.dmadmin.domain.submission.DashboardSubmission;
import edu.wgu.dmadmin.model.submission.SubmissionByEvaluatorAndTaskModel;
import edu.wgu.dmadmin.service.SearchServiceTest;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;

public class EvaluatorSearchServiceTest extends SearchServiceTest {
	
	private static Logger logger = LoggerFactory.getLogger(EvaluatorSearchServiceTest.class);

	@Test
	public void testSearchByEvaluator1() {
		criteria.setStatus(StatusUtil.COMPLETED);
		criteria.setEvaluatorFirstName(first1);

		when(this.repo.getUsersByFirstName(criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> criteria.getEvaluatorFirstName().equals(s.getEvaluatorFirstName()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByEvaluators(getUserIdsByFirstName(criteria.getEvaluatorFirstName()))).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(criteria);
		
		logger.info("testSearchByEvaluator1 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluators(getUserIdsByFirstName(criteria.getEvaluatorFirstName()));
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator2() {
		criteria.setStatus(StatusUtil.COMPLETED);
		criteria.setEvaluatorFirstName(first1);
		criteria.setEvaluatorLastName(last3);

		when(this.repo.getUsersByFirstName(criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> criteria.getEvaluatorFirstName().equals(s.getEvaluatorFirstName()))
				.filter(s -> criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByFirstAndLastName(criteria.getEvaluatorFirstName(), criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluators(userIds)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(criteria);
		
		logger.info("testSearchByEvaluator2 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluators(userIds);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator3() {
		criteria.setStatus(StatusUtil.COMPLETED);
		criteria.setEvaluatorLastName(last2);

		when(this.repo.getUsersByFirstName(criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByLastName(criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluators(userIds)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(criteria);
		
		logger.info("testSearchByEvaluator3 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluators(userIds);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator4() {
		criteria.setStatus(StatusUtil.COMPLETED);
		criteria.setEvaluatorFirstName(first1);
		List<UUID> searchTasks = Arrays.asList(task1, task2);
		criteria.setTasks(searchTasks);

		when(this.repo.getUsersByFirstName(criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> criteria.getEvaluatorFirstName().equals(s.getEvaluatorFirstName()))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByFirstName(criteria.getEvaluatorFirstName());
		
		when(this.repo.getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(criteria);
		
		logger.info("testSearchByEvaluator4 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator5() {
		criteria.setStatus(StatusUtil.COMPLETED);
		criteria.setEvaluatorFirstName(first1);
		criteria.setEvaluatorLastName(last3);
		List<UUID> searchTasks = Arrays.asList(task1, task2);
		criteria.setTasks(searchTasks);

		when(this.repo.getUsersByFirstName(criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> criteria.getEvaluatorFirstName().equals(s.getEvaluatorFirstName()))
				.filter(s -> criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByFirstAndLastName(criteria.getEvaluatorFirstName(), criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(criteria);
		
		logger.info("testSearchByEvaluator5 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator6() {
		criteria.setStatus(StatusUtil.COMPLETED);
		criteria.setEvaluatorLastName(last2);
		List<UUID> searchTasks = Arrays.asList(task1, task2);
		criteria.setTasks(searchTasks);

		when(this.repo.getUsersByFirstName(criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByLastName(criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(criteria);
		
		logger.info("testSearchByEvaluator6 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator7() {
		criteria.setStatus(StatusUtil.COMPLETED);
		criteria.setEvaluatorLastName(last2);
		criteria.setDateRange(DateRange.TIMEFRAME_24_HOURS);
		
		Calendar searchCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.SERVER_ZONEID));
		searchCalendar.add(Calendar.DATE, DateRange.timeframe24Hours);
		Date searchRange = searchCalendar.getTime();

		when(this.repo.getUsersByFirstName(criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.filter(s -> s.getDateUpdated().after(searchRange))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByLastName(criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluators(userIds)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(criteria);
		
		logger.info("testSearchByEvaluator7 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluators(userIds);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator8() {
		criteria.setStatus(StatusUtil.COMPLETED);
		criteria.setEvaluatorFirstName(first1);
		List<UUID> searchTasks = Arrays.asList(task1, task2);
		criteria.setTasks(searchTasks);
		criteria.setDateRange(DateRange.TIMEFRAME_30_DAYS);
		
		Calendar searchCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.SERVER_ZONEID));
		searchCalendar.add(Calendar.DATE, DateRange.timeframe30Days);
		Date searchRange = searchCalendar.getTime();

		when(this.repo.getUsersByFirstName(criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> criteria.getEvaluatorFirstName().equals(s.getEvaluatorFirstName()))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.filter(s -> s.getDateUpdated().after(searchRange))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByFirstName(criteria.getEvaluatorFirstName());
		
		when(this.repo.getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(criteria);
		
		logger.info("testSearchByEvaluator8 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator9() {
		criteria.setStatus(StatusUtil.COMPLETED);
		criteria.setEvaluatorLastName("testing");
		criteria.setDateRange(DateRange.TIMEFRAME_ANY);

		when(this.repo.getUsersByFirstName(criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByLastName(criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluators(userIds)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(criteria);
		
		logger.info("testSearchByEvaluator3 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		assertEquals(filtered.size(), result.size());
	}
}
