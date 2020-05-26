package edu.wgu.dm.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.projection.security.RoleIdProjection;
import edu.wgu.dm.projection.security.RoleProjection;
import edu.wgu.dm.repo.security.RoleRepository;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RoleRepo {

    private final RoleRepository roleRepository;

    @Transactional
    public List<Role> saveRoles(List<Role> roles) {
        List<RoleEntity> entities = this.roleRepository.saveAll(roles.stream()
                                                                     .map(r -> new RoleEntity(r))
                                                                     .collect(Collectors.toList()));
        return RoleEntity.toRoles(entities);
    }

    @Transactional
    public void deleteRole(Long roleId) {
        this.roleRepository.deleteById(roleId);
    }

    public Optional<Role> getRoleById(Long roleId) {
        Optional<RoleEntity> role = this.roleRepository.findById(roleId);
        return RoleEntity.toRole(role);
    }

    public List<Long> getRolesByPermission(String permission) {
        List<RoleIdProjection> ids = this.roleRepository.findByPermissionsPermission(permission);
        return ids.stream()
                  .map(id -> id.getRoleId())
                  .collect(Collectors.toList());
    }

    public List<Role> getAllRoles() {
        return RoleProjection.toRoles(this.roleRepository.findAllProjectedBy());
    }
}
