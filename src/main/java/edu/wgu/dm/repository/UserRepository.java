package edu.wgu.dm.repository;

import edu.wgu.dm.entity.projection.security.UserProjection;
import edu.wgu.dm.entity.security.UserEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    List<UserProjection> findAllProjectedBy();

    List<UserProjection> findByTasksTaskId(Long taskId);

    UserProjection findByUserIdAndRolesPermissionsPermission(String userId, String permission);

    int countByUserIdAndRolesPermissionsPermission(String userId, String permission);

    UserProjection findByUserId(String userId);

    @Modifying
    @Query("UPDATE UserEntity u SET u.lastLogin = NOW() WHERE u.userId = :userId")
    void updateLastLogin(@Param(value = "userId") String userId);

    @EntityGraph(value = "roles")
    Optional<UserEntity> findById(String userId);
}
