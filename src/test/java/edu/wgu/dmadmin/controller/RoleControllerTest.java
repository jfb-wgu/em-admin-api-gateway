package edu.wgu.dmadmin.controller;

import static org.junit.Assert.assertEquals;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

import edu.wgu.dmadmin.domain.security.Role;
import edu.wgu.dmadmin.service.RoleService;
import edu.wgu.dmadmin.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class RoleControllerTest {
	@InjectMocks
	private RoleController roleController;

	@Mock
	private RoleService securityService;

	private MockMvc mockMvc;
	private ObjectMapper mapper = new ObjectMapper();
	private UUID roleId = UUID.randomUUID();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = standaloneSetup(this.roleController).build();
	}

	@Test
	public void testAddRoles() throws Exception {
		String url = "/v1/admin/roles";
		Set<UUID> permissions = new HashSet<>();
		permissions.add(UUID.randomUUID());
		permissions.add(UUID.randomUUID());

		Set<String> permissionNames = new HashSet<>();
		permissionNames.add("1");
		permissionNames.add("2");
		permissionNames.add("3");

		Role[] roles = new Role[1];
		Role role = new Role();
		role.setDateCreated(DateUtil.getZonedNow());
		role.setRole("Admin");
		role.setPermissions(permissions);
		role.setDateUpdated(DateUtil.getZonedNow());
		role.setRoleId(UUID.randomUUID());
		role.setPermissionNames(permissionNames);
		role.setRoleDescription("Description");
		roles[0] = role;

		List<Role> roleList = new ArrayList<>();
		roleList.add(role);

		when(this.securityService.saveRoles(roles)).thenReturn(roleList);

		MvcResult result = this.mockMvc.perform(
				post(url).contentType(MediaType.APPLICATION_JSON).content(this.mapper.writeValueAsString(roles)))
				.andExpect(status().isOk()).andReturn();

		assertEquals(this.mapper.writeValueAsString(roleList), result.getResponse().getContentAsString());

		ArgumentCaptor<Role[]> arg1 = ArgumentCaptor.forClass(Role[].class);

		verify(this.securityService).saveRoles(arg1.capture());
		assertEquals(Arrays.toString(roles), Arrays.toString(arg1.getValue()));
	}

	@Test
	public void testGetRoles() throws Exception {
		String url = "/v1/admin/roles";
		Set<UUID> permissions = new HashSet<>();
		permissions.add(UUID.randomUUID());
		permissions.add(UUID.randomUUID());

		Set<String> permissionNames = new HashSet<>();
		permissionNames.add("1");
		permissionNames.add("2");
		permissionNames.add("3");

		Role role = new Role();
		role.setDateCreated(DateUtil.getZonedNow());
		role.setRole("Admin");
		role.setPermissions(permissions);
		role.setDateUpdated(DateUtil.getZonedNow());
		role.setRoleId(UUID.randomUUID());
		role.setPermissionNames(permissionNames);
		role.setRoleDescription("Description");

		List<Role> roleList = new ArrayList<>();
		roleList.add(role);

		when(this.securityService.getRoles()).thenReturn(roleList);

		MvcResult result = this.mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

		assertEquals(this.mapper.writeValueAsString(roleList), result.getResponse().getContentAsString());

		verify(this.securityService).getRoles();
	}

	@Test
	public void testGetRole() throws Exception {
		String url = "/v1/admin/roles/" + this.roleId;
		Set<UUID> permissions = new HashSet<>();
		Set<String> permissionNames = new HashSet<>();

		permissions.add(UUID.randomUUID());
		permissions.add(UUID.randomUUID());

		permissionNames.add("1");
		permissionNames.add("2");
		permissionNames.add("3");

		Role role = new Role();
		role.setDateCreated(DateUtil.getZonedNow());
		role.setRole("Admin");
		role.setPermissions(permissions);
		role.setDateUpdated(DateUtil.getZonedNow());
		role.setRoleId(UUID.randomUUID());
		role.setPermissionNames(permissionNames);
		role.setRoleDescription("Description");

		when(this.securityService.getRole(this.roleId)).thenReturn(role);

		MvcResult result = this.mockMvc.perform(get(url)).andExpect(status().isOk()).andReturn();

		assertEquals(this.mapper.writeValueAsString(role), result.getResponse().getContentAsString());

		ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

		verify(this.securityService).getRole(arg1.capture());
		assertEquals(this.roleId, arg1.getValue());
	}

	@Test
	public void testDeleteRole() throws Exception {
		String url = "/v1/admin/roles/" + this.roleId;
		Set<UUID> permissions = new HashSet<>();
		Set<String> permissionNames = new HashSet<>();

		permissions.add(UUID.randomUUID());
		permissions.add(UUID.randomUUID());

		permissionNames.add("1");
		permissionNames.add("2");
		permissionNames.add("3");

		Role role = new Role();
		role.setDateCreated(DateUtil.getZonedNow());
		role.setRole("Admin");
		role.setPermissions(permissions);
		role.setDateUpdated(DateUtil.getZonedNow());
		role.setRoleId(UUID.randomUUID());
		role.setPermissionNames(permissionNames);
		role.setRoleDescription("Description");

		doNothing().when(this.securityService).deleteRole(this.roleId);

		this.mockMvc.perform(delete(url)).andExpect(status().isNoContent()).andReturn();

		ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

		verify(this.securityService).deleteRole(arg1.capture());
		assertEquals(this.roleId, arg1.getValue());
	}
}
