package edu.wgu.dreammachine.model.evaluation;

import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface EvaluationAccessor {

    @Query("SELECT * FROM dm.evaluation_by_student_and_task WHERE student_id = ?")
    Result<EvaluationByStudentAndTaskModel> getEvaluations(String studentId);
	
    @Query("SELECT * FROM dm.evaluation_by_student_and_task WHERE student_id = ? and task_id = ?")
    Result<EvaluationByStudentAndTaskModel> getEvaluations(String studentId, UUID taskId);
    
    @Query("SELECT * FROM dm.evaluation_by_student_and_task WHERE student_id = ? and task_id = ? and submission_id = ?")
    Result<EvaluationByStudentAndTaskModel> getEvaluations(String studentId, UUID taskId, UUID submissionId);
    
    @Query("SELECT * FROM dm.evaluation_by_id WHERE evaluation_id = ?")
    EvaluationByIdModel getEvaluation(UUID evaluationId);
    
    @Query("SELECT * FROM dm.evaluation_by_id WHERE evaluation_id = ? and evaluator_id = ?")
    EvaluationByIdModel getEvaluation(UUID evaluationId, String evaluatorId);
    
    @Query("SELECT evaluation_id, evaluator_id, status, evaluator_first_name, evaluator_last_name FROM dm.evaluation_by_id WHERE evaluation_id = ?")
    EvaluationByIdModel getEvaluationStatus(UUID evaluationId);
    
    @Query("SELECT evaluation_id, evaluator_id, status, evaluator_first_name, evaluator_last_name FROM dm.evaluation_by_id WHERE evaluation_id = ? and evaluator_id = ?")
    EvaluationByIdModel getEvaluationStatus(UUID evaluationId, String evaluatorId);
    
    @Query("SELECT evaluator_first_name, evaluator_last_name, evaluator_id, status, evaluation_id, submission_id FROM dm.evaluation_by_evaluator_and_status WHERE evaluator_id = ? and status = ?")
    Result<EvaluationByEvaluatorAndStatusModel> getSubmissionIdsForUserAndStatus(String userId, String status);
    
    @Query("SELECT * FROM dm.evaluation_attachment WHERE student_id = ? AND task_id = ?")
    Result<EvaluationAttachmentModel> getEvaluationAttachments(String studentId, UUID taskId);
    
    @Query("SELECT * FROM dm.evaluation_attachment WHERE student_id = ? AND task_id = ? AND submission_id = ?")
	Result<EvaluationAttachmentModel> getEvaluationAttachments(String studentId, UUID taskId, UUID submissionId);
    
    @Query("SELECT * FROM dm.evaluation_attachment WHERE student_id = ? AND task_id = ? AND submission_id = ? AND evaluation_id = ?")
    Result<EvaluationAttachmentModel> getEvaluationAttachments(String studentId, UUID taskId, UUID submissionId, UUID evaluationId);
    
    @Query("SELECT * FROM dm.evaluation_attachment_by_id WHERE evaluation_attachment_id = ?")
    EvaluationAttachmentByIdModel getEvaluationAttachment(UUID attachmentId);
    
    @Query("DELETE FROM dm.evaluation_attachment WHERE student_id = ? and task_id = ? and submission_id = ? and evaluation_id = ?")
    void deleteEvaluationAttachments(String studentId, UUID taskId, UUID submissionId, UUID evaluationId);
}
