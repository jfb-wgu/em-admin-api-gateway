package edu.wgu.dmadmin.domain.evaluator;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

import edu.wgu.dmadmin.domain.security.User;

/**
 * @author Jessica Pamdeth
 */

public class UserListResponse {

   private List<User> evaluators;

   public UserListResponse(List<User> list) {
	   this.evaluators = list;
   }
   
   @JsonGetter("evaluators") 
   public List<User> getEvaluators() {
	   Collections.sort(evaluators);
	   return evaluators;
   }
}
