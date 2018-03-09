package edu.wgu.dreammachine.model.evaluation;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.UDT;

import edu.wgu.dreammachine.domain.evaluation.EvaluationAspect;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@UDT(keyspace = "dm", name = "evaluation_aspect")
public class EvaluationAspectModel {

    @Field(name = "aspect_name")
    String aspectName;

    @Field(name = "passing_score")
    int passingScore = -1;

    @Field(name = "assigned_score")
    int assignedScore = -1;

    String comments;
    
    public EvaluationAspectModel(EvaluationAspect aspect) {
    		this.setAspectName(aspect.getAspectName());
    		this.setAssignedScore(aspect.getAssignedScore());
    		this.setComments(aspect.getComments());
    		this.setPassingScore(aspect.getPassingScore());
    }
}
