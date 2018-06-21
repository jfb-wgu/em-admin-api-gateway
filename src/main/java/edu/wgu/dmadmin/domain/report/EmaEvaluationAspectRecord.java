package edu.wgu.dmadmin.domain.report;

import java.util.Date;
import java.util.UUID;
import edu.wgu.dmadmin.model.evaluation.EvaluationAspectModel;
import edu.wgu.dmadmin.model.evaluation.EvaluationModel;
import lombok.Data;

@Data
public class EmaEvaluationAspectRecord {
    private String evaluatorId;
    private UUID evaluationId;
    private String aspectName;
    private int passingScore;
    private int assignedScore;
    private String comments;
    private Date dateCompleted;
    private String status;
    
    public EmaEvaluationAspectRecord(EvaluationAspectModel aspect, EvaluationModel eval) {
        this.setEvaluatorId(eval.getEvaluatorId());
        this.setEvaluationId(eval.getEvaluationId());
        this.setAspectName(aspect.getAspectName());
        this.setPassingScore(aspect.getPassingScore());
        this.setAssignedScore(aspect.getAssignedScore());
        this.setComments(aspect.getComments());
        this.setDateCompleted(eval.getDateCompleted());
        this.setStatus(eval.getStatus());
    }
}
