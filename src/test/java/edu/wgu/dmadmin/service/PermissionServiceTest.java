package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import edu.wgu.dm.common.exception.PermissionNotFoundException;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.entity.security.PermissionModel;
import edu.wgu.dm.entity.security.RoleModel;
import edu.wgu.dm.entity.security.UserModel;
import edu.wgu.dm.repo.DMRepository;
import edu.wgu.dm.service.admin.PermissionService;
import edu.wgu.dm.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class PermissionServiceTest {

    @Mock
    DMRepository repo;

    @InjectMocks
    PermissionService service;

    Random random = new Random();
    Long permissionId1 = random.nextLong();
    Long permissionId2 = random.nextLong();
    Long roleId1 = random.nextLong();
    Long roleId2 = random.nextLong();
    String userId1 = "user1";
    String userId2 = "user2";

    PermissionModel permission1 = new PermissionModel();
    PermissionModel permission2 = new PermissionModel();

    RoleModel role1 = new RoleModel();
    RoleModel role2 = new RoleModel();

    UserModel user1 = new UserModel();
    UserModel user2 = new UserModel();

    List<PermissionModel> permissions;
    List<Permission> permissiondtos;
    Permission permDto1;
    Permission permDto2;
    List<RoleModel> roles;
    List<UserModel> users;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void initialize() {
        permission1.setPermissionId(permissionId1);
        permission1.setPermission("permission1");
        permission1.setLanding("landing");
        permission2.setPermissionId(permissionId2);
        permission2.setPermission("permission2");
        permissions = Arrays.asList(permission1, permission2);
        permDto1 = permission1.toPermission();
        permDto2 = permission2.toPermission();
        role1.setRoleId(roleId1);
        role2.setRoleId(roleId2);
        roles = Arrays.asList(role1, role2);
        users = Arrays.asList(user1, user2);
    }

    @Test
    public void testGetPermissions() {
        permissiondtos = Arrays.asList(permDto1, permDto2);
        when(repo.getAllPermissions()).thenReturn(permissiondtos);
        List<Permission> result = service.getPermissions();
        assertEquals(permissiondtos.size(), result.size());
    }

    @Test(expected = PermissionNotFoundException.class)
    public void testPermissionNotFound() {
        when(repo.getPermissionById(123L)).thenReturn(Optional.empty());
        Permission result = service.getPermission(123L);
    }

    @Test
    public void testGetNoPermissions() {
        permissiondtos = Arrays.asList(permDto1, permDto2);
        when(repo.getAllPermissions()).thenReturn(permissiondtos);
        when(repo.getAllPermissions()).thenReturn(Collections.emptyList());
        List<Permission> result = service.getPermissions();
        assertEquals(0, result.size());
    }

    @Test
    public void testSavePermissions() {
        // Arrange
        Permission newPermission = new Permission();
        newPermission.setPermission("newPermission");
        newPermission.setPermissionId(1234L);
        Permission[] newPermissions = {newPermission};
        when(repo.getPermissionById(any(Long.class))).thenReturn(Optional.empty());
        when(repo.getPermissionByPermission(any(String.class))).thenReturn(Optional.empty());
        ArgumentCaptor<Permission> argument = ArgumentCaptor.forClass(Permission.class);

        // Act
        service.savePermissions(newPermissions);

        // Assert
        verify(repo).savePermission(argument.capture());
        assertNotNull(argument.getValue().getPermission());
        assertEquals(newPermission.getPermissionId(), argument.getValue().getPermissionId());
    }

    @Test
    public void testUpdatePermissionDescription() {
        // Arrange
        Permission newPermission = new Permission();
        newPermission.setPermission(permission1.getPermission());
        String permissionDescription = "This is updated Description";
        newPermission.setPermissionDescription(permissionDescription);
        Permission[] newPermissions = {newPermission};
        when(repo.getPermissionById(any(Long.class))).thenReturn(Optional.empty());
        when(repo.getPermissionByPermission(any(String.class)))
                .thenReturn(Optional.of(permission1.toPermission()));
        ArgumentCaptor<Permission> argument = ArgumentCaptor.forClass(Permission.class);

        // Act
        service.savePermissions(newPermissions);

        // Assert
        verify(repo).savePermission(argument.capture());
        assertEquals(permissionId1, argument.getValue().getPermissionId());
        assertEquals(newPermission.getPermission(), argument.getValue().getPermission());
        assertEquals(permissionDescription, argument.getValue().getPermissionDescription());
    }

    @Test
    public void testUpdatePermissionsSamePermission() {
        // arrange
        Permission newPermission = permission1.toPermission();
        newPermission.setLanding("test");
        Permission[] newPermissions = {newPermission};
        when(repo.getPermissionById(any(Long.class))).thenReturn(Optional.of(newPermission));
        ArgumentCaptor<Permission> argument = ArgumentCaptor.forClass(Permission.class);

        // act
        service.savePermissions(newPermissions);

        // assert
        verify(repo).savePermission(argument.capture());
        assertEquals(permissionId1, argument.getValue().getPermissionId());
        assertEquals(newPermission.getPermission(), argument.getValue().getPermission());
        assertEquals("test", argument.getValue().getLanding());
    }



    @Test
    public void testUpdatePermissionDescriptionHasDate() {
        // arrange
        Permission newPermission = permission1.toPermission();
        String permissionDescription = "new description";
        newPermission.setPermissionDescription(permissionDescription);
        Date test = DateUtil.getZonedNow();
        newPermission.setDateUpdated(test);
        Permission[] newPermissions = {newPermission};
        when(repo.getPermissionById(any(Long.class)))
                .thenReturn(Optional.of(permission1.toPermission()));
        ArgumentCaptor<Permission> argument = ArgumentCaptor.forClass(Permission.class);

        // act
        service.savePermissions(newPermissions);

        // assert
        verify(repo).savePermission(argument.capture());
        assertEquals(permissionId1, argument.getValue().getPermissionId());
        assertEquals(permissionDescription, argument.getValue().getPermissionDescription());
        assertNotNull("newPermission", argument.getValue().getPermission());
        assertEquals(test, argument.getValue().getDateUpdated());
    }
}
