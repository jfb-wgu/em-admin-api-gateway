package edu.wgu.dm.dto.response;

import edu.wgu.dm.dto.security.User;

public class UserResponse {

    private User evaluator;

    public UserResponse(User evaluator) {
        this.evaluator = evaluator;
    }

    public UserResponse() {
    }

    public User getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(User evaluator) {
        this.evaluator = evaluator;
    }
}
