package edu.wgu.dmadmin.domain.evaluator;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author Jessica Pamdeth
 */

public class EvaluatorDashboardResponse {

	private EvaluatorDashboard evaluatorDashboard;

	public EvaluatorDashboardResponse(EvaluatorDashboard evalDash) {
		this.evaluatorDashboard = evalDash;
	}
	
    @JsonGetter("evaluatorDashboard")
    public EvaluatorDashboard getEvaluatorDashboard() {
    	return evaluatorDashboard;
    }
}
