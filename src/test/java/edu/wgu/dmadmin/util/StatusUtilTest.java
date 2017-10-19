package edu.wgu.dmadmin.util;

import static edu.wgu.dmadmin.util.StatusUtil.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.wgu.dmadmin.domain.security.Permissions;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Before;
import org.junit.Rule;

public class StatusUtilTest {
	
	List<String> statuses;
	Set<String> queues;
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Before
	public void initialize() {
		statuses = new ArrayList<String>();
		statuses.add(AUTHOR_SUBMISSION_STARTED);
		statuses.add(AUTHOR_RESUBMISSION_STARTED);
		statuses.add(AUTHOR_WORK_SUBMITTED);
		statuses.add(AUTHOR_WORK_RESUBMITTED);
		statuses.add(AUTHOR_WORK_EVALUATED);
		statuses.add(AUTHOR_WORK_NEEDS_REVISION);
		statuses.add(EVALUATION_BEGUN);
		statuses.add(EVALUATION_CANCELLED);
		statuses.add(EVALUATION_TAKEN_OVER);
		statuses.add(EVALUATION_EDITED);
		statuses.add(SUBMISSION_CANCELLED);
		statuses.add(EVALUATION_RELEASED);
		statuses.add(LEAD_HOLD);
		statuses.add(ORIGINALITY_HOLD);
		statuses.add(ARTICULATION_HOLD);
		statuses.add(OPEN_HOLD);
		
		queues = new HashSet<String>();
		queues.add(Permissions.LEAD_QUEUE);
		queues.add(Permissions.ORIGINALITY_QUEUE);
		queues.add(Permissions.ARTICULATION_QUEUE);
		queues.add(Permissions.OPEN_QUEUE);
		queues.add(Permissions.TASK_QUEUE);
	}

	@Test
	public void testIsStarted() {
		assertTrue(isStarted(AUTHOR_SUBMISSION_STARTED));
		assertTrue(isStarted(AUTHOR_RESUBMISSION_STARTED));
		
		this.statuses.remove(AUTHOR_SUBMISSION_STARTED);
		this.statuses.remove(AUTHOR_RESUBMISSION_STARTED);
		
		this.statuses.forEach(status -> {
			assertFalse(isStarted(status));
		});
	}

	@Test
	public void testIsCancelled() {
		assertTrue(isCancelled(SUBMISSION_CANCELLED));
		
		this.statuses.remove(SUBMISSION_CANCELLED);
		
		this.statuses.forEach(status -> {
			assertFalse(isCancelled(status));
		});
	}

	@Test
	public void testIsPending() {
		assertTrue(isPending(AUTHOR_WORK_SUBMITTED));
		assertTrue(isPending(AUTHOR_WORK_RESUBMITTED));
		assertTrue(isPending(EVALUATION_CANCELLED));
		
		this.statuses.remove(AUTHOR_WORK_SUBMITTED);
		this.statuses.remove(AUTHOR_WORK_RESUBMITTED);
		this.statuses.remove(EVALUATION_CANCELLED);
		
		this.statuses.forEach(status -> {
			assertFalse(isPending(status));
		});
	}
	
	@Test
	public void testIsEvaluationCancelled() {
		assertTrue(isEvaluationCancelled(EVALUATION_CANCELLED));
		
		this.statuses.remove(EVALUATION_CANCELLED);
		
		this.statuses.forEach(status -> {
			assertFalse(isEvaluationCancelled(status));
		});
	}
		
	@Test
	public void testIsSubmitted() {
		assertTrue(isSubmitted(AUTHOR_WORK_SUBMITTED));
		assertTrue(isSubmitted(AUTHOR_WORK_RESUBMITTED));
		
		this.statuses.remove(AUTHOR_WORK_SUBMITTED);
		this.statuses.remove(AUTHOR_WORK_RESUBMITTED);
		
		this.statuses.forEach(status -> {
			assertFalse(isSubmitted(status));
		});
	}

