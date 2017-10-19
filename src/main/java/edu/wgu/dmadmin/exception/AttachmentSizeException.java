package edu.wgu.dmadmin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.CONFLICT)
public class AttachmentSizeException extends RuntimeException {
    private static final long serialVersionUID = -8942018993792368679L;

    public AttachmentSizeException() {
        super("There are no attachments on this submission, or there is a file that is of size 0.");
    }
}
