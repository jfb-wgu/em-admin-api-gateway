package edu.wgu.dmadmin.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class WorkingEvaluationException extends Exception {
	
	private static final long serialVersionUID = -5455432891843013030L;

	public WorkingEvaluationException(UUID evaluationId, String userId, UUID submissionId) {
		super("A working evaluation already exists for submission [" +
				submissionId + "].  Evaluation ID [" +
				evaluationId + "] for user [" +
				userId + "].");
	}
	
	public WorkingEvaluationException(UUID submissionId) {
		super("A working evaluation already exists for submission ID [" + submissionId + "].");
	}
	
	public WorkingEvaluationException(UUID submissionId, int count) {
		super(count + " working evaluations found for submission ID [" + submissionId + "].");
	}
}
