package edu.wgu.dmadmin.domain.submission;

import java.util.UUID;

public interface QualifyingSubmission {
	public UUID getTaskId();
	public String getStatus();
}
