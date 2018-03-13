package edu.wgu.dmadmin.domain.security;

import com.fasterxml.jackson.annotation.JsonGetter;

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
	   return this.evaluator;
   }
}
 