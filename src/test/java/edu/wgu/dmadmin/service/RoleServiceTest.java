package edu.wgu.dmadmin.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
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
import edu.wgu.dm.common.exception.RoleNotFoundException;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.entity.security.UserEntity;
import edu.wgu.dm.repo.ema.RoleRepository;
import edu.wgu.dm.repository.admin.AdminRepository;
import edu.wgu.dm.service.admin.RoleService;
import edu.wgu.dm.util.DateUtil;

@RunWith(MockitoJUnitRunner.class)
public class RoleServiceTest {

    @Mock
    AdminRepository repo;

    @Mock
    RoleRepository roleRepo;

    @InjectMocks
    RoleService service;

    Random random = new Random();
    Long permissionId1 = random.nextLong();
    Long permissionId2 = random.nextLong();
    Long roleId1 = random.nextLong();
    Long roleId2 = random.nextLong();
    String userId1 = "user1";
    String userId2 = "user2";
    RoleEntity role1 = new RoleEntity();
    RoleEntity role2 = new RoleEntity();

    UserEntity user1 = new UserEntity();
    UserEntity user2 = new UserEntity();

    List<RoleEntity> roleEntities = new ArrayList<>();
    List<Role> roles = new ArrayList<>();
    Role role = new Role();

    @Captor
    ArgumentCaptor<List<User>> captorUsers;


    @Before
    public void initialize() {
        role.setRoleId(random.nextLong());
        role1.setRoleId(random.nextLong());
        roleEntities.add(role1);
        roleEntities.add(role2);
        user1.setRoles(roleEntities);
    }

    @Test
    public void testGetRoles() {
        when(repo.getAllRoles()).thenReturn(roles);
        assertEquals(service.getRoles().size(), roles.size());
    }

    @Test
    public void testGetNoRoles() {
        when(repo.getAllRoles()).thenReturn(Collections.emptyList());
        List<Role> result = service.getRoles();
        assertEquals(0, result.size());
    }

    @Test(expected = RoleNotFoundException.class)
    public void testNoRoleFound() {
        when(repo.getRoleById(anyLong())).thenThrow(new RoleNotFoundException(anyLong()));
        service.getRole(random.nextLong());
    }

    @Test
    public void testGetRole() {
        // Arrange
        when(repo.getRoleById(anyLong())).thenReturn(Optional.of(role));
        // Act + Assert
        assertEquals(service.getRole(role.getRoleId()).getRoleId(), role.getRoleId());
    }


    @Test
    public void testDeleteRole() {
        // Arrange
        Long roleId1 = random.nextLong();
        List<User> usersWithRole = new ArrayList<>();
        List<Long> roles = new ArrayList<>();
        roles.add(roleId1);
        User userRole = new User();
        userRole.setRoles(roles);
        usersWithRole.add(userRole);
        when(repo.getUsersByRole(roleId1)).thenReturn(usersWithRole);

        // Act
        this.service.deleteRole(roleId1);

        // Verify
        verify(repo).saveUsers(this.captorUsers.capture());
        assertEquals(0, this.captorUsers.getValue().get(0).getRoles().size());
        verify(repo).deleteRole(roleId1);
    }


    @Test
    public void testDeleteRoleNoUsers() { // Arrange
        Long roleId1 = random.nextLong();
        List<User> usersWithRole = new ArrayList<>();
        when(repo.getUsersByRole(roleId1)).thenReturn(usersWithRole);

        // Act
        this.service.deleteRole(roleId1);

        // Verify
        verify(repo).saveUsers(this.captorUsers.capture());
        assertEquals(0, this.captorUsers.getValue().size());
        verify(repo).deleteRole(roleId1);
    }


    @Test
    public void testSaveNewRole() {
        // Arrange
        Role newRole = new Role();
        newRole.setRole("newRoleToSave");
        newRole.setPermissions(Arrays.asList(permissionId1));
        newRole.setRoleId(random.nextLong());
        newRole.setDateCreated(DateUtil.getZonedNow());
        Role[] newRoles = {newRole};

        //  Setup no existing roles is found
        when(repo.getRolesByRole(newRole.getRole())).thenReturn(Optional.empty());
        when(repo.getRoleById(newRole.getRoleId())).thenReturn(Optional.empty());
        
        when(repo.saveOrUpdateRole(any(Role.class))).thenReturn(newRole);
      
        ArgumentCaptor<Role> argument = ArgumentCaptor.forClass(Role.class);

        // Act
        List<Role> result = this.service.saveRoles(newRoles);

        // Assert
        verify(repo).saveOrUpdateRole(argument.capture());
        assertEquals(this.permissionId1, argument.getValue().getPermissions().iterator().next());
        System.out.println(argument.getValue());
        assertNotNull(result.get(0).getRoleId());
        assertNotNull(result.get(0).getDateCreated());
    }


    @Test
    public void testUpdateRoles() {
        // Arrange
        Long roleId = random.nextLong();
        
        Role role = new Role();
        role.setRoleId(roleId);
        role.setPermissions(Arrays.asList(permissionId1, permissionId2));
        role.setDateUpdated(DateUtil.getZonedNow());
        Role[] newRoles = {role};
        
        Role existingRole = new Role();
        existingRole.setRoleId(roleId);
        existingRole.setRole("existingRole");
        existingRole.setRoleDescription("Description for ExistingRole");
        List<Long> permissionIds = Arrays.asList(permissionId1);
        existingRole.setPermissions(permissionIds);
        
        when(repo.getRoleById(roleId)).thenReturn(Optional.of(existingRole));
        when(repo.saveOrUpdateRole(any(Role.class))).thenReturn(role);
        ArgumentCaptor<Role> argument = ArgumentCaptor.forClass(Role.class);

        // Act
        List<Role> result = this.service.saveRoles(newRoles);

        // Assert
        verify(repo).saveOrUpdateRole(argument.capture());
        assertEquals(2, argument.getValue().getPermissions().size());
        assertEquals(existingRole.getRoleDescription(), argument.getValue().getRoleDescription());
        assertEquals(existingRole.getRole(), argument.getValue().getRole());
        System.out.println(argument.getValue());
        assertNotNull(result.get(0).getRoleId());
        assertNotNull(result.get(0).getDateUpdated());
    }

}
