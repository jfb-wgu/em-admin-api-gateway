package edu.wgu.dm.admin.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.common.exception.RoleNotFoundException;
import edu.wgu.dm.dto.security.Role;

@Service
public class RoleService {

    @Autowired
    private AdminRepository adminRepo;

    /**
     * Get All Role DTO
     * 
     * @return List<Role>
     */
    public List<Role> getRoles() {
        return this.adminRepo.getAllRoles();
    }

    /**
     * Get Role by Id
     * 
     * @param roleId
     * @return Role DTO
     */
    public Role getRole(Long roleId) {
        return this.adminRepo.getRoleById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));
    }

    /**
     * Delete a role. Delete cascades on the role_permissions and user_roles table will remove the
     * role from any users.
     * 
     * @param roleId
     */
    public void deleteRole(Long roleId) {
        this.adminRepo.deleteRole(roleId);
    }

    /**
     * Add or update a system Role.
     * 
     * Permissions need to exist before adding them to the role.
     * 
     * @param role array
     */
    public List<Role> saveRoles(Role[] roles) {
        return this.adminRepo.saveRoles(Arrays.asList(roles));
    }
}
