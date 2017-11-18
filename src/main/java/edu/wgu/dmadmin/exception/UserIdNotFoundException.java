package edu.wgu.dmadmin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND, reason="User ID could not be found in the request.")
public class UserIdNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 6062941484626767813L;
}
