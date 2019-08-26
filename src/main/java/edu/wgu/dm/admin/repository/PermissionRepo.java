package edu.wgu.dm.admin.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.entity.security.PermissionEntity;
import edu.wgu.dm.repo.security.PermissionRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PermissionRepo {

    private final PermissionRepository permissionRepository;

    @Transactional
    public void savePermissions(List<Permission> permissions) {
        this.permissionRepository.saveAll(permissions.stream()
                                                     .map(PermissionEntity::new)
                                                     .collect(Collectors.toList()));
    }

    public Optional<Permission> getPermissionById(Long id) {
        Optional<PermissionEntity> perm = this.permissionRepository.findById(id);
        return PermissionEntity.toPermission(perm);
    }

    public Optional<Permission> getPermissionByName(String permission) {
        return PermissionEntity.toPermission(this.permissionRepository.findByPermission(permission));
    }

    public List<Permission> getAllPermissions() {
        return PermissionEntity.toPermissions(this.permissionRepository.findAll());
    }
}
