package edu.wgu.dmadmin.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class CourseNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -5294604686884483630L;

	public CourseNotFoundException(String courseCode) {
		super("No assessments found for course code [" + courseCode + "].");
	}

	public CourseNotFoundException(UUID assessmentId) {
		super("No assessments found for ID [" + assessmentId + "].");
	}

	public CourseNotFoundException(Long courseId) {
		super("No assessments found for course ID [" + courseId + "].");
	}
}
