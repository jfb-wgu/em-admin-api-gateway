package edu.wgu.dmadmin.repo;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

import edu.wgu.dreammachine.model.audit.StatusLogByAssessmentModel;

@Accessor
public interface CassandraAccessor {
    @Query("select * from dm.status_log_by_assessment where assessment_id IN :assessmentIds")
    Result<StatusLogByAssessmentModel> getAssessmentStatus(List<UUID> assessmentIds);
    
    @Query("select * from dm.status_log_by_assessment where activity_date >= ? allow filtering")
    Result<StatusLogByAssessmentModel> getAssessmentStatusByDate(Date activityDate);
}