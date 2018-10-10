package edu.wgu.dm.admin.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.wgu.common.exception.AuthorizationException;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.common.exception.UserIdNotFoundException;
import edu.wgu.dm.dto.security.BulkCreateResponse;
import edu.wgu.dm.dto.security.BulkUsers;
import edu.wgu.dm.dto.security.Person;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.dto.security.UserSummary;
import edu.wgu.dm.dto.security.UserTask;
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

    public User getUser(@NonNull String userId) {
        return this.adminRepo.getUserById(userId)
                             .orElseThrow(() -> new UserIdNotFoundException(userId));
    }

    public void addUsers(@NonNull String userId, @NonNull List<User> users) {
        List<Long> roles = users.stream()
                                .map(User::getRoleIds)
                                .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);

        checkIfSystemUser(roles, userId);
        this.adminRepo.saveUsers(users);
    }

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

    public BulkCreateResponse createUsers(@NonNull String userId, @NonNull BulkUsers users) {
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
                         .addAll(users.getRoles().stream().map(r -> new Role(r)).collect(Collectors.toList()));
                     user.getTasks()
                         .addAll(users.getTasks().stream().map(t -> new UserTask(t)).collect(Collectors.toList()));
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
     * Validate --> If role has any system permission, only a system user can assign those.
     * Find the role IDs for all roles with the SYSTEM permission.  If any of the incoming
     * role IDs match, then check to see if the current user has the SYSTEM permission.
     * If not, throw an AuthorizationException.
     * 
     * @param roleIds
     * @param userId
     */
    private void checkIfSystemUser(@NonNull List<Long> roleIds, @NonNull String userId) {
        List<Long> rolesWithSystem = this.adminRepo.getRolesByPermission(Permissions.SYSTEM);
        if (ListUtils.intersection(roleIds, rolesWithSystem).size() > 0) {
            this.adminRepo.getUserWithPermission(userId, Permissions.SYSTEM).orElseThrow(() -> 
                new AuthorizationException("Only SYSTEM users can assign SYSTEM permissions"));
        }
    }
}
