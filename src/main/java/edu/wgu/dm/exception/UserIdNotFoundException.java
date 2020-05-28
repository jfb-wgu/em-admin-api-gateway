package edu.wgu.dm.exception;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User ID could not be found in the request.")
@NoArgsConstructor
public class UserIdNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -3536222746684776666L;

    public UserIdNotFoundException(String message) {
        super(message);
    }
}
