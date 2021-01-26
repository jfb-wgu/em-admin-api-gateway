package edu.wgu.dm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -7227568295142134596L;

    public UserNotFoundException(String evaluatorId) {
        super("No evaluator with ID [" + evaluatorId + "] was found in the system.");
    }
}