package edu.wgu.dmadmin.model.evaluation;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(keyspace = "dm", name = "evaluation_by_id")
public class EvaluationModel {

    @PartitionKey(0)
    @Column(name = "evaluator_id")
    String evaluatorId;

    @PartitionKey(1)
    @Column(name = "evaluation_id")
    UUID evaluationId;
    
    @Column(name = "submission_id")
    UUID submissionId;

    @Column(name = "aspects")
    @Frozen
    Map<String, EvaluationAspectModel> aspects;

    String comments;

    @Column(name = "date_completed")
    Date dateCompleted;
    
    @Column(name = "date_updated")
    Date dateUpdated;

    String status;
}