	@Test
	public void testIsInEvaluation() {
		assertTrue(isInEvaluation(EVALUATION_BEGUN));
		assertTrue(isInEvaluation(EVALUATION_TAKEN_OVER));
		
		this.statuses.remove(EVALUATION_BEGUN);
		this.statuses.remove(EVALUATION_TAKEN_OVER);
		
		this.statuses.forEach(status -> {
			assertFalse(isInEvaluation(status));
		});
	}

	@Test
	public void testIsEvaluated() {
		assertTrue(isEvaluated(AUTHOR_WORK_NEEDS_REVISION));
		assertTrue(isEvaluated(EVALUATION_RELEASED));
		assertTrue(isEvaluated(AUTHOR_WORK_EVALUATED));
		assertTrue(isEvaluated(EVALUATION_EDITED));
		
		this.statuses.remove(AUTHOR_WORK_NEEDS_REVISION);
		this.statuses.remove(EVALUATION_RELEASED);
		this.statuses.remove(AUTHOR_WORK_EVALUATED);
		this.statuses.remove(EVALUATION_EDITED);
		
		this.statuses.forEach(status -> {
			assertFalse(isEvaluated(status));
		});
	}

	@Test
	public void testCanReview() {
		assertTrue(canReview(AUTHOR_WORK_NEEDS_REVISION));
		assertTrue(canReview(EVALUATION_RELEASED));
		assertTrue(canReview(AUTHOR_WORK_EVALUATED));
		
		this.statuses.remove(AUTHOR_WORK_NEEDS_REVISION);
		this.statuses.remove(EVALUATION_RELEASED);
		this.statuses.remove(AUTHOR_WORK_EVALUATED);
		
		this.statuses.forEach(status -> {
			assertFalse(canReview(status));
		});
	}

	@Test
	public void testIsPassed() {
		assertTrue(isPassed(EVALUATION_RELEASED));
		
		this.statuses.remove(EVALUATION_RELEASED);
		
		this.statuses.forEach(status -> {
			assertFalse(isPassed(status));
		});
	}

	@Test
	public void testIsFailed() {
		assertTrue(isFailed(AUTHOR_WORK_NEEDS_REVISION));
		assertTrue(isFailed(AUTHOR_WORK_EVALUATED));
		
		this.statuses.remove(AUTHOR_WORK_NEEDS_REVISION);
		this.statuses.remove(AUTHOR_WORK_EVALUATED);
		
		this.statuses.forEach(status -> {
			assertFalse(isFailed(status));
		});
	}

	@Test
	public void testHeld() {
		assertTrue(isHeld(LEAD_HOLD));
		assertTrue(isHeld(ORIGINALITY_HOLD));
		assertTrue(isHeld(ARTICULATION_HOLD));
		assertTrue(isHeld(OPEN_HOLD));
		
		this.statuses.remove(LEAD_HOLD);
		this.statuses.remove(ORIGINALITY_HOLD);
		this.statuses.remove(ARTICULATION_HOLD);
		this.statuses.remove(OPEN_HOLD);
		
		this.statuses.forEach(status -> {
			assertFalse(isHeld(status));
		});
	}

	@Test
	public void testCanBeginSubmission() {
		assertTrue(canBeginSubmission(SUBMISSION_CANCELLED));
		assertTrue(canBeginSubmission(AUTHOR_WORK_NEEDS_REVISION));
		assertTrue(canBeginSubmission(AUTHOR_WORK_EVALUATED));
		
		this.statuses.remove(SUBMISSION_CANCELLED);
		this.statuses.remove(AUTHOR_WORK_NEEDS_REVISION);
		this.statuses.remove(AUTHOR_WORK_EVALUATED);
		
		this.statuses.forEach(status -> {
			assertFalse(canBeginSubmission(status));
		});
	}
	
	@Test
	public void testGetSubmittedStatus() {
		thrown.expect(IllegalArgumentException.class);
		getSubmittedStatus(ALL_EVALUATIONS_CANCELLED);
	}

