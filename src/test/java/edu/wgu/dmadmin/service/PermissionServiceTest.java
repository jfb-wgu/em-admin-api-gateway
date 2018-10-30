package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.admin.service.PermissionService;
import edu.wgu.dm.common.exception.PermissionNotFoundException;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.entity.security.PermissionEntity;
import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.entity.security.UserEntity;

@RunWith(MockitoJUnitRunner.class)
public class PermissionServiceTest {

    @Mock
    AdminRepository repo;

    @InjectMocks
    PermissionService service;

    @Captor
    ArgumentCaptor<List<Permission>> permissionsCaptor;

    Random random = new Random();
    Long permissionId1 = this.random.nextLong();
    Long permissionId2 = this.random.nextLong();
    Long roleId1 = this.random.nextLong();
    Long roleId2 = this.random.nextLong();
    String userId1 = "user1";
    String userId2 = "user2";

    PermissionEntity permission1 = new PermissionEntity();
    PermissionEntity permission2 = new PermissionEntity();

    RoleEntity role1 = new RoleEntity();
    RoleEntity role2 = new RoleEntity();

    UserEntity user1 = new UserEntity();
    UserEntity user2 = new UserEntity();

    List<PermissionEntity> permissions;
    List<Permission> permissiondtos;
    Permission permDto1;
    Permission permDto2;
    List<RoleEntity> roles;
    List<UserEntity> users;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void initialize() {
        this.permission1.setPermissionId(this.permissionId1);
        this.permission1.setPermission("permission1");
        this.permission1.setLanding("landing");
        this.permission2.setPermissionId(this.permissionId2);
        this.permission2.setPermission("permission2");
        this.permissions = Arrays.asList(this.permission1, this.permission2);
        this.permDto1 = this.permission1.toPermission();
        this.permDto2 = this.permission2.toPermission();
        this.role1.setRoleId(this.roleId1);
        this.role2.setRoleId(this.roleId2);
        this.roles = Arrays.asList(this.role1, this.role2);
        this.users = Arrays.asList(this.user1, this.user2);
    }

    @Test
    public void testGetPermissions() {
        this.permissiondtos = Arrays.asList(this.permDto1, this.permDto2);
        when(this.repo.getAllPermissions()).thenReturn(this.permissiondtos);
        List<Permission> result = this.service.getPermissions();
        assertEquals(this.permissiondtos.size(), result.size());
    }

    @Test(expected = PermissionNotFoundException.class)
    public void testPermissionNotFound() {
        when(this.repo.getPermissionById(123L)).thenReturn(Optional.empty());
        Permission result = this.service.getPermission(123L);
    }

    @Test
    public void testGetNoPermissions() {
        this.permissiondtos = Arrays.asList(this.permDto1, this.permDto2);
        when(this.repo.getAllPermissions()).thenReturn(Collections.emptyList());
        List<Permission> result = this.service.getPermissions();
        assertEquals(0, result.size());
    }

    @Test
    public void testGetPermissionById() {
        when(this.repo.getPermissionById(anyLong())).thenReturn(Optional.of(this.permDto1));

        this.service.getPermission(this.permissionId1);

        verify(this.repo).getPermissionById(eq(this.permissionId1));
    }

    @Test(expected = PermissionNotFoundException.class)
    public void testGetPermissionByIdNotFound() {
        when(this.repo.getPermissionById(anyLong())).thenReturn(Optional.empty());

        this.service.getPermission(this.permissionId1);
    }

    @Test(expected = NullPointerException.class)
    public void testGetPermissionNullPermissionId() {
        this.service.getPermission(null);
    }

    @Test
    public void testSavePermissions() {
        // Arrange
        Permission newPermission = new Permission();
        newPermission.setPermission("newPermission");
        newPermission.setPermissionId(1234L);
        Permission[] newPermissions = {newPermission};
        when(this.repo.getPermissionById(any(Long.class))).thenReturn(Optional.empty());

        // Act
        this.service.savePermissions(newPermissions);

        // Assert
        verify(this.repo).savePermissions(this.permissionsCaptor.capture());
        assertNotNull(this.permissionsCaptor.getValue()
                                            .get(0)
                                            .getPermission());
        assertEquals(newPermission.getPermissionId(), this.permissionsCaptor.getValue()
                                                                            .get(0)
                                                                            .getPermissionId());
    }

    @Test(expected = NullPointerException.class)
    public void testSavePermissionsNullPermissionArray() {
        this.service.savePermissions(null);
    }
}
