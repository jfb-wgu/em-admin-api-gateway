package edu.wgu.dm.dto.response;

import java.util.Date;
import java.util.List;

public class ExceptionResponse {

    private int status;
    private String error;
    private String message;
    private Date timestamp;
    private String path;
    private List<String> messages;

    public ExceptionResponse(int status, String error, String message, Date timestamp, String path,
        List<String> messages) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = timestamp;
        this.path = path;
        this.messages = messages;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
}
