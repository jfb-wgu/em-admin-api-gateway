package edu.wgu.dmadmin.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class EvaluationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2525879870272542078L;

	public EvaluationNotFoundException(String evaluatorId) {
		super("No evaluation for [" + evaluatorId + "] was found for the submission.");
	}
	
	public EvaluationNotFoundException(UUID evaluationId) {
		super("No evaluation for id [" + evaluationId + "] was found.");
	}

	public EvaluationNotFoundException(String evaluatorId, UUID submissionId) {
		super("No evaluation for submission [" + submissionId + "] and evaluator [" + evaluatorId + "] was found.");
	}
}
