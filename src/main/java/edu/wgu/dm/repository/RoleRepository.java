package edu.wgu.dm.repository;

import edu.wgu.dm.entity.security.RoleEntity;
import edu.wgu.dm.projection.security.RoleIdProjection;
import edu.wgu.dm.projection.security.RoleProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    List<RoleIdProjection> findByPermissionsPermission(String permission);

    List<RoleProjection> findAllProjectedBy();
}
