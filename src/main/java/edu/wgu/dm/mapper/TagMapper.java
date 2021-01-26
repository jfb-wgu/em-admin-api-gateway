package edu.wgu.dm.mapper;

import edu.wgu.dm.dto.response.Tag;
import edu.wgu.dm.entity.security.TagEntity;

public final class TagMapper {

    private TagMapper() {
    }

    public static TagEntity toTagEntity(Tag tag) {
        if (tag == null) {
            throw new NullPointerException("non null tag required ");
        }
        TagEntity tagEntity = new TagEntity();
        tagEntity.setTagId(tag.getTagId());
        tagEntity.setName(tag.getName());
        tagEntity.setDescription(tag.getDescription());
        tagEntity.setRoleId(tag.getRoleId());
        tagEntity.setActive(tag.getActive());
        return tagEntity;
    }

    public static Tag toTag(TagEntity tagEntity) {
        if (tagEntity == null) {
            throw new NullPointerException("non null tagEntity required");
        }
        Tag tag = new Tag();
        tag.setTagId(tagEntity.getTagId());
        tag.setRoleId(tagEntity.getRoleId());
        tag.setActive(tagEntity.getActive());
        tag.setName(tagEntity.getName());
        tag.setDescription(tagEntity.getDescription());
        tag.setCreatedBy(tagEntity.getCreatedBy());
        tag.setDateCreated(tagEntity.getDateCreated());
        tag.setLastModifiedBy(tagEntity.getLastModifiedBy());
        tag.setDateUpdated(tagEntity.getDateUpdated());
        return tag;
    }
}
