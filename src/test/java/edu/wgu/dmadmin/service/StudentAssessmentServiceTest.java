package edu.wgu.dmadmin.service;

import edu.wgu.dmadmin.domain.assessment.Course;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.ScoreReportModel;
import edu.wgu.dmadmin.model.publish.RubricModel;
import edu.wgu.dmadmin.model.publish.TaskByCourseModel;
import edu.wgu.dmadmin.model.submission.AttachmentModel;
import edu.wgu.dmadmin.model.submission.SubmissionByStudentAndTaskModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.util.StatusUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by joshua.barnett on 5/25/17.
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("boxing")
public class StudentAssessmentServiceTest {
    @InjectMocks
    StudentAssessmentService service;

    @Mock
    CassandraRepo repo;

    @Mock
    FeedbackService feedbackService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

	@Test
    public void testGetAssessmentsForCourseByIdSubmissionNull() throws Exception {
        when(repo.getTasksByCourseId(anyLong())).thenAnswer(new Answer<List<TaskByCourseModel>>() {
            @Override
            public List<TaskByCourseModel> answer(InvocationOnMock invocationOnMock) throws Throwable {

                TaskByCourseModel task = new TaskByCourseModel();
                task.setTaskId(UUID.randomUUID());
                task.setDateCreated(new Date());
                task.setAssessmentDate("0117");
                task.setRubric(new RubricModel());

                List<TaskByCourseModel> tasks = new ArrayList<>();
                tasks.add(task);

                return tasks;
            }
        });

        when(repo.getLastSubmissionByStudentAndTaskId(anyString(), any(UUID.class))).thenReturn(null);

        Course course = service.getAssessmentsForCourse("106679", 1235465L);
        assertEquals(1, course.getAssessments().size());
        assertEquals(StatusUtil.NOT_STARTED, course.getAssessments().get(0).getTasks().get(0).getStudentStatus());
    }

    @Test
    public void testGetAssessmentsForCourseByIdSubmissionNotNull() throws Exception {
        when(repo.getTasksByCourseId(anyLong())).thenAnswer(new Answer<List<TaskByCourseModel>>() {
            @Override
            public List<TaskByCourseModel> answer(InvocationOnMock invocationOnMock) throws Throwable {

                TaskByCourseModel task = new TaskByCourseModel();
                task.setTaskId(UUID.randomUUID());
                task.setDateCreated(new Date());
                task.setAssessmentDate("0117");
                task.setRubric(new RubricModel());


                List<TaskByCourseModel> tasks = new ArrayList<>();
                tasks.add(task);

                return tasks;
            }
        });

        when(repo.getLastSubmissionByStudentAndTaskId(anyString(), any(UUID.class))).thenAnswer(new Answer<SubmissionByStudentAndTaskModel>() {
            @Override
            public SubmissionByStudentAndTaskModel answer(InvocationOnMock invocationOnMock) throws Throwable {

                SubmissionByStudentAndTaskModel submission = new SubmissionByStudentAndTaskModel();
                submission.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);
                submission.setAttempt(1);

                return submission;
            }
        });

