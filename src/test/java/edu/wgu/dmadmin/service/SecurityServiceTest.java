package edu.wgu.dmadmin.service;

import static org.junit.Assert.*;
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

import edu.wgu.dmadmin.domain.security.Permission;
import edu.wgu.dmadmin.domain.security.Role;
import edu.wgu.dmadmin.exception.RoleNotFoundException;
import edu.wgu.dmadmin.model.security.PermissionModel;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;

public class SecurityServiceTest {
	
	SecurityService service = new SecurityService();
	
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
		this.service.setCassandraRepo(repo);
		
		permission1.setPermissionId(permissionId1);
		permission2.setPermissionId(permissionId2);
		permission1.setPermission("permission1");
		permission1.setLanding("landing");
		permission2.setPermission("permission2");
		permissions = Arrays.asList(permission1, permission2);
		
		role1.setRoleId(roleId1);
		role2.setRoleId(roleId2);
		role1.setPermissions(new HashSet<UUID>(Arrays.asList(permissionId1)));
		role2.setPermissions(new HashSet<UUID>(Arrays.asList(permissionId1, permissionId2)));
		roles = Arrays.asList(role1, role2);
		
		user1.setRoles(new HashSet<UUID>(Arrays.asList(roleId1)));
		user2.setRoles(new HashSet<UUID>(Arrays.asList(roleId2)));
		users = Arrays.asList(user1, user2);
		
