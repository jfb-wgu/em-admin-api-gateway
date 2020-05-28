package edu.wgu.dm.tag;

import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.RoleInfo;
import edu.wgu.dm.exception.InvalidTagException;
import edu.wgu.dm.exception.TagNotFoundException;
import edu.wgu.dm.repository.RoleRepo;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {

    @Value("#{'${wgu.ema.tag.roles:Originality,Lead,Professional Communications}'.split(',')}")
    private List<String> assignableRoles;

    private final TagRepository tagRepository;
    private final RoleRepo roleRepo;

    public Tag upsertTag( Tag tag) {
        Assert.notNull(tag,"Tag can not be null");
        Assert.hasText(tag.getName(), "Tag name can not be empty or null");
        Assert.notNull(tag.getActive(), "Tag status can not be empty or null");
        TagEntity entity = TagMapper.toTagEntity(tag);
        try {
            entity = tagRepository.save(entity);
        } catch (DataIntegrityViolationException ex) {
            log.error("DataIntegrityViolationException:", ex);
            throw new InvalidTagException(tag);
        }
        return getTag(entity.getTagId());
    }

    public Tag getTag(Long tagId) {
        TagEntity tagEntity = tagRepository.findBytagId(tagId)
                                           .orElseThrow(() -> new TagNotFoundException("Tag not found by id:"+tagId));
        return TagMapper.toTag(tagEntity);
    }

    public List<Tag> getTags() {
        return tagRepository.findAll()
                            .stream()
                            .map(TagMapper::toTag)
                            .collect(Collectors.toList());
    }

    public void deleteTag(Long tagId) {
        tagRepository.deleteById(tagId);
    }

    /**
     * As of May 2020, we will just smart code roles that can be assigned to a tag.
     *
     * @return
     */
    public List<RoleInfo> getRolesThatCanBeAssignedToTag() {
        return this.roleRepo.getRoles(assignableRoles);
    }
}
