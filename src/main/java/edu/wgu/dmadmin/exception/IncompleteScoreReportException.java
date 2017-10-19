package edu.wgu.dmadmin.exception;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class IncompleteScoreReportException extends Exception {
	
	private static final long serialVersionUID = -3641930514021397801L;

	public IncompleteScoreReportException(String message) {
		super(message);
	}
	
	public IncompleteScoreReportException(Set<String> aspects) {
		super("The following aspects have not been scored: " + aspects.toString());
	}
}
