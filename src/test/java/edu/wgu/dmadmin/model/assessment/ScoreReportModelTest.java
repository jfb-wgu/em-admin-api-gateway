package edu.wgu.dmadmin.model.assessment;

import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.domain.assessment.Score;
import edu.wgu.dmadmin.domain.assessment.ScoreReport;
import edu.wgu.dmadmin.model.publish.AspectModel;
import edu.wgu.dmadmin.model.publish.RubricModel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Created by joshua.barnett on 6/16/17.
 */
public class ScoreReportModelTest {
    private ScoreReportModel scoreReportModel;

    @Before
    public void setUp() throws Exception {
        scoreReportModel = new ScoreReportModel();
    }

    @Test
    public void testIsPassed() throws Exception {
        String key1 = "A1";
        String key2 = "A2";
        String key3 = "A3";

        ScoreModel score1 = new ScoreModel();
        score1.setPassingScore(1);
        score1.setAssignedScore(2);
        score1.setName(key1);

        ScoreModel score2 = new ScoreModel();
        score2.setPassingScore(3);
        score2.setAssignedScore(4);
        score2.setName(key2);

        ScoreModel score3 = new ScoreModel();
        score3.setPassingScore(2);
        score3.setAssignedScore(3);
        score3.setName(key3);

        Map<String, ScoreModel> scores = new HashMap<>();
        scores.put(key1, score1);
        scores.put(key2, score2);
        scores.put(key3, score3);

        scoreReportModel.setScores(scores);
        assertTrue(scoreReportModel.isPassed());
    }

    @Test
    public void testIsPassedFalse() throws Exception {
        String key1 = "A1";
        String key2 = "A2";
        String key3 = "A3";

        ScoreModel score1 = new ScoreModel();
        score1.setPassingScore(1);
        score1.setAssignedScore(1);
        score1.setName(key1);

        ScoreModel score2 = new ScoreModel();
        score2.setPassingScore(3);
        score2.setAssignedScore(2);
        score2.setName(key2);

        ScoreModel score3 = new ScoreModel();
        score3.setPassingScore(2);
        score3.setAssignedScore(2);
        score3.setName(key3);

        Map<String, ScoreModel> scores = new HashMap<>();
        scores.put(key1, score1);
        scores.put(key2, score2);
        scores.put(key3, score3);

        scoreReportModel.setScores(scores);
        assertFalse(scoreReportModel.isPassed());
    }

    @Test
    public void testGetComments() throws Exception {
        UUID commentKey = UUID.randomUUID();

        CommentModel commentModel = new CommentModel();
        String comment1 = "this is a comment";
        commentModel.setComments(comment1);

        Map<UUID, CommentModel> comments = new HashMap<>();
        comments.put(commentKey, commentModel);

        scoreReportModel.setComments(comments);
        assertEquals(1, scoreReportModel.getComments().size());
        assertEquals(comment1, scoreReportModel.getComments().get(commentKey).getComments());
    }

    @Test
    public void testGetComments2() throws Exception {

        assertEquals(0, scoreReportModel.getComments().size());
    }

    @Test
    public void testtestGetScores() throws Exception {
        assertEquals(0, scoreReportModel.getScores().size());
    }

    @Test
    public void testtestGetScores2() throws Exception {
        String key1 = "A1";
        String key2 = "A2";
        String key3 = "A3";

        ScoreModel score1 = new ScoreModel();
        score1.setPassingScore(1);
        score1.setAssignedScore(1);
        score1.setName(key1);

        ScoreModel score2 = new ScoreModel();
        score2.setPassingScore(3);
        score2.setAssignedScore(2);
        score2.setName(key2);

        ScoreModel score3 = new ScoreModel();
        score3.setPassingScore(2);
        score3.setAssignedScore(2);
        score3.setName(key3);

        Map<String, ScoreModel> scores = new HashMap<>();
        scores.put(key1, score1);
        scores.put(key2, score2);
        scores.put(key3, score3);

        scoreReportModel.setScores(scores);
        assertEquals(3, scoreReportModel.getScores().size());
    }

