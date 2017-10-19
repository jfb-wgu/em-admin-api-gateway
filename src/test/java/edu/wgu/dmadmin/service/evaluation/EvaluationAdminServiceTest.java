package edu.wgu.dmadmin.service.evaluation;

import edu.wgu.dmadmin.exception.EvaluationNotFoundException;
import edu.wgu.dmadmin.exception.EvaluationStatusException;
import edu.wgu.dmadmin.exception.SubmissionNotFoundException;
import edu.wgu.dmadmin.exception.SubmissionStatusException;
import edu.wgu.dmadmin.model.assessment.EvaluationByIdModel;
import edu.wgu.dmadmin.model.assessment.EvaluationBySubmissionModel;
import edu.wgu.dmadmin.model.security.UserByIdModel;
import edu.wgu.dmadmin.model.submission.SubmissionByIdModel;
import edu.wgu.dmadmin.repo.CassandraRepo;
import edu.wgu.dmadmin.service.EvaluationAdminService;
import edu.wgu.dmadmin.service.SubmissionUtilityService;
import edu.wgu.dmadmin.util.StatusUtil;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by joshua.barnett on 6/7/17.
 */
public class EvaluationAdminServiceTest {
    private EvaluationAdminService service;
    private CassandraRepo cassandraRepo;
    private SubmissionUtilityService submissionService;

    @Before
    public void setUp() throws Exception {
        service = new EvaluationAdminService();
        cassandraRepo = Mockito.mock(CassandraRepo.class);
        service.setCassandraRepo(cassandraRepo);
        submissionService = Mockito.mock(SubmissionUtilityService.class);
        service.setSubmissionUtility(submissionService);
    }

    @Test
    public void testAssignEvaluation() throws Exception {
    }

    @Test
    public void testCancelEvaluation() throws Exception {
    }

    @Test
    public void testReleaseEvaluation() throws Exception {
    }

    @Test
    public void testReviewEvaluation() throws Exception {
        when(cassandraRepo.getEvaluationById(Matchers.<UUID>any())).thenAnswer(new Answer<Optional<EvaluationByIdModel>>() {
            @Override
            public Optional<EvaluationByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                EvaluationByIdModel model = new EvaluationByIdModel();
                model.setStatus(StatusUtil.COMPLETED);
                return Optional.of(model);
            }
        });

