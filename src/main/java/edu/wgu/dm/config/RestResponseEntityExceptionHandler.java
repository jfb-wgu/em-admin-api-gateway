package edu.wgu.dm.config;

import edu.wgu.dm.dto.response.ExceptionResponse;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {Exception.class})
    @ResponseBody
    protected ResponseEntity<ExceptionResponse> handleException(Exception ex, HttpServletRequest request) {

        HttpStatus status = null;
        String reason = null;
        UUID errorUuid = UUID.randomUUID();
        Class<? extends Exception> exceptionObject = ex.getClass();
        List<String> messages = null;
        try {
            Field messagesField = ReflectionUtils.findField(exceptionObject, "messages", List.class);
            if (messagesField != null) {
                messagesField.setAccessible(true);
                messages = (List<String>) ReflectionUtils.getField(messagesField, ex);
            }
        } catch (Exception e) {
            // Intentional
        }
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

        log.error("Exception for {}: {}, and Reason: {}, Exception: {}, , Reference ID: {}", request.getMethod(),
                  request.getRequestURI(), reason, ex, errorUuid);
        log.error("Error", ex);
        String message = String.format("Reason: %s, Reference ID: %s", reason, errorUuid);
        ExceptionResponse exResponse = new ExceptionResponse(status.value(), status.name(), message, new Date(),
                                                             request.getRequestURI(), messages);
        return new ResponseEntity<>(exResponse, status);
    }
}
