package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import edu.wgu.common.exception.AuthorizationException;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.admin.service.RoleService;
import edu.wgu.dm.common.exception.RoleNotFoundException;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserSummary;
import edu.wgu.dm.repo.ema.RoleRepository;
import edu.wgu.dm.util.DateUtil;
import edu.wgu.dm.util.Permissions;
import edu.wgu.dmadmin.test.TestObjectFactory;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("boxing")
public class RoleServiceTest {

    @Mock
    AdminRepository repo;

    @Mock
    RoleRepository roleRepo;

    @InjectMocks
    RoleService service;

    Random random = new Random();
    Long permissionId1 = this.random.nextLong();
    Long permissionId2 = this.random.nextLong();
    Long roleId1 = this.random.nextLong();
    Long roleId2 = this.random.nextLong();

    List<Permission> permissions;
    Role role;
    Role[] roleArray;
    List<Role> roles;

    @Captor
    ArgumentCaptor<List<User>> captorUsers;

    @Captor
    ArgumentCaptor<List<Role>> captorRoles;

    @Before
    public void initialize() {
        Permission perm1 = new Permission();
        perm1.setPermissionId(this.permissionId1);
        perm1.setPermission("1");

        Permission perm2 = new Permission();
        perm2.setPermissionId(this.permissionId2);
        perm2.setPermission("2");

        this.permissions = Arrays.asList(perm1, perm2);

        this.role = new Role();
        this.role.setDateCreated(DateUtil.getZonedNow());
        this.role.setRole("Admin");
        this.role.setPermissions(this.permissions);
        this.role.setDateUpdated(DateUtil.getZonedNow());
        this.role.setRoleId(this.roleId1);
        this.role.setRoleDescription("Description");

        this.roleArray = new Role[] {this.role};
        this.roles = Arrays.asList(this.role);
    }

    @Test
    public void testGetRoles() {
        when(this.repo.getAllRoles()).thenReturn(this.roles);
        assertEquals(this.service.getRoles()
                                 .size(),
                this.roles.size());
    }

    @Test
    public void testGetNoRoles() {
        when(this.repo.getAllRoles()).thenReturn(Collections.emptyList());
        List<Role> result = this.service.getRoles();
        assertEquals(0, result.size());
    }

    @Test(expected = RoleNotFoundException.class)
    public void testNoRoleFound() {
        when(this.repo.getRoleById(anyLong())).thenThrow(new RoleNotFoundException(anyLong()));
        this.service.getRole(this.random.nextLong());
    }

    @Test
    public void testGetRole() {
        // Arrange
        when(this.repo.getRoleById(anyLong())).thenReturn(Optional.of(this.role));
        // Act + Assert
        assertEquals(this.service.getRole(this.role.getRoleId())
                                 .getRoleId(),
                this.role.getRoleId());
    }

    @Test(expected = NullPointerException.class)
    public void testGetRoleNullRoleId() {
        this.service.getRole(null);
    }

    @Test
    public void testDeleteRole() {
        // Act
        this.service.deleteRole(this.roleId1);

        // Verify
        verify(this.repo).deleteRole(this.roleId1);
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteRoleNullRoleId() {
        this.service.deleteRole(null);
    }

    @Test
    public void testSaveRole() {
        // Arrange
        when(this.repo.saveRoles(this.roles)).thenReturn(this.roles);
        when(this.repo.getPermissionByName(Permissions.SYSTEM)).thenReturn(
                Optional.of(TestObjectFactory.getPermission("test")));

        // Act
        List<Role> result = this.service.saveRoles("test", this.roleArray);

        // Assert
        verify(this.repo).saveRoles(this.captorRoles.capture());
        assertEquals(this.permissionId1, this.captorRoles.getValue()
                                                         .get(0)
                                                         .getPermissions()
                                                         .get(0)
                                                         .getPermissionId());
        System.out.println(this.captorRoles.getValue());
        assertNotNull(result.get(0)
                            .getRoleId());
        assertNotNull(result.get(0)
                            .getDateCreated());
    }

    @Test
    public void testSaveRoleSystemRole() {
        // Arrange
        Permission systemPerm = TestObjectFactory.getPermission("SYSTEM");
        Role systemRole = TestObjectFactory.getRole("system");
        systemRole.getPermissions()
                  .add(systemPerm);

        when(this.repo.getPermissionByName(Permissions.SYSTEM)).thenReturn(Optional.of(systemPerm));
        when(this.repo.getUserWithPermission("test", Permissions.SYSTEM)).thenReturn(Optional.of(new UserSummary()));

        // Act
        this.service.saveRoles("test", new Role[] {systemRole});

        // Assert
        verify(this.repo).saveRoles(this.captorRoles.capture());
        assertEquals(systemPerm.getPermissionId(), this.captorRoles.getValue()
                                                                   .get(0)
                                                                   .getPermissions()
                                                                   .get(0)
                                                                   .getPermissionId());
        assertNotNull(this.captorRoles.getValue()
                                      .get(0)
                                      .getRoleId());
        assertNotNull(this.captorRoles.getValue()
                                      .get(0)
                                      .getDateCreated());
    }

    @Test(expected = AuthorizationException.class)
    public void testSaveRoleSystemRoleNotSystemUser() {
        // Arrange
        Permission systemPerm = TestObjectFactory.getPermission("SYSTEM");
        Role systemRole = TestObjectFactory.getRole("system");
        systemRole.getPermissions()
                  .add(systemPerm);

        when(this.repo.getPermissionByName(Permissions.SYSTEM)).thenReturn(Optional.of(systemPerm));
        when(this.repo.getUserWithPermission("test", Permissions.SYSTEM)).thenReturn(Optional.empty());

        // Act
        this.service.saveRoles("test", new Role[] {systemRole});
    }

    @Test(expected = IllegalStateException.class)
    public void testSaveRoleSystemRoleNoSystemPermission() {
        // Arrange
        Permission systemPerm = TestObjectFactory.getPermission("SYSTEM");
        Role systemRole = TestObjectFactory.getRole("system");
        systemRole.getPermissions()
                  .add(systemPerm);

        when(this.repo.getPermissionByName(Permissions.SYSTEM)).thenReturn(Optional.empty());

        // Act
        this.service.saveRoles("test", new Role[] {systemRole});
    }
}
