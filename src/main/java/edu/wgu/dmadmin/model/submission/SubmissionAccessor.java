package edu.wgu.dmadmin.model.submission;

import java.util.List;
import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.driver.mapping.annotations.QueryParameters;

@Accessor
public interface SubmissionAccessor {

    @Query("SELECT * FROM dm.submission_by_status_group_and_task WHERE status_group = ? and task_id = ?")
    Result<SubmissionByStatusGroupAndTaskModel> getSubmissionsByStatusGroupAndTask(String statusGroup, UUID taskId);
    
    @Query("SELECT * FROM dm.submission_by_status_group_and_task WHERE status_group = ?")
    Result<SubmissionByStatusGroupAndTaskModel> getSubmissionsByStatusGroup(String statusGroup);

    @Query("SELECT * FROM dm.submission_by_status_and_task WHERE status = ? and task_id = ?")
    Result<SubmissionByStatusAndTaskModel> getSubmissionsByStatusAndTask(String status, UUID taskId);
    
    @Query("SELECT * FROM dm.submission_by_status_and_task WHERE status = ?")
    Result<SubmissionByStatusAndTaskModel> getSubmissionsByStatus(String status);

    @Query("SELECT * FROM dm.submission_by_student_and_task WHERE student_id = ?")
    Result<SubmissionByStudentAndTaskModel> getSubmissionsByStudent(String studentId);
    
    @Query("SELECT * FROM dm.submission_by_student_and_task WHERE student_id = ? AND task_id = ?")
    Result<SubmissionByStudentAndTaskModel> getSubmissionsByStudentAndTask(String studentId, UUID taskId);
    
    @Query("SELECT * FROM dm.submission_by_student_and_task WHERE student_id = ? AND task_id = ? limit 1")
    SubmissionByStudentAndTaskModel getLastSubmissionByStudentAndTask(String studentId, UUID taskId);
    
    @Query("SELECT submission_id, status, attempt, date_updated FROM dm.submission_by_student_and_task WHERE student_id = ? AND task_id = ?")
    Result<SubmissionByStudentAndTaskModel> getSubmissionHistoryByStudentAndTask(String studentId, UUID taskId);

    @Query("SELECT submission_id FROM dm.submission_by_student_and_assessment WHERE student_id = ? AND assessment_id = ?")
    Result<SubmissionByStudentAndAssessmentModel> getCountSubmissionsByStudentAndAssessment(String studentId, UUID assessmentId);
    
    @Query("SELECT * FROM dm.submission_by_id WHERE submission_id = ?")
    SubmissionByIdModel getSubmissionById(UUID submissionId);
    
    @Query("SELECT submission_id, student_id, task_id, attempt, status FROM dm.submission_by_id WHERE submission_id = ?")
    SubmissionByIdModel getSubmissionForAssignment(UUID submissionId);

    @Query("SELECT * FROM dm.submission_by_id WHERE submission_id = ? AND student_id = ?")
    SubmissionByIdModel getSubmissionByIdAndStudent(UUID submissionId, String studentId);

    @Query("SELECT referrals FROM dm.submission_by_id WHERE submission_id = ?")
    SubmissionByIdModel getReferrals(UUID submissionId);

    @Query("SELECT submission_id, student_id, status, attempt FROM dm.submission_by_id WHERE submission_id = ?")
    SubmissionByIdModel getSubmissionStatus(UUID submissionid);

    @Query("SELECT date_submitted, task_id, submission_id, status, attempt, referrals, evaluator_id, evaluation_id FROM dm.submission_by_id WHERE submission_id = ?")
    SubmissionByIdModel getConfirmationSubmission(UUID submissionId);

    @Query("SELECT submission_id, internal_comments, attempt FROM dm.submission_by_id WHERE submission_id = ?")
    SubmissionByIdModel getSubmissionInternalComments(UUID submissionId);

    @Query("SELECT * FROM dm.submission_by_evaluator_and_task WHERE evaluator_id = ?")
    Result<SubmissionByEvaluatorAndTaskModel> getSubmissionsByEvaluator(String evaluatorId);
    
    @Query("SELECT * FROM dm.submission_by_evaluator_and_task WHERE evaluator_id = ? AND task_id = ?")
    Result<SubmissionByEvaluatorAndTaskModel> getSubmissionsByEvaluatorAndTask(String evaluatorId, UUID taskId);

