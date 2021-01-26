package edu.wgu.dm.service;

import edu.wgu.boot.core.exception.AuthorizationException;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.exception.RoleNotFoundException;
import edu.wgu.dm.repository.PermissionRepo;
import edu.wgu.dm.repository.RoleRepo;
import edu.wgu.dm.repository.UserRepo;
import edu.wgu.dm.util.Permissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    private final RoleRepo roleRepo;
    private final UserRepo userRepo;
    private final PermissionRepo permRepo;

    public RoleService(RoleRepo roleRepo, UserRepo userRepo, PermissionRepo permRepo) {
        this.roleRepo = roleRepo;
        this.userRepo = userRepo;
        this.permRepo = permRepo;
    }

    /**
     * Get All Role DTO
     *
     * @return List<Role>
     */
    public List<Role> getRoles() {
        return this.roleRepo.getAllRoles();
    }

    /**
     * Get Role by Id
     *
     * @param roleId
     * @return Role DTO
     */
    public Role getRole(Long roleId) {
        if (roleId == null) {
            throw new NullPointerException("non null roleId is required");
        }
        return this.roleRepo.getRoleById(roleId)
                            .orElseThrow(() -> new RoleNotFoundException(roleId));
    }

    /**
     * Delete a role. Delete cascades on the role_permissions and user_roles table will remove the role from any users.
     *
     * @param roleId
     */
    public void deleteRole(Long roleId) {
        if (roleId == null) {
            throw new NullPointerException("non null roleId is required");
        }
        this.roleRepo.deleteRole(roleId);
    }

    /**
     * Add or update system Roles. Only allow users with the SYSTEM permission to create or update a role with the
     * SYSTEM permission.
     * <p>
     * Permissions need to exist before adding them to the role.
     *
     * @param userId
     * @param roles
     * @return
     */
    public List<Role> saveRoles(String userId, Role[] roles) {
        if (userId == null) {
            throw new NullPointerException("non null userId is required");
        }
        if (roles == null) {
            throw new NullPointerException("non null roles is required");
        }
        List<Role> roleList = Arrays.asList(roles);

        List<Long> permissions = roleList.stream()
                                         .map(Role::getPermissionIds)
                                         .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);

        checkIfSystemUser(permissions, userId);

        return this.roleRepo.saveRoles(roleList);
    }

    /**
     * Validate --> If role has any system permission, only a system user can assign those. Find the SYSTEM permission.
     * If any of the incoming permissions match, then check to see if the current user has the SYSTEM permission. If
     * not, throw an AuthorizationException.
     *
     * @param userId
     */
    private void checkIfSystemUser(List<Long> permissions, String userId) {
        if (userId == null) {
            throw new NullPointerException("non null userId is required");
        }
        if (permissions == null) {
            throw new NullPointerException("non null permissions is required");
        }
        Permission system = this.permRepo.getPermissionByName(Permissions.SYSTEM)
                                         .orElseThrow(() -> new IllegalStateException(
                                             "No SYSTEM permission is configured."));
        if (permissions.contains(system.getPermissionId())) {
            this.userRepo.getUserWithPermission(userId, Permissions.SYSTEM)
                         .orElseThrow(
                             () -> new AuthorizationException("Only SYSTEM users can assign SYSTEM permissions"));
        }
    }
}
