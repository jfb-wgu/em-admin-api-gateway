package edu.wgu.dmadmin.domain.evaluator;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author Jessica Pamdeth
 */

public class EvaluatorWorkspaceResponse {

	private EvaluatorWorkspace evaluatorWorkspace;

	public EvaluatorWorkspaceResponse(EvaluatorWorkspace evalWork) {
		this.evaluatorWorkspace = evalWork;
	}
	
    @JsonGetter("evaluatorWorkspace")
    public EvaluatorWorkspace getEvaluatorWorkspace() {
    	return evaluatorWorkspace;
    }
}
