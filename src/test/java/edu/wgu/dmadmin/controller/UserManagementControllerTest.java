package edu.wgu.dmadmin.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import java.util.Arrays;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dm.admin.controller.UserManagementController;
import edu.wgu.dm.admin.service.UserManagementService;
import edu.wgu.dm.dto.response.UserListResponse;
import edu.wgu.dm.dto.response.UserResponse;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserSummary;
import edu.wgu.dm.util.IdentityUtil;
import edu.wgu.dmadmin.test.TestObjectFactory;

@SuppressWarnings("boxing")
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

    private User user = TestObjectFactory.getUser("Test", "User");

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(this.controller).build();

        when(this.iUtil.getUserId()).thenReturn(this.user.getUserId());
    }

    @Test
    public void testGetUser() throws Exception {
        String url = "/v1/users/" + this.user.getUserId();

        UserResponse userResponse = new UserResponse(this.user);

        when(this.userService.getUser(this.user.getUserId())).thenReturn(this.user);

        MvcResult result = this.mockMvc.perform(get(url))
                                       .andExpect(status().isOk())
                                       .andReturn();

        assertEquals(this.mapper.writeValueAsString(userResponse), result.getResponse()
                                                                         .getContentAsString());

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.userService).getUser(arg1.capture());
        assertEquals(this.user.getUserId(), arg1.getValue());
    }

    @Test
    public void testAddUsers() throws Exception {
        String url = "/v1/users";
        User newUser = TestObjectFactory.getUser("Test", "User");

        newUser.getTasks()
               .add(this.random.nextLong());

        this.mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                                      .content(this.mapper.writeValueAsString(newUser)))
                    .andExpect(status().isNoContent())
                    .andReturn();

        ArgumentCaptor<User> arg1 = ArgumentCaptor.forClass(User.class);

        verify(this.userService).saveUser(eq(this.user.getUserId()), arg1.capture());
        assertEquals(newUser, arg1.getValue());
    }

    @Test
    public void testCreateUser() throws Exception {
        String userName = "pParker";
        String url = "/v1/users/" + userName;

        when(this.userService.createUser(userName)).thenReturn(this.user);

        MvcResult result = this.mockMvc.perform(post(url))
                                       .andExpect(status().isOk())
                                       .andReturn();

        assertEquals(this.mapper.writeValueAsString(this.user), result.getResponse()
                                                                      .getContentAsString());

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.userService).createUser(arg1.capture());
        assertEquals(userName, arg1.getValue());
    }

    @Test
    public void testDeleteEvaluator() throws Exception {
        String url = "/v1/users/" + this.user.getUserId();

        doNothing().when(this.userService)
                   .deleteUser(this.user.getUserId());

        this.mockMvc.perform(delete(url))
                    .andExpect(status().isNoContent())
                    .andReturn();

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.userService).deleteUser(arg1.capture());
        assertEquals(this.user.getUserId(), arg1.getValue());
    }

    @Test
    public void testGetAllUsers() throws Exception {
        String url = "/v1/users";

        UserSummary userSum = new UserSummary();
        userSum.setUserId("test");

        UserListResponse listResponse = new UserListResponse(Arrays.asList(userSum));

        when(this.userService.getUsers()).thenReturn(Arrays.asList(userSum));

        MvcResult result = this.mockMvc.perform(get(url))
                                       .andExpect(status().isOk())
                                       .andReturn();

        assertEquals(this.mapper.writeValueAsString(listResponse), result.getResponse()
                                                                         .getContentAsString());

        verify(this.userService).getUsers();
    }

    @Test
    public void testGetUsersForTask() throws Exception {
        Long taskId = this.random.nextLong();
        String url = "/v1/users/task/" + taskId;

        UserSummary userSum = new UserSummary();
        userSum.setUserId("test");

        when(this.userService.getUsersForTask(taskId)).thenReturn(Arrays.asList(userSum));

        this.mockMvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andReturn();

        ArgumentCaptor<Long> arg1 = ArgumentCaptor.forClass(Long.class);

        verify(this.userService).getUsersForTask(arg1.capture());
        assertEquals(taskId, arg1.getValue());
    }
}
