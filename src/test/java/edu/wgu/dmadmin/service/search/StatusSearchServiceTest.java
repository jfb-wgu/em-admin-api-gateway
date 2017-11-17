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
import edu.wgu.dreammachine.model.submission.SubmissionByStatusGroupAndTaskModel;
import edu.wgu.dreammachine.util.StatusUtil;

public class StatusSearchServiceTest extends SearchServiceTest {
	
	private static Logger logger = LoggerFactory.getLogger(StatusSearchServiceTest.class);

	@Test
	/**
	 * Special case:  PENDING submissions have no evaluator attached,
	 * so this search should never return any submissions.
	 */
	public void testSearchByStatus1() {
		this.criteria.setStatus(StatusUtil.PENDING);
		this.criteria.setEvaluatorFirstName(this.first2);
		
		List<SubmissionByStatusGroupAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.PENDING))
				.map(s -> new SubmissionByStatusGroupAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStatusGroupAndTaskModel> filtered = matches.stream()
				.filter(s -> this.first2.equals(s.getEvaluatorFirstName()))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStatusGroup(StatusUtil.PENDING)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByStatus1 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByStatusGroup(StatusUtil.PENDING);
		assertEquals(0, result.size());
	}
	
	@Test
	/**
	 * Special case:  PENDING submissions have no evaluator attached,
	 * so this search should never return any submissions.
	 */
	public void testSearchByStatus2() {
		this.criteria.setStatus(StatusUtil.PENDING);
		this.criteria.setEvaluatorLastName(this.last2);
		
		List<SubmissionByStatusGroupAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.PENDING))
				.map(s -> new SubmissionByStatusGroupAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStatusGroupAndTaskModel> filtered = matches.stream()
				.filter(s -> this.last2.equals(s.getEvaluatorLastName()))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStatusGroup(StatusUtil.PENDING)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByStatus2 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByStatusGroup(StatusUtil.PENDING);
		assertEquals(0, result.size());
	}
	
	@Test
	/**
	 * Special case:  PENDING submissions have no evaluator attached,
	 * so this search should never return any submissions.
	 */
	public void testSearchByStatus3() {
		this.criteria.setStatus(StatusUtil.PENDING);
		this.criteria.setEvaluatorFirstName(this.first3);
		this.criteria.setEvaluatorLastName(this.last3);
		
		List<SubmissionByStatusGroupAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.PENDING))
				.map(s -> new SubmissionByStatusGroupAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStatusGroupAndTaskModel> filtered = matches.stream()
				.filter(s -> this.first3.equals(s.getEvaluatorFirstName()))
				.filter(s -> this.last3.equals(s.getEvaluatorLastName()))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStatusGroup(StatusUtil.PENDING)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByStatus3 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByStatusGroup(StatusUtil.PENDING);
		assertEquals(0, result.size());
	}
	
	@Test
	public void testSearchByStatus4() {
		this.criteria.setStatus(StatusUtil.WORKING);
		this.criteria.setEvaluatorLastName(this.last2);
		
		List<SubmissionByStatusGroupAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.WORKING))
				.map(s -> new SubmissionByStatusGroupAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStatusGroupAndTaskModel> filtered = matches.stream()
				.filter(s -> this.last2.equals(s.getEvaluatorLastName()))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStatusGroup(StatusUtil.WORKING)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByStatus4 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByStatusGroup(StatusUtil.WORKING);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByStatus5() {
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setDateRange(DateRange.TIMEFRAME_7_DAYS);
		
		Calendar searchCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.SERVER_ZONEID));
		searchCalendar.add(Calendar.DATE, DateRange.timeframe7Days);
		Date searchRange = searchCalendar.getTime();
		
		List<SubmissionByStatusGroupAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.COMPLETED))
				.map(s -> new SubmissionByStatusGroupAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStatusGroupAndTaskModel> filtered = matches.stream()
				.filter(s -> s.getDateUpdated().after(searchRange))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStatusGroup(StatusUtil.COMPLETED)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByStatus5 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByStatusGroup(StatusUtil.COMPLETED);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByStatus6() {
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setDateRange(DateRange.TIMEFRAME_24_HOURS);
		List<UUID> searchTasks = Arrays.asList(this.task1, this.task3);
		this.criteria.setTasks(searchTasks);
		
		Calendar searchCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.SERVER_ZONEID));
		searchCalendar.add(Calendar.DATE, DateRange.timeframe24Hours);
		Date searchRange = searchCalendar.getTime();
		
		List<SubmissionByStatusGroupAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.COMPLETED))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByStatusGroupAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStatusGroupAndTaskModel> filtered = matches.stream()
				.filter(s -> s.getDateUpdated().after(searchRange))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStatusGroupAndTasks(StatusUtil.COMPLETED, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByStatus6 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByStatusGroupAndTasks(StatusUtil.COMPLETED, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByStatus7() {
		this.criteria.setStatus(StatusUtil.PENDING);
		this.criteria.setDateRange(DateRange.TIMEFRAME_72_HOURS);
		List<UUID> searchTasks = Arrays.asList(this.task1, this.task3);
		this.criteria.setTasks(searchTasks);
		
		Calendar searchCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.SERVER_ZONEID));
		searchCalendar.add(Calendar.DATE, DateRange.timeframe72Hours);
		Date searchRange = searchCalendar.getTime();
		
		List<SubmissionByStatusGroupAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.PENDING))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByStatusGroupAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStatusGroupAndTaskModel> filtered = matches.stream()
				.filter(s -> s.getDateUpdated().after(searchRange))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStatusGroupAndTasks(StatusUtil.PENDING, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("testSearchByStatus7 matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());

		verify(this.repo).getSubmissionsByStatusGroupAndTasks(StatusUtil.PENDING, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByStatus8() {
		this.criteria.setStatus(StatusUtil.PENDING);
		this.criteria.setDateRange(DateRange.TIMEFRAME_ANY);
		List<UUID> searchTasks = Arrays.asList(this.task1, this.task3);
		this.criteria.setTasks(searchTasks);

		List<SubmissionByStatusGroupAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.PENDING))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByStatusGroupAndTaskModel(s))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStatusGroupAndTasks(StatusUtil.PENDING, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);

		verify(this.repo).getSubmissionsByStatusGroupAndTasks(StatusUtil.PENDING, searchTasks);
		assertEquals(matches.size(), result.size());
	}
	
	@Test
	public void testSearchByStatus9() {
		this.criteria.setStatus(StatusUtil.HOLD);
		this.criteria.setDateRange(DateRange.TIMEFRAME_ANY);
		List<UUID> searchTasks = Arrays.asList(this.task1, this.task3);
		this.criteria.setTasks(searchTasks);

		List<SubmissionByStatusGroupAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.HOLD))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByStatusGroupAndTaskModel(s))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStatusGroupAndTasks(StatusUtil.HOLD, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);

		verify(this.repo).getSubmissionsByStatusGroupAndTasks(StatusUtil.HOLD, searchTasks);
		assertEquals(matches.size(), result.size());
	}
}
