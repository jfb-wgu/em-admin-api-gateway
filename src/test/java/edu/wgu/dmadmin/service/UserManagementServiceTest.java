package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.wgu.dmadmin.domain.user.Person;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.test.TestObjectFactory;
import edu.wgu.dreammachine.domain.security.User;
import edu.wgu.dreammachine.model.publish.TaskByCourseModel;
import edu.wgu.dreammachine.model.security.RoleModel;
import edu.wgu.dreammachine.model.security.UserByIdModel;

public class UserManagementServiceTest {
	
	UserManagementService service = new UserManagementService();
	
	@Mock
	CassandraRepo repo;
	
	@Mock
	PersonService pService;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	RoleModel role1 = TestObjectFactory.getRoleModel("role1");
	RoleModel role2 = TestObjectFactory.getRoleModel("role2");
	TaskByCourseModel task1 = new TaskByCourseModel(TestObjectFactory.getTaskModel());
	TaskByCourseModel task2 = new TaskByCourseModel(TestObjectFactory.getTaskModel());	
	UserByIdModel user1 = TestObjectFactory.getUserModel("test1", "testing1");
	UserByIdModel user2 = TestObjectFactory.getUserModel("test2", "testing2");
	Person person1;
	
	@Before
	public void initialize() {
		MockitoAnnotations.initMocks(this);
		this.service.setCassandraRepo(this.repo);
		this.service.setPersonService(this.pService);

		this.user1.getRoles().add(this.role1.getRoleId());
		this.user1.getTasks().add(this.task1.getTaskId());
		this.user2.getRoles().add(this.role2.getRoleId());
		this.user2.getTasks().add(this.task2.getTaskId());
		
		when(this.repo.getRoles()).thenReturn(Arrays.asList(this.role1, this.role2));
		when(this.repo.getTaskBasics()).thenReturn(Arrays.asList(this.task1, this.task2));
		when(this.repo.getUsers()).thenReturn(Arrays.asList(this.user1, this.user2));
		when(this.repo.getUser(this.user1.getUserId())).thenReturn(Optional.of(this.user1));
		
		this.person1 = new Person();
		this.person1.setFirstName("Bruce");
		this.person1.setLastName("Wayne");
		this.person1.setPidm(new Long(234234));
		this.person1.setIsEmployee(Boolean.TRUE);
		this.person1.setStudentId(this.user1.getUserId());
		when(this.pService.getPersonByBannerId(this.user1.getUserId())).thenReturn(this.person1);
		when(this.pService.getPersonByUsername("testing")).thenReturn(this.person1);
	}
	
	@Test
	public void testGetUser() {
		when(this.repo.getUser("123")).thenReturn(Optional.of(this.user1));
		this.service.getUser("123");
		verify(this.repo).getUser("123");
	}
	
	@Test
	public void testGetUserFail() {
		when(this.repo.getUser("none")).thenReturn(Optional.empty());
		
		this.thrown.expect(UserNotFoundException.class);
		this.service.getUser("none");
	}

	@Test
	public void testAddUser() {
		User evaluator = new User(this.user1);
		this.service.addUsers(Arrays.asList(evaluator));
		verify(this.repo).saveUser(new UserByIdModel(evaluator));
	}

	@Test
	public void testAddUsers() {
		List<User> users = Arrays.asList(this.user1, this.user2).stream().map(u -> new User(u)).collect(Collectors.toList());
		this.service.addUsers(users);
		
		ArgumentCaptor<UserByIdModel> argument = ArgumentCaptor.forClass(UserByIdModel.class);
		verify(this.repo, times(2)).saveUser(argument.capture());
		assertEquals("testing1", argument.getAllValues().get(0).getEmployeeId());
		assertEquals("testing2", argument.getAllValues().get(1).getEmployeeId());
	}

	@Test
	public void testDeleteEvaluator() {
		this.service.deleteUser("123");
		verify(this.repo).deleteUser("123");
	}

	@Test
	public void testGetUsers() {
		List<User> result = this.service.getUsers();
		assertEquals(2, result.size());
		assertEquals(1, result.get(0).getRoleNames().size());
		assertTrue(result.get(0).getRoleNames().contains("role1"));
		assertTrue(result.get(1).getTaskNames().contains("A1A1-Task Name"));
	}
	
	@Test
	public void testGetUsersMissingData() {
		when(this.repo.getRoles()).thenReturn(Arrays.asList(this.role1));
		when(this.repo.getTaskBasics()).thenReturn(Arrays.asList(this.task2));
		List<User> result = this.service.getUsers();
		assertEquals(2, result.size());
		assertEquals(1, result.get(0).getRoleNames().size());
		assertTrue(result.get(0).getRoleNames().contains("role1"));
		assertTrue(result.get(1).getTaskNames().contains("A1A1-Task Name"));
	}
	
	@Test
	public void testGetUsersForTask() {
		List<User> result = this.service.getUsersForTask(this.task2.getTaskId());
		assertEquals(result.get(0).getUserId(), this.user2.getUserId());
	}
	
	@Test
	public void testCreateUser() {
		this.service.createUser("testing");
		
		ArgumentCaptor<UserByIdModel> argument = ArgumentCaptor.forClass(UserByIdModel.class);
		verify(this.repo).saveUser(argument.capture());
		assertEquals("Bruce", argument.getValue().getFirstName());
		assertEquals("Wayne", argument.getValue().getLastName());
	}
}
