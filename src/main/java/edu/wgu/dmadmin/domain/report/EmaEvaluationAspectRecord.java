package edu.wgu.dmadmin.domain.report;

import java.util.Date;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;
import edu.wgu.dmadmin.model.evaluation.EvaluationAspectModel;
import edu.wgu.dmadmin.model.evaluation.EvaluationModel;
import lombok.Data;

@Data
public class EmaEvaluationAspectRecord {
    private String evaluatorId;
    private UUID evaluationId;
    private UUID submissionId;
    private String overallComments;
    private String aspectName;
    private int passingScore;
    private int assignedScore;
    private String comments;
    private String status;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM-dd-yyyy HH:mm:ss")
    private Date dateCompleted;
    
    public EmaEvaluationAspectRecord(EvaluationAspectModel aspect, EvaluationModel eval) {
        this.setEvaluatorId(eval.getEvaluatorId());
        this.setEvaluationId(eval.getEvaluationId());
        this.setSubmissionId(eval.getSubmissionId());
        this.setOverallComments(eval.getComments());
        this.setAspectName(aspect.getAspectName());
        this.setPassingScore(aspect.getPassingScore());
        this.setAssignedScore(aspect.getAssignedScore());
        this.setComments(aspect.getComments());
        this.setDateCompleted(eval.getDateCompleted());
        this.setStatus(eval.getStatus());
    }
}
