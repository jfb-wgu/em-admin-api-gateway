package edu.wgu.dm.admin.service;

import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import edu.wgu.dm.admin.repository.PermissionRepo;
import edu.wgu.dm.common.exception.PermissionNotFoundException;
import edu.wgu.dm.dto.security.Permission;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

    PermissionRepo repo;


    public List<Permission> getPermissions() {
        return this.repo.getAllPermissions();
    }

    public Permission getPermission(@NonNull Long permissionId) {
        return this.repo.getPermissionById(permissionId)
                        .orElseThrow(() -> new PermissionNotFoundException(permissionId));
    }

    /**
     * Add or update Permissions. Permission name is unique in the permission table.
     * 
     * @param permission array
     */
    public void savePermissions(@NonNull Permission[] permissions) {
        this.repo.savePermissions(Arrays.asList(permissions));
    }
}
