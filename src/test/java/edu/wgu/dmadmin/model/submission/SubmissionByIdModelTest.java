package edu.wgu.dmadmin.model.submission;

import edu.wgu.dmadmin.TestObjectFactory;
import edu.wgu.dmadmin.domain.submission.Submission;
import edu.wgu.dmadmin.util.StatusUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubmissionByIdModelTest {
    private SubmissionByIdModel submissionByIdModel;
    Submission submission;

    @Before
    public void setUp() throws Exception {
        submission = TestObjectFactory.getTestSubmission(StatusUtil.AUTHOR_SUBMISSION_STARTED);
        submissionByIdModel = new SubmissionByIdModel(submission);
    }

    @Test
    public void testGetSubmissionId() throws Exception {
        assertEquals(submission.getSubmissionId(), submissionByIdModel.getSubmissionId());
    }

    @Test
    public void testGetStudentId() throws Exception {
        assertEquals(submission.getStudentId(), submissionByIdModel.getStudentId());
    }

    @Test
    public void testGetTaskId() throws Exception {
        assertEquals(submission.getTaskId(), submissionByIdModel.getTaskId());
    }

    @Test
    public void testGetAttempt() throws Exception {
        assertEquals(submission.getAttempt(), submissionByIdModel.getAttempt());
    }
}
