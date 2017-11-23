package edu.wgu.dmadmin.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

import edu.wgu.dmadmin.domain.security.Permission;
import edu.wgu.dmadmin.service.PermissionService;
import edu.wgu.dreammachine.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class PermissionControllerTest {
    @InjectMocks
    private PermissionController securityAdminController;

    @Mock
    private PermissionService securityService;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(this.securityAdminController).build();
    }

    @Test
    public void testGetPermissions() throws Exception {
        String url = "/v1/admin/permissions";

        List<Permission> permissions = new ArrayList<>();
        Permission permission = new Permission();
        permission.setDateCreated(DateUtil.getZonedNow());
        permission.setPermission("Admin");
        permission.setPermissionDescription("Able to do everything");
        permission.setLanding("Landing");
        permission.setPermissionId(UUID.randomUUID());
        permission.setPermissionDescription("description");
        permission.setDateUpdated(DateUtil.getZonedNow());
        permission.setPermissionType("amdin");
        permissions.add(permission);

        when(this.securityService.getPermissions()).thenReturn(permissions);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(permissions), result.getResponse().getContentAsString());

        verify(this.securityService).getPermissions();

    }

    @Test
    public void testAddPermissions() throws Exception {
        String url = "/v1/admin/permissions";

        Permission[] permissions = new Permission[1];
        Permission permission = new Permission();
        permission.setDateCreated(DateUtil.getZonedNow());
        permission.setPermission("Admin");
        permission.setPermissionDescription("Able to do everything");
        permission.setLanding("Landing");
        permission.setPermissionId(UUID.randomUUID());
        permission.setPermissionDescription("description");
        permission.setDateUpdated(DateUtil.getZonedNow());
        permission.setPermissionType("amdin");
        permissions[0] = permission;

        doNothing().when(this.securityService).savePermissions(permissions);

        this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(permissions)))
                .andExpect(status().isNoContent())
                .andReturn();

        ArgumentCaptor<Permission[]> arg1 = ArgumentCaptor.forClass(Permission[].class);

        verify(this.securityService).savePermissions(arg1.capture());
        assertEquals(Arrays.toString(permissions), Arrays.toString(arg1.getValue()));
    }
}
