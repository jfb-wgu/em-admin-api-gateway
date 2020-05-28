package edu.wgu.dm.repo.security;

import edu.wgu.dm.entity.security.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

    PermissionEntity findByPermission(String permission);
}
