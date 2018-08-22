package edu.wgu.dmadmin.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dm.admin.controller.UserManagementController;
import edu.wgu.dm.admin.service.UserManagementService;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserListResponse;
import edu.wgu.dm.dto.security.UserResponse;
import edu.wgu.dm.util.DateUtil;
import edu.wgu.dm.util.IdentityUtil;
import edu.wgu.dmadmin.test.TestObjectFactory;

@RunWith(MockitoJUnitRunner.class)
public class UserManagementControllerTest {
    @InjectMocks
    private UserManagementController controller;

    @Mock
    private UserManagementService userService;

    @Mock
    private IdentityUtil iUtil;

    MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();
    Random random = new Random();
    private String userId = "123456";
    private Long taskId = random.nextLong();
    
    private Person person;
    private List<Long> teams;
    private User user;
   

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(this.controller).build();

        when(this.iUtil.getUserId()).thenReturn(this.userId);
        teams = new ArrayList<>();
        teams.add(random.nextLong());
        teams.add(random.nextLong());
        teams.add(random.nextLong());

        List<Long> tasks = new ArrayList<>();
        tasks.add(random.nextLong());
        tasks.add(random.nextLong());
        tasks.add(random.nextLong());

        List<String> roles = new ArrayList<>();
        roles.add("roles");

        List<String> permissions = new ArrayList<>();
        permissions.add("Student");

        List<Long> emaRoles = new ArrayList<>();
        emaRoles.add(random.nextLong());
        emaRoles.add(random.nextLong());
        emaRoles.add(random.nextLong());


        List<String> landings = new ArrayList<>();
        landings.add("hi");
        landings.add("there");
        landings.add("Jim!");


        this.person = new Person();
        this.person.setIsEmployee(Boolean.FALSE);
        this.person.setStudentId(this.userId);
        this.person.setFirstName("Bruce");
        this.person.setLastName("Wayne");
        this.person.setPidm(new Long(1234566));
        this.person.setUserInfo(TestObjectFactory.getUserModel().toUser());
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

        this.user = TestObjectFactory.getUser("Peter", "Parker", this.userId, emaRoles, permissions,
                tasks, landings, "234");
 
        this.user.setRoles(emaRoles);
        this.user.setTeams(this.teams);
        this.user.setLandings(landings);
        this.user.setPermissions(permissions);
        this.user.setTasks(tasks);
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

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
    public void testAddUsers() throws Exception {
        String url = "/v1/users";

        User[] userArray = new User[1];
        userArray[0] = this.user;

        doNothing().when(this.userService).addUsers("test", Arrays.asList(userArray));

        this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(userArray)))
                .andExpect(status().isNoContent())
                .andReturn();

        ArgumentCaptor<List> arg1 = ArgumentCaptor.forClass(List.class);

        verify(this.userService).addUsers(eq(this.userId), arg1.capture());
        assertEquals(Arrays.asList(userArray), arg1.getValue());
    }

    @Test
    public void testCreateUser() throws Exception {
        String userName = "pParker";
        String url = "/v1/users/" + userName;

        
        when(this.userService.createUser(userName)).thenReturn(this.user);

        MvcResult result = this.mockMvc.perform(post(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(this.user), result.getResponse().getContentAsString());

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

        ArgumentCaptor<Long> arg1 = ArgumentCaptor.forClass(Long.class);

        verify(this.userService).getUsersForTask(arg1.capture());
        assertEquals(this.taskId, arg1.getValue());
    }
}
