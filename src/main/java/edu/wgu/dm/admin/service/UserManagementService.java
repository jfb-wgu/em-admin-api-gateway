package edu.wgu.dm.admin.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.wgu.common.exception.AuthorizationException;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.annotation.NonNullPositive;
import edu.wgu.dm.common.exception.UserIdNotFoundException;
import edu.wgu.dm.dto.security.BulkCreateResponse;
import edu.wgu.dm.dto.security.BulkUsers;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.service.feign.PersonService;
import edu.wgu.dm.util.Permissions;
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
        return this.adminRepo.getUserById(userId)
                             .orElseThrow(() -> new UserIdNotFoundException(userId));
    }

    public void addUsers(String userId, @NonNull List<User> users) {
        List<Long> roles = new ArrayList<>();
        users.forEach(user -> roles.addAll(user.getRoles()));
        checkIfSystemUser(roles, userId);
        this.adminRepo.saveUsers(users);
    }

    public void deleteUser(String userId) {
        this.adminRepo.deleteUser(userId);
    }

    public List<User> getUsers() {
        return this.adminRepo.getAllUsers();
    }

    public List<User> getUsersForTask(@NonNullPositive Long taskId) {
        return this.adminRepo.getUsersByTask(taskId);
    }

    public User createUser(String userId) {
        Person person = this.personService.getPersonByUsername(userId);
        User newUser = this.adminRepo.getUserById(person.getUserId())
                                     .orElseGet(() -> {
                                         User user = new User();
                                         user.setUserId(person.getUserId());
                                         user.setFirstName(person.getFirstName());
                                         user.setLastName(person.getLastName());
                                         user.setEmployeeId(person.getUsername());
                                         return this.adminRepo.saveUser(user)
                                                              .get();
                                     });
        return newUser;
    }

    public BulkCreateResponse createUsers(String userId, @Nonnull BulkUsers users) {
        List<User> toCreate = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        checkIfSystemUser(users.getRoles(), userId);
        users.getUsernames()
             .forEach(name -> {
                 try {
                     Person person = this.personService.getPersonByUsername(name);
                     User user = this.adminRepo.getUserById(person.getUserId())
                                               .orElseGet(() -> {
                                                   User userdto = new User();
                                                   userdto.setUserId(person.getUserId());
                                                   userdto.setFirstName(person.getFirstName());
                                                   userdto.setLastName(person.getLastName());
                                                   userdto.setEmployeeId(person.getUsername());
                                                   return userdto;
                                               });

                     user.getRoles()
                         .addAll(users.getRoles());
                     user.getTasks()
                         .addAll(users.getTasks());
                     toCreate.add(user);
                 } catch (Exception e) {
                     log.error(Arrays.toString(e.getStackTrace()));
                     failed.add(name);
                 }
             });

        this.adminRepo.saveUsers(toCreate);
        return new BulkCreateResponse(toCreate, failed);
    }

    /**
     * Validate --> If role has any system permission, only a sys user can assign those.
     * 
     * @param roleIds
     * @param userId
     */
    public void checkIfSystemUser(@NonNull List<Long> roleIds, String userId) {
        List<Role> roles = this.adminRepo.getAllRoles();
        List<Permission> permissions = new ArrayList<>();
        roles.forEach(role -> permissions.addAll(role.getPermissions()));
        boolean isAnySysPermissionExist = permissions.stream()
                                                     .anyMatch(permission -> Permissions.SYSTEM.equalsIgnoreCase(
                                                             permission.getPermission()));

        if (isAnySysPermissionExist) {
            User changeRequestedBy = getUser(userId);
            boolean isSysUser = changeRequestedBy.getPermissions()
                                                 .stream()
                                                 .anyMatch(permissionName -> Permissions.SYSTEM.equalsIgnoreCase(
                                                         permissionName));
            if (!isSysUser) {
                throw new AuthorizationException("Only SYSTEM users can assign SYSTEM permissions");
            }
        }
    }
}
