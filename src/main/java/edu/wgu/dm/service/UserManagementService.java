package edu.wgu.dm.service;

import edu.wgu.boot.core.exception.AuthorizationException;
import edu.wgu.dm.dto.response.BulkCreateResponse;
import edu.wgu.dm.dto.security.BulkUsers;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserSummary;
import edu.wgu.dm.exception.UserNotFoundException;
import edu.wgu.dm.repository.RoleRepo;
import edu.wgu.dm.repository.UserRepo;
import edu.wgu.dm.service.feign.PersonService;
import edu.wgu.dm.util.Permissions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserManagementService {

    private final UserRepo adminRepo;

    private final RoleRepo roleRepo;

    PersonService personService;

    public User getUser(@NonNull String userId) {
        return this.adminRepo.getUserById(userId)
                             .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public void saveUser(@NonNull String userId, @NonNull User user) {
        Set<Long> roles = user.getRoleIds();

        checkIfSystemUser(roles, userId);
        this.adminRepo.saveUser(user);
    }

    /**
     *  We will need to user deletion as a soft delete
      */
    public void deleteUser(@NonNull String userId) {
        this.adminRepo.deleteUser(userId);
    }

    public List<UserSummary> getUsers() {
        return this.adminRepo.getAllUsers();
    }

    public List<UserSummary> getUsersForTask(@NonNull Long taskId) {
        return this.adminRepo.getUsersByTask(taskId);
    }

    public User createUser(@NonNull String userId) {
        Person person = this.personService.getPersonByUsername(userId);
        if (Boolean.FALSE.equals(person.getIsEmployee()))
            throw new IllegalArgumentException("User is not an employee.");

        return this.adminRepo.getUserById(person.getUserId())
                             .orElseGet(() -> saveUser(person));
    }

    private User saveUser(Person person) {
        User user = new User(person);
        return this.adminRepo.saveUser(user).orElseThrow(()->new IllegalStateException("Saving User failed"));
    }

    public BulkCreateResponse createUsers(@NonNull String userId, @NonNull BulkUsers users) {
        List<User> toCreate = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        List<Role> roles = users.getRoles()
                                .stream()
                                .map(Role::new)
                                .collect(Collectors.toList());

        checkIfSystemUser(users.getRoles(), userId);
        users.getUsernames()
             .forEach(name -> {
                 try {
                     Person person = this.personService.getPersonByUsername(name);
                     User user = this.adminRepo.getUserById(person.getUserId())
                                               .orElseGet(() -> new User(person));
                     user.getRoles()
                         .addAll(roles);
                     user.getTasks()
                         .addAll(users.getTasks());
                     toCreate.add(user);
                 } catch (Exception e) {
                     log.error("Error creating new user.", e);
                     failed.add(name);
                 }
             });

        this.adminRepo.saveUsers(toCreate);
        return new BulkCreateResponse(toCreate, failed);
    }

    /**
     * Validate that only a system user can assign system role to other user. If any of the incoming
     * role IDs match with any role that has system permission, then check to see if the current user
     * has the SYSTEM permission. If not, throw an AuthorizationException.
     *  @param roleIds
     * @param userId
     * @return
     */
    private UserSummary checkIfSystemUser(@NonNull Collection<Long> roleIds, @NonNull String userId) {
        List<Long> rolesWithSystem = this.roleRepo.getRolesByPermission(Permissions.SYSTEM);
        UserSummary summary = null;
        if (CollectionUtils.containsAny(roleIds, rolesWithSystem)) {
            summary= this.adminRepo.getUserWithPermission(userId, Permissions.SYSTEM)
                          .orElseThrow(
                                  () -> new AuthorizationException("Only SYSTEM users can assign SYSTEM permissions"));
        }

        return summary;
    }
}
