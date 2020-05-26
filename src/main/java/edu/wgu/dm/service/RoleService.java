package edu.wgu.dm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import edu.wgu.boot.core.exception.AuthorizationException;
import edu.wgu.dm.repository.PermissionRepo;
import edu.wgu.dm.repository.RoleRepo;
import edu.wgu.dm.repository.UserRepo;
import edu.wgu.dm.common.exception.RoleNotFoundException;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.util.Permissions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepo roleRepo;

    private final UserRepo userRepo;

    private final PermissionRepo permRepo;

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
    public Role getRole(@NonNull Long roleId) {
        return this.roleRepo.getRoleById(roleId)
                            .orElseThrow(() -> new RoleNotFoundException(roleId));
    }

    /**
     * Delete a role. Delete cascades on the role_permissions and user_roles table will remove the role
     * from any users.
     * 
     * @param roleId
     */
    public void deleteRole(@NonNull Long roleId) {
        this.roleRepo.deleteRole(roleId);
    }

    /**
     * Add or update system Roles. Only allow users with the SYSTEM permission to create or update a
     * role with the SYSTEM permission.
     * 
     * Permissions need to exist before adding them to the role.
     * 
     * @param role array
     */
    public List<Role> saveRoles(@NonNull String userId, @NonNull Role[] roles) {
        List<Role> roleList = Arrays.asList(roles);

        List<Long> permissions = roleList.stream()
                                         .map(Role::getPermissionIds)
                                         .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);

        checkIfSystemUser(permissions, userId);

        return this.roleRepo.saveRoles(roleList);
    }

    /**
     * Validate --> If role has any system permission, only a system user can assign those. Find the
     * SYSTEM permission. If any of the incoming permissions match, then check to see if the current
     * user has the SYSTEM permission. If not, throw an AuthorizationException.
     * 
     * @param roleIds
     * @param userId
     */
    private void checkIfSystemUser(@NonNull List<Long> permissions, @NonNull String userId) {
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
