package edu.wgu.dmadmin.service;

import static edu.wgu.dmadmin.util.StatusUtil.AUTHOR_WORK_EVALUATED;
import static edu.wgu.dmadmin.util.StatusUtil.AUTHOR_WORK_NEEDS_REVISION;
import static edu.wgu.dmadmin.util.StatusUtil.AUTHOR_WORK_RESUBMITTED;
import static edu.wgu.dmadmin.util.StatusUtil.AUTHOR_WORK_SUBMITTED;
import static edu.wgu.dmadmin.util.StatusUtil.EVALUATION_BEGUN;
import static edu.wgu.dmadmin.util.StatusUtil.EVALUATION_CANCELLED;
import static edu.wgu.dmadmin.util.StatusUtil.EVALUATION_RELEASED;
import static edu.wgu.dmadmin.util.StatusUtil.EVALUATION_TAKEN_OVER;
import static edu.wgu.dmadmin.util.StatusUtil.OPEN_HOLD;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;

import edu.wgu.dmadmin.domain.search.SearchCriteria;
import edu.wgu.dmadmin.model.security.UserByFirstNameModel;
import edu.wgu.dmadmin.model.security.UserByLastNameModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.test.TestObjectFactory;
import edu.wgu.dmadmin.util.StatusUtil;
import edu.wgu.dreammachine.model.submission.SubmissionModel;

public class SearchServiceTest {
	
	protected SearchService service = new SearchService();
	
	protected CassandraRepo repo = mock(CassandraRepo.class);
	protected SearchCriteria criteria;
	
	protected SubmissionModel submission = TestObjectFactory.getSubmissionModel(StatusUtil.AUTHOR_WORK_EVALUATED);
	
	protected String student1 = "student1";
	protected String student2 = "student2";
	protected String student3 = "student3";
	protected String first1 = "first1";
	protected String first2 = "first2";
	protected String first3 = "first3";
	protected String last1 = "last1";
	protected String last2 = "last2";
	protected String last3 = "last3";
	protected UUID task1 = UUID.randomUUID();
	protected UUID task2 = UUID.randomUUID();
	protected UUID task3 = UUID.randomUUID();
	protected String status1 = AUTHOR_WORK_SUBMITTED;
	protected String status2 = AUTHOR_WORK_RESUBMITTED;
	protected String status3 = EVALUATION_BEGUN;
	protected String status4 = AUTHOR_WORK_EVALUATED;
	protected String status5 = EVALUATION_RELEASED;
	protected String status6 = EVALUATION_TAKEN_OVER;
	protected String status7 = EVALUATION_CANCELLED;
	protected String status8 = AUTHOR_WORK_NEEDS_REVISION;
	protected String status9 = OPEN_HOLD;
	
	protected List<String> studentIds = Arrays.asList(student1, student2, student3);
	protected List<String> firstNames = Arrays.asList(first1, first2, first3);
	protected List<String> lastNames = Arrays.asList(last1, last2, last3);
	protected List<String> evalIds = Arrays.asList("E00485967", "E00485686", "E00348585");
	protected List<String> statuses = Arrays.asList(status1, status2, status3, status4, status5, status6, status7, status8);
	protected List<UUID> tasks = Arrays.asList(task1, task2, task3);
	
	protected List<SubmissionModel> submissions = TestObjectFactory.getSubmissions(studentIds, firstNames, lastNames, statuses, tasks, evalIds, 500);

	@Before
	public void initialize() {
		this.service.setCassandraRepo(repo);
		this.criteria = new SearchCriteria();
	}
	
	protected List<UserByFirstNameModel> getUsersByFirstName(String firstName) {
		return getUserByFirstNameModels().stream()
				.filter(u -> u.getFirstName().equals(firstName))
				.collect(Collectors.toList());
	}
	
	protected List<UserByLastNameModel> getUsersByLastName(String lastName) {
		return getUserByLastNameModels().stream()
				.filter(u -> u.getLastName().equals(lastName))
				.collect(Collectors.toList());
	}
	
	protected List<String> getUserIdsByFirstName(String firstName) {
		return getUsersByFirstName(firstName).stream()
				.map(u -> u.getUserId())
				.collect(Collectors.toList());
	}
	
	protected List<String> getUserIdsByLastName(String lastName) {
		return getUsersByLastName(lastName).stream()
				.map(u -> u.getUserId())
				.collect(Collectors.toList());
	}
	
	protected List<String> getUserIdsByFirstAndLastName(String firstName, String lastName) {
		return getUsersByLastName(lastName).stream()
				.filter(u -> u.getFirstName().equals(firstName))
				.map(u -> u.getUserId())
				.collect(Collectors.toList());
	}
	
	protected List<UserByFirstNameModel> getUserByFirstNameModels() {
		List<UserByFirstNameModel> models = new ArrayList<UserByFirstNameModel>();
		
		firstNames.forEach(firstName -> {
			lastNames.forEach(lastName -> {
				UserByFirstNameModel user = new UserByFirstNameModel();
				user.setFirstName(firstName);
				user.setLastName(lastName);
				user.setUserId(firstName + lastName);
				models.add(user);
			});
		});

		return models;
	}
	
	protected List<UserByLastNameModel> getUserByLastNameModels() {
		List<UserByLastNameModel> models = new ArrayList<UserByLastNameModel>();
		
		firstNames.forEach(firstName -> {
			lastNames.forEach(lastName -> {
				UserByLastNameModel user = new UserByLastNameModel();
				user.setFirstName(firstName);
				user.setLastName(lastName);
				user.setUserId(firstName + lastName);
				models.add(user);
			});
		});

		return models;
	}
}
