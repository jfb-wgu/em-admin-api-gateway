package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import edu.wgu.dm.dto.security.LdapGroup;
import edu.wgu.dm.dto.security.LdapUser;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.entity.publish.TaskEntity;
import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.entity.security.UserEntity;
import edu.wgu.dm.repository.admin.AdminRepository;
import edu.wgu.dm.service.admin.DirectoryService;
import edu.wgu.dm.service.admin.PersonService;
import edu.wgu.dm.service.ldap.LdapLookup;
import edu.wgu.dmadmin.test.TestObjectFactory;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryServiceTest {
    @Mock
    AdminRepository repo;

    @Mock
    AdminRepository adminrepo;

    @Mock
    PersonService personService;

    @Mock
    LdapLookup lookup;

    DirectoryService service;

    RoleEntity role1 = TestObjectFactory.getRoleModel("role1");
    RoleEntity role2 = TestObjectFactory.getRoleModel("role2");

    TaskEntity task1 = TestObjectFactory.getTaskModel();
    TaskEntity task2 = TestObjectFactory.getTaskModel();
    UserEntity user1 = TestObjectFactory.getUserModel("test1", "testing1");
    UserEntity user2 = TestObjectFactory.getUserModel("test2", "testing2");
    Person person1;


    @Before
    public void initialize() {
        service = new DirectoryService(personService, repo, lookup);
        this.user1.getRoles().add(this.role1);
        this.user1.getTasks().add(this.task1);
        this.user2.getRoles().add(this.role2);
        this.user2.getTasks().add(this.task2);
        this.person1 = new Person();
        this.person1.setFirstName("Bruce");
        this.person1.setLastName("Wayne");
        this.person1.setPidm(new Long(234234));
        this.person1.setIsEmployee(Boolean.TRUE);
        this.person1.setStudentId(this.user1.getUserId());
        when(this.personService.getPersonByBannerId(this.user1.getUserId()))
                .thenReturn(this.person1);
        when(this.personService.getPersonByUsername("testing")).thenReturn(this.person1);
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
        when(this.personService.getPersonByUsername("ldap2")).thenReturn(person2);
        when(this.repo.getUserById(this.user2.getUserId())).thenReturn(Optional.empty());

        Set<Person> result = this.service.getMissingUsers("test");
        assertEquals(1, result.size());
        assertEquals("test2", result.iterator().next().getUserId());
    }
}
