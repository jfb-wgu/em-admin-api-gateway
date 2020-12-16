package edu.wgu.dm.repository;

import edu.wgu.dm.entity.projection.TagIdNameProjection;
import edu.wgu.dm.entity.security.TagEntity;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    Optional<TagEntity> findByTagId(Long tagId);

    @Query(nativeQuery = true, value = "SELECT tag_id as tagId, "
            + "       name as tagName "
            + "FROM user_roles ur "
            + "         JOIN tag t ON ur.role_id = t.role_id "
            + "    AND ur.user_id = :emaUserId"
            + "    AND t.active = TRUE")
    List<TagIdNameProjection> getTagsAllowedForUser(String emaUserId);
}
