package edu.wgu.dmadmin.domain.security;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author Jessica Pamdeth
 */

public class UserListResponse {

   private List<User> evaluators;

   public UserListResponse(List<User> list) {
	   this.evaluators = list;
   }
   
   @JsonGetter("evaluators") 
   public List<User> getUsers() {
	   return this.evaluators;
   }
}
