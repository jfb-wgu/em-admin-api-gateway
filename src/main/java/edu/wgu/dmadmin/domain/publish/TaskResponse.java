package edu.wgu.dmadmin.domain.publish;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author Jessica Pamdeth
 */

public class TaskResponse {

   private Task task;

   public TaskResponse(Task t) {
	   this.task = t;
   }
   
   @JsonGetter("task")
   public Task getTask() {
	   return task;
   }
}
 