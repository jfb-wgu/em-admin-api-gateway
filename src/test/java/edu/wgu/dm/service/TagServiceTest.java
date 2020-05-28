package edu.wgu.dm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import edu.wgu.dm.repository.RoleRepo;
import edu.wgu.dm.tag.Tag;
import edu.wgu.dm.tag.TagEntity;
import edu.wgu.dm.tag.TagMapper;
import edu.wgu.dm.tag.TagRepository;
import edu.wgu.dm.tag.TagService;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TagServiceTest {

    @InjectMocks
    TagService tagService;

    @Mock
    private  TagRepository tagRepository;

    @Mock
    private  RoleRepo roleRepo;

    @Test(expected = IllegalArgumentException.class)
    public void upsertTag_fail_NoActiveValueSet(){

        // arrange
        Tag tag = new Tag();
        tag.setRoleId(1l);
        tag.setTagId(1l);
        tag.setActive(null);
        tag.setName("name");
        tag.setDescription("desc");
        // act
        tagService.upsertTag(tag);
    }

    @Test(expected = IllegalArgumentException.class)
    public void upsertTag_fail_NoTagNameSet(){

        // arrange
        Tag tag = new Tag();
        tag.setRoleId(1l);
        tag.setTagId(1l);
        tag.setActive(true);
        tag.setDescription("desc");

        // act
        tagService.upsertTag(tag);
    }

    @Test
    public void upsertTag(){

        // arrange
        Tag tag = new Tag();
        tag.setRoleId(1l);
        tag.setTagId(1L);
        tag.setName("name");
        tag.setActive(true);
        tag.setDescription("desc");

        TagEntity tagEntity = TagMapper.toTagEntity(tag);
        when(tagRepository.save(any(TagEntity.class))).thenReturn(tagEntity);
        when(tagRepository.findBytagId(tag.getTagId())).thenReturn(Optional.of(tagEntity));

        // act
        Tag tagResult = tagService.upsertTag(tag);

        // assert
        assertThat(tagResult).isNotNull();
        assertThat(tagResult.getName()).isEqualTo(tag.getName());
        assertThat(tagResult.getActive()).isEqualTo(tag.getActive());
        assertThat(tagResult.getDescription()).isEqualTo(tag.getDescription());
    }
}
