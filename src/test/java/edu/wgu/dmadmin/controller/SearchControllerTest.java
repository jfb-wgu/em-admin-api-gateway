package edu.wgu.dmadmin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.audit.StatusLogEntry;
import edu.wgu.dmadmin.domain.audit.StatusLogResponse;
import edu.wgu.dmadmin.domain.search.SearchCriteria;
import edu.wgu.dmadmin.domain.search.SearchResponse;
import edu.wgu.dmadmin.domain.submission.DashboardSubmission;
import edu.wgu.dmadmin.model.audit.StatusLogModel;
import edu.wgu.dmadmin.service.SearchService;
import edu.wgu.dmadmin.util.DateUtil;
import edu.wgu.dmadmin.util.StatusUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {
    @InjectMocks
    private SearchController searchcontroller;

    @Mock
    private SearchService searchService;

    private MockMvc mockMvc;
    private ObjectMapper mapper = new ObjectMapper();
    private UUID assessmentId = UUID.randomUUID();

    private SearchCriteria searchCriteria;
    private List<DashboardSubmission> searchResponse;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(this.searchcontroller).build();
        this.searchCriteria = TestObjectFactory.getSearchCriteria();
    }

    @Test
    public void testGetSubmissionsByCriteria() throws Exception {
        String url = "/v1/search/submissions";
        SearchResponse expectedOutput = new SearchResponse(this.searchCriteria, this.searchResponse);

        when(this.searchService.search(this.searchCriteria)).thenReturn(this.searchResponse);

        MvcResult result = this.mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(this.searchCriteria)))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(expectedOutput), result.getResponse().getContentAsString());

        ArgumentCaptor<SearchCriteria> arg1 = ArgumentCaptor.forClass(SearchCriteria.class);

        verify(this.searchService).search(arg1.capture());
        assertEquals(arg1.getValue(), this.searchCriteria);

    }

    @Test
    public void testGetLogsForAssessment() throws Exception {
        String url = "/v1/search/logs/assessment/" + this.assessmentId;
        List<StatusLogEntry> statusLogEntries = new ArrayList<>();

        StatusLogModel statusLogModel = new StatusLogModel();
        statusLogModel.setActivityDate(DateUtil.getZonedNow());
        statusLogModel.setAssessmentId(this.assessmentId);
        statusLogModel.setCourseCode("C740");
        statusLogModel.setLogId(UUID.randomUUID());
        statusLogModel.setNewStatus(StatusUtil.COMPLETED);
        statusLogModel.setOldStatus(StatusUtil.WORKING);
        statusLogModel.setStudentId("StudentId");
        statusLogModel.setSubmissionId(UUID.randomUUID());
        statusLogModel.setTaskId(UUID.randomUUID());
        statusLogModel.setUserId("UserId");

        StatusLogEntry statusLogEntry = new StatusLogEntry(statusLogModel);

        statusLogEntries.add(statusLogEntry);

        StatusLogResponse expectedOutput = new StatusLogResponse(statusLogEntries);

        when(this.searchService.getStatusLogByAssessment(this.assessmentId)).thenReturn(statusLogEntries);

        MvcResult result = this.mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(this.mapper.writeValueAsString(expectedOutput), result.getResponse().getContentAsString());

        ArgumentCaptor<UUID> arg1 = ArgumentCaptor.forClass(UUID.class);

        verify(this.searchService).getStatusLogByAssessment(arg1.capture());
        assertEquals(arg1.getValue(), this.assessmentId);
    }

}