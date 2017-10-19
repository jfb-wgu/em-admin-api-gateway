package edu.wgu.dmadmin.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.security.LdapUser;
import edu.wgu.dmadmin.domain.security.Person;
import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.exception.UserNotFoundException;
import edu.wgu.dmadmin.model.publish.TaskByCourseModel;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;

public class UserManagementServiceTest {
	
	UserManagementService service = new UserManagementService();
	
	@Mock
	CassandraRepo repo;
	
	@Mock
	PersonService pService;
	
	@Mock
	DirectoryService dService;
	
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
		this.service.setCassandraRepo(repo);
		this.service.setDirectoryService(dService);
		this.service.setPersonService(pService);

		user1.getRoles().add(role1.getRoleId());
		user1.getTasks().add(task1.getTaskId());
		user2.getRoles().add(role2.getRoleId());
		user2.getTasks().add(task2.getTaskId());
		
		when(this.repo.getRoles()).thenReturn(Arrays.asList(role1, role2));
		when(this.repo.getTaskBasics()).thenReturn(Arrays.asList(task1, task2));
		when(this.repo.getUsers()).thenReturn(Arrays.asList(user1, user2));
		when(this.repo.getUser(user1.getUserId())).thenReturn(Optional.of(user1));
		
		this.person1 = new Person();
		this.person1.setFirstName("Bruce");
		this.person1.setLastName("Wayne");
		this.person1.setPidm(new Long(234234));
		this.person1.setIsEmployee(Boolean.TRUE);
		this.person1.setStudentId(user1.getUserId());
		when(this.pService.getPersonByBannerId(user1.getUserId())).thenReturn(this.person1);
		when(this.pService.getPersonByUsername("testing")).thenReturn(this.person1);
	}
	
	@Test
	public void testGetUser() {
		when(repo.getUser("123")).thenReturn(Optional.of(user1));
		service.getUser("123");
		verify(repo).getUser("123");
	}
	
	@Test
	public void testGetUserFail() {
		when(repo.getUser("none")).thenReturn(Optional.empty());
		
		thrown.expect(UserNotFoundException.class);
		service.getUser("none");
	}

	@Test
	public void testAddUser() {
		User evaluator = new User(user1);
		this.service.addUsers(Arrays.asList(evaluator));
		verify(repo).saveUser(new UserByIdModel(evaluator));
	}

	@Test
	public void testAddUsers() {
		List<User> users = Arrays.asList(user1, user2).stream().map(u -> new User(u)).collect(Collectors.toList());
		this.service.addUsers(users);
		
		ArgumentCaptor<UserByIdModel> argument = ArgumentCaptor.forClass(UserByIdModel.class);
		verify(repo, times(2)).saveUser(argument.capture());
		assertEquals("testing1", argument.getAllValues().get(0).getEmployeeId());
		assertEquals("testing2", argument.getAllValues().get(1).getEmployeeId());
	}

	@Test
	public void testDeleteEvaluator() {
		service.deleteUser("123");
		verify(repo).deleteUser("123");
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
		when(this.repo.getRoles()).thenReturn(Arrays.asList(role1));
		when(this.repo.getTaskBasics()).thenReturn(Arrays.asList(task2));
		List<User> result = this.service.getUsers();
		assertEquals(2, result.size());
		assertEquals(1, result.get(0).getRoleNames().size());
		assertTrue(result.get(0).getRoleNames().contains("role1"));
		assertTrue(result.get(1).getTaskNames().contains("A1A1-Task Name"));
	}
	
	@Test
	public void testGetUsersForTask() {
		List<User> result = this.service.getUsersForTask(task2.getTaskId());
		assertEquals(result.get(0).getUserId(), user2.getUserId());
	}
	
	@Test
	public void testGetPersonFromRequestEmployee() throws ParseException {
		String authToken = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImFjY2Vzcy5kZXYud2d1LmVkdSJ9.eyJzY29wZSI6W10sImNsaWVudF9pZCI6IndndV9tb2JpbGUiLCJ3Z3VVVUlEIjoiZjI3M2IzYTQtMWM2OC00MDdiLTllZTEtMmQxNDM2MTdkOTc5Iiwid2d1QmFubmVySUQiOiJFMDAxMDc0NDgiLCJiYW5uZXJfaWQiOiJFMDAxMDc0NDgiLCJwaWRtIjoiNTQ2NDQxIiwiZ2l2ZW5OYW1lIjoiSmVzc2ljYSIsIndndUxldmVsT25lUm9sZSI6IkVtcGxveWVlIiwid2d1UElETSI6IjU0NjQ0MSIsIndndV9yb2xlX29uZSI6IkVtcGxveWVlIiwiY24iOiJKZXNzaWNhIFBhbWRldGgiLCJzbiI6IlBhbWRldGgiLCJ1c2VybmFtZSI6Implc3NpY2EucGFtZGV0aCIsImV4cCI6MTUwNjY0MDUwOH0.BgidTYhM-9hMTDhOjqjoo2wfybqsuhH7WuRsAGty-edY6l162LNVIoOFZboo1jCtb-hxbZYIDHXe_efa1K9fqTQJQ1lbq8TpgGbyTOJ3jjffj7YHw0n-qLR1c2DcwMi1d_N3ytd7kCC65E9-SUbr2dcGx5fsbIhNW5Zqpu3P9IIIjuueNbbBqMFxK4sTgiCStaLPJH2qH3iNGUTi29zaulV5zhIXQmtjsJrp54K62PR8wOKN4FUNDDPwLLbOr8R2abOtGV_SVkTcWHs500KnTFXhkgVO9HtBj-sz10HONUIPU3OlPh3jjWnwt6SyXc-1otY71oEDK4X5bK7HFRmScw";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("authorization", authToken);
		when(this.repo.getUser("E00107448")).thenReturn(Optional.of(user1));
		Person result = this.service.getPersonFromRequest(request, user1.getUserId());
		assertEquals("Jessica", result.getFirstName());
	}

	@Test
	public void testGetPersonFromRequestStudent() throws ParseException {
		String authToken = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImFjY2Vzcy5kZXYud2d1LmVkdSJ9.eyJzY29wZSI6W10sImNsaWVudF9pZCI6IndndV9tb2JpbGUiLCJ3Z3VVVUlEIjoiZWM3ZWQ4MGMtNGRlNS00NWFjLTgwZDEtZDdkYmNlMTMzOTMyIiwiYmFubmVyX2lkIjoiUUEwMDAwMDEzIiwiZ2l2ZW5OYW1lIjoiS3Jpc3RpbiIsImNuIjoiS3Jpc3RpbiBQb2luZGV4dGVyIiwid2d1TGV2ZWxUd29Sb2xlIjoiU3R1ZGVudCIsIndndUJhbm5lcklEIjoiUUEwMDAwMDEzIiwid2d1X3JvbGVfdHdvIjoiU3R1ZGVudCIsInBpZG0iOiIxMDY4MDYiLCJ3Z3VMZXZlbE9uZVJvbGUiOiJTdHVkZW50Iiwid2d1UElETSI6IjEwNjgwNiIsIndndV9yb2xlX29uZSI6IlN0dWRlbnQiLCJzbiI6IlBvaW5kZXh0ZXIiLCJ1c2VybmFtZSI6Imtwb2luZGV4dGVyIiwiZXhwIjoxNTA2NjQwNzM1fQ.CY4mwbLl9Q8aphSdgI3uLaCOPpam7CRaaY09tmKCb0mfhE229UTvs_DSpKbgOCdrS-i5EWcsaBcyw7b3hIBegBRru-EuhYGAxjWtZxL9Rj9-zT7ijpEk8a6V8mGZH-CXhOHE_abQXlW818lZOlS-ZeLgaAIW4BUnQm0Bvl-CYHXIfZmR-wsl_ZY1hO27qxW8Gb8YSXw9AZnqgjFKSiWiKBoYrlCYmsUU5vD7WSqArNcIifWnxfjKrmCCQSMD4DoxAZwyIphB-aM1MjRkjMYoRmDe3I3eorjPhp6ZvBbyp5VWUIHf2b7RHhVyJFBiN8fo_55rcNYZYdYMEuJWiSq4dw";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("authorization", authToken);
		Person result = this.service.getPersonFromRequest(request, user1.getUserId());
		assertEquals("Kristin", result.getFirstName());
	}
	
	@Test
	public void testGetPersonByUserId() {
		this.person1.setIsEmployee(Boolean.FALSE);
		
		Person result = this.service.getPersonByUserId(user1.getUserId());
		verify(this.repo, never()).getUser(user1.getUserId());
		assertEquals(result.getPidm(), person1.getPidm());
	}
	
	@Test
	public void testGetPersonByUserIdNotFound() {
		this.person1.setIsEmployee(Boolean.TRUE);
		when(this.repo.getUser(user1.getUserId())).thenReturn(Optional.empty());
		
		thrown.expect(UserNotFoundException.class);
		this.service.getPersonByUserId(user1.getUserId());
	}
	
	@Test
	public void testGetPersonByUserIdEmployee() {
		this.person1.setIsEmployee(Boolean.TRUE);
		
		Person result = this.service.getPersonByUserId(user1.getUserId());
		verify(this.repo).getUser(user1.getUserId());
		assertEquals(result.getPidm(), person1.getPidm());
	}
	
	@Test
	public void testCreateUser() {
		this.service.createUser("testing");
		
		ArgumentCaptor<UserByIdModel> argument = ArgumentCaptor.forClass(UserByIdModel.class);
		verify(this.repo).saveUser(argument.capture());
		assertEquals("Bruce", argument.getValue().getFirstName());
		assertEquals("Wayne", argument.getValue().getLastName());
	}
	
	@Test
	public void testGetMissingUsers() {
		LdapUser ldap1 = new LdapUser();
		ldap1.setSAMAccountName("testing");
		LdapUser ldap2 = new LdapUser();
		ldap2.setSAMAccountName("ldap2");
		LdapUser ldap3 = new LdapUser();
		ldap3.setSAMAccountName("ldap3");
		Set<LdapUser> ldapUsers = new HashSet<LdapUser>();
		ldapUsers.add(ldap1);
		ldapUsers.add(ldap2);
		ldapUsers.add(ldap3);
		
		Person person2 = new Person();
		person2.setIsEmployee(Boolean.TRUE);
		person2.setStudentId(user2.getUserId());
		
		when(this.dService.getMembersForGroup(anyString())).thenReturn(ldapUsers);
		when(this.pService.getPersonByUsername("ldap2")).thenReturn(person2);
		when(this.repo.getUser(user2.getUserId())).thenReturn(Optional.empty());
		
		Set<Person> result = this.service.getMissingUsers("group");
		assertEquals(1, result.size());
	}
}
