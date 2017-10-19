package edu.wgu.dmadmin.domain.publish;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author Jessica Pamdeth
 */
public class TaskListResponse {

   private List<Task> tasks;

   public TaskListResponse(List<Task> ts) {
	   this.tasks = ts;
   }
   
   @JsonGetter("tasks")
   public List<Task> getTasks() {
	   return tasks;
   }
}
 