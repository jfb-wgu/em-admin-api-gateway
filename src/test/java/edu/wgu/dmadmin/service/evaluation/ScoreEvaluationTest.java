package edu.wgu.dmadmin.service.evaluation;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import edu.wgu.dmadmin.service.EvaluatorService;
import edu.wgu.dmadmin.service.SubmissionUtilityService;
import edu.wgu.dmadmin.util.StatusUtil;
import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.CommentTypes;
import edu.wgu.dmadmin.exception.WorkingEvaluationException;
import edu.wgu.dmadmin.model.assessment.EvaluationByEvaluatorModel;
import edu.wgu.dmadmin.model.assessment.ScoreModel;
import edu.wgu.dmadmin.model.assessment.ScoreReportModel;
import edu.wgu.dmadmin.repo.CassandraRepo;

public class ScoreEvaluationTest {
	EvaluatorService evaluatorService;
	CassandraRepo cassandraRepo = mock(CassandraRepo.class);
	SubmissionUtilityService suService = mock(SubmissionUtilityService.class);
	UUID submissionId;
	UUID taskId;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initialize() throws WorkingEvaluationException {
		evaluatorService = new EvaluatorService();
		evaluatorService.setCassandraRepo(cassandraRepo);
		evaluatorService.setSubmissionUtilityService(suService);
		submissionId = UUID.randomUUID();

		ScoreModel score1 = new ScoreModel();
		score1.setName("first");
		score1.setPassingScore(2);
		
		ScoreModel score2 = new ScoreModel();
		score2.setName("second");
		score2.setPassingScore(2);
		
		ScoreReportModel scoreReport = new ScoreReportModel();
		scoreReport.getScores().put(score1.getName(), score1);
		scoreReport.getScores().put(score2.getName(), score2);
		
		EvaluationByEvaluatorModel eval = new EvaluationByEvaluatorModel();
		eval.setStatus(StatusUtil.WORKING);
		eval.setScoreReport(scoreReport);
		eval.setEvaluatorId("123");
		eval.setSubmissionId(submissionId);
		
		when(suService.getWorkingEvaluation("123", submissionId)).thenReturn(eval);
	}

	@Test
	public void testSaveScore() throws WorkingEvaluationException {
		ArgumentCaptor<EvaluationByEvaluatorModel> arg1 = ArgumentCaptor.forClass(EvaluationByEvaluatorModel.class);
		
		evaluatorService.saveAspectScore("123", submissionId, "first", 2);
		Mockito.verify(cassandraRepo).saveScoreReport(arg1.capture());
		assertEquals(2, arg1.getValue().getScoreReport().getScores().get("first").getAssignedScore());
	}
	
	@Test
	public void testSaveComment() throws WorkingEvaluationException {
		ArgumentCaptor<EvaluationByEvaluatorModel> arg1 = ArgumentCaptor.forClass(EvaluationByEvaluatorModel.class);
		Comment comment = new Comment();
		comment.setType(CommentTypes.STUDENT);
		comment.setComments("testing");
		comment.setUserId("123");
		
		evaluatorService.saveAspectComment("123", submissionId, "first", comment);
		Mockito.verify(cassandraRepo).saveScoreReport(arg1.capture());
		assertEquals("testing", arg1.getValue().getScoreReport().getScores().get("first").getComments().values().iterator().next().getComments());
	}
}
