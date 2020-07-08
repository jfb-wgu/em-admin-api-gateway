package edu.wgu.dm.repository;

import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.RoleInfo;
import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.entity.projection.security.RoleIdNameProjection;
import edu.wgu.dm.entity.projection.security.RoleIdProjection;
import edu.wgu.dm.entity.projection.security.RoleProjection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class RoleRepo {

    private final RoleRepository roleRepository;

    @Transactional
    public List<Role> saveRoles(List<Role> roles) {
        List<RoleEntity> entities = this.roleRepository.saveAll(roles.stream()
                                                                     .map(RoleEntity::new)
                                                                     .collect(Collectors.toList()));
        return RoleEntity.toRoles(entities);
    }

    @Transactional
    public void deleteRole(Long roleId) {
        this.roleRepository.deleteById(roleId);
    }

    public List<RoleInfo> getRoles(List<String> roleNames) {
        return RoleIdNameProjection.toRoles(this.roleRepository.findByRoleInOrderByRole(roleNames));
    }

    public Optional<Role> getRoleById(Long roleId) {
        Optional<RoleEntity> role = this.roleRepository.findById(roleId);
        return RoleEntity.toRole(role);
    }

    public List<Long> getRolesByPermission(String permission) {
        List<RoleIdProjection> ids = this.roleRepository.findByPermissionsPermission(permission);
        return ids.stream()
                  .map(RoleIdProjection::getRoleId)
                  .collect(Collectors.toList());
    }

    public List<Role> getAllRoles() {
        return RoleProjection.toRoles(this.roleRepository.findAllProjectedBy());
    }
}
