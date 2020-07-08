package edu.wgu.dm.repository;

import edu.wgu.dm.entity.security.TagEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    Optional<TagEntity> findBytagId(Long tagId);

}
