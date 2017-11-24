package edu.wgu.dmadmin.domain.user;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

import edu.wgu.dreammachine.domain.security.User;

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
	   Collections.sort(this.evaluators);
	   return this.evaluators;
   }
}
