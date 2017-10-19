package edu.wgu.dmadmin.domain.assessment;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import edu.wgu.dmadmin.model.publish.TaskModel;

@Data
@NoArgsConstructor
@JsonPropertyOrder({"assessmentName", "assessmentCode", "assessmentId", "assessmentType", "assessmentOrder", "status", "tasks"})
public class Assessment implements Comparable<Assessment> {

    String assessmentName;
    String assessmentCode;
    UUID assessmentId;
    String assessmentType;
    int assessmentOrder;
    List<Task> tasks;
    
    @JsonIgnore
    public YearMonth assessmentDate;

    @JsonGetter("tasks")
    public List<Task> getTasks() {
    	if (tasks == null) tasks = new ArrayList<Task>();
    	
    	Collections.sort(tasks);
    	return tasks;
    }
    
    public void addAssessmentTask(TaskModel task) {
    	if (tasks == null) tasks = new ArrayList<Task>();
    	this.tasks.add(new Task(task));
    }

    public Assessment(TaskModel model) {
    	this.assessmentName = model.getAssessmentName();
    	this.assessmentCode = model.getAssessmentCode();
    	this.assessmentType = model.getAssessmentType();
    	this.assessmentOrder = model.getAssessmentOrder();
    	this.assessmentId = model.getAssessmentId();
    	this.assessmentDate = YearMonth.parse(model.getAssessmentDate(), DateTimeFormatter.ofPattern("MMyy"));
    }
    
	@Override
	public int compareTo(Assessment o) {
		return this.assessmentOrder - o.assessmentOrder;
	}    
}
