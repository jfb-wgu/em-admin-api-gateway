package edu.wgu.dmadmin.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class TaskNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -600189869822313750L;

	public TaskNotFoundException(UUID taskId) {
		super("No assessment task was found for task ID [" + taskId + "].");
	}
	
	public TaskNotFoundException(Long courseId) {
		super("No assessment tasks were found for course ID [" + courseId + "].");
	}

	public TaskNotFoundException(String msg) {
		super(msg);
	}
}
