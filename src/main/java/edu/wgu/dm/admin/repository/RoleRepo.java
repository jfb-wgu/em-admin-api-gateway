package edu.wgu.dm.admin.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.projection.security.RoleIdProjection;
import edu.wgu.dm.repo.security.RoleRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoleRepo {

    @Autowired
    RoleRepository roleRepo;

    @Transactional
    public List<Role> saveRoles(List<Role> roles) {
        List<RoleEntity> entities = this.roleRepo.save(roles.stream()
                                                            .map(r -> new RoleEntity(r))
                                                            .collect(Collectors.toList()));
        return RoleEntity.toRoles(entities);
    }

    @Transactional
    public void deleteRole(Long roleId) {
        this.roleRepo.delete(roleId);
    }

    public Optional<Role> getRoleById(Long roleId) {
        return RoleEntity.toRole(this.roleRepo.findOne(roleId));
    }

    public List<Long> getRolesByPermission(String permission) {
        List<RoleIdProjection> ids = this.roleRepo.findByPermissionsPermission(permission);
        return ids.stream()
                  .map(id -> id.getRoleId())
                  .collect(Collectors.toList());
    }

    public List<Role> getAllRoles() {
        return RoleEntity.toRoles(this.roleRepo.findAll());
    }
}