    @Test
    public void testGetUnscoredAspects() throws Exception {
        String key1 = "A1";
        String key2 = "A2";
        String key3 = "A3";

        ScoreModel score1 = new ScoreModel();
        score1.setPassingScore(1);
        score1.setAssignedScore(1);
        score1.setName(key1);

        ScoreModel score2 = new ScoreModel();
        score2.setPassingScore(3);
        score2.setAssignedScore(2);
        score2.setName(key2);

        ScoreModel score3 = new ScoreModel();
        score3.setPassingScore(2);
        score3.setAssignedScore(2);
        score3.setName(key3);

        Map<String, ScoreModel> scores = new HashMap<>();
        scores.put(key1, score1);
        scores.put(key2, score2);
        scores.put(key3, score3);

        scoreReportModel.setScores(scores);
        Set<String> result = scoreReportModel.getUnscoredAspects();
        assertEquals(0, result.size());
    }

    @Test
    public void testGetUnscoredAspects2() throws Exception {
        String key1 = "A1";
        String key2 = "A2";
        String key3 = "A3";

        ScoreModel score1 = new ScoreModel();
        score1.setPassingScore(1);
        score1.setAssignedScore(1);
        score1.setName(key1);

        ScoreModel score2 = new ScoreModel();
        score2.setPassingScore(3);
        score2.setAssignedScore(-1);
        score2.setName(key2);

        ScoreModel score3 = new ScoreModel();
        score3.setPassingScore(2);
        score3.setAssignedScore(2);
        score3.setName(key3);

        Map<String, ScoreModel> scores = new HashMap<>();
        scores.put(key1, score1);
        scores.put(key2, score2);
        scores.put(key3, score3);

        scoreReportModel.setScores(scores);
        Set<String> result = scoreReportModel.getUnscoredAspects();
        assertEquals(1, result.size());
    }

    @Test
    public void testGetName() throws Exception {
        String name = "this is a score report";
        scoreReportModel.setName(name);
        assertEquals(name, scoreReportModel.getName());
    }

    @Test
    public void testGetDescription() throws Exception {
        String description = "description";
        scoreReportModel.setDescription(description);
        assertEquals(description, scoreReportModel.getDescription());
    }

    @Test
    public void testSetAspectComment() throws Exception {
        String key1 = "A1";
        String key2 = "A2";
        String key3 = "A3";

        ScoreModel score1 = new ScoreModel();
        score1.setPassingScore(1);
        score1.setAssignedScore(1);
        score1.setName(key1);

        ScoreModel score2 = new ScoreModel();
        score2.setPassingScore(3);
        score2.setAssignedScore(-1);
        score2.setName(key2);

        ScoreModel score3 = new ScoreModel();
        score3.setPassingScore(2);
        score3.setAssignedScore(2);
        score3.setName(key3);

        Map<String, ScoreModel> scores = new HashMap<>();
        scores.put(key1, score1);
        scores.put(key2, score2);
        scores.put(key3, score3);

        scoreReportModel.setScores(scores);

        Comment comment = new Comment();
        String com = "this is a comment";
        comment.setComments(com);

        CommentModel result = scoreReportModel.setAspectComment(comment, key1, 1, "jbarnett", "Joshua", "Barnett");
        assertEquals(com, result.getComments());

    }

    @Test
    public void testSetAspectCommentCommentIdNotNull() throws Exception {
        String key1 = "A1";
        String key2 = "A2";
        String key3 = "A3";

        ScoreModel score1 = new ScoreModel();
        score1.setPassingScore(1);
        score1.setAssignedScore(1);
        score1.setName(key1);


        ScoreModel score2 = new ScoreModel();
        score2.setPassingScore(3);
        score2.setAssignedScore(-1);
        score2.setName(key2);

        ScoreModel score3 = new ScoreModel();
        score3.setPassingScore(2);
        score3.setAssignedScore(2);
        score3.setName(key3);

        Map<String, ScoreModel> scores = new HashMap<>();
        scores.put(key1, score1);
        scores.put(key2, score2);
        scores.put(key3, score3);

        scoreReportModel.setScores(scores);

        Comment comment = new Comment();
        UUID commentId = UUID.randomUUID();
        String com = "this is a comment";

        comment.setComments(com);
        comment.setCommentId(commentId);

        Map<UUID, CommentModel> aspectcomments = new HashMap<>();
        CommentModel model = new CommentModel(comment);
        aspectcomments.put(commentId, model);
        score1.setComments(aspectcomments);

        CommentModel result = scoreReportModel.setAspectComment(comment, key1, 1, "jbarnett", "Joshua", "Barnett");
        assertEquals(com, result.getComments());

    }

