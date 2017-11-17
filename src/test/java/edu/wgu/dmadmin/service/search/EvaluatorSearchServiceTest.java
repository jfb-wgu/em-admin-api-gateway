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
import edu.wgu.dmadmin.service.SearchServiceTest;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dreammachine.domain.submission.DashboardSubmission;
import edu.wgu.dreammachine.model.submission.SubmissionByEvaluatorAndTaskModel;
import edu.wgu.dreammachine.util.StatusUtil;

public class EvaluatorSearchServiceTest extends SearchServiceTest {
	
	private static Logger logger = LoggerFactory.getLogger(EvaluatorSearchServiceTest.class);

	@Test
	public void testSearchByEvaluator1() {
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setEvaluatorFirstName(this.first1);

		when(this.repo.getUsersByFirstName(this.criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(this.criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(this.criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(this.criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> this.criteria.getEvaluatorFirstName().equals(s.getEvaluatorFirstName()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByEvaluators(getUserIdsByFirstName(this.criteria.getEvaluatorFirstName()))).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByEvaluator1 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluators(getUserIdsByFirstName(this.criteria.getEvaluatorFirstName()));
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator2() {
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setEvaluatorFirstName(this.first1);
		this.criteria.setEvaluatorLastName(this.last3);

		when(this.repo.getUsersByFirstName(this.criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(this.criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(this.criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(this.criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> this.criteria.getEvaluatorFirstName().equals(s.getEvaluatorFirstName()))
				.filter(s -> this.criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByFirstAndLastName(this.criteria.getEvaluatorFirstName(), this.criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluators(userIds)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByEvaluator2 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluators(userIds);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator3() {
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setEvaluatorLastName(this.last2);

		when(this.repo.getUsersByFirstName(this.criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(this.criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(this.criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(this.criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> this.criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByLastName(this.criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluators(userIds)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByEvaluator3 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluators(userIds);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator4() {
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setEvaluatorFirstName(this.first1);
		List<UUID> searchTasks = Arrays.asList(this.task1, this.task2);
		this.criteria.setTasks(searchTasks);

		when(this.repo.getUsersByFirstName(this.criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(this.criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(this.criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(this.criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> this.criteria.getEvaluatorFirstName().equals(s.getEvaluatorFirstName()))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByFirstName(this.criteria.getEvaluatorFirstName());
		
		when(this.repo.getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByEvaluator4 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator5() {
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setEvaluatorFirstName(this.first1);
		this.criteria.setEvaluatorLastName(this.last3);
		List<UUID> searchTasks = Arrays.asList(this.task1, this.task2);
		this.criteria.setTasks(searchTasks);

		when(this.repo.getUsersByFirstName(this.criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(this.criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(this.criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(this.criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> this.criteria.getEvaluatorFirstName().equals(s.getEvaluatorFirstName()))
				.filter(s -> this.criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByFirstAndLastName(this.criteria.getEvaluatorFirstName(), this.criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByEvaluator5 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator6() {
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setEvaluatorLastName(this.last2);
		List<UUID> searchTasks = Arrays.asList(this.task1, this.task2);
		this.criteria.setTasks(searchTasks);

		when(this.repo.getUsersByFirstName(this.criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(this.criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(this.criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(this.criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> this.criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByLastName(this.criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByEvaluator6 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator7() {
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setEvaluatorLastName(this.last2);
		this.criteria.setDateRange(DateRange.TIMEFRAME_24_HOURS);
		
		Calendar searchCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.SERVER_ZONEID));
		searchCalendar.add(Calendar.DATE, DateRange.timeframe24Hours);
		Date searchRange = searchCalendar.getTime();

		when(this.repo.getUsersByFirstName(this.criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(this.criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(this.criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(this.criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> this.criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.filter(s -> s.getDateUpdated().after(searchRange))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByLastName(this.criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluators(userIds)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByEvaluator7 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluators(userIds);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator8() {
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setEvaluatorFirstName(this.first1);
		List<UUID> searchTasks = Arrays.asList(this.task1, this.task2);
		this.criteria.setTasks(searchTasks);
		this.criteria.setDateRange(DateRange.TIMEFRAME_30_DAYS);
		
		Calendar searchCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.SERVER_ZONEID));
		searchCalendar.add(Calendar.DATE, DateRange.timeframe30Days);
		Date searchRange = searchCalendar.getTime();

		when(this.repo.getUsersByFirstName(this.criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(this.criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(this.criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(this.criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> this.criteria.getEvaluatorFirstName().equals(s.getEvaluatorFirstName()))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.filter(s -> s.getDateUpdated().after(searchRange))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByFirstName(this.criteria.getEvaluatorFirstName());
		
		when(this.repo.getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByEvaluator8 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByEvaluatorsAndTasks(userIds, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByEvaluator9() {
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setEvaluatorLastName("testing");
		this.criteria.setDateRange(DateRange.TIMEFRAME_ANY);

		when(this.repo.getUsersByFirstName(this.criteria.getEvaluatorFirstName())).thenReturn(getUsersByFirstName(this.criteria.getEvaluatorFirstName()));
		when(this.repo.getUsersByLastName(this.criteria.getEvaluatorLastName())).thenReturn(getUsersByLastName(this.criteria.getEvaluatorLastName()));
		
		List<SubmissionByEvaluatorAndTaskModel> matches = this.submissions.stream()
				.filter(s -> this.criteria.getEvaluatorLastName().equals(s.getEvaluatorLastName()))
				.map(s -> new SubmissionByEvaluatorAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByEvaluatorAndTaskModel> filtered = matches.stream()
				.filter(s -> StatusUtil.COMPLETED.equals(s.getStatusGroup()))
				.collect(Collectors.toList());
		
		List<String> userIds = getUserIdsByLastName(this.criteria.getEvaluatorLastName());
		
		when(this.repo.getSubmissionsByEvaluators(userIds)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByEvaluator3 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		assertEquals(filtered.size(), result.size());
	}
}