	@Test
	public void testHasBeenSubmitted() {
		assertFalse(hasBeenSubmitted(SUBMISSION_CANCELLED));
		assertFalse(hasBeenSubmitted(AUTHOR_RESUBMISSION_STARTED));
		assertFalse(hasBeenSubmitted(AUTHOR_SUBMISSION_STARTED));
		
		this.statuses.remove(SUBMISSION_CANCELLED);
		this.statuses.remove(AUTHOR_RESUBMISSION_STARTED);
		this.statuses.remove(AUTHOR_SUBMISSION_STARTED);
		
		this.statuses.forEach(status -> {
			assertTrue(hasBeenSubmitted(status));
		});
	}

	@Test
	public void testHasBeenClaimed() {
		assertFalse(hasBeenClaimed(SUBMISSION_CANCELLED));
		assertFalse(hasBeenClaimed(AUTHOR_RESUBMISSION_STARTED));
		assertFalse(hasBeenClaimed(AUTHOR_SUBMISSION_STARTED));
		assertFalse(hasBeenClaimed(AUTHOR_WORK_SUBMITTED));
		assertFalse(hasBeenClaimed(AUTHOR_WORK_RESUBMITTED));
		assertFalse(hasBeenClaimed(EVALUATION_CANCELLED));
		
		this.statuses.remove(SUBMISSION_CANCELLED);
		this.statuses.remove(AUTHOR_RESUBMISSION_STARTED);
		this.statuses.remove(AUTHOR_SUBMISSION_STARTED);
		this.statuses.remove(AUTHOR_WORK_SUBMITTED);
		this.statuses.remove(AUTHOR_WORK_RESUBMITTED);
		this.statuses.remove(EVALUATION_CANCELLED);	
		
		this.statuses.forEach(status -> {
			assertTrue(hasBeenClaimed(status));
		});
	}

	@Test
	public void testHasBeenEvaluated() {
		assertTrue(hasBeenEvaluated(AUTHOR_WORK_EVALUATED));
		assertTrue(hasBeenEvaluated(AUTHOR_WORK_NEEDS_REVISION));
		assertTrue(hasBeenEvaluated(EVALUATION_EDITED));
		assertTrue(hasBeenEvaluated(EVALUATION_RELEASED));
		
		this.statuses.remove(AUTHOR_WORK_EVALUATED);
		this.statuses.remove(AUTHOR_WORK_NEEDS_REVISION);
		this.statuses.remove(EVALUATION_EDITED);
		this.statuses.remove(EVALUATION_RELEASED);
		
		this.statuses.forEach(status -> {
			assertFalse(hasBeenEvaluated(status));
		});
	}
	
	@Test
	public void testAuthorSubmissionStarted() {
		assertEquals(STARTED, getStatusGroup(AUTHOR_SUBMISSION_STARTED));
		assertEquals(DISPLAY_STARTED, getSearchStatus(AUTHOR_SUBMISSION_STARTED));
		assertEquals(DISPLAY_IN_PROGRESS, getStudentStatus(AUTHOR_SUBMISSION_STARTED, 1));
		assertEquals(AUTHOR_WORK_SUBMITTED, getSubmittedStatus(AUTHOR_SUBMISSION_STARTED));
	}
	
	@Test
	public void testAuthorReSubmissionStarted() {
		assertEquals(STARTED, getStatusGroup(AUTHOR_RESUBMISSION_STARTED));
		assertEquals(DISPLAY_STARTED, getSearchStatus(AUTHOR_RESUBMISSION_STARTED));
		assertEquals(DISPLAY_REVISION, getStudentStatus(AUTHOR_RESUBMISSION_STARTED, 1));
		assertEquals(AUTHOR_WORK_RESUBMITTED, getSubmittedStatus(AUTHOR_RESUBMISSION_STARTED));
	}
	
	@Test
	public void testAuthorWorkSubmitted() {
		assertEquals(PENDING, getStatusGroup(AUTHOR_WORK_SUBMITTED));
		assertEquals(DISPLAY_STARTED, getSearchStatus(AUTHOR_WORK_SUBMITTED));
		assertEquals(DISPLAY_WORK_SUBMITTED, getStudentStatus(AUTHOR_WORK_SUBMITTED, 1));
		assertEquals(Permissions.TASK_QUEUE, getQueueForStatus(AUTHOR_WORK_SUBMITTED));
	}
	
