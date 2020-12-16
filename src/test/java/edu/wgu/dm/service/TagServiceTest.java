package edu.wgu.dm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import edu.wgu.dm.dto.response.Tag;
import edu.wgu.dm.entity.projection.TagIdNameProjection;
import edu.wgu.dm.entity.security.TagEntity;
import edu.wgu.dm.mapper.TagMapper;
import edu.wgu.dm.repository.RoleRepo;
import edu.wgu.dm.repository.TagRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private TagRepository tagRepository;

    @Mock
    private RoleRepo roleRepo;

    @Test(expected = IllegalArgumentException.class)
    public void upsertTag_fail_NoActiveValueSet() {

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
    public void upsertTag_fail_NoTagNameSet() {

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
    public void upsertTag() {

        // arrange
        Tag tag = new Tag();
        tag.setRoleId(1l);
        tag.setTagId(1L);
        tag.setName("name");
        tag.setActive(true);
        tag.setDescription("desc");

        TagEntity tagEntity = TagMapper.toTagEntity(tag);
        when(tagRepository.save(any(TagEntity.class))).thenReturn(tagEntity);
        when(tagRepository.findByTagId(tag.getTagId())).thenReturn(Optional.of(tagEntity));

        // act
        Tag tagResult = tagService.upsertTag(tag);

        // assert
        assertThat(tagResult).isNotNull();
        assertThat(tagResult.getName()).isEqualTo(tag.getName());
        assertThat(tagResult.getActive()).isEqualTo(tag.getActive());
        assertThat(tagResult.getDescription()).isEqualTo(tag.getDescription());
    }





    @Test
    public void getTag() {
        // arrange
        Long tagId = 10l;
        TagEntity tag = new TagEntity();
        tag.setTagId(tagId);
        tag.setRoleId(12l);
        when(tagRepository.findByTagId(tagId)).thenReturn(Optional.of(tag));
        // act
        Tag t = tagService.getTag(tagId);

        // assert
        assertThat(t).isNotNull();
    }



    @Test
    public void getAllowedTagsForUser() {
        //arrange
        String emaUserId = "ema";
        Map<Long, String> map = new HashMap<>();
        map.put(1l, "testTag");
        List<TagIdNameProjection> lst = new ArrayList<>();
        lst.add(new TagIdNameProjection() {

            @Override
            public String getTagName() {
                return "testTag";
            }

            @Override
            public Long getTagId() {
                return 1l;
            }
        });
        when(tagRepository.getTagsAllowedForUser(anyString())).thenReturn(lst);

        // act
        var result = tagService.getAllowedTagsForUser(emaUserId);

        //assert
        assertThat(result).hasSizeGreaterThan(0)
         .containsEntry(1l,"testTag");
    }

    @Test
    public void getAllowedTagsForUserNoTagsFound() {
        //arrange
        String emaUserId = "ema";
        Map<Long, String> map = new HashMap<>();
        map.put(1l, "testTag");
        List<TagIdNameProjection> lst = Collections.emptyList();
        when(tagRepository.getTagsAllowedForUser(anyString())).thenReturn(lst);

        // act
        var result = tagService.getAllowedTagsForUser(emaUserId);

        //assert
        assertThat(result).isEmpty();
    }

}
