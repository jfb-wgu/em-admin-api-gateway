package edu.wgu.dmadmin.domain.evaluation;

import java.util.UUID;

import edu.wgu.dmadmin.domain.publish.Aspect;
import edu.wgu.dmadmin.model.evaluation.EvaluationAspectModel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EvaluationAspect {
    String aspectName;
    int passingScore;
    int assignedScore = -1;
    int previousScore = -1;
    String comments;
    UUID evaluationId;

    public EvaluationAspect(EvaluationAspectModel a, UUID inEvaluationId) {
		this.setAspectName(a.getAspectName());
		this.setAssignedScore(a.getAssignedScore());
		this.setComments(a.getComments());
		this.setPassingScore(a.getPassingScore());
		this.evaluationId = inEvaluationId;
	}
    
    public EvaluationAspect(EvaluationAspect a, UUID inEvaluationId) {
		this.setAspectName(a.getAspectName());
		this.setAssignedScore(a.getAssignedScore());
		this.setPreviousScore(a.getPreviousScore());
		this.setComments(a.getComments());
		this.setPassingScore(a.getPassingScore());
		this.evaluationId = inEvaluationId;
	}
    
    public EvaluationAspect(Aspect a, UUID inEvaluationId) {
		this.setAspectName(a.getName());
		this.setPassingScore(a.getPassingScore());
		this.evaluationId = inEvaluationId;
	}
}
