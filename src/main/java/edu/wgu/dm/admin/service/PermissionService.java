package edu.wgu.dm.admin.service;

import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.wgu.dm.admin.repository.AdminRepository;
import edu.wgu.dm.common.exception.PermissionNotFoundException;
import edu.wgu.dm.dto.security.Permission;

@Service
public class PermissionService {

    @Autowired
    private AdminRepository dmRepo;


    public List<Permission> getPermissions() {
        return this.dmRepo.getAllPermissions();
    }

    public Permission getPermission(Long permissionId) {
        return this.dmRepo.getPermissionById(permissionId)
                          .orElseThrow(() -> new PermissionNotFoundException(permissionId));
    }

    /**
     * Add or update Permissions. Permission name is unique in the permission table.
     * 
     * @param permission array
     */
    public void savePermissions(@Nonnull Permission[] permissions) {
        this.dmRepo.savePermissions(Arrays.asList(permissions));
    }
}
