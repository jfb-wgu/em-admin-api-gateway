package edu.wgu.dm.dto.response;

import java.util.Date;

public class ExceptionResponse {

    private final int status;
    private final String error;
    private final String message;
    private final Date timestamp;
    private final String path;

    public ExceptionResponse(int status, String error, String message, Date timestamp, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
    }

    public int getStatus() {
        return status;
    }


    public String getError() {
        return error;
    }


    public String getMessage() {
        return message;
    }


    public Date getTimestamp() {
        return timestamp;
    }


    public String getPath() {
        return path;
    }

}
