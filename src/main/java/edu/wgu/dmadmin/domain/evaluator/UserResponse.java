package edu.wgu.dmadmin.domain.evaluator;

import com.fasterxml.jackson.annotation.JsonGetter;

import edu.wgu.dmadmin.domain.security.User;

/**
 * @author Jessica Pamdeth
 */

public class UserResponse {

   private User evaluator;

   public UserResponse(User eval) {
	   this.evaluator = eval;
   }
   
   @JsonGetter("evaluator")
   public User getEvaluator() {
	   return evaluator;
   }
}
 