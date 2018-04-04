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

import edu.wgu.dmadmin.model.security.UserModel;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;

import edu.wgu.dmadmin.domain.security.Permission;
import edu.wgu.dmadmin.model.security.PermissionModel;
import edu.wgu.dmadmin.model.security.RoleModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.DateUtil;

public class PermissionServiceTest {
	
	PermissionService service = new PermissionService();
	
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

	UserModel user1 = new UserModel();
	UserModel user2 = new UserModel();
	
	List<PermissionModel> permissions;
	List<RoleModel> roles;
	List<UserModel> users;
	
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
	public void testGetPermissions() {
		List<Permission> result = this.service.getPermissions();
		assertEquals(this.permissions.size(), result.size());
	}
	
	@Test
	public void testGetNoPermissions() {
		when(this.repo.getPermissions()).thenReturn(Collections.emptyList());
		
		List<Permission> result = this.service.getPermissions();
		assertEquals(0, result.size());
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
		Permission newPermission = new Permission(this.permission1);
		newPermission.setPermission("newPermission");
		Permission[] newPermissions = {newPermission};
		
		when(this.repo.getPermission(this.permissionId1)).thenReturn(Optional.of(this.permission1));
		
		ArgumentCaptor<PermissionModel> argument = ArgumentCaptor.forClass(PermissionModel.class);
		
		this.service.savePermissions(newPermissions);
		verify(this.repo).savePermission(argument.capture());
		assertEquals(this.permissionId1, argument.getValue().getPermissionId());
		assertNotNull("newPermission", argument.getValue().getPermission());
		
		verify(this.repo).getUsersForPermission(this.permission1.getPermission());
		verify(this.repo).saveUsers(any());
	}
	
	@Test
	public void testUpdatePermissionsSamePermission() {
		Permission newPermission = new Permission(this.permission1);
		newPermission.setLanding("test");
		Permission[] newPermissions = {newPermission};
		
		when(this.repo.getPermission(this.permissionId1)).thenReturn(Optional.of(this.permission1));
		
		ArgumentCaptor<PermissionModel> argument = ArgumentCaptor.forClass(PermissionModel.class);
		
		this.service.savePermissions(newPermissions);
		verify(this.repo).savePermission(argument.capture());
		assertEquals(this.permissionId1, argument.getValue().getPermissionId());
		assertNotNull("newPermission", argument.getValue().getPermission());
		
		verify(this.repo).getUsersForPermission(this.permission1.getPermission());
		verify(this.repo).saveUsers(any());
	}
	
	@Test
	public void testUpdatePermissionDescription() {
		Permission newPermission = new Permission(this.permission1);
		newPermission.setPermissionDescription("new description");
		Permission[] newPermissions = {newPermission};
		
		when(this.repo.getPermission(this.permissionId1)).thenReturn(Optional.of(this.permission1));
		
		ArgumentCaptor<PermissionModel> argument = ArgumentCaptor.forClass(PermissionModel.class);
		
		this.service.savePermissions(newPermissions);
		verify(this.repo).savePermission(argument.capture());
		assertEquals(this.permissionId1, argument.getValue().getPermissionId());
		assertNotNull("newPermission", argument.getValue().getPermission());
		
		verify(this.repo, never()).getUsersForPermission(this.permission1.getPermission());
		verify(this.repo, never()).saveUsers(any());
	}
	
	@Test
	public void testUpdatePermissionDescriptionHasDate() {
		Permission newPermission = new Permission(this.permission1);
		newPermission.setPermissionDescription("new description");
		Date test = DateUtil.getZonedNow();
		newPermission.setDateCreated(test);
		Permission[] newPermissions = {newPermission};
		
		when(this.repo.getPermission(this.permissionId1)).thenReturn(Optional.of(this.permission1));
		
		ArgumentCaptor<PermissionModel> argument = ArgumentCaptor.forClass(PermissionModel.class);
		
		this.service.savePermissions(newPermissions);
		verify(this.repo).savePermission(argument.capture());
		assertEquals(this.permissionId1, argument.getValue().getPermissionId());
		assertNotNull("newPermission", argument.getValue().getPermission());
		assertEquals(test, argument.getValue().getDateCreated());
		
		verify(this.repo, never()).getUsersForPermission(this.permission1.getPermission());
		verify(this.repo, never()).saveUsers(any());
	}
}
