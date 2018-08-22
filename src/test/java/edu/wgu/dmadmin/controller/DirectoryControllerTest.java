package edu.wgu.dmadmin.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.naming.Name;
import javax.naming.ldap.LdapName;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dm.admin.controller.DirectoryController;
import edu.wgu.dm.admin.service.DirectoryService;
import edu.wgu.dm.dto.security.LdapUser;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.util.DateUtil;
import edu.wgu.dm.util.IdentityUtil;

@RunWith(MockitoJUnitRunner.class)
public class DirectoryControllerTest {

    @InjectMocks
    private DirectoryController controller;

    @Mock
    private DirectoryService directoryService;

    @Mock
    private IdentityUtil iUtil;

    MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    private String userId = "123456";

    private Person person;
   
    @Before
    public void setUp() throws Exception {
         this.mockMvc = standaloneSetup(this.controller).build();
        when(this.iUtil.getUserId()).thenReturn(this.userId);
        this.person = new Person();
        this.person.setIsEmployee(Boolean.FALSE);
        this.person.setStudentId(this.userId);
        this.person.setFirstName("Bruce");
        this.person.setLastName("Wayne");
        this.person.setPidm(new Long(1234566));
        this.person.setUsername("UserName");
        this.person.setLastLogin(DateUtil.getZonedNow());
        this.person.setPersonType("Student");
        this.person.setPrimaryPhone("123-555-5555");
        this.person.setWguEmailAddress("bwayne@wgu.edu");
    }

    @Test
    public void testGetMembersForGroup() throws Exception {
        String group = "admin";
        String url = "/v1/users/ldap/" + group;

        LdapName ldapName = new LdapName("cn=Mango,ou=Fruits,o=Food");

        Set<Name> names = new HashSet<>();
        names.add(ldapName);

        List<LdapUser> ldapUsers = new ArrayList<>();
        LdapUser ldapUser = new LdapUser();
        ldapUser.setSAMAccountName("Accoutname");
        ldapUser.setGivenName("givenName");
        ldapUser.setGroups(names);
        ldapUser.setDn(ldapName);
        ldapUser.setMailNickname("MainNickName");
        ldapUser.setName("Name");
        ldapUser.setSn("sn");
        ldapUser.setUserPrincipalName("Priciple Name");
        ldapUsers.add(ldapUser);

        when(this.directoryService.getMembersForGroup(group)).thenReturn(ldapUsers);

        MvcResult result = this.mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        assertEquals(this.mapper.writeValueAsString(ldapUsers),
                result.getResponse().getContentAsString());

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.directoryService).getMembersForGroup(arg1.capture());
        assertEquals(group, arg1.getValue());
    }

    @Test
    public void testGetMissingGroupMembers() throws Exception {
        String group = "admin";
        String url = "/v1/users/ldap/" + group + "/missing";

        Set<Person> userSet = new HashSet<>();
        userSet.add(this.person);

        when(this.directoryService.getMissingUsers(group)).thenReturn(userSet);

        this.mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.directoryService).getMissingUsers(arg1.capture());
        assertEquals(group, arg1.getValue());
    }

}