	@Test
	public void testAuthorWorkReSubmitted() {
		assertEquals(PENDING, getStatusGroup(AUTHOR_WORK_RESUBMITTED));
		assertEquals(DISPLAY_STARTED, getSearchStatus(AUTHOR_WORK_RESUBMITTED));
		assertEquals(DISPLAY_WORK_RESUBMITTED, getStudentStatus(AUTHOR_WORK_RESUBMITTED, 1));
		assertEquals(Permissions.TASK_QUEUE, getQueueForStatus(AUTHOR_WORK_RESUBMITTED));
	}
	
	@Test
	public void testAuthorWorkEvaluated() {
		assertEquals(COMPLETED, getStatusGroup(AUTHOR_WORK_EVALUATED));
		assertEquals(DISPLAY_MENTOR, getSearchStatus(AUTHOR_WORK_EVALUATED));
		assertEquals(DISPLAY_REVISION, getStudentStatus(AUTHOR_WORK_EVALUATED, 1));
	}
	
	@Test
	public void testAuthorWorkNeedsRevision() {
		assertEquals(COMPLETED, getStatusGroup(AUTHOR_WORK_NEEDS_REVISION));
		assertEquals(DISPLAY_REVISION, getSearchStatus(AUTHOR_WORK_NEEDS_REVISION));
		assertEquals(DISPLAY_REVISION, getStudentStatus(AUTHOR_WORK_NEEDS_REVISION, 1));
	}
	
	@Test
	public void testEvaluationBegun() {
		assertEquals(WORKING, getStatusGroup(EVALUATION_BEGUN));
		assertEquals(DISPLAY_WORKING, getSearchStatus(EVALUATION_BEGUN));
		assertEquals(DISPLAY_WORK_SUBMITTED, getStudentStatus(EVALUATION_BEGUN, 1));
		assertEquals(DISPLAY_WORK_RESUBMITTED, getStudentStatus(EVALUATION_BEGUN, 2));
	}
	
	@Test
	public void testEvaluationCancelled() {
		assertEquals(PENDING, getStatusGroup(EVALUATION_CANCELLED));
		assertEquals(DISPLAY_CANCELLED, getSearchStatus(EVALUATION_CANCELLED));
		assertEquals(DISPLAY_WORK_SUBMITTED, getStudentStatus(EVALUATION_CANCELLED, 1));
		assertEquals(DISPLAY_WORK_RESUBMITTED, getStudentStatus(EVALUATION_CANCELLED, 2));
		assertEquals(Permissions.TASK_QUEUE, getQueueForStatus(EVALUATION_CANCELLED));
	}
	
	@Test
	public void testEvaluationTakenOver() {
		assertEquals(WORKING, getStatusGroup(EVALUATION_TAKEN_OVER));
		assertEquals(DISPLAY_WORKING, getSearchStatus(EVALUATION_TAKEN_OVER));
		assertEquals(DISPLAY_WORK_SUBMITTED, getStudentStatus(EVALUATION_TAKEN_OVER, 1));
		assertEquals(DISPLAY_WORK_RESUBMITTED, getStudentStatus(EVALUATION_TAKEN_OVER, 2));
	}
	
	@Test
	public void testEvaluationEdited() {
		assertEquals(COMPLETED, getStatusGroup(EVALUATION_EDITED));
		assertEquals(DISPLAY_EVALUATION_REVIEW, getSearchStatus(EVALUATION_EDITED));
		assertEquals(UNKNOWN, getStudentStatus(EVALUATION_EDITED, 1));
	}
	
	@Test
	public void testSubmissionCancelled() {
		assertEquals(CANCELLED, getStatusGroup(SUBMISSION_CANCELLED));
		assertEquals(DISPLAY_STARTED, getSearchStatus(SUBMISSION_CANCELLED));
		assertEquals(DISPLAY_IN_PROGRESS, getStudentStatus(SUBMISSION_CANCELLED, 1));
		assertEquals(DISPLAY_REVISION, getStudentStatus(SUBMISSION_CANCELLED, 2));
	}
	
