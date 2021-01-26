package edu.wgu.dm.service;

import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.exception.PermissionNotFoundException;
import edu.wgu.dm.repository.PermissionRepo;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {

    private final PermissionRepo repo;

    public PermissionService(PermissionRepo repo) {
        this.repo = repo;
    }

    public List<Permission> getPermissions() {
        return this.repo.getAllPermissions();
    }

    public Permission getPermission(Long permissionId) {
        if (permissionId == null) {
            throw new NullPointerException("non null permissionId is required");
        }
        return this.repo.getPermissionById(permissionId)
                        .orElseThrow(() -> new PermissionNotFoundException(permissionId));
    }

    /**
     * Add or update Permissions. Permission name is unique in the permission table.
     *
     * @param permissions
     */
    public void savePermissions(Permission[] permissions) {
        if (permissions == null) {
            throw new NullPointerException("non null permissions is required");
        }
        this.repo.savePermissions(Arrays.asList(permissions));
    }
}
