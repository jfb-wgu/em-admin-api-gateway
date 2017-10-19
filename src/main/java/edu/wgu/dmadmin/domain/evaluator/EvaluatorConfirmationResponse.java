package edu.wgu.dmadmin.domain.evaluator;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author Jessica Pamdeth
 */

public class EvaluatorConfirmationResponse {

	private EvaluatorConfirmation evaluatorConfirmation;

	public EvaluatorConfirmationResponse(EvaluatorConfirmation evalConf) {
		this.evaluatorConfirmation = evalConf;
	}
	
    @JsonGetter("evaluatorConfirmation")
    public EvaluatorConfirmation getEvaluatorConfirmation() {
    	return evaluatorConfirmation;
    }
}
