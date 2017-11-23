package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import edu.wgu.dmadmin.domain.security.Role;
import edu.wgu.dmadmin.exception.RoleNotFoundException;
import edu.wgu.dmadmin.model.security.PermissionModel;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.user.UserByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dreammachine.util.DateUtil;

public class RoleServiceTest {
	
	RoleService service = new RoleService();
	
	CassandraRepo repo = mock(CassandraRepo.class);
	
	UUID permissionId1 = UUID.randomUUID();
	UUID permissionId2 = UUID.randomUUID();
	UUID roleId1 = UUID.randomUUID();
	UUID roleId2 = UUID.randomUUID();
	String userId1 = "user1";
	String userId2 = "user2";
	
	PermissionModel permission1 = new PermissionModel();
	PermissionModel permission2 = new PermissionModel();
	
	RoleModel role1 = new RoleModel();
	RoleModel role2 = new RoleModel();
	
	UserByIdModel user1 = new UserByIdModel();
	UserByIdModel user2 = new UserByIdModel();
	
	List<PermissionModel> permissions;
	List<RoleModel> roles;
	List<UserByIdModel> users;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initialize() {
		this.service.setCassandraRepo(this.repo);
		
		this.permission1.setPermissionId(this.permissionId1);
		this.permission2.setPermissionId(this.permissionId2);
		this.permission1.setPermission("permission1");
		this.permission1.setLanding("landing");
		this.permission2.setPermission("permission2");
		this.permissions = Arrays.asList(this.permission1, this.permission2);
		
		this.role1.setRoleId(this.roleId1);
		this.role2.setRoleId(this.roleId2);
		this.role1.setPermissions(new HashSet<UUID>(Arrays.asList(this.permissionId1)));
		this.role2.setPermissions(new HashSet<UUID>(Arrays.asList(this.permissionId1, this.permissionId2)));
		this.roles = Arrays.asList(this.role1, this.role2);
		
		this.user1.setRoles(new HashSet<UUID>(Arrays.asList(this.roleId1)));
		this.user2.setRoles(new HashSet<UUID>(Arrays.asList(this.roleId2)));
		this.users = Arrays.asList(this.user1, this.user2);
		
		when(this.repo.getPermissions()).thenReturn(this.permissions);
		when(this.repo.getUsers()).thenReturn(this.users);
	}
	
	@Test
	public void testGetRoles() {
		when(this.repo.getRoles()).thenReturn(this.roles);
		
		List<Role> result = this.service.getRoles();
		assertEquals(result.size(), this.roles.size());
	}
	
	@Test
	public void testGetNoRoles() {
		when(this.repo.getRoles()).thenReturn(Collections.emptyList());
		
		List<Role> result = this.service.getRoles();
		assertEquals(0, result.size());
	}

	@Test
	public void testGetRole() {
		when(this.repo.getRole(this.roleId1)).thenReturn(Optional.of(this.role1));
		
		Role result = this.service.getRole(this.roleId1);
		assertEquals(this.roleId1, result.getRoleId());
	}
	
	@Test
	public void testGetNoRole() {
		when(this.repo.getRole(this.roleId1)).thenReturn(Optional.empty());
		
		this.thrown.expect(RoleNotFoundException.class);
		this.service.getRole(this.roleId1);
	}
	
	@Test
	public void testDeleteRole() {
		ArgumentCaptor<UserByIdModel> argument = ArgumentCaptor.forClass(UserByIdModel.class);
		
		this.service.deleteRole(this.roleId1);
		verify(this.repo).saveUser(argument.capture());
		assertEquals(0, argument.getValue().getRoles().size());
		verify(this.repo).deleteRole(this.roleId1);
	}
	
	@Test
	public void testDeleteRoleNoUsers() {
		this.user1.setRoles(Collections.emptySet());
		
		this.service.deleteRole(this.roleId1);
		verify(this.repo, never()).saveUser(any(UserByIdModel.class));
		verify(this.repo).deleteRole(this.roleId1);
	}
	
	@Test
	public void testSaveRoles() {
		Role newRole = new Role();
		newRole.setRole("newRole");
		newRole.setPermissions(new HashSet<UUID>(Arrays.asList(this.permissionId1)));
		Role[] newRoles = {newRole};
		
		when(this.repo.getRole(any(UUID.class))).thenReturn(Optional.empty());
		
		ArgumentCaptor<RoleModel> argument = ArgumentCaptor.forClass(RoleModel.class);
		
		List<Role> result = this.service.saveRoles(newRoles);
		verify(this.repo).saveRole(argument.capture());
		assertEquals(this.permissionId1, argument.getValue().getPermissions().iterator().next());
		assertNotNull(argument.getValue().getRoleId());
		assertNotNull(result.get(0).getDateCreated());
	}
	
	@Test
	public void testUpdateRoles() {
		Role newRole = new Role(this.role2);
		newRole.setPermissions(new HashSet<UUID>(Arrays.asList(this.permissionId1)));
		Role[] newRoles = {newRole};
		
		when(this.repo.getRole(this.roleId2)).thenReturn(Optional.of(this.role2));
		
		ArgumentCaptor<RoleModel> argument = ArgumentCaptor.forClass(RoleModel.class);
		
		List<Role> result = this.service.saveRoles(newRoles);
		verify(this.repo).saveRole(argument.capture());
		assertEquals(1, argument.getValue().getPermissions().size());
		assertEquals(result.get(0).getRoleId(), this.roleId2);
		
		verify(this.repo).saveUsers(any());
	}
	
	@Test
	public void testUpdateRolesSamePerms() {
		Role newRole = new Role(this.role2);
		newRole.setRole("test");
		Role[] newRoles = {newRole};
		
		when(this.repo.getRole(this.roleId2)).thenReturn(Optional.of(this.role2));
		
		ArgumentCaptor<RoleModel> argument = ArgumentCaptor.forClass(RoleModel.class);
		
		List<Role> result = this.service.saveRoles(newRoles);
		verify(this.repo).saveRole(argument.capture());
		assertEquals(2, argument.getValue().getPermissions().size());
		assertEquals(result.get(0).getRoleId(), this.roleId2);
		assertEquals(result.get(0).getRole(), "test");
		
		verify(this.repo, never()).saveUsers(any());
	}
	
	@Test
	public void testUpdateRolesSamePermsHasDate() {
		Role newRole = new Role(this.role2);
		newRole.setRole("test");
		Date test = DateUtil.getZonedNow();
		newRole.setDateCreated(test);
		Role[] newRoles = {newRole};
		
		when(this.repo.getRole(this.roleId2)).thenReturn(Optional.of(this.role2));
		
		ArgumentCaptor<RoleModel> argument = ArgumentCaptor.forClass(RoleModel.class);
		
		List<Role> result = this.service.saveRoles(newRoles);
		verify(this.repo).saveRole(argument.capture());
		assertEquals(2, argument.getValue().getPermissions().size());
		assertEquals(result.get(0).getRoleId(), this.roleId2);
		assertEquals(result.get(0).getRole(), "test");
		assertEquals(test, result.get(0).getDateCreated());
		
		verify(this.repo, never()).saveUsers(any());
	}
}
