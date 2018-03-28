package edu.wgu.dmadmin.model.publish;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface TaskAccessor {
    
    @Query("SELECT task_id, task_name, assessment_id, assessment_code FROM dm.task_by_id")
    Result<TaskByIdModel> getAllBasics();
    
    @Query("SELECT assessment_id, assessment_code, task_id, task_name, task_order FROM dm.task_by_assessment where assessment_id = ?")
    Result<TaskByAssessmentModel> getBasicTasksByAssessment(Long assessmentId);
}
