package edu.wgu.dm.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import edu.wgu.common.exception.AuthorizationException;
import edu.wgu.dm.admin.repository.PermissionRepo;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.entity.security.UserEntity;
import edu.wgu.dm.util.Permissions;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("local")
@Transactional
public class RoleServiceIT {

    @Autowired
    RoleService roleService;

    @Autowired
    private PermissionRepo permRepo;

    @Autowired
    EntityManager entityManager;

    @Test(expected = AuthorizationException.class)
    public void testOnlySystemUserCanAddSystemRole() {

        // arrange
        // Create a non system user that would try to save a new role with system permission
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("test first name");
        userEntity.setLastName("test last name");
        userEntity.setUserId("test_user");
        userEntity.setEmployeeId("test.userId");
        userEntity = entityManager.merge(userEntity);

        // create a dummy role with system permission
        Optional<Permission> sysPerm = permRepo.getPermissionByName(Permissions.SYSTEM);
        Role role = new Role();
        role.setRole("test_role");
        role.setRoleDescription("test_role_desc");
        List<Permission> permissions = new ArrayList<>();
        Permission perm = new Permission();
        perm.setPermissionId(sysPerm.get()
                                    .getPermissionId());
        permissions.add(perm);
        role.setPermissions(permissions);

        // act
        // save should fail
        roleService.saveRoles(userEntity.getUserId(), new Role[] {role});
    }



}
