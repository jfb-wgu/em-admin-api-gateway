package edu.wgu.dm.dto.security;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserTask {

    Long taskId;
    String taskName;
    String courseCode;
    String courseName;
    Long emaAssessmentId;
    Long pamsAssessmentId;
    String assessmentCode;
    String assessmentName;

    public UserTask(Long taskId) {
        this.setTaskId(taskId);
    }
}
