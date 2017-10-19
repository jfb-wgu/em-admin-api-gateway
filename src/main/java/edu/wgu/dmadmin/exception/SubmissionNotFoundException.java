package edu.wgu.dmadmin.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class SubmissionNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 3173365595072151799L;

	public SubmissionNotFoundException(UUID submissionId) {
		super("No submission with ID [" + submissionId + "] was found in the system.");
	}
	
	public SubmissionNotFoundException(UUID submissionId, String studentId) {
		super("No submission with ID [" + submissionId + "] was found for student [" + studentId + "].");
	}
}
