package edu.wgu.dmadmin.factory;

import java.util.UUID;

import edu.wgu.dmadmin.model.submission.SubmissionLockModel;

public class SubmissionLockFactory {

	public SubmissionLockModel getSubmissionLock(UUID submissionId, String userId) {
		return new SubmissionLockModel(submissionId, userId, UUID.randomUUID());
	}
}
