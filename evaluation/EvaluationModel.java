package edu.wgu.dreammachine.model.evaluation;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EvaluationModel {

    @Column(name = "student_id")
    String studentId;

    @Column(name = "task_id")
    UUID taskId;

    @Column(name = "submission_id")
    UUID submissionId;

    @Column(name = "evaluator_id")
    String evaluatorId;

    @Column(name = "evaluation_id")
    UUID evaluationId;

    @Column(name = "aspects")
    @Frozen
    Map<String, EvaluationAspectModel> aspects;

    String comments;

    @Column(name = "date_completed")
    Date dateCompleted;

    @Column(name = "date_started")
    Date dateStarted;

    @Column(name = "date_updated")
    Date dateUpdated;

    @Column(name = "evaluator_first_name")
    String evaluatorFirstName;

    @Column(name = "evaluator_last_name")
    String evaluatorLastName;
    
    @Column(name="minutes_spent")
    int minutesSpent;

    String status;
    int attempt;
}
