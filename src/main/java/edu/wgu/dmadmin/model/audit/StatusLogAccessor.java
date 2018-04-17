package edu.wgu.dmadmin.model.audit;

import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface StatusLogAccessor {
    @Query("select * from dm.status_log where assessment_id = ?")
    Result<StatusLogModel> getAssessmentStatus(Long assessmentId);
    
    @Query("select * from dm.status_log where activity_date >= ? ALLOW FILTERING")
    Result<StatusLogModel> getAssessmentStatusByDate(Date activityDate);
    
    @Query("SELECT * FROM dm.status_log WHERE student_id = ? AND submission_id = ? limit 1")
    StatusLogModel getLastStatusEntry(String studentId, UUID submissionId);
}
