package edu.wgu.dmadmin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class UserIdNotFoundException extends Exception {
	
	private static final long serialVersionUID = -3536222746684776666L;

	public UserIdNotFoundException(String message) {
		super(message);
	}
}
