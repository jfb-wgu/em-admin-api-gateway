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
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserManagementService {

    private static final Logger log = LoggerFactory.getLogger(UserManagementService.class);
    private final UserRepo adminRepo;
    private final RoleRepo roleRepo;
    private final PersonService personService;

    public UserManagementService(UserRepo adminRepo, RoleRepo roleRepo,
        PersonService personService) {
        this.adminRepo = adminRepo;
        this.roleRepo = roleRepo;
        this.personService = personService;
    }

    public User getUser(  String userId) {
        if (userId == null) {
            throw new NullPointerException("non null userId is required");
        }
        return this.adminRepo.getUserById(userId)
                             .orElseThrow(() -> new UserNotFoundException(userId));
    }

    public void saveUser( String userId,  User user) {
        if (userId == null) {
            throw new NullPointerException("non null userId is required");
        }
        if (user == null) {
            throw new NullPointerException("non null user is required");
        }
        Set<Long> roles = user.getRoleIds();

        checkIfSystemUser(roles, userId);
        this.adminRepo.saveUser(user);
    }

    /**
     * We will need to user deletion as a soft delete
     */
    public void deleteUser(String userId) {
        if (userId == null) {
            throw new NullPointerException("non null userId is required");
        }
        this.adminRepo.deleteUser(userId);
    }

    public List<UserSummary> getUsers() {
        return this.adminRepo.getAllUsers();
    }

    public List<UserSummary> getUsersForTask(Long taskId) {
        if (taskId == null) {
            throw new NullPointerException("non null TaskId is required");
        }
        return this.adminRepo.getUsersByTask(taskId);
    }

    public User createUser(String userId) {
        if (userId == null) {
            throw new NullPointerException("non null userId is required");
        }
        Person person = this.personService.getPersonByUsername(userId);
        if (Boolean.FALSE.equals(person.getIsEmployee())) {
            throw new IllegalArgumentException("User is not an employee.");
        }

        return this.adminRepo.getUserById(person.getUserId())
                             .orElseGet(() -> saveUser(person));
    }

    private User saveUser(Person person) {
        User user = new User(person);
        return this.adminRepo.saveUser(user)
                             .orElseThrow(() -> new IllegalStateException("Saving User failed"));
    }

    public BulkCreateResponse createUsers(String userId, BulkUsers users) {
        if (userId == null) {
            throw new NullPointerException("non null userId is required");
        }
        if (users == null) {
            throw new NullPointerException("non null users is required");
        }
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
     * Validate that only a system user can assign system role to other user. If any of the incoming role IDs match with
     * any role that has system permission, then check to see if the current user has the SYSTEM permission. If not,
     * throw an AuthorizationException.
     *
     * @param roleIds
     * @param userId
     * @return
     */
    private UserSummary checkIfSystemUser(Collection<Long> roleIds, String userId) {
        if (roleIds == null) {
            throw new NullPointerException("non null roleIds is required");
        }
        if (userId == null) {
            throw new NullPointerException("non null userId is required");
        }
        List<Long> rolesWithSystem = this.roleRepo.getRolesByPermission(Permissions.SYSTEM);
        UserSummary summary = null;
        if (CollectionUtils.containsAny(roleIds, rolesWithSystem)) {
            summary = this.adminRepo.getUserWithPermission(userId, Permissions.SYSTEM)
                                    .orElseThrow(
                                        () -> new AuthorizationException(
                                            "Only SYSTEM users can assign SYSTEM permissions"));
        }

        return summary;
    }
}