        Course course = service.getAssessmentsForCourse("106679", 1235465L);
        assertEquals(1, course.getAssessments().size());
        assertEquals(StatusUtil.AUTHOR_SUBMISSION_STARTED, course.getAssessments().get(0).getTasks().get(0).getStatus());
    }

    @Test
    public void testGetAssessmentsForCourseByIdTasksWithSupportingDocuments() throws Exception {
        UUID taskId = UUID.randomUUID();
        when(repo.getTasksByCourseId(anyLong())).thenAnswer(new Answer<List<TaskByCourseModel>>() {
            @Override
            public List<TaskByCourseModel> answer(InvocationOnMock invocationOnMock) throws Throwable {

                AttachmentModel attachment = new AttachmentModel();
                attachment.setTitle("Attach 1");
                attachment.setIsUrl(Boolean.FALSE);
                attachment.setIsTaskDocument(Boolean.TRUE);

                Map<String, AttachmentModel> documents = new HashMap<>();
                documents.put("Attachment 1", attachment);

                TaskByCourseModel task = new TaskByCourseModel();
                task.setTaskId(taskId);
                task.setDateCreated(new Date());
                task.setAssessmentDate("0117");
                task.setRubric(new RubricModel());
                task.setSupportingDocuments(documents);
                task.setCourseCode("C740");
                task.setAssessmentCode("HIT2");

                List<TaskByCourseModel> tasks = new ArrayList<>();
                tasks.add(task);

                return tasks;
            }
        });

        when(repo.getLastSubmissionByStudentAndTaskId(anyString(), any(UUID.class))).thenAnswer(new Answer<SubmissionByStudentAndTaskModel>() {
            @Override
            public SubmissionByStudentAndTaskModel answer(InvocationOnMock invocationOnMock) throws Throwable {

                SubmissionByStudentAndTaskModel submission = new SubmissionByStudentAndTaskModel();
                submission.setStatus(StatusUtil.AUTHOR_WORK_SUBMITTED);
                submission.setAttempt(1);

                return submission;
            }
        });

        Course course = service.getAssessmentsForCourse("106679", 1235465L);
        assertEquals(1, course.getAssessments().size());
        assertEquals(1, course.getAssessments().get(0).getTasks().get(0).getSupportingDocuments().size());
        assertEquals("dmsubmission/v1/assessment/supportingdocument/course/C740/assessment/HIT2/task/" + taskId + "/Attach 1", course.getAssessments().get(0).getTasks().get(0).getSupportingDocuments().get("Attachment 1").getDisplayUrl());
    }

    @Test
    public void testGetAssessmentsForCourseByIdTasksWithSupportingDocumentURL() throws Exception {

        UUID taskId = UUID.randomUUID();

        when(repo.getTasksByCourseId(anyLong())).thenAnswer(new Answer<List<TaskByCourseModel>>() {
            @Override
            public List<TaskByCourseModel> answer(InvocationOnMock invocationOnMock) throws Throwable {

                AttachmentModel attachment = new AttachmentModel();
                attachment.setTitle("Attach 1");
                attachment.setIsUrl(true);
                attachment.setIsTaskDocument(true);
                attachment.setUrl("www.starwars.com");

                Map<String, AttachmentModel> documents = new HashMap<>();
                documents.put("Attachment 1", attachment);

                TaskByCourseModel task = new TaskByCourseModel();
                task.setTaskId(taskId);
                task.setDateCreated(new Date());
                task.setAssessmentDate("0117");
                task.setRubric(new RubricModel());
                task.setSupportingDocuments(documents);

                List<TaskByCourseModel> tasks = new ArrayList<>();
                tasks.add(task);

                return tasks;
            }
        });

        when(repo.getLastSubmissionByStudentAndTaskId(anyString(), any(UUID.class))).thenAnswer(new Answer<SubmissionByStudentAndTaskModel>() {
            @Override
            public SubmissionByStudentAndTaskModel answer(InvocationOnMock invocationOnMock) throws Throwable {

                SubmissionByStudentAndTaskModel submission = new SubmissionByStudentAndTaskModel();
                submission.setStatus(StatusUtil.AUTHOR_SUBMISSION_STARTED);
                submission.setAttempt(1);

                return submission;
            }
        });

        Course course = service.getAssessmentsForCourse("106679", 1235465L);
        assertEquals(1, course.getAssessments().size());
        assertEquals(1, course.getAssessments().get(0).getTasks().get(0).getSupportingDocuments().size());
        assertEquals("www.starwars.com", course.getAssessments().get(0).getTasks().get(0).getSupportingDocuments().get("Attachment 1").getUrl());
    }

    @Test
    public void testGetAssessmentsForCourseByIdSubmissionAttachments() throws Exception {

        UUID taskId = UUID.randomUUID();
        UUID submissionId = UUID.randomUUID();

        when(repo.getTasksByCourseId(anyLong())).thenAnswer(new Answer<List<TaskByCourseModel>>() {
            @Override
            public List<TaskByCourseModel> answer(InvocationOnMock invocationOnMock) throws Throwable {

                TaskByCourseModel task = new TaskByCourseModel();
                task.setTaskId(taskId);
                task.setDateCreated(new Date());
                task.setAssessmentDate("0117");
                task.setRubric(new RubricModel());

                List<TaskByCourseModel> tasks = new ArrayList<>();
                tasks.add(task);

                return tasks;
            }
        });

        when(repo.getLastSubmissionByStudentAndTaskId(anyString(), any(UUID.class))).thenAnswer(new Answer<SubmissionByStudentAndTaskModel>() {
            @Override
            public SubmissionByStudentAndTaskModel answer(InvocationOnMock invocationOnMock) throws Throwable {

                AttachmentModel attachment = new AttachmentModel();
                attachment.setTitle("Attach 1");
                attachment.setIsUrl(false);
                attachment.setIsTaskDocument(false);

                Map<String, AttachmentModel> attachments = new HashMap<>();
                attachments.put("Attachment 1", attachment);

                SubmissionByStudentAndTaskModel submission = new SubmissionByStudentAndTaskModel();
                submission.setStatus(StatusUtil.AUTHOR_WORK_SUBMITTED);
                submission.setAttempt(1);
                submission.setAttachments(attachments);
                submission.setSubmissionId(submissionId);
                submission.setStudentId("106679");

                return submission;
            }
        });

        Course course = service.getAssessmentsForCourse("106679", 1235465L);
        assertEquals(1, course.getAssessments().size());
        assertEquals(1, course.getAssessments().get(0).getTasks().get(0).getSubmittedFiles().size());
        assertEquals("dmsubmission/v1/student/106679/submission/" + submissionId + "/attachment/Attach 1", course.getAssessments().get(0).getTasks().get(0).getSubmittedFiles().get(0).getDisplayUrl());
    }

    @Test
    public void testGetAssessmentsForCourseByIdSubmissionStatusComplete() throws Exception {

        when(repo.getTasksByCourseId(anyLong())).thenAnswer(new Answer<List<TaskByCourseModel>>() {
            @Override
            public List<TaskByCourseModel> answer(InvocationOnMock invocationOnMock) throws Throwable {

                TaskByCourseModel task = new TaskByCourseModel();
                task.setTaskId(UUID.randomUUID());
                task.setDateCreated(new Date());
                task.setAssessmentDate("0117");
                task.setRubric(new RubricModel());

                List<TaskByCourseModel> tasks = new ArrayList<>();
                tasks.add(task);

                return tasks;
            }
        });

        when(repo.getLastSubmissionByStudentAndTaskId(anyString(), any(UUID.class))).thenAnswer(new Answer<SubmissionByStudentAndTaskModel>() {
            @Override
            public SubmissionByStudentAndTaskModel answer(InvocationOnMock invocationOnMock) throws Throwable {

                AttachmentModel attachment = new AttachmentModel();
                attachment.setTitle("Attach 1");
                attachment.setIsUrl(false);
                attachment.setIsTaskDocument(false);

                Map<String, AttachmentModel> attachments = new HashMap<>();
                attachments.put("Attachment 1", attachment);

                SubmissionByStudentAndTaskModel submission = new SubmissionByStudentAndTaskModel();
                submission.setStatus(StatusUtil.EVALUATION_RELEASED);
                submission.setAttempt(1);
                submission.setAttachments(attachments);

                return submission;
            }
        });

        when(repo.getEvaluationById(any(UUID.class))).thenAnswer(new Answer<Optional<EvaluationByIdModel>>() {
            @Override
            public Optional<EvaluationByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                ScoreReportModel scoreReport = new ScoreReportModel();
                scoreReport.setPassed(true);

                EvaluationByIdModel evaluation = new EvaluationByIdModel();
                evaluation.setScoreReport(scoreReport);
                return Optional.of(evaluation);
            }
        });

        Course course = service.getAssessmentsForCourse("106679", 1235465L);
        assertTrue(course.getAssessments().get(0).getTasks().get(0).getScoreReport().isPassed());
        assertTrue(course.isRequestFeedback());
    }

    @Test
    public void testGetAssessmentsForCourseByIdFeedbackServiceFalse() throws Exception {

        when(repo.getTasksByCourseId(anyLong())).thenAnswer(new Answer<List<TaskByCourseModel>>() {
            @Override
            public List<TaskByCourseModel> answer(InvocationOnMock invocationOnMock) throws Throwable {

                TaskByCourseModel task = new TaskByCourseModel();
                task.setTaskId(UUID.randomUUID());
                task.setDateCreated(new Date());
                task.setAssessmentDate("0117");
                task.setRubric(new RubricModel());

                List<TaskByCourseModel> tasks = new ArrayList<>();
                tasks.add(task);

                return tasks;
            }
        });

        when(repo.getLastSubmissionByStudentAndTaskId(anyString(), any(UUID.class))).thenAnswer(new Answer<SubmissionByStudentAndTaskModel>() {
            @Override
            public SubmissionByStudentAndTaskModel answer(InvocationOnMock invocationOnMock) throws Throwable {

                AttachmentModel attachment = new AttachmentModel();
                attachment.setTitle("Attach 1");
                attachment.setIsUrl(false);
                attachment.setIsTaskDocument(false);

                Map<String, AttachmentModel> attachments = new HashMap<>();
                attachments.put("Attachment 1", attachment);

                SubmissionByStudentAndTaskModel submission = new SubmissionByStudentAndTaskModel();
                submission.setStatus(StatusUtil.EVALUATION_RELEASED);
                submission.setAttempt(1);
                submission.setAttachments(attachments);

                return submission;
            }
        });

        when(repo.getEvaluationById(any(UUID.class))).thenAnswer(new Answer<Optional<EvaluationByIdModel>>() {
            @Override
            public Optional<EvaluationByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                ScoreReportModel scoreReport = new ScoreReportModel();
                scoreReport.setPassed(true);

                EvaluationByIdModel evaluation = new EvaluationByIdModel();
                evaluation.setScoreReport(scoreReport);
                return Optional.of(evaluation);
            }
        });

        when(feedbackService.hasStudentFeedback(anyString())).thenReturn(false);

        Course course = service.getAssessmentsForCourse("106679", 1235465L);
        assertTrue("Requested feedback should be true", course.isRequestFeedback());
    }

    @Test
    public void testGetAssessmentsForCourseByIdFeedbackServiceTrue() throws Exception {

        when(repo.getTasksByCourseId(anyLong())).thenAnswer(new Answer<List<TaskByCourseModel>>() {
            @Override
            public List<TaskByCourseModel> answer(InvocationOnMock invocationOnMock) throws Throwable {

                TaskByCourseModel task = new TaskByCourseModel();
                task.setTaskId(UUID.randomUUID());
                task.setDateCreated(new Date());
                task.setAssessmentDate("0117");
                task.setRubric(new RubricModel());

                List<TaskByCourseModel> tasks = new ArrayList<>();
                tasks.add(task);

                return tasks;
            }
        });

        when(repo.getLastSubmissionByStudentAndTaskId(anyString(), any(UUID.class))).thenAnswer(new Answer<SubmissionByStudentAndTaskModel>() {
            @Override
            public SubmissionByStudentAndTaskModel answer(InvocationOnMock invocationOnMock) throws Throwable {

                AttachmentModel attachment = new AttachmentModel();
                attachment.setTitle("Attach 1");
                attachment.setIsUrl(false);
                attachment.setIsTaskDocument(false);

                Map<String, AttachmentModel> attachments = new HashMap<>();
                attachments.put("Attachment 1", attachment);

                SubmissionByStudentAndTaskModel submission = new SubmissionByStudentAndTaskModel();
                submission.setStatus(StatusUtil.EVALUATION_RELEASED);
                submission.setAttempt(1);
                submission.setAttachments(attachments);

                return submission;
            }
        });

        when(repo.getEvaluationById(any(UUID.class))).thenAnswer(new Answer<Optional<EvaluationByIdModel>>() {
            @Override
            public Optional<EvaluationByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                ScoreReportModel scoreReport = new ScoreReportModel();
                scoreReport.setPassed(true);

                EvaluationByIdModel evaluation = new EvaluationByIdModel();
                evaluation.setScoreReport(scoreReport);
                return Optional.of(evaluation);
            }
        });

        when(feedbackService.hasStudentFeedback(anyString())).thenReturn(true);

        Course course = service.getAssessmentsForCourse("106679", 1235465L);
        assertFalse("Requested feedback should be false", course.isRequestFeedback());
    }

    @Test
    public void testGetScoreReport() throws Exception {
    }

}