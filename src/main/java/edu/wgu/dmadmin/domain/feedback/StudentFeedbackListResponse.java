package edu.wgu.dmadmin.domain.feedback;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author Jessica Pamdeth
 */

public class StudentFeedbackListResponse {

   private List<StudentFeedback> feedback;

   public StudentFeedbackListResponse(List<StudentFeedback> list) {
	   this.feedback = list;
   }
   
   @JsonGetter("feedback") 
   public List<StudentFeedback> getFeedback() {
	   return feedback;
   }
}