    @Query("SELECT status, status_group, date_created, date_submitted, date_updated, date_estimated, date_started, date_completed, "
    		+ "task_id, task_name, submission_id, student_id, attempt, assessment_code, assessment_name, course_code, course_name, "
    		+ "aspect_count, evaluator_id, evaluator_first_name, evaluator_last_name "
    		+ "FROM dm.submission_by_id WHERE submission_id IN :submissionIds")
    Result<SubmissionByIdModel> getSubmissionsById(List<UUID> submissionIds);

    @Query("SELECT status, status_group, date_created, date_submitted, date_updated, date_estimated, date_started, date_completed, "
    		+ "task_id, task_name, submission_id, student_id, attempt, assessment_code, assessment_name, course_code, course_name, "
    		+ "aspect_count, evaluator_id, evaluator_first_name, evaluator_last_name "
    		+ "FROM dm.submission_by_student_and_task WHERE student_id = ? AND task_id IN :taskIds")
    Result<SubmissionByStudentAndTaskModel> getSubmissionsByStudentAndTasks(String studentId, List<UUID> taskIds);

    @Query("SELECT status, status_group, date_created, date_submitted, date_updated, date_estimated, date_started, date_completed, "
    		+ "task_id, task_name, submission_id, student_id, attempt, assessment_code, assessment_name, course_code, course_name, "
    		+ "aspect_count, evaluator_id, evaluator_first_name, evaluator_last_name "
    		+ "FROM dm.submission_by_status_group_and_task WHERE status_group = ? and task_id IN :taskIds")
    Result<SubmissionByStatusGroupAndTaskModel> getSubmissionsByStatusGroupAndTasks(String statusGroup, List<UUID> taskIds);

    @Query("SELECT status, status_group, date_created, date_submitted, date_updated, date_estimated, date_started, date_completed, "
    		+ "task_id, task_name, submission_id, student_id, attempt, assessment_code, assessment_name, course_code, course_name, "
    		+ "aspect_count, evaluator_id, evaluator_first_name, evaluator_last_name "
    		+ "FROM dm.submission_by_status_and_task WHERE status IN :statuses and task_id IN :taskIds")
    Result<SubmissionByStatusAndTaskModel> getSubmissionsByStatusesAndTasks(List<String> statuses, List<UUID> taskIds);

    @Query("SELECT status, status_group, date_created, date_submitted, date_updated, date_estimated, date_started, date_completed, "
    		+ "task_id, task_name, submission_id, student_id, attempt, assessment_code, assessment_name, course_code, course_name, "
    		+ "aspect_count, evaluator_id, evaluator_first_name, evaluator_last_name "
    		+ "FROM dm.submission_by_status_and_task WHERE status IN :statuses")
    Result<SubmissionByStatusAndTaskModel> getSubmissionsByStatuses(List<String> statuses);

    @Query("SELECT status, status_group, date_created, date_submitted, date_updated, date_estimated, date_started, date_completed, "
    		+ "task_id, task_name, submission_id, student_id, attempt, assessment_code, assessment_name, course_code, course_name, "
    		+ "aspect_count, evaluator_id, evaluator_first_name, evaluator_last_name "
    		+ "FROM dm.submission_by_evaluator_and_task WHERE evaluator_id IN :evaluatorIds AND task_id IN :taskIds")
    Result<SubmissionByEvaluatorAndTaskModel> getSubmissionsByEvaluatorAndTasks(List<String> evaluatorIds, List<UUID> taskIds);

    @Query("SELECT status, status_group, date_created, date_submitted, date_updated, date_estimated, date_started, date_completed, "
    		+ "task_id, task_name, submission_id, student_id, attempt, assessment_code, assessment_name, course_code, course_name, "
    		+ "aspect_count, evaluator_id, evaluator_first_name, evaluator_last_name "
    		+ "FROM dm.submission_by_evaluator_and_task WHERE evaluator_id IN :evaluatorIds")
    Result<SubmissionByEvaluatorAndTaskModel> getSubmissionsByEvaluators(List<String> evaluatorIds);
    
    @Query("INSERT INTO dm.submission_lock (submission_id, user_id, date_locked, lock_id) values (?, ?, toTimestamp(now()), ?)")
    @QueryParameters(consistency="QUORUM")
    void insertSubmissionLock(UUID submissionId, String userId, UUID lockId);
    
    @Query("SELECT * FROM dm.submission_lock WHERE submission_id = ?")
    @QueryParameters(consistency="QUORUM")
    Result<SubmissionLockModel> getSubmissionLocks(UUID submissionId);
}
