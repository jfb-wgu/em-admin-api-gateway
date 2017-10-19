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
import edu.wgu.dmadmin.model.submission.SubmissionByStudentAndTaskModel;
import edu.wgu.dmadmin.service.SearchServiceTest;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;

public class StudentSearchServiceTest extends SearchServiceTest {
	
	private static Logger logger = LoggerFactory.getLogger(StudentSearchServiceTest.class);
	
	@Test
	public void testSearchByStudent1() {
		this.criteria.setStudentId(this.student1);
		this.criteria.setStatus(StatusUtil.WORKING);
		
		List<SubmissionByStudentAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStudentId().equals(this.student1))
				.map(s -> new SubmissionByStudentAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStudentAndTaskModel> filtered = matches.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.WORKING))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStudentId(this.student1)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());
		
		verify(this.repo).getSubmissionsByStudentId(this.student1);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByStudent2() {
		this.criteria.setStudentId(this.student1);
		this.criteria.setStatus(StatusUtil.WORKING);
		this.criteria.setEvaluatorFirstName(first2);
		
		List<SubmissionByStudentAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStudentId().equals(this.student1))
				.map(s -> new SubmissionByStudentAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStudentAndTaskModel> filtered = matches.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.WORKING))
				.filter(s -> s.getEvaluatorFirstName().equals(this.first2))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStudentId(this.student1)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());
		
		verify(this.repo).getSubmissionsByStudentId(this.student1);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByStudent3() {
		this.criteria.setStudentId(this.student1);
		this.criteria.setStatus(StatusUtil.COMPLETED);
		this.criteria.setEvaluatorLastName(last1);
		
		List<SubmissionByStudentAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStudentId().equals(this.student1))
				.map(s -> new SubmissionByStudentAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStudentAndTaskModel> filtered = matches.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.COMPLETED))
				.filter(s -> this.last1.equals(s.getEvaluatorLastName()))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionsByStudentId(this.student1)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());
		
		verify(this.repo).getSubmissionsByStudentId(this.student1);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByStudentAndTask1() {
		this.criteria.setStudentId(this.student2);
		this.criteria.setStatus(StatusUtil.WORKING);
		List<UUID> searchTasks = Arrays.asList(task2, task3);
		this.criteria.setTasks(searchTasks);
		
		List<SubmissionByStudentAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStudentId().equals(this.student2))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByStudentAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStudentAndTaskModel> filtered = matches.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.WORKING))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionByStudentByTasks(this.student2, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());
		
		verify(this.repo).getSubmissionByStudentByTasks(this.student2, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByStudentAndTask2() {
		this.criteria.setStudentId(this.student2);
		this.criteria.setStatus(StatusUtil.PENDING);
		List<UUID> searchTasks = Arrays.asList(task2, task3);
		this.criteria.setTasks(searchTasks);
		this.criteria.setDateRange(DateRange.TIMEFRAME_72_HOURS);
		
		Calendar searchCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.SERVER_ZONEID));
		searchCalendar.add(Calendar.DATE, DateRange.timeframe72Hours);
		Date searchRange = searchCalendar.getTime();
		
		List<SubmissionByStudentAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStudentId().equals(this.student2))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByStudentAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStudentAndTaskModel> filtered = matches.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.PENDING))
				.filter(s -> s.getDateUpdated().after(searchRange))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionByStudentByTasks(this.student2, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());
		
		verify(this.repo).getSubmissionByStudentByTasks(this.student2, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
	
	@Test
	public void testSearchByStudentAndTask3() {
		this.criteria.setStudentId(this.student2);
		this.criteria.setStatus(StatusUtil.COMPLETED);
		List<UUID> searchTasks = Arrays.asList(task2, task3);
		this.criteria.setTasks(searchTasks);
		this.criteria.setDateRange(DateRange.TIMEFRAME_30_DAYS);
		
		Calendar searchCalendar = Calendar.getInstance(TimeZone.getTimeZone(DateUtil.SERVER_ZONEID));
		searchCalendar.add(Calendar.DATE, DateRange.timeframe30Days);
		Date searchRange = searchCalendar.getTime();
		
		List<SubmissionByStudentAndTaskModel> matches = this.submissions.stream()
				.filter(s -> s.getStudentId().equals(this.student2))
				.filter(s -> searchTasks.contains(s.getTaskId()))
				.map(s -> new SubmissionByStudentAndTaskModel(s))
				.collect(Collectors.toList());
		
		List<SubmissionByStudentAndTaskModel> filtered = matches.stream()
				.filter(s -> s.getStatusGroup().equals(StatusUtil.COMPLETED))
				.filter(s -> s.getDateUpdated().after(searchRange))
				.collect(Collectors.toList());
		
		when(this.repo.getSubmissionByStudentByTasks(this.student2, searchTasks)).thenReturn(matches);
		
		List<DashboardSubmission> result = this.service.search(this.criteria);
		
		logger.info("matches size: " + matches.size() + " filtered size: " + filtered.size() + " result size: " + result.size());
		
		verify(this.repo).getSubmissionByStudentByTasks(this.student2, searchTasks);
		assertEquals(filtered.size(), result.size());
	}
}
