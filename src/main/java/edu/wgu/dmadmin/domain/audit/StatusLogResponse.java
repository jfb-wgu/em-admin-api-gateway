package edu.wgu.dmadmin.domain.audit;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author Jessica Pamdeth
 */

public class StatusLogResponse {

   private List<StatusLogEntry> logs;

   public StatusLogResponse(List<StatusLogEntry> list) {
	   this.logs = list;	   
   }
   
   @JsonGetter("logs")
   public List<StatusLogEntry> getLogs() {
	   Collections.sort(logs);
	   return logs;
   }
}
 