package edu.wgu.dmadmin.model.audit;

import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface LogAccessor {

    @Query("SELECT * FROM dm.status_log_by_assessment where assessment_Id = ?")
    Result<StatusLogByAssessmentModel> getByAssessmentId(UUID assessmentId);
    
    @Query("SELECT * FROM dm.status_log_by_student WHERE student_id = ? and submission_id = ? limit 1")
    StatusLogByStudentModel getLastStatusForSubmission(String studentId, UUID submissionId);
}