package edu.wgu.dmadmin.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import edu.wgu.dm.admin.controller.RoleController;
import edu.wgu.dm.admin.service.RoleService;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.util.DateUtil;
import edu.wgu.dm.util.IdentityUtil;

@RunWith(MockitoJUnitRunner.class)
public class RoleControllerTest {

    @InjectMocks
    private RoleController roleController;

    @Mock
    private RoleService securityService;

    @Mock
    private IdentityUtil iUtil;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    Random random = new Random();
    private Long roleId = this.random.nextLong();
    List<Permission> permissions;
    List<Role> roles;
    Role[] roleArray;

    @Before
    public void setUp() throws Exception {

        this.mockMvc = standaloneSetup(this.roleController).build();

        Permission perm1 = new Permission();
        perm1.setPermissionId(this.random.nextLong());
        perm1.setPermission("1");

        Permission perm2 = new Permission();
        perm2.setPermissionId(this.random.nextLong());
        perm2.setPermission("2");

        Permission perm3 = new Permission();
        perm3.setPermissionId(this.random.nextLong());
        perm3.setPermission("3");

        this.permissions = Arrays.asList(perm1, perm2, perm3);

        Role role = new Role();
        role.setDateCreated(DateUtil.getZonedNow());
        role.setRole("Admin");
        role.setPermissions(this.permissions);
        role.setDateUpdated(DateUtil.getZonedNow());
        role.setRoleId(this.roleId);
        role.setRoleDescription("Description");

        this.roleArray = new Role[] {role};
        this.roles = Arrays.asList(role);
    }

    @Test
    public void testAddRoles() throws Exception {
        String url = "/v1/admin/roles";

        List<Role> localRoles = Collections.unmodifiableList(this.roles);

        when(this.securityService.saveRoles(any(), any())).thenReturn(localRoles);
        when(this.iUtil.getUserId()).thenReturn("test");

        MvcResult result = this.mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON)
                                                         .content(this.mapper.writeValueAsString(localRoles)))
                                       .andExpect(status().isOk())
                                       .andReturn();

        CollectionType javaType = this.mapper.getTypeFactory()
                                             .constructCollectionType(List.class, Role.class);
        List<Role> responseRoles = this.mapper.readValue(result.getResponse()
                                                               .getContentAsString(),
                javaType);

        assertEquals(localRoles.get(0)
                               .getPermissionIds()
                               .size(),
                responseRoles.get(0)
                             .getPermissionIds()
                             .size());

        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Role[]> arg2 = ArgumentCaptor.forClass(Role[].class);

        verify(this.securityService).saveRoles(arg1.capture(), arg2.capture());
        assertEquals(localRoles.get(0)
                               .getRole(),
                arg2.getValue()[0].getRole());
    }

    @Test
    public void testGetRoles() throws Exception {
        String url = "/v1/admin/roles";

        when(this.securityService.getRoles()).thenReturn(this.roles);

        MvcResult result = this.mockMvc.perform(get(url))
                                       .andExpect(status().isOk())
                                       .andReturn();

        assertEquals(this.mapper.writeValueAsString(this.roles), result.getResponse()
                                                                       .getContentAsString());

        verify(this.securityService).getRoles();
    }

    @Test
    public void testGetRole() throws Exception {
        String url = "/v1/admin/roles/" + this.roleId;

        when(this.securityService.getRole(this.roleId)).thenReturn(this.roles.get(0));

        MvcResult result = this.mockMvc.perform(get(url))
                                       .andExpect(status().isOk())
                                       .andReturn();

        assertEquals(this.mapper.writeValueAsString(this.roles.get(0)), result.getResponse()
                                                                              .getContentAsString());

        ArgumentCaptor<Long> arg1 = ArgumentCaptor.forClass(Long.class);

        verify(this.securityService).getRole(arg1.capture());
        assertEquals(this.roleId, arg1.getValue());
    }

    @Test
    public void testDeleteRole() throws Exception {
        String url = "/v1/admin/roles/" + this.roleId;

        doNothing().when(this.securityService)
                   .deleteRole(this.roleId);

        this.mockMvc.perform(delete(url))
                    .andExpect(status().isNoContent())
                    .andReturn();

        ArgumentCaptor<Long> arg1 = ArgumentCaptor.forClass(Long.class);

        verify(this.securityService).deleteRole(arg1.capture());
        assertEquals(this.roleId, arg1.getValue());
    }
}
