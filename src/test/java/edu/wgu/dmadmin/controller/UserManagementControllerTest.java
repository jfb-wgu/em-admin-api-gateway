package edu.wgu.dmadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.evaluator.UserListResponse;
import edu.wgu.dmadmin.domain.evaluator.UserResponse;
import edu.wgu.dmadmin.domain.security.LdapUser;
import edu.wgu.dmadmin.domain.security.Person;
import edu.wgu.dmadmin.domain.security.User;
import edu.wgu.dmadmin.model.security.UserModel;
import edu.wgu.dmadmin.service.DirectoryService;
import edu.wgu.dmadmin.service.UserManagementService;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.IdentityUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.naming.Name;
import javax.naming.ldap.LdapName;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementControllerTest {
    @InjectMocks
    private UserManagementController controller;

    @Mock
    private UserManagementService userService;

    @Mock
    private DirectoryService directoryService;

    @Mock
    private IdentityUtil iUtil;

    MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    private String userId = "123456";
    private UUID taskId = UUID.randomUUID();

    private Person person;
    private Set<UUID> teams;
    private User user;
    private UserModel userModel;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(this.controller).build();

        when(this.iUtil.getUserId()).thenReturn(this.userId);

        this.teams = new HashSet<>();
        this.teams.add(UUID.randomUUID());
        this.teams.add(UUID.randomUUID());
        this.teams.add(UUID.randomUUID());

        Set<UUID> tasks = new HashSet<>();
        tasks.add(UUID.randomUUID());
        tasks.add(UUID.randomUUID());
        tasks.add(UUID.randomUUID());

        Set<String> roles = new HashSet<>();
        roles.add("roles");

        Set<String> permissions = new HashSet<>();
        permissions.add("Student");

        Set<UUID> emaRoles = new HashSet<>();
        emaRoles.add(UUID.randomUUID());
        emaRoles.add(UUID.randomUUID());
        emaRoles.add(UUID.randomUUID());

        Set<String> landings = new HashSet<>();
        landings.add("hi");
        landings.add("there");
        landings.add("Jim!");

        this.person = new Person();
        this.person.setIsEmployee(false);
        this.person.setStudentId(this.userId);
        this.person.setFirstName("Bruce");
        this.person.setLastName("Wayne");
        this.person.setPidm(1234566L);
        this.person.setUserInfo(TestObjectFactory.getUserModel());
        this.person.setUsername("UserName");
        this.person.setEmaRoles(emaRoles);
        this.person.setLandings(landings);
        this.person.setLastLogin(DateUtil.getZonedNow());
        this.person.setPermissions(permissions);
        this.person.setPersonType("Student");
        this.person.setPrimaryPhone("123-555-5555");
        this.person.setRoles(roles);
        this.person.setTasks(tasks);
        this.person.setTeams(this.teams);
        this.person.setWguEmailAddress("bwayne@wgu.edu");

        this.userModel = TestObjectFactory.getUserModel("Peter", "Parker", this.userId, emaRoles, permissions, tasks, landings, "234");
        this.user = TestObjectFactory.getUser("Peter", "Parker", this.userId, emaRoles, permissions, tasks, landings, "234");

        this.userModel.setRoles(emaRoles);
        this.userModel.setTeams(teams);
        this.userModel.setLandings(landings);
        this.userModel.setPermissions(permissions);
        this.userModel.setTasks(tasks);

        this.user.setRoles(emaRoles);
        this.user.setTeams(teams);
        this.user.setLandings(landings);
        this.user.setPermissions(permissions);
        this.user.setTasks(tasks);
    }

    @Test
    public void testGetPerson() throws Exception {
        String url = "/v1/person";

        when(this.userService.getPersonFromRequest(any(HttpServletRequest.class), eq(this.userId))).thenReturn(this.person);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(this.person), result.getResponse().getContentAsString());

        ArgumentCaptor<HttpServletRequest> arg1 = ArgumentCaptor.forClass(HttpServletRequest.class);
        ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

        verify(this.userService).getPersonFromRequest(arg1.capture(), arg2.capture());
        assertEquals(org.springframework.mock.web.MockHttpServletRequest.class, arg1.getValue().getClass());
        assertEquals(this.userId, arg2.getValue());
    }

    @Test
    public void testGetPerson1() throws Exception {
        String url = "/v1/person/bannerId/" + this.userId;

        when(this.userService.getPersonByUserId(this.userId)).thenReturn(this.person);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(this.person), result.getResponse().getContentAsString());

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.userService).getPersonByUserId(arg1.capture());
    }

    @Test
    public void testGetUser() throws Exception {
        String url = "/v1/users/" + this.userId;

        UserResponse userResponse = new UserResponse(this.user);

        when(this.userService.getUser(this.userId)).thenReturn(this.user);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(userResponse), result.getResponse().getContentAsString());

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.userService).getUser(arg1.capture());
        assertEquals(this.userId, arg1.getValue());
    }

    @Test
    public void testAddUsers() throws Exception {
        String url = "/v1/users";

        User[] userArray = new User[1];
        userArray[0] = this.user;

        doNothing().when(this.userService).addUsers(Arrays.asList(userArray));

        this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(userArray)))
                .andExpect(status().isNoContent())
                .andReturn();

        ArgumentCaptor<List> arg1 = ArgumentCaptor.forClass(List.class);

        verify(this.userService).addUsers(arg1.capture());
        assertEquals(Arrays.asList(userArray), arg1.getValue());
    }

    @Test
    public void testCreateUser() throws Exception {
        String userName = "pParker";
        String url = "/v1/users/" + userName;

        this.userModel.setTeams(this.teams);

        User user = new User(this.userModel);

        when(this.userService.createUser(userName)).thenReturn(this.userModel);

        MvcResult result = this.mockMvc.perform(post(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(user), result.getResponse().getContentAsString());

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.userService).createUser(arg1.capture());
        assertEquals(userName, arg1.getValue());
    }

    @Test
    public void testDeleteEvaluator() throws Exception {
        String url = "/v1/users/" + this.userId;

        doNothing().when(this.userService).deleteUser(this.userId);

        this.mockMvc.perform(delete(url))
                .andExpect(status().isNoContent())
                .andReturn();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.userService).deleteUser(arg1.capture());
        assertEquals(this.userId, arg1.getValue());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        String url = "/v1/users";

        List<User> userList = new ArrayList<>();
        userList.add(this.user);

        UserListResponse listResponse = new UserListResponse(userList);

        when(this.userService.getUsers()).thenReturn(userList);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(listResponse), result.getResponse().getContentAsString());

        verify(this.userService).getUsers();
    }

    @Test
    public void testGetUsersForTask() throws Exception {
        String url = "/v1/users/task/" + this.taskId;

        List<User> userList = new ArrayList<>();
        userList.add(this.user);

        when(this.userService.getUsersForTask(this.taskId)).thenReturn(userList);

        this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

        verify(this.userService).getUsersForTask(arg1.capture());
        assertEquals(this.taskId, arg1.getValue());
    }

    @Test
    public void testGetMembersForGroup() throws Exception {
        String group = "admin";
        String url = "/v1/users/ldap/" + group;

        LdapName ldapName = new LdapName("cn=Mango,ou=Fruits,o=Food");

        Set<Name> names = new HashSet<>();
        names.add(ldapName);

        Set<LdapUser> ldapUsers = new HashSet<>();
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

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(ldapUsers), result.getResponse().getContentAsString());

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

        when(this.userService.getMissingUsers(group)).thenReturn(userSet);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.userService).getMissingUsers(arg1.capture());
        assertEquals(group, arg1.getValue());
    }

}