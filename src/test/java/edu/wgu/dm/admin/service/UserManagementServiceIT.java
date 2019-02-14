package edu.wgu.dm.admin.service;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import edu.wgu.boot.core.exception.AuthorizationException;
import edu.wgu.dm.admin.repository.RoleRepo;
import edu.wgu.dm.admin.repository.UserRepo;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.entity.security.UserEntity;
import edu.wgu.dm.util.Permissions;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles("local")
@Transactional
public class UserManagementServiceIT {

    @Autowired
    RoleRepo roleRepo;

    @Autowired
    UserRepo adminRepo;

    @Autowired
    EntityManager entityManager;

    @Autowired
    UserManagementService userMgmntSvc;

    // Only an admin user can save a user with System Permission role
    @Test(expected = AuthorizationException.class)
    public void testSaveByNonSystemUser() {
        // arrange
        List<Long> rolesWithSystem = this.roleRepo.getRolesByPermission(Permissions.SYSTEM);
        if (CollectionUtils.isEmpty(rolesWithSystem)) {
            throw new RuntimeException("Role with System permission do not exist");
        }
        // create a user to be saved with Sys Role
        User user = new User();
        List<Role> roles = new ArrayList<>();
        Role r = new Role();
        r.setRoleId(rolesWithSystem.get(0));
        roles.add(r);
        user.setRoles(roles);

        // create a temp user to get user_id
        UserEntity userPerformingSaveCall = createUser("test_user");

        // act
        userMgmntSvc.saveUser(userPerformingSaveCall.getUserId(), user);
    }

    /**
     * @param userId TODO
     * @return
     */
    private UserEntity createUser(String userId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName("test first name");
        userEntity.setLastName("test last name");
        userEntity.setUserId(userId);
        userEntity.setEmployeeId("test.person");
        return entityManager.merge(userEntity);
    }

}
