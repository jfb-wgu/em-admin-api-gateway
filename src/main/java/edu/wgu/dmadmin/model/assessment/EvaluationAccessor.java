package edu.wgu.dmadmin.model.assessment;

import java.util.UUID;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface EvaluationAccessor {

    @Query("SELECT submission_id, date_started FROM dm.evaluation_by_evaluator WHERE status = ? and evaluator_id = ?")
    Result<EvaluationByEvaluatorModel> getSubmissionsByEvaluatorIdAndStatus(String status, String evaluatorId);

    @Query("SELECT * FROM dm.evaluation_by_submission WHERE evaluator_id = ? and submission_id = ?")
    Result<EvaluationBySubmissionModel> getEvaluationBySubmission(String evaluatorId, UUID submissionId);

    @Query("SELECT * FROM dm.evaluation_by_id WHERE evaluation_id = ?")
    EvaluationByIdModel getEvaluationById(UUID evaluationId);

    @Query("SELECT evaluation_id, evaluator_id, submission_id, student_id, task_id, attempt, score_report, status, evaluator_first_name, evaluator_last_name FROM dm.evaluation_by_submission WHERE evaluator_id = ? and submission_id = ?")
    Result<EvaluationBySubmissionModel> getScoreReport(String evaluatorId, UUID submissionId);

    @Query("SELECT * FROM dm.evaluation_by_evaluator WHERE evaluator_id = ? and status = ? and submission_id = ?")
    Result<EvaluationByEvaluatorModel> getEvaluationByEvaluaatorAndSubmission(String evaluatorId, String status, UUID submissionId);

    @Query("UPDATE dm.evaluation_by_id set score_report = ? WHERE evaluation_id = ? and evaluator_id = ? and submission_id = ? and student_id = ? and task_id = ? and attempt = ?")
    void setScoreReport(ScoreReportModel scoreReport, UUID evaluationId, String evaluatorId, UUID submissionId, String studentId, UUID taskId, int attempt);

    @Query("SELECT * FROM dm.evaluation_by_submission WHERE submission_id = ?")
    Result<EvaluationBySubmissionModel> getEvaluationsBySubmission(UUID submissionId);
}