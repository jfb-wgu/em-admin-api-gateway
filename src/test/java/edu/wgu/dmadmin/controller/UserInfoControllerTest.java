package edu.wgu.dmadmin.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
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
import edu.wgu.dm.admin.controller.UserInfoController;
import edu.wgu.dm.admin.service.UserInfoService;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.util.IdentityUtil;
import edu.wgu.dmadmin.test.TestObjectFactory;

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

    private Person person = TestObjectFactory.getPerson("Bruce", "Wayne");
    String userId = person.getStudentId();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(this.controller).build();

        when(this.iUtil.getUserId()).thenReturn(this.userId);
    }

    @Test
    public void testGetPerson() throws Exception {
        String url = "/v1/person";

        when(this.userService.getPersonFromRequest(any(HttpServletRequest.class), eq(this.userId))).thenReturn(
                this.person);

        MvcResult result = this.mockMvc.perform(get(url))
                                       .andExpect(status().isOk())
                                       .andReturn();

        assertEquals(this.mapper.writeValueAsString(this.person), result.getResponse()
                                                                        .getContentAsString());

        ArgumentCaptor<HttpServletRequest> arg1 = ArgumentCaptor.forClass(HttpServletRequest.class);
        ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

        verify(this.userService).getPersonFromRequest(arg1.capture(), arg2.capture());
        assertEquals(org.springframework.mock.web.MockHttpServletRequest.class, arg1.getValue()
                                                                                    .getClass());
        assertEquals(this.userId, arg2.getValue());
    }

    @Test
    public void testGetPerson1() throws Exception {
        String url = "/v1/person/" + this.userId;

        when(this.userService.getPersonByUserId(this.userId)).thenReturn(this.person);

        MvcResult result = this.mockMvc.perform(get(url))
                                       .andExpect(status().isOk())
                                       .andReturn();

        assertEquals(this.mapper.writeValueAsString(this.person), result.getResponse()
                                                                        .getContentAsString());

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);

        verify(this.userService).getPersonByUserId(arg1.capture());
    }
}
