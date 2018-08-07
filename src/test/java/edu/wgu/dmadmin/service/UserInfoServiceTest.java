package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import edu.wgu.dm.admin.domain.person.Person;
import edu.wgu.dm.admin.exception.UserNotFoundException;
import edu.wgu.dm.admin.model.publish.TaskModel;
import edu.wgu.dm.admin.model.security.RoleModel;
import edu.wgu.dm.admin.model.security.UserModel;
import edu.wgu.dm.admin.repo.CassandraRepo;
import edu.wgu.dm.admin.service.PersonService;
import edu.wgu.dmadmin.test.TestObjectFactory;

public class UserInfoServiceTest {
	
	UserInfoService service = new UserInfoService();
	
	@Mock
	CassandraRepo repo;
	
	@Mock
	PersonService pService;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	RoleModel role1 = TestObjectFactory.getRoleModel("role1");
	RoleModel role2 = TestObjectFactory.getRoleModel("role2");

	TaskModel task1 = TestObjectFactory.getTaskModel();
	TaskModel task2 = TestObjectFactory.getTaskModel();
	UserModel user1 = TestObjectFactory.getUserModel("test1", "testing1");
	UserModel user2 = TestObjectFactory.getUserModel("test2", "testing2");
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
		when(this.repo.getUserModel(this.user1.getUserId())).thenReturn(Optional.of(this.user1));
		
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
	public void testGetPersonFromRequestEmployee() throws ParseException {
		String authToken = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImFjY2Vzcy5kZXYud2d1LmVkdSJ9.eyJzY29wZSI6W10sImNsaWVudF9pZCI6IndndV9tb2JpbGUiLCJ3Z3VVVUlEIjoiZjI3M2IzYTQtMWM2OC00MDdiLTllZTEtMmQxNDM2MTdkOTc5Iiwid2d1QmFubmVySUQiOiJFMDAxMDc0NDgiLCJiYW5uZXJfaWQiOiJFMDAxMDc0NDgiLCJwaWRtIjoiNTQ2NDQxIiwiZ2l2ZW5OYW1lIjoiSmVzc2ljYSIsIndndUxldmVsT25lUm9sZSI6IkVtcGxveWVlIiwid2d1UElETSI6IjU0NjQ0MSIsIndndV9yb2xlX29uZSI6IkVtcGxveWVlIiwiY24iOiJKZXNzaWNhIFBhbWRldGgiLCJzbiI6IlBhbWRldGgiLCJ1c2VybmFtZSI6Implc3NpY2EucGFtZGV0aCIsImV4cCI6MTUwNjY0MDUwOH0.BgidTYhM-9hMTDhOjqjoo2wfybqsuhH7WuRsAGty-edY6l162LNVIoOFZboo1jCtb-hxbZYIDHXe_efa1K9fqTQJQ1lbq8TpgGbyTOJ3jjffj7YHw0n-qLR1c2DcwMi1d_N3ytd7kCC65E9-SUbr2dcGx5fsbIhNW5Zqpu3P9IIIjuueNbbBqMFxK4sTgiCStaLPJH2qH3iNGUTi29zaulV5zhIXQmtjsJrp54K62PR8wOKN4FUNDDPwLLbOr8R2abOtGV_SVkTcWHs500KnTFXhkgVO9HtBj-sz10HONUIPU3OlPh3jjWnwt6SyXc-1otY71oEDK4X5bK7HFRmScw";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("authorization", authToken);
		when(this.repo.getUserModel("E00107448")).thenReturn(Optional.of(this.user1));
		Person result = this.service.getPersonFromRequest(request, this.user1.getUserId());
		assertEquals("Jessica", result.getFirstName());
	}

	@Test
	public void testGetPersonFromRequestStudent() throws ParseException {
		String authToken = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImFjY2Vzcy5kZXYud2d1LmVkdSJ9.eyJzY29wZSI6W10sImNsaWVudF9pZCI6IndndV9tb2JpbGUiLCJ3Z3VVVUlEIjoiZWM3ZWQ4MGMtNGRlNS00NWFjLTgwZDEtZDdkYmNlMTMzOTMyIiwiYmFubmVyX2lkIjoiUUEwMDAwMDEzIiwiZ2l2ZW5OYW1lIjoiS3Jpc3RpbiIsImNuIjoiS3Jpc3RpbiBQb2luZGV4dGVyIiwid2d1TGV2ZWxUd29Sb2xlIjoiU3R1ZGVudCIsIndndUJhbm5lcklEIjoiUUEwMDAwMDEzIiwid2d1X3JvbGVfdHdvIjoiU3R1ZGVudCIsInBpZG0iOiIxMDY4MDYiLCJ3Z3VMZXZlbE9uZVJvbGUiOiJTdHVkZW50Iiwid2d1UElETSI6IjEwNjgwNiIsIndndV9yb2xlX29uZSI6IlN0dWRlbnQiLCJzbiI6IlBvaW5kZXh0ZXIiLCJ1c2VybmFtZSI6Imtwb2luZGV4dGVyIiwiZXhwIjoxNTA2NjQwNzM1fQ.CY4mwbLl9Q8aphSdgI3uLaCOPpam7CRaaY09tmKCb0mfhE229UTvs_DSpKbgOCdrS-i5EWcsaBcyw7b3hIBegBRru-EuhYGAxjWtZxL9Rj9-zT7ijpEk8a6V8mGZH-CXhOHE_abQXlW818lZOlS-ZeLgaAIW4BUnQm0Bvl-CYHXIfZmR-wsl_ZY1hO27qxW8Gb8YSXw9AZnqgjFKSiWiKBoYrlCYmsUU5vD7WSqArNcIifWnxfjKrmCCQSMD4DoxAZwyIphB-aM1MjRkjMYoRmDe3I3eorjPhp6ZvBbyp5VWUIHf2b7RHhVyJFBiN8fo_55rcNYZYdYMEuJWiSq4dw";
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("authorization", authToken);
		Person result = this.service.getPersonFromRequest(request, this.user1.getUserId());
		assertEquals("Kristin", result.getFirstName());
	}
	
	@Test
	public void testGetPersonByUserId() {
		this.person1.setIsEmployee(Boolean.FALSE);
		
		Person result = this.service.getPersonByUserId(this.user1.getUserId());
		verify(this.repo, never()).getUserModel(this.user1.getUserId());
		assertEquals(result.getPidm(), this.person1.getPidm());
	}
	
	@Test
	public void testGetPersonByUserIdNotFound() {
		this.person1.setIsEmployee(Boolean.TRUE);
		when(this.repo.getUserModel(this.user1.getUserId())).thenReturn(Optional.empty());
		
		this.thrown.expect(UserNotFoundException.class);
		this.service.getPersonByUserId(this.user1.getUserId());
	}
	
	@Test
	public void testGetPersonByUserIdEmployee() {
		this.person1.setIsEmployee(Boolean.TRUE);
		
		Person result = this.service.getPersonByUserId(this.user1.getUserId());
		verify(this.repo).getUserModel(this.user1.getUserId());
		assertEquals(result.getPidm(), this.person1.getPidm());
	}
}
