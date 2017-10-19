package edu.wgu.dmadmin.exception;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class SubmissionStatusException extends Exception {
	
	private static final long serialVersionUID = 191873018911849476L;

	public SubmissionStatusException(String message) {
        super(message);
    }

    public SubmissionStatusException(String message, Throwable throwable) {
        super(message, throwable);
    }

	public SubmissionStatusException(UUID submissionId, String status) {
		super("The status [" + status + "] of submission [" + submissionId + "] does not allow this action.");
	}
	
	public SubmissionStatusException(UUID submissionId, String status, List<String> validStatuses) {
		super("The status [" + status + "] of the submission [" + submissionId + "] does not allow this action.  Valid statuses are:  " + validStatuses.toString());
	}
}