	@Test
	public void testEvaluationReleased() {
		assertEquals(COMPLETED, getStatusGroup(EVALUATION_RELEASED));
		assertEquals(DISPLAY_PASSED, getSearchStatus(EVALUATION_RELEASED));
		assertEquals(DISPLAY_PASSED, getStudentStatus(EVALUATION_RELEASED, 1));
	}
	
	@Test
	public void testLeadHold() {
		assertEquals(HOLD, getStatusGroup(LEAD_HOLD));
		assertEquals(HOLD, getSearchStatus(LEAD_HOLD));
		assertEquals(UNKNOWN, getStudentStatus(LEAD_HOLD, 1));
		assertEquals(Permissions.LEAD_QUEUE, getQueueForStatus(LEAD_HOLD));
	}
	
	@Test
	public void testOriginalityHold() {
		assertEquals(HOLD, getStatusGroup(ORIGINALITY_HOLD));
		assertEquals(HOLD, getSearchStatus(ORIGINALITY_HOLD));
		assertEquals(UNKNOWN, getStudentStatus(ORIGINALITY_HOLD, 1));
		assertEquals(Permissions.ORIGINALITY_QUEUE, getQueueForStatus(ORIGINALITY_HOLD));
	}
	
	@Test
	public void testArticulationHold() {
		assertEquals(HOLD, getStatusGroup(ARTICULATION_HOLD));
		assertEquals(HOLD, getSearchStatus(ARTICULATION_HOLD));
		assertEquals(UNKNOWN, getStudentStatus(ARTICULATION_HOLD, 1));
		assertEquals(Permissions.ARTICULATION_QUEUE, getQueueForStatus(ARTICULATION_HOLD));
	}
	
	@Test
	public void testOpenHold() {
		assertEquals(HOLD, getStatusGroup(OPEN_HOLD));
		assertEquals(HOLD, getSearchStatus(OPEN_HOLD));
		assertEquals(UNKNOWN, getStudentStatus(OPEN_HOLD, 1));
		assertEquals(Permissions.OPEN_QUEUE, getQueueForStatus(OPEN_HOLD));
	}
	
	@Test
	public void testGeStatusesForQueue1() {
		List<String> stats = Arrays.asList(LEAD_HOLD, ORIGINALITY_HOLD, ARTICULATION_HOLD, OPEN_HOLD,
				AUTHOR_WORK_SUBMITTED, AUTHOR_WORK_RESUBMITTED, EVALUATION_CANCELLED);
		assertTrue(CollectionUtils.isEqualCollection(stats, getStatusesForQueues(this.queues)));
	}
	
	@Test
	public void testGeStatusesForQueue2() {
		List<String> stats = Arrays.asList(LEAD_HOLD, OPEN_HOLD,
				AUTHOR_WORK_SUBMITTED, AUTHOR_WORK_RESUBMITTED, EVALUATION_CANCELLED);
		this.queues.remove(Permissions.ORIGINALITY_QUEUE);
		this.queues.remove(Permissions.ARTICULATION_QUEUE);
		assertTrue(CollectionUtils.isEqualCollection(stats, getStatusesForQueues(this.queues)));
	}
	
	@Test
	public void testGeStatusesForQueue3() {
		List<String> stats = Arrays.asList(AUTHOR_WORK_SUBMITTED, AUTHOR_WORK_RESUBMITTED, EVALUATION_CANCELLED);
		this.queues.remove(Permissions.ORIGINALITY_QUEUE);
		this.queues.remove(Permissions.ARTICULATION_QUEUE);
		this.queues.remove(Permissions.LEAD_QUEUE);
		this.queues.remove(Permissions.OPEN_QUEUE);
		assertTrue(CollectionUtils.isEqualCollection(stats, getStatusesForQueues(this.queues)));
	}
	
	@Test
	public void testGeStatusesForQueue4() {
		this.queues.add(Permissions.CREATE_TASK);
		
		thrown.expect(IllegalArgumentException.class);
		getStatusesForQueues(this.queues);
	}
}
