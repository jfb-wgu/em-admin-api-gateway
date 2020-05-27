package edu.wgu.dmadmin.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dm.dto.security.Role;
import edu.wgu.dm.dto.security.RoleInfo;
import edu.wgu.dm.tag.Tag;
import edu.wgu.dm.tag.TagController;
import edu.wgu.dm.tag.TagService;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@RunWith(MockitoJUnitRunner.class)
public class TagControllerTest {

    @InjectMocks
    TagController tagController;

    @Mock
    TagService tagService;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tagController)
                                 .build();
    }

    @Test
    public void getRoles() throws Exception {
        // arrange
        RoleInfo role = new RoleInfo();
        role.setRoleId(1l);
        role.setRole("Originality");

        when(tagService.getRolesThatCanBeAssignedToTag()).thenReturn(List.of(role));

        // act
        MvcResult mvcResult = mockMvc.perform(get("/v1/admin/tags/roles"))
                                     .andExpect(status().isOk())
                                     .andDo(print())
                                     .andReturn();

        // assert
        assertThat(mvcResult.getResponse()
                            .getContentAsString()).contains(objectMapper.writeValueAsString(role));
    }

    @Test
    public void upsertTag() throws Exception {

        // arrange
        Tag tag = new Tag();
        tag.setRoleId(1l);
        tag.setTagId(1l);
        tag.setActive(true);
        tag.setName("name");
        tag.setDescription("desc");

        when(tagService.upsertTag(any(Tag.class))).thenReturn(tag);

        // act
        MvcResult mvcResult = mockMvc.perform(post("/v1/admin/tags").content(objectMapper.writeValueAsString(tag))
                                                                    .contentType(MediaType.APPLICATION_JSON))
                                     .andExpect(status().isOk())
                                     .andDo(print())
                                     .andReturn();

        // assert
        assertThat(mvcResult.getResponse()
                            .getContentAsString()).contains(objectMapper.writeValueAsString(tag));
    }

    @Test
    public void getTag() throws Exception {

        // arrange
        Tag tag = new Tag();
        tag.setRoleId(1l);
        tag.setTagId(1l);
        tag.setActive(true);
        tag.setName("name");
        tag.setDescription("desc");

        when(tagService.getTag(anyLong())).thenReturn(tag);

        // act
        MvcResult mvcResult = mockMvc.perform(get("/v1/admin/tags/1"))
                                     .andExpect(status().isOk())
                                     .andDo(print())
                                     .andReturn();

        // assert
        assertThat(mvcResult.getResponse()
                            .getContentAsString()).contains(objectMapper.writeValueAsString(tag));
    }

    @Test
    public void getTagList() throws Exception {

        // arrange
        Tag tag = new Tag();
        tag.setRoleId(1l);
        tag.setTagId(1l);
        tag.setActive(true);
        tag.setName("name");
        tag.setDescription("desc");

        when(tagService.getTags()).thenReturn(List.of(tag));

        // act
        MvcResult mvcResult = mockMvc.perform(get("/v1/admin/tags"))
                                     .andExpect(status().isOk())
                                     .andDo(print())
                                     .andReturn();

        // assert
        assertThat(mvcResult.getResponse()
                            .getContentAsString()).contains(objectMapper.writeValueAsString(tag));
    }

    @Test
    public void deleteTag() throws Exception {

        // arrange
        doNothing().when(tagService)
                   .deleteTag(anyLong());

        // act
        MvcResult mvcResult = mockMvc.perform(delete("/v1/admin/tags/1"))
                                     .andExpect(status().is(204))
                                     .andDo(print())
                                     .andReturn();

    }
}
