package edu.wgu.dm.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.common.exception.RoleNotFoundException;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.User;
import edu.wgu.dm.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RoleService {

    @Autowired
    private AdminRepository adminRepo;
 

    /**
     * Get All Role DTO
     * 
     * @return List<Role>
     */
    public List<Role> getRoles() {
        return adminRepo.getAllRoles();
    }

    /**
     * Get Role by Id
     * 
     * @param roleId
     * @return Role DTO
     */
    public Role getRole(Long roleId) {
        return adminRepo.getRoleById(roleId).orElseThrow(() -> new RoleNotFoundException(roleId));
    }

    /**
     * When deleting a role, check for any users who have it assigned and remove it.
     * 
     * @param roleId
     */
    public void deleteRole(Long roleId) {
        List<User> usersWithRole = adminRepo.getUsersByRole(roleId);
        for (User user : usersWithRole) {
            // using streams filter to account for duplicate role-ids
            List<Long> roles =
                    user.getRoles().stream().filter(r -> r != roleId).collect(Collectors.toList());
            user.setRoles(roles);
        }
        adminRepo.saveUsers(usersWithRole);
        adminRepo.deleteRole(roleId);
    }

    /**
     * Add or update a system Role.
     * 
     * Permissions need to exist before adding them to the role.
     * 
     * @param role array
     */
    @Transactional
    public List<Role> saveRoles(Role[] roles) {
        List<Role> roleList = new ArrayList<>();
        if (roles == null || roles.length == 0) {
            log.error("Roles array can not be null or empty");
            return roleList;
        }
        for (Role role : roles) {
            try {
                if (role == null) {
                    log.info("Null Role object passed is not allowed");
                    continue;
                }
                if (role.getRoleId() == null && StringUtils.isBlank(role.getRole())) {
                    log.error("Saving Failed: Role Id and Role, both can not be empty for: {}",
                            role);
                    continue;
                }
                Optional<Role> existingRole = Optional.empty();
                if (role.getRoleId() != null) {
                    existingRole = adminRepo.getRoleById(role.getRoleId());
                }
                if (!existingRole.isPresent()) {
                    log.info("Could not find Role by roleId: {}", role);
                    // We will first check if the role exist by id, if not we will check by role
                    // field. Role table has unique constraint on the role field
                    existingRole = adminRepo.getRolesByRole(StringUtils.trim(role.getRole()));
                    if (!existingRole.isPresent()) {
                        log.info("Could not find Role by role field: {}", role);
                    }
                }

                // if Role by id or role field is present, then copy user intended changes
                if (existingRole.isPresent()) {
                    // copy changes from the user provided RoleDto to dto retrieved from the
                    // database.
                    Role existingRoleDto = existingRole.get();
                    if (StringUtils.isNotBlank(role.getRole())) {
                        existingRoleDto.setRole(role.getRole());
                    }
                    if (StringUtils.isNotBlank(role.getRoleDescription())) {
                        existingRoleDto.setRoleDescription(role.getRoleDescription());
                    }
                    existingRoleDto.setDateUpdated(role.getDateUpdated());
                    existingRoleDto.setPermissions(role.getPermissions());
                    role = existingRoleDto;
                    log.info("Existing Role will be updated: {}", role);
                } else {
                    // Lets save this RoleDto as a brand new role since we couldnt find the existing
                    // one.
                    if (role.getDateCreated() == null) {
                        role.setDateCreated(DateUtil.getZonedNow());
                    }
                    log.info("New Role will be created: {}", role);
                }
                roleList.add(adminRepo.saveOrUpdateRole(role));
            } catch (Exception ex) {
                log.error("Error: Could not save role:" + role, ex);
            }
        }
        return roleList;
    }
}


