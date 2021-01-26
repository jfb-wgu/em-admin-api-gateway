package edu.wgu.dm.dto.response;

import edu.wgu.dm.dto.security.UserSummary;
import java.util.List;


public class UserListResponse {

    private List<UserSummary> evaluators;

    public UserListResponse(List<UserSummary> evaluators) {
        this.evaluators = evaluators;
    }

    public List<UserSummary> getEvaluators() {
        return evaluators;
    }

    public void setEvaluators(List<UserSummary> evaluators) {
        this.evaluators = evaluators;
    }
}
