package edu.wgu.dm.mapper;

import edu.wgu.dm.dto.response.Tag;
import edu.wgu.dm.entity.security.TagEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Value;

@Value
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TagMapper {

    public static TagEntity toTagEntity(@NonNull Tag tag) {

        TagEntity tagEntity = new TagEntity();
        tagEntity.setTagId(tag.getTagId());
        tagEntity.setName(tag.getName());
        tagEntity.setDescription(tag.getDescription());
        tagEntity.setRoleId(tag.getRoleId());
        tagEntity.setActive(tag.getActive());
        return tagEntity;
    }

    public static  Tag toTag(@NonNull TagEntity tagEntity) {

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
