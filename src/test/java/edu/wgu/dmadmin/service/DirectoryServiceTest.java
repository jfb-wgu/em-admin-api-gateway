package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.wgu.dmadmin.domain.security.LdapGroup;
import edu.wgu.dmadmin.domain.security.LdapLookup;
import edu.wgu.dmadmin.domain.security.LdapUser;
import edu.wgu.dmadmin.domain.user.Person;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.test.TestObjectFactory;
import edu.wgu.dreammachine.model.publish.TaskByCourseModel;
import edu.wgu.dreammachine.model.security.RoleModel;
import edu.wgu.dreammachine.model.security.UserByIdModel;

public class DirectoryServiceTest {
	
	DirectoryService service = new DirectoryService();
	
	@Mock
	CassandraRepo repo;
	
	@Mock
	PersonService pService;
	
	@Mock
	LdapLookup lookup;
	
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
		this.service.setLdapLookup(this.lookup);

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
	public void testGetMissingUsers() throws InvalidNameException {
		LdapName name1 = new LdapName("cn=Mango,ou=Fruits,o=Food");
		
		LdapGroup group = new LdapGroup();
		group.getMembers().add(name1);
		group.setDn(new LdapName("cn=Banana,ou=Fruits,o=Food"));
		
		Set<Name> groups = new HashSet<>();
		groups.add(group.getDn());
		
		LdapUser ldap1 = new LdapUser();
		ldap1.setSAMAccountName("testing");
		ldap1.setGroups(groups);
		
		LdapUser ldap2 = new LdapUser();
		ldap2.setSAMAccountName("ldap2");
		ldap2.setGroups(groups);
		
		LdapUser ldap3 = new LdapUser();
		ldap3.setSAMAccountName("ldap3");
		ldap3.setGroups(groups);
		
		List<LdapUser> ldapUsers = new ArrayList<LdapUser>();
		ldapUsers.add(ldap1);
		ldapUsers.add(ldap2);
		ldapUsers.add(ldap3);
		
		Person person2 = new Person();
		person2.setIsEmployee(Boolean.TRUE);
		person2.setStudentId(this.user2.getUserId());
		
		when(this.lookup.getGroup(anyString())).thenReturn(group);
		when(this.lookup.getUsers(anyString())).thenReturn(ldapUsers);
		when(this.pService.getPersonByUsername("ldap2")).thenReturn(person2);
		when(this.repo.getUser(this.user2.getUserId())).thenReturn(Optional.empty());
		
		Set<Person> result = this.service.getMissingUsers("test");
		assertEquals(1, result.size());
		assertEquals("test2", result.iterator().next().getUserId());
	}
}
