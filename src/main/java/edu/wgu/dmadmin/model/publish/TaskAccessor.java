package edu.wgu.dmadmin.model.publish;

import java.util.Date;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface TaskAccessor {
    
    @Query("SELECT task_id, task_name, pams_assessment_id, assessment_code FROM dm.task_by_id")
    Result<TaskModel> getAllBasics();
    
    @Query("SELECT pams_assessment_id, assessment_code, task_id, task_name, task_order FROM dm.task_by_id where pams_assessment_id = ?")
    Result<TaskModel> getBasicTasksByAssessment(Long pamsAssessmentId);
    
    @Query("SELECT task_id, task_name, rubric FROM dm.task_by_id WHERE date_published >= ? allow filtering")
    Result<TaskModel> getRubrics(Date datePublished);
    
    @Query("SELECT task_id, task_name, competencies FROM dm.task_by_id WHERE date_published >= ? allow filtering")
    Result<TaskModel> getCompetencies(Date datePublished);
}
