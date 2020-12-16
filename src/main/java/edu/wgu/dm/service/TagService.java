package edu.wgu.dm.service;

import edu.wgu.dm.dto.response.Tag;
import edu.wgu.dm.dto.security.RoleInfo;
import edu.wgu.dm.entity.projection.TagIdNameProjection;
import edu.wgu.dm.entity.security.TagEntity;
import edu.wgu.dm.exception.InvalidTagException;
import edu.wgu.dm.exception.TagNotFoundException;
import edu.wgu.dm.repository.RoleRepo;
import edu.wgu.dm.repository.TagRepository;
import edu.wgu.dm.mapper.TagMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * The type Tag service.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {

    @Value("#{'${wgu.ema.tag.roles:Originality,Lead,Professional Communications}'.split(',')}")
    private List<String> assignableRoles;

    private final TagRepository tagRepository;
    private final RoleRepo roleRepo;

    /**
     * Upsert tag tag.
     *
     * @param tag the tag
     * @return the tag
     */
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

    /**
     * Gets tag.
     *
     * @param tagId the tag id
     * @return the tag
     */
    public Tag getTag(Long tagId) {
        TagEntity tagEntity = tagRepository.findByTagId(tagId)
                                           .orElseThrow(() -> new TagNotFoundException("Tag not found by id:"+tagId));
        return TagMapper.toTag(tagEntity);
    }

    /**
     * Gets tags.
     *
     * @return the tags
     */
    public List<Tag> getTags() {
        return tagRepository.findAll()
                            .stream()
                            .map(TagMapper::toTag)
                            .collect(Collectors.toList());
    }

    public Map<Long, String> getAllowedTagsForUser(String emaUserId) {

        if (StringUtils.isEmpty(emaUserId)) {
            return Collections.emptyMap();
        }
        List<TagIdNameProjection> tagsAllowedForUser = tagRepository.getTagsAllowedForUser(emaUserId);
        if (CollectionUtils.isEmpty(tagsAllowedForUser)) {
            return Collections.emptyMap();
        }
        return tagsAllowedForUser.stream()
                .collect(
                        Collectors.toMap(TagIdNameProjection::getTagId, TagIdNameProjection::getTagName));
    }

    /**
     * Delete tag.
     *
     * @param tagId the tag id
     */
    public void deleteTag(Long tagId) {
        tagRepository.deleteById(tagId);
    }

    /**
     * As of May 2020, we will just smart code roles that can be assigned to a tag.
     *
     * @return roles that can be assigned to tag
     */
    public List<RoleInfo> getRolesThatCanBeAssignedToTag() {
        return this.roleRepo.getRoles(assignableRoles);
    }
}
