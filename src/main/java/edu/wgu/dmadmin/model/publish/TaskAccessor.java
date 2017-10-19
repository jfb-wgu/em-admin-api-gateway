package edu.wgu.dmadmin.model.publish;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

import edu.wgu.dmadmin.model.submission.AttachmentModel;

@Accessor
public interface TaskAccessor {
    @Query("SELECT * FROM dm.task_by_course")
    Result<TaskByCourseModel> getAll();
    
    @Query("SELECT task_id, task_name, assessment_id, assessment_code FROM dm.task_by_course")
    Result<TaskByCourseModel> getAllBasics();

    @Query("SELECT * FROM dm.task_by_course where course_id = ?")
    Result<TaskByCourseModel> getTasksByCourseId(Long courseId);
    
    @Query("SELECT * FROM dm.task_by_assessment where assessment_id = ?")
    Result<TaskByAssessmentModel> getTasksByAssessment(UUID assessmentId);
    
    @Query("SELECT assessment_id, assessment_code, task_id, task_name FROM dm.task_by_assessment where assessment_id = ?")
    Result<TaskByAssessmentModel> getBasicTasksByAssessment(UUID assessmentId);
    
    @Query("SELECT * FROM dm.task_by_id WHERE task_id = ?")
    TaskByIdModel getTaskById(UUID taskId);
    
    @Query("SELECT rubric FROM dm.task_by_id WHERE task_id = ?")
    TaskByIdModel getTaskRubric(UUID taskId);
    
    @Query("SELECT task_name, task_id, crd_notes, rubric FROM dm.task_by_id WHERE task_id = ?")
    TaskByIdModel getTaskBasics(UUID taskId);
    
    @Query("SELECT course_id, assessment_id, task_id FROM dm.task_by_id WHERE task_id = ?")
    TaskByIdModel getTaskKeys(UUID taskId);

    @Query("SELECT task_id, course_name, course_code, assessment_id, assessment_code, assessment_name, task_name, rubric FROM dm.task_by_id WHERE task_id = ?")
    TaskByIdModel getTaskForSubmission(UUID taskId);
    
    @Query("SELECT course_id, course_code, assessment_id, assessment_code, task_id, supporting_documents FROM dm.task_by_id WHERE task_id = ?")
    TaskByIdModel getSupportingDocuments(UUID taskId);
    
    @Query("UPDATE dm.task_by_course SET supporting_documents = ?, date_updated = ? WHERE course_id = ? and assessment_id = ? and  task_id = ?")
    void saveSupportingDocuments(Map<String, AttachmentModel> supportingDocuments, Date dateUpdated, Long courseId, UUID assessmentId, UUID taskId);
}