		when(this.repo.getPermissions()).thenReturn(permissions);
		when(this.repo.getUsers()).thenReturn(users);
	}

	@Test
	public void testGetPermissions() {
		List<Permission> result = this.service.getPermissions();
		assertEquals(permissions.size(), result.size());
	}
	
	@Test
	public void testGetNoPermissions() {
		when(this.repo.getPermissions()).thenReturn(Collections.emptyList());
		
		List<Permission> result = this.service.getPermissions();
		assertEquals(0, result.size());
	}
	
	@Test
	public void testGetRoles() {
		when(this.repo.getRoles()).thenReturn(roles);
		
		List<Role> result = this.service.getRoles();
		assertEquals(result.size(), roles.size());
	}
	
	@Test
	public void testGetNoRoles() {
		when(this.repo.getRoles()).thenReturn(Collections.emptyList());
		
		List<Role> result = this.service.getRoles();
		assertEquals(0, result.size());
	}

	@Test
	public void testGetRole() {
		when(this.repo.getRole(roleId1)).thenReturn(Optional.of(role1));
		
		Role result = this.service.getRole(roleId1);
		assertEquals(roleId1, result.getRoleId());
	}
	
	@Test
	public void testGetNoRole() {
		when(this.repo.getRole(roleId1)).thenReturn(Optional.empty());
		
		thrown.expect(RoleNotFoundException.class);
		this.service.getRole(roleId1);
	}
	
	@Test
	public void testDeleteRole() {
		ArgumentCaptor<UserByIdModel> argument = ArgumentCaptor.forClass(UserByIdModel.class);
		
		this.service.deleteRole(roleId1);
		verify(this.repo).saveUser(argument.capture());
		assertEquals(0, argument.getValue().getRoles().size());
		verify(this.repo).deleteRole(roleId1);
	}
	
	@Test
	public void testDeleteRoleNoUsers() {
		user1.setRoles(Collections.emptySet());
		
		this.service.deleteRole(roleId1);
		verify(this.repo, never()).saveUser(any(UserByIdModel.class));
		verify(this.repo).deleteRole(roleId1);
	}
	
	@Test
	public void testSaveRoles() {
		Role newRole = new Role();
		newRole.setRole("newRole");
		newRole.setPermissions(new HashSet<UUID>(Arrays.asList(permissionId1)));
		Role[] newRoles = {newRole};
		
		when(this.repo.getRole(any(UUID.class))).thenReturn(Optional.empty());
		
		ArgumentCaptor<RoleModel> argument = ArgumentCaptor.forClass(RoleModel.class);
		
		List<Role> result = this.service.saveRoles(newRoles);
		verify(this.repo).saveRole(argument.capture());
		assertEquals(permissionId1, argument.getValue().getPermissions().iterator().next());
		assertNotNull(argument.getValue().getRoleId());
		assertNotNull(result.get(0).getDateCreated());
	}
	
	@Test
	public void testUpdateRoles() {
		Role newRole = new Role(role2);
		newRole.setPermissions(new HashSet<UUID>(Arrays.asList(permissionId1)));
		Role[] newRoles = {newRole};
		
		when(this.repo.getRole(this.roleId2)).thenReturn(Optional.of(role2));
		
		ArgumentCaptor<RoleModel> argument = ArgumentCaptor.forClass(RoleModel.class);
		
		List<Role> result = this.service.saveRoles(newRoles);
		verify(this.repo).saveRole(argument.capture());
		assertEquals(1, argument.getValue().getPermissions().size());
		assertEquals(result.get(0).getRoleId(), this.roleId2);
		
		verify(this.repo).saveUsers(any());
	}
	
	@Test
	public void testUpdateRolesSamePerms() {
		Role newRole = new Role(role2);
		newRole.setRole("test");
		Role[] newRoles = {newRole};
		
		when(this.repo.getRole(this.roleId2)).thenReturn(Optional.of(role2));
		
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
		Role newRole = new Role(role2);
		newRole.setRole("test");
		Date test = DateUtil.getZonedNow();
		newRole.setDateCreated(test);
		Role[] newRoles = {newRole};
		
		when(this.repo.getRole(this.roleId2)).thenReturn(Optional.of(role2));
		
		ArgumentCaptor<RoleModel> argument = ArgumentCaptor.forClass(RoleModel.class);
		
		List<Role> result = this.service.saveRoles(newRoles);
		verify(this.repo).saveRole(argument.capture());
		assertEquals(2, argument.getValue().getPermissions().size());
		assertEquals(result.get(0).getRoleId(), this.roleId2);
		assertEquals(result.get(0).getRole(), "test");
		assertEquals(test, result.get(0).getDateCreated());
		
		verify(this.repo, never()).saveUsers(any());
	}
	
	@Test
	public void testSavePermissions() {
		Permission newPermission = new Permission();
		newPermission.setPermission("newPermission");
		Permission[] newPermissions = {newPermission};
		
		when(this.repo.getPermission(any(UUID.class))).thenReturn(Optional.empty());
		
		ArgumentCaptor<PermissionModel> argument = ArgumentCaptor.forClass(PermissionModel.class);
		
		this.service.savePermissions(newPermissions);
		verify(this.repo).savePermission(argument.capture());
		assertNotNull(argument.getValue().getPermissionId());
		assertNotNull(argument.getValue().getDateCreated());
	}
	
	@Test
	public void testUpdatePermissions() {
		Permission newPermission = new Permission(permission1);
		newPermission.setPermission("newPermission");
		Permission[] newPermissions = {newPermission};
		
		when(this.repo.getPermission(permissionId1)).thenReturn(Optional.of(permission1));
		
		ArgumentCaptor<PermissionModel> argument = ArgumentCaptor.forClass(PermissionModel.class);
		
		this.service.savePermissions(newPermissions);
		verify(this.repo).savePermission(argument.capture());
		assertEquals(permissionId1, argument.getValue().getPermissionId());
		assertNotNull("newPermission", argument.getValue().getPermission());
		
		verify(this.repo).deletePermission(permissionId1, permission1.getPermission());
		verify(this.repo).getUsersForPermission(permission1.getPermission());
		verify(this.repo).saveUsers(any());
	}
	
	@Test
	public void testUpdatePermissionsSamePermission() {
		Permission newPermission = new Permission(permission1);
		newPermission.setLanding("test");
		Permission[] newPermissions = {newPermission};
		
		when(this.repo.getPermission(permissionId1)).thenReturn(Optional.of(permission1));
		
		ArgumentCaptor<PermissionModel> argument = ArgumentCaptor.forClass(PermissionModel.class);
		
		this.service.savePermissions(newPermissions);
		verify(this.repo).savePermission(argument.capture());
		assertEquals(permissionId1, argument.getValue().getPermissionId());
		assertNotNull("newPermission", argument.getValue().getPermission());
		
		verify(this.repo, never()).deletePermission(permissionId1, permission1.getPermission());
		verify(this.repo).getUsersForPermission(permission1.getPermission());
		verify(this.repo).saveUsers(any());
	}
	
	@Test
	public void testUpdatePermissionDescription() {
		Permission newPermission = new Permission(permission1);
		newPermission.setPermissionDescription("new description");
		Permission[] newPermissions = {newPermission};
		
		when(this.repo.getPermission(permissionId1)).thenReturn(Optional.of(permission1));
		
		ArgumentCaptor<PermissionModel> argument = ArgumentCaptor.forClass(PermissionModel.class);
		
		this.service.savePermissions(newPermissions);
		verify(this.repo).savePermission(argument.capture());
		assertEquals(permissionId1, argument.getValue().getPermissionId());
		assertNotNull("newPermission", argument.getValue().getPermission());
		
		verify(this.repo, never()).deletePermission(permissionId1, permission1.getPermission());
		verify(this.repo, never()).getUsersForPermission(permission1.getPermission());
		verify(this.repo, never()).saveUsers(any());
	}
	
	@Test
	public void testUpdatePermissionDescriptionHasDate() {
		Permission newPermission = new Permission(permission1);
		newPermission.setPermissionDescription("new description");
		Date test = DateUtil.getZonedNow();
		newPermission.setDateCreated(test);
		Permission[] newPermissions = {newPermission};
		
		when(this.repo.getPermission(permissionId1)).thenReturn(Optional.of(permission1));
		
		ArgumentCaptor<PermissionModel> argument = ArgumentCaptor.forClass(PermissionModel.class);
		
		this.service.savePermissions(newPermissions);
		verify(this.repo).savePermission(argument.capture());
		assertEquals(permissionId1, argument.getValue().getPermissionId());
		assertNotNull("newPermission", argument.getValue().getPermission());
		assertEquals(test, argument.getValue().getDateCreated());
		
		verify(this.repo, never()).deletePermission(permissionId1, permission1.getPermission());
		verify(this.repo, never()).getUsersForPermission(permission1.getPermission());
		verify(this.repo, never()).saveUsers(any());
	}
}
