package edu.wgu.dmadmin.repo;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

import edu.wgu.dreammachine.model.audit.StatusLogByAssessmentModel;
import edu.wgu.dreammachine.model.audit.StatusLogByStudentModel;
import edu.wgu.dreammachine.model.publish.TaskByAssessmentModel;
import edu.wgu.dreammachine.model.submission.SubmissionByStudentAndTaskModel;

@Accessor
public interface CassandraAccessor {
    @Query("select * from dm.status_log_by_assessment where assessment_id IN :assessmentIds")
    Result<StatusLogByAssessmentModel> getAssessmentStatus(List<UUID> assessmentIds);
    
    @Query("select * from dm.status_log_by_assessment where activity_date >= ? allow filtering")
    Result<StatusLogByAssessmentModel> getAssessmentStatusByDate(Date activityDate);
    
    @Query("SELECT assessment_id, assessment_code, task_id, task_name, task_order FROM dm.task_by_assessment where assessment_id = ?")
    Result<TaskByAssessmentModel> getBasicTasksByAssessment(UUID assessmentId);
    
    @Query("SELECT * FROM dm.submission_by_student_and_task WHERE student_id = ? AND task_id = ? limit 1")
    SubmissionByStudentAndTaskModel getLastSubmissionByStudentAndTask(String studentId, UUID taskId);
    
    @Query("SELECT * FROM dm.status_log_by_student WHERE student_id = ? AND submission_id = ? limit 1")
    StatusLogByStudentModel getLastStatusEntry(String studentId, UUID submissionId);
}