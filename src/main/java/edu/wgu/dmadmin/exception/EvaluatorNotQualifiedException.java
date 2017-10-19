package edu.wgu.dmadmin.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class EvaluatorNotQualifiedException extends Exception {

	private static final long serialVersionUID = -2259479515463175336L;

	public EvaluatorNotQualifiedException(String evaluatorId, UUID submissionId) {
		super("Evaluator [" + evaluatorId + "] is not qualified for submission [" + submissionId + "].");
	}
}
