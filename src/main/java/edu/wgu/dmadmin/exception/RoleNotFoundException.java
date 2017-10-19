package edu.wgu.dmadmin.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class RoleNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = -2302993188019794290L;

	public RoleNotFoundException(UUID roleId) {
		super("No role found for ID [" + roleId + "].");
	}
}