    @Test
    public void testSetAspectCommentRemoveComment() throws Exception {
        String key1 = "A1";
        String key2 = "A2";
        String key3 = "A3";

        ScoreModel score1 = new ScoreModel();
        score1.setPassingScore(1);
        score1.setAssignedScore(1);
        score1.setName(key1);


        ScoreModel score2 = new ScoreModel();
        score2.setPassingScore(3);
        score2.setAssignedScore(-1);
        score2.setName(key2);

        ScoreModel score3 = new ScoreModel();
        score3.setPassingScore(2);
        score3.setAssignedScore(2);
        score3.setName(key3);

        Map<String, ScoreModel> scores = new HashMap<>();
        scores.put(key1, score1);
        scores.put(key2, score2);
        scores.put(key3, score3);

        scoreReportModel.setScores(scores);

        Comment comment = new Comment();
        UUID commentId = UUID.randomUUID();

        comment.setComments("");
        comment.setCommentId(commentId);

        Map<UUID, CommentModel> aspectcomments = new HashMap<>();
        CommentModel model = new CommentModel(comment);
        aspectcomments.put(commentId, model);
        score1.setComments(aspectcomments);

        CommentModel result = scoreReportModel.setAspectComment(comment, key1, 1, "jbarnett", "Joshua", "Barnett");
        assertNull(result);
    }

    @Test
    public void testSetReportComment() throws Exception {

        Comment comment = new Comment();
        String com = "comment";

        comment.setComments(com);

        CommentModel result = scoreReportModel.setReportComment("jbarnett", "Joshua", "Barnett", comment, 1);
        assertEquals(com, result.getComments());
    }

    @Test
    public void testSetReportCommentExisting() throws Exception {
        UUID commentId = UUID.randomUUID();

        CommentModel existing = new CommentModel();
        String existingComment = "Existing Comment";
        existing.setComments(existingComment);
        existing.setCommentId(commentId);
        Map<UUID, CommentModel> map = new HashMap<>();
        map.put(commentId, existing);
        scoreReportModel.setComments(map);

        assertEquals(existingComment, scoreReportModel.getComments().get(commentId).getComments());

        Comment comment = new Comment();
        String com = "comment";

        comment.setComments(com);
        comment.setCommentId(commentId);

        CommentModel result = scoreReportModel.setReportComment("jbarnett", "Joshua", "Barnett", comment, 1);
        assertEquals(com, result.getComments());
    }

    @Test
    public void testSetReportCommentExistingBlank() throws Exception {
        UUID commentId = UUID.randomUUID();

        CommentModel existing = new CommentModel();
        String existingComment = "Existing Comment";
        existing.setComments(existingComment);
        existing.setCommentId(commentId);
        Map<UUID, CommentModel> map = new HashMap<>();
        map.put(commentId, existing);
        scoreReportModel.setComments(map);

        assertEquals(existingComment, scoreReportModel.getComments().get(commentId).getComments());

        Comment comment = new Comment();
        String com = "";

        comment.setComments(com);
        comment.setCommentId(commentId);

        CommentModel result = scoreReportModel.setReportComment("jbarnett", "Joshua", "Barnett", comment, 1);
        assertNull(result);
    }

    @Test
    public void testConstructorReport() {
        String name = "report Name";
        String reportDescription = "reportDescription";

        List<Score> scorelist = new ArrayList<>();

        Score score = new Score();
        score.setName("A1");
        score.setPassingScore(1);
        score.setAssignedScore(1);

        scorelist.add(score);

        List<Comment> comments = new ArrayList<>();

        Comment comment = new Comment();
        comment.setComments("this is a comment");

        comments.add(comment);

        ScoreReport report = new ScoreReport();
        report.setName(name);
        report.setDescription(reportDescription);
        report.setScores(scorelist);
        report.setComments(comments);

        ScoreReportModel model = new ScoreReportModel(report);
        assertEquals(name, model.getName());
        assertEquals(reportDescription, model.getDescription());
        assertEquals(1, model.getScores().size());
        assertEquals(1, model.getComments().size());
    }

    @Test
    public void testConstructorRubricModel() {
        String name = "report Name";
        String description = "reportDescription";

        List<AspectModel> aspectModels = new ArrayList<>();
        AspectModel aspectModel = new AspectModel();
        aspectModels.add(aspectModel);

        RubricModel rubricModel = new RubricModel();
        rubricModel.setName(name);
        rubricModel.setDescription(description);
        rubricModel.setAspects(aspectModels);

        ScoreReportModel model = new ScoreReportModel(rubricModel);
        assertEquals(name, model.getName());
        assertEquals(description, model.getDescription());
        assertEquals(1, model.getScores().size());
    }

}