        when(cassandraRepo.getEvaluation(anyString(), Matchers.<UUID>any())).thenAnswer(new Answer<Optional<EvaluationBySubmissionModel>>() {
            @Override
            public Optional<EvaluationBySubmissionModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
            	EvaluationBySubmissionModel model = new EvaluationBySubmissionModel();
                model.setEvaluationId(UUID.randomUUID());
                return Optional.of(model);

            }
        });

        when(cassandraRepo.getSubmissionById(Matchers.<UUID>any())).thenAnswer(new Answer<Optional<SubmissionByIdModel>>() {
            @Override
            public Optional<SubmissionByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                SubmissionByIdModel model = new SubmissionByIdModel();
                model.setEvaluationId(UUID.randomUUID());
                model.setSubmissionId(UUID.randomUUID());
                model.setEvaluatorId("JBARNETT");
                model.setStatus(StatusUtil.EVALUATION_RELEASED);
                return Optional.of(model);
            }
        });

        when(cassandraRepo.getUser(anyString())).thenAnswer(new Answer<Optional<UserByIdModel>>() {
            @Override
            public Optional<UserByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                UserByIdModel model = new UserByIdModel();
                return Optional.of(model);
            }
        });

        Mockito.doNothing().when(cassandraRepo).saveEvaluation(Matchers.<EvaluationByIdModel>any());

        service.reviewEvaluation("106679", UUID.randomUUID());
    }

    @Test(expected = SubmissionStatusException.class)
    public void testReviewEvaluationSubmissionStatusException() throws Exception {

        when(cassandraRepo.getSubmissionById(Matchers.<UUID>any())).thenAnswer(new Answer<Optional<SubmissionByIdModel>>() {
            @Override
            public Optional<SubmissionByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                SubmissionByIdModel model = new SubmissionByIdModel();
                model.setEvaluationId(UUID.randomUUID());
                model.setSubmissionId(UUID.randomUUID());
                model.setEvaluatorId("JBARNETT");
                model.setStatus(StatusUtil.EVALUATION_EDITED);
                return Optional.of(model);
            }
        });

        service.reviewEvaluation("106679", UUID.randomUUID());
    }

    @Test
    public void testReviewEvaluationStatus16() throws Exception {
        when(cassandraRepo.getEvaluationById(Matchers.<UUID>any())).thenAnswer(new Answer<Optional<EvaluationByIdModel>>() {
            @Override
            public Optional<EvaluationByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                EvaluationByIdModel model = new EvaluationByIdModel();
                model.setStatus(StatusUtil.COMPLETED);
                return Optional.of(model);
            }
        });

        when(cassandraRepo.getEvaluation(anyString(), Matchers.<UUID>any())).thenAnswer(new Answer<Optional<EvaluationBySubmissionModel>>() {
            @Override
            public Optional<EvaluationBySubmissionModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
            	EvaluationBySubmissionModel model = new EvaluationBySubmissionModel();
                model.setEvaluationId(UUID.randomUUID());
                return Optional.of(model);

            }
        });

        when(cassandraRepo.getSubmissionById(Matchers.<UUID>any())).thenAnswer(new Answer<Optional<SubmissionByIdModel>>() {
            @Override
            public Optional<SubmissionByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                SubmissionByIdModel model = new SubmissionByIdModel();
                model.setEvaluationId(UUID.randomUUID());
                model.setSubmissionId(UUID.randomUUID());
                model.setEvaluatorId("JBARNETT");
                model.setStatus(StatusUtil.AUTHOR_WORK_NEEDS_REVISION);
                return Optional.of(model);
            }
        });

        when(cassandraRepo.getUser(anyString())).thenAnswer(new Answer<Optional<UserByIdModel>>() {
            @Override
            public Optional<UserByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                UserByIdModel model = new UserByIdModel();
                return Optional.of(model);
            }
        });

        Mockito.doNothing().when(cassandraRepo).saveEvaluation(Matchers.<EvaluationByIdModel>any());

        service.reviewEvaluation("106679", UUID.randomUUID());
    }

    @Test
    public void testReviewEvaluationStatus64() throws Exception {
        when(cassandraRepo.getEvaluationById(Matchers.<UUID>any())).thenAnswer(new Answer<Optional<EvaluationByIdModel>>() {
            @Override
            public Optional<EvaluationByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                EvaluationByIdModel model = new EvaluationByIdModel();
                model.setStatus(StatusUtil.COMPLETED);
                return Optional.of(model);
            }
        });

        when(cassandraRepo.getEvaluation(anyString(), Matchers.<UUID>any())).thenAnswer(new Answer<Optional<EvaluationBySubmissionModel>>() {
            @Override
            public Optional<EvaluationBySubmissionModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
            	EvaluationBySubmissionModel model = new EvaluationBySubmissionModel();
                model.setEvaluationId(UUID.randomUUID());
                return Optional.of(model);

            }
        });

        when(cassandraRepo.getSubmissionById(Matchers.<UUID>any())).thenAnswer(new Answer<Optional<SubmissionByIdModel>>() {
            @Override
            public Optional<SubmissionByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                SubmissionByIdModel model = new SubmissionByIdModel();
                model.setEvaluationId(UUID.randomUUID());
                model.setSubmissionId(UUID.randomUUID());
                model.setEvaluatorId("JBARNETT");
                model.setStatus(StatusUtil.EVALUATION_RELEASED);
                return Optional.of(model);
            }
        });

        when(cassandraRepo.getUser(anyString())).thenAnswer(new Answer<Optional<UserByIdModel>>() {
            @Override
            public Optional<UserByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                UserByIdModel model = new UserByIdModel();
                return Optional.of(model);
            }
        });

        Mockito.doNothing().when(cassandraRepo).saveEvaluation(Matchers.<EvaluationByIdModel>any());

        service.reviewEvaluation("106679", UUID.randomUUID());
    }

    @Test(expected = EvaluationStatusException.class)
    public void testReviewEvaluationStatusNotSupported() throws Exception {
        when(cassandraRepo.getEvaluationById(Matchers.<UUID>any())).thenAnswer(new Answer<Optional<EvaluationByIdModel>>() {
            @Override
            public Optional<EvaluationByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                EvaluationByIdModel model = new EvaluationByIdModel();
                model.setStatus("Not Complete");
                return Optional.of(model);
            }
        });

        when(cassandraRepo.getEvaluation(anyString(), Matchers.<UUID>any())).thenAnswer(new Answer<Optional<EvaluationByIdModel>>() {
            @Override
            public Optional<EvaluationByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
            	EvaluationByIdModel model = new EvaluationByIdModel();
                model.setEvaluationId(UUID.randomUUID());
                return Optional.of(model);

            }
        });

        when(cassandraRepo.getSubmissionById(Matchers.<UUID>any())).thenAnswer(new Answer<Optional<SubmissionByIdModel>>() {
            @Override
            public Optional<SubmissionByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                SubmissionByIdModel model = new SubmissionByIdModel();
                model.setEvaluationId(UUID.randomUUID());
                model.setSubmissionId(UUID.randomUUID());
                model.setEvaluatorId("JBARNETT");
                model.setStatus(StatusUtil.AUTHOR_WORK_EVALUATED);
                return Optional.of(model);
            }
        });

        when(cassandraRepo.getUser(anyString())).thenAnswer(new Answer<Optional<UserByIdModel>>() {
            @Override
            public Optional<UserByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                UserByIdModel model = new UserByIdModel();
                return Optional.of(model);
            }
        });

        Mockito.doNothing().when(cassandraRepo).saveEvaluation(Matchers.<EvaluationByIdModel>any());

        service.reviewEvaluation("106679", UUID.randomUUID());
    }

    @Test (expected = EvaluationNotFoundException.class)
    public void testReviewEvaluationEvaluationNull() throws Exception {

        when(cassandraRepo.getEvaluationById(Matchers.<UUID>any())).thenReturn(Optional.empty());

        when(cassandraRepo.getSubmissionById(Matchers.<UUID>any())).thenAnswer(new Answer<Optional<SubmissionByIdModel>>() {
            @Override
            public Optional<SubmissionByIdModel> answer(InvocationOnMock invocationOnMock) throws Throwable {
                SubmissionByIdModel model = new SubmissionByIdModel();
                model.setEvaluationId(UUID.randomUUID());
                model.setSubmissionId(UUID.randomUUID());
                model.setEvaluatorId("JBARNETT");
                model.setStatus(StatusUtil.AUTHOR_WORK_NEEDS_REVISION);
                return Optional.of(model);
            }
        });

        service.reviewEvaluation("106679", UUID.randomUUID());
    }

    @Test(expected = SubmissionNotFoundException.class)
    public void testReviewEvaluationSubmissionNull() throws Exception {
        when(cassandraRepo.getSubmissionById(Matchers.<UUID>any())).thenReturn(Optional.empty());

        service.reviewEvaluation("106679", UUID.randomUUID());
    }

}