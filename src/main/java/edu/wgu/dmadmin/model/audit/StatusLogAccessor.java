package edu.wgu.dmadmin.model.audit;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface StatusLogAccessor {
    @Query("select * from dm.status_log_by_assessment where assessment_id IN :assessmentIds")
    Result<StatusLogByAssessmentModel> getAssessmentStatus(List<UUID> assessmentIds);
    
    @Query("select * from dm.status_log_by_assessment where activity_date >= ? allow filtering")
    Result<StatusLogByAssessmentModel> getAssessmentStatusByDate(Date activityDate);
    
    @Query("SELECT * FROM dm.status_log_by_student WHERE student_id = ? AND submission_id = ? limit 1")
    StatusLogByStudentModel getLastStatusEntry(String studentId, UUID submissionId);
}