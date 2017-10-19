package edu.wgu.dmadmin.exception;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class EvaluationStatusException extends Exception {

	private static final long serialVersionUID = -1119925030719375931L;

	public EvaluationStatusException(String foundStatus, String requiredStatus) {
        super("Found [" + foundStatus + "] required [" + requiredStatus + "] status for evaluation.");
    }

    public EvaluationStatusException(String message) {
        super(message);
    }
    
	public EvaluationStatusException(UUID evaluationId, String status, List<String> validStatuses) {
		super("The status [" + status + "] of the evaluation [" + evaluationId + "] does not allow this action.  Valid statuses are:  " + validStatuses.toString());
	}
}
