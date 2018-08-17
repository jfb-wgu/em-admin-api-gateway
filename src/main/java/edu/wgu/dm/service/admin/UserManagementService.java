package edu.wgu.dm.service.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.wgu.common.exception.AuthorizationException;
import edu.wgu.dm.common.exception.UserIdNotFoundException;
import edu.wgu.dm.dto.security.BulkCreateResponse;
import edu.wgu.dm.dto.security.BulkUsers;
import edu.wgu.dm.dto.security.Permissions;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.entity.security.PermissionEntity;
import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.repository.admin.AdminRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserManagementService {

    @Autowired
    AdminRepository adminRepo;

    @Autowired
    PersonService personService;
 

    public User getUser(String userId) {
        return adminRepo.getUserById(userId).orElseThrow(() -> new UserIdNotFoundException(userId));
    }

    public void addUsers(String userId, @NonNull List<User> users) {
        List<Long> roles = new ArrayList<>();
        users.forEach(user -> roles.addAll(user.getRoles()));
        checkIfSystemUser(roles, userId);
        adminRepo.saveUsers(users);
    }

    public void deleteUser(String userId) {
        adminRepo.deleteUser(userId);
    }

    public List<User> getUsers() {
         return adminRepo.getAllUsers();
    }

    public List<User> getUsersForTask(Long taskId) {
        return adminRepo.getAllUsers().stream().filter(u -> u.getTasks().contains(taskId)).sorted()
                .collect(Collectors.toList());
    }

    public User createUser(String userId) {
        Person person = personService.getPersonByUsername(userId);
        User newUser = adminRepo.getUserById(person.getUserId()).orElseGet(() -> {
            User user = new User();
            user.setUserId(person.getUserId());
            user.setFirstName(person.getFirstName());
            user.setLastName(person.getLastName());
            user.setEmployeeId(person.getUsername());
            return adminRepo.saveUser(user).get();
        });
        return newUser;
    }

    public BulkCreateResponse createUsers(String userId, BulkUsers users) {
        List<User> toCreate = new ArrayList<>();
        List<String> failed = new ArrayList<>();
        
        checkIfSystemUser(users.getRoles(), userId);
        users.getUsernames().forEach(name -> {
            try {
                Person person = this.personService.getPersonByUsername(name);
                User user = adminRepo.getUserById(person.getUserId()).orElseGet(() -> {
                    User userdto = new User();
                    userdto.setUserId(person.getUserId());
                    userdto.setFirstName(person.getFirstName());
                    userdto.setLastName(person.getLastName());
                    userdto.setEmployeeId(person.getUsername());
                    return userdto;
                });

                user.getRoles().addAll(users.getRoles());
                user.getTasks().addAll(users.getTasks());
                toCreate.add(user);
            } catch (Exception e) {
                log.error(Arrays.toString(e.getStackTrace()));
                failed.add(name);
            }
        });

        adminRepo.saveUsers(toCreate);
        return new BulkCreateResponse(toCreate, failed);
    }


    /**
     * Validate --> If role has any system permission, only a sys user can assign those.
     * 
     * @param roleIds
     * @param userId
     */
    public void checkIfSystemUser(@NonNull List<Long> roleIds, String userId) {
        List<RoleEntity> roles = adminRepo.findAllRoles(roleIds);
        List<PermissionEntity> permissions = new ArrayList<>();
        roles.forEach(role -> permissions.addAll(role.getPermissions()));
        boolean isAnySysPermissionExist = permissions.stream().anyMatch(
                permission -> Permissions.SYSTEM.equalsIgnoreCase(permission.getPermission()));

        if (isAnySysPermissionExist) {
            User changeRequestedBy = getUser(userId);
            boolean isSysUser = changeRequestedBy.getPermissions().stream().anyMatch(
                    permissionName -> Permissions.SYSTEM.equalsIgnoreCase(permissionName));
            if (!isSysUser) {
                throw new AuthorizationException("Only SYSTEM users can assign SYSTEM permissions");
            }
        }
    }

}
