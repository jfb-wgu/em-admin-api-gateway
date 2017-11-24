package edu.wgu.dmadmin.service;

import static edu.wgu.dreammachine.util.StatusUtil.AUTHOR_WORK_EVALUATED;
import static edu.wgu.dreammachine.util.StatusUtil.AUTHOR_WORK_NEEDS_REVISION;
import static edu.wgu.dreammachine.util.StatusUtil.AUTHOR_WORK_RESUBMITTED;
import static edu.wgu.dreammachine.util.StatusUtil.AUTHOR_WORK_SUBMITTED;
import static edu.wgu.dreammachine.util.StatusUtil.EVALUATION_BEGUN;
import static edu.wgu.dreammachine.util.StatusUtil.EVALUATION_CANCELLED;
import static edu.wgu.dreammachine.util.StatusUtil.EVALUATION_RELEASED;
import static edu.wgu.dreammachine.util.StatusUtil.EVALUATION_TAKEN_OVER;
import static edu.wgu.dreammachine.util.StatusUtil.OPEN_HOLD;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Before;

import edu.wgu.dmadmin.domain.search.SearchCriteria;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.test.TestObjectFactory;
import edu.wgu.dreammachine.model.security.UserByFirstNameModel;
import edu.wgu.dreammachine.model.security.UserByLastNameModel;
import edu.wgu.dreammachine.model.submission.SubmissionModel;
import edu.wgu.dreammachine.util.StatusUtil;

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
	
	protected List<String> studentIds = Arrays.asList(this.student1, this.student2, this.student3);
	protected List<String> firstNames = Arrays.asList(this.first1, this.first2, this.first3);
	protected List<String> lastNames = Arrays.asList(this.last1, this.last2, this.last3);
	protected List<String> evalIds = Arrays.asList("E00485967", "E00485686", "E00348585");
	protected List<String> statuses = Arrays.asList(this.status1, this.status2, this.status3, this.status4, this.status5, this.status6, this.status7, this.status8);
	protected List<UUID> tasks = Arrays.asList(this.task1, this.task2, this.task3);
	
	protected List<SubmissionModel> submissions = TestObjectFactory.getSubmissions(this.studentIds, this.firstNames, this.lastNames, this.statuses, this.tasks, this.evalIds, 500);

	@Before
	public void initialize() {
		this.service.setCassandraRepo(this.repo);
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
		
		this.firstNames.forEach(firstName -> {
			this.lastNames.forEach(lastName -> {
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
		
		this.firstNames.forEach(firstName -> {
			this.lastNames.forEach(lastName -> {
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
