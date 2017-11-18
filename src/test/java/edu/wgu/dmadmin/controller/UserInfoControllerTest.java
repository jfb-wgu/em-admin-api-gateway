package edu.wgu.dmadmin.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wgu.dmadmin.domain.user.Person;
import edu.wgu.dmadmin.service.UserInfoService;
import edu.wgu.dmadmin.test.TestObjectFactory;
import edu.wgu.dreammachine.util.DateUtil;
import edu.wgu.dreammachine.util.IdentityUtil;

@RunWith(MockitoJUnitRunner.class)
public class UserInfoControllerTest {
    @InjectMocks
    private UserInfoController controller;

    @Mock
    private UserInfoService userService;

    @Mock
    private IdentityUtil iUtil;

    MockMvc mockMvc;
    ObjectMapper mapper = new ObjectMapper();

    private String userId = "123456";

    private Person person;
    private Set<UUID> teams;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(this.controller).build();
        this.controller.setUserInfoService(this.userService);

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
        this.person.setIsEmployee(Boolean.FALSE);
        this.person.setStudentId(this.userId);
        this.person.setFirstName("Bruce");
        this.person.setLastName("Wayne");
        this.person.setPidm(new Long(1234566));
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
}
