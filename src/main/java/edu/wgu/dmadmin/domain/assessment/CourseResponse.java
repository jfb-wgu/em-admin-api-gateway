package edu.wgu.dmadmin.domain.assessment;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author Jessica Pamdeth
 */

public class CourseResponse {

   private Course course;

   public CourseResponse(Course c) {
	   this.course = c;
   }
   
   @JsonGetter("course")
   public Course getCourse() {
	   return course;
   }
}
 