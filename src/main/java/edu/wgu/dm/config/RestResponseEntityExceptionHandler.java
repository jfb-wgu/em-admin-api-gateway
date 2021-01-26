package edu.wgu.dm.config;

import edu.wgu.dm.dto.response.ExceptionResponse;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestResponseEntityExceptionHandler.class);

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    protected ResponseEntity<ExceptionResponse> handleException(Exception ex, HttpServletRequest request) {

        HttpStatus status = null;
        String reason = null;
        Class<? extends Exception> exceptionObject = ex.getClass();
        ResponseStatus annotation = exceptionObject.getAnnotation(ResponseStatus.class);
        if (annotation != null) {
            status = annotation.value();
            reason = annotation.reason();
        }
        // Default setting if we don't get response code from the ResponseStatus annotation
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        if (StringUtils.isBlank(reason)) {
            reason = ex.getMessage();
        }

        log.error("Exception for {}: {}, and Reason: {}, Exception: {}", request.getMethod(),
                  request.getRequestURI(), reason, ex);
        log.error("Error", ex);
        String message = String.format("Reason: %s", reason);
        ExceptionResponse exResponse = new ExceptionResponse(status.value(), status.name(), message, new Date(),
                                                             request.getRequestURI());
        return new ResponseEntity<>(exResponse, status);
    }
}
