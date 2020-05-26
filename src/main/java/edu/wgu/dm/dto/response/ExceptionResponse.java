package edu.wgu.dm.dto.response;

import java.util.Date;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ExceptionResponse {

    int status;
    String error;
    String message;
    Date timestamp;
    String path;
    List<String> messages;
}
