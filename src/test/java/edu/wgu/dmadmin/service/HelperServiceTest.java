package edu.wgu.dmadmin.service;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.util.ReflectionUtils.findField;
import static org.springframework.util.ReflectionUtils.makeAccessible;
import static org.springframework.util.ReflectionUtils.setField;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.publish.TaskByIdModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.StatusUtil;

@RunWith(MockitoJUnitRunner.class)
public class HelperServiceTest {
    @InjectMocks
    private HelperService helperService;

    private Submission submission;

    @Mock
    private PublishAcademicActivityService publishAcademicActivityService;

    @Mock
    private CassandraRepo cassandraRepo;

    private String helperString = ".helperMethod";

    private String titleValue = "Test.Title";
    private String returnValue = "Test.Return";
    private String passedValue = "Test.Passed";


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        Field academicActivityTitle = findField(helperService.getClass(), "academicActivityTitle");
        Field academicActivityReturnForRevision = findField(helperService.getClass(), "academicActivityReturnForRevision");
        Field academicActivityPassed = findField(helperService.getClass(), "academicActivityPassed");

        makeAccessible(academicActivityPassed);
        makeAccessible(academicActivityTitle);
        makeAccessible(academicActivityReturnForRevision);

        setField(academicActivityTitle, helperService, titleValue);
        setField(academicActivityReturnForRevision, helperService, returnValue);
        setField(academicActivityPassed, helperService, passedValue);

    }

    @Test
    public void testCompleteEvaluationFailed() throws Exception {
        submission = TestObjectFactory.getTestSubmission(StatusUtil.AUTHOR_WORK_NEEDS_REVISION);

        SubmissionByIdModel idModel = new SubmissionByIdModel(submission);
        EvaluationByIdModel evaluationByIdModel = TestObjectFactory.getEvaluationByIdModelFailing();
        TaskByIdModel taskByIdModel = new TaskByIdModel();
        UserByIdModel userByIdModel = new UserByIdModel();

        doNothing().when(publishAcademicActivityService).publishAcademicActivity(any(SubmissionModel.class), anyString());
        doNothing().when(cassandraRepo).saveSubmission(any(SubmissionByIdModel.class), any(EvaluationByIdModel.class), anyString(), anyString(), anyBoolean());

        when(cassandraRepo.getSubmissionById(any(UUID.class))).thenReturn(Optional.of(idModel));
        when(cassandraRepo.getEvaluationById(any(UUID.class))).thenReturn(Optional.of(evaluationByIdModel));
        when(cassandraRepo.getTaskById(any(UUID.class))).thenReturn(Optional.of(taskByIdModel));
        when(cassandraRepo.getUser(anyString())).thenReturn(Optional.of(userByIdModel));

        helperService.completeEvaluation(submission, "UserId");


        verify(publishAcademicActivityService, times(1)).publishAcademicActivity(any(SubmissionModel.class), eq(titleValue + returnValue + helperString));
    }

    @Test
    public void testCompleteEvaluationPassed() throws Exception {
        submission = TestObjectFactory.getTestSubmission(StatusUtil.EVALUATION_RELEASED);

        SubmissionByIdModel idModel = new SubmissionByIdModel(submission);
        EvaluationByIdModel evaluationByIdModel = TestObjectFactory.getEvaluationByIdModelPassing();
        TaskByIdModel taskByIdModel = new TaskByIdModel();
        UserByIdModel userByIdModel = new UserByIdModel();

        doNothing().when(publishAcademicActivityService).publishAcademicActivity(any(SubmissionModel.class), anyString());
        doNothing().when(cassandraRepo).saveSubmission(any(SubmissionByIdModel.class), any(EvaluationByIdModel.class), anyString(), anyString(), anyBoolean());

        when(cassandraRepo.getSubmissionById(any(UUID.class))).thenReturn(Optional.of(idModel));
        when(cassandraRepo.getEvaluationById(any(UUID.class))).thenReturn(Optional.of(evaluationByIdModel));
        when(cassandraRepo.getTaskById(any(UUID.class))).thenReturn(Optional.of(taskByIdModel));
        when(cassandraRepo.getUser(anyString())).thenReturn(Optional.of(userByIdModel));

        helperService.completeEvaluation(submission, "UserId");

        verify(publishAcademicActivityService, times(1)).publishAcademicActivity(any(SubmissionModel.class), eq(titleValue + passedValue + helperString));
    }

    @Test
    public void testCompleteEvaluationOther() throws Exception {
        submission = TestObjectFactory.getTestSubmission(StatusUtil.EVALUATION_BEGUN);

        SubmissionByIdModel idModel = new SubmissionByIdModel(submission);
        EvaluationByIdModel evaluationByIdModel = TestObjectFactory.getEvaluationByIdModelPassing();
        TaskByIdModel taskByIdModel = new TaskByIdModel();
        UserByIdModel userByIdModel = new UserByIdModel();

        doNothing().when(publishAcademicActivityService).publishAcademicActivity(any(SubmissionModel.class), anyString());
        doNothing().when(cassandraRepo).saveSubmission(any(SubmissionByIdModel.class), any(EvaluationByIdModel.class), anyString(), anyString(), anyBoolean());

        when(cassandraRepo.getSubmissionById(any(UUID.class))).thenReturn(Optional.of(idModel));
        when(cassandraRepo.getEvaluationById(any(UUID.class))).thenReturn(Optional.of(evaluationByIdModel));
        when(cassandraRepo.getTaskById(any(UUID.class))).thenReturn(Optional.of(taskByIdModel));
        when(cassandraRepo.getUser(anyString())).thenReturn(Optional.of(userByIdModel));

        helperService.completeEvaluation(submission, "UserId");

        verify(publishAcademicActivityService, times(0)).publishAcademicActivity(any(SubmissionModel.class), anyString());
    }
}