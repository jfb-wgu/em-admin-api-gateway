package edu.wgu.dm.admin.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import edu.wgu.dm.dto.security.Permission;
import edu.wgu.dm.entity.security.PermissionEntity;
import edu.wgu.dm.repo.security.PermissionRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionRepo {

    @Autowired
    PermissionRepository permissionRepo;
    
    @Transactional
    public void savePermissions(List<Permission> permissions) {
        this.permissionRepo.save(permissions.stream()
                                            .map(p -> new PermissionEntity(p))
                                            .collect(Collectors.toList()));
    }

    public Optional<Permission> getPermissionById(Long id) {
        return PermissionEntity.toPermission(this.permissionRepo.findOne(id));
    }

    public Optional<Permission> getPermissionByName(String permission) {
        return PermissionEntity.toPermission(this.permissionRepo.findByPermission(permission));
    }

    public List<Permission> getAllPermissions() {
        return PermissionEntity.toPermissions(this.permissionRepo.findAll());
    }
}
