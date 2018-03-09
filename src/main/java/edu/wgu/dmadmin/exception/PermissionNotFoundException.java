package edu.wgu.dmadmin.exception;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class PermissionNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 211747241477944093L;

	public PermissionNotFoundException(UUID permissionId) {
		super("No permission found for ID [" + permissionId + "].");
	}
}
