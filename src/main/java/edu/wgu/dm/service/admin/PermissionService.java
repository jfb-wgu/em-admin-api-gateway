package edu.wgu.dm.service.admin;

import java.util.List;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.wgu.dm.common.exception.PermissionNotFoundException;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.repository.admin.AdminRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PermissionService {
    
    @Autowired
    private AdminRepository dmRepo;
    
   
	public List<Permission> getPermissions() {
		return dmRepo.getAllPermissions();
	}

	public Permission getPermission(Long permissionId) {
		return dmRepo.getPermissionById(permissionId).orElseThrow(()-> new PermissionNotFoundException(permissionId));
	}

	/**
	 * Add or update Permissions. Permission name is unique
	 * in the permission table.
	 * 
	 * @param permission
	 *            array
	 */
    public void savePermissions(Permission[] permissions) {
        if (permissions == null || permissions.length == 0) {
            log.error("Permissions array can not be null or empty");
            return;
        }
        for (Permission permissionDto : permissions) {
            if (permissionDto == null)
                continue;
            Optional<Permission> permission = Optional.empty();
            if (permissionDto.getPermissionId() != null && permissionDto.getPermissionId() != 0) {
                permission = dmRepo.getPermissionById(permissionDto.getPermissionId());
            }
            // if we find existing permission by id, then save the user-supplied permission DTO
            if (permission.isPresent()) {
                Permission existingPermission = permission.get();
                existingPermission = captureValidUpdates(permissionDto, existingPermission);
                dmRepo.savePermission(existingPermission);
                continue;
            }

            if (StringUtils.isNotBlank(permissionDto.getPermission())) {
                permission = dmRepo.getPermissionByPermission(permissionDto.getPermission());
            }
            // If we can not find existing permission, create new one and return
            if (permission.isPresent()) {
                Permission existingPermission = permission.get();
                existingPermission = captureValidUpdates(permissionDto, existingPermission);
                dmRepo.savePermission(existingPermission);
                continue;
            }

            dmRepo.savePermission(permissionDto);

        }
    }

    /**
     * Copy non null and non blank values from the user supplied permission dto to the existing dto
     * @param userSuppliedPermissionDto
     * @param existingPermissionDto
     * @return
     */
    private Permission captureValidUpdates(@NonNull Permission userSuppliedPermissionDto,
            @NonNull Permission existingPermissionDto) {
        if (userSuppliedPermissionDto.getDateUpdated() != null) {
            existingPermissionDto.setDateUpdated(userSuppliedPermissionDto.getDateUpdated());
        }

        if (StringUtils.isNotBlank(userSuppliedPermissionDto.getLanding())) {
            existingPermissionDto.setLanding(userSuppliedPermissionDto.getLanding());
        }

        if (StringUtils.isNotBlank(userSuppliedPermissionDto.getPermission())) {
            existingPermissionDto.setPermission(userSuppliedPermissionDto.getPermission());
        }

        if (StringUtils.isNotBlank(userSuppliedPermissionDto.getPermissionDescription())) {
            existingPermissionDto
                    .setPermissionDescription(userSuppliedPermissionDto.getPermissionDescription());
        }

        if (StringUtils.isNotBlank(userSuppliedPermissionDto.getPermissionType())) {
            existingPermissionDto.setPermissionType(userSuppliedPermissionDto.getPermissionType());
        }
        return existingPermissionDto;
    }
}
