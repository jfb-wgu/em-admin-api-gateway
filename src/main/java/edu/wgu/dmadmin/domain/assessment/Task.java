package edu.wgu.dmadmin.domain.assessment;

import edu.wgu.dmadmin.domain.submission.Submission;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import edu.wgu.dmadmin.domain.publish.Competency;
import edu.wgu.dmadmin.domain.submission.Attachment;
import edu.wgu.dmadmin.model.publish.TaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.StatusUtil;

@Data
@NoArgsConstructor
public class Task implements Comparable<Task> {
	String courseName;
    UUID taskId;
    String taskName;
    String description;
    List<Competency> competencies;
    String introduction;
    String scenario;
    String requirements;
    ScoreReport scoreReport;
    String notes;
    List<String> webLinks;
    Map<String, Attachment> supportingDocuments;
    int taskOrder;
    UUID submissionId;
    List<Attachment> submittedFiles;
    String status;
    int attempt;
    String studentComments;
    String studentStatus;
    List<Submission> submissionHistory;
    Date dateEstimated;
    
    public List<Attachment> getSubmittedFiles() {
    	if (submittedFiles == null) submittedFiles = new ArrayList<Attachment>();
    	return submittedFiles;
    }

    public void setSubmissionInfo(SubmissionModel submission) {
		this.setSubmissionId(submission.getSubmissionId());
		this.setStatus(submission.getStatus());
		this.setStudentStatus(StatusUtil.getStudentStatus(submission.getStatus(), submission.getAttempt()));
		this.setDateEstimated(submission.getDateEstimated());
		this.setStudentComments(submission.getComments());
		this.setAttempt(submission.getAttempt());
		
    	submission.getAttachmentsNS().values().forEach(model -> {
    		Attachment a = new Attachment(model, submission.getStudentId(), submission.getSubmissionId());
            this.getSubmittedFiles().add(a);
    	});
    }
    
    public Task(TaskModel model) {
    	this.courseName = model.getCourseName();
    	this.taskId = model.getTaskId();
    	this.taskName = model.getTaskName();
    	this.description = model.getDescription();
    	this.introduction = model.getIntroduction();
    	this.scenario = model.getScenario();
    	this.requirements = model.getRequirements();
        this.studentStatus = StatusUtil.NOT_STARTED;
    	this.scoreReport = new ScoreReport(model.getRubric());
    	this.notes = model.getNotes();
    	this.webLinks = model.getWebLinks();
    	this.taskOrder = model.getTaskOrder();
    	
        this.supportingDocuments = model.getSupportingDocuments().entrySet().stream()
        		.collect(Collectors.toMap(e -> e.getKey(), e -> new Attachment(e.getValue(), model.getCourseCode(), model.getAssessmentCode(), model.getTaskId())));
        
   		this.competencies = model.getCompetencies().stream().map(competency -> new Competency(competency)).collect(Collectors.toList());
    }

	@Override
	public int compareTo(Task o) {
		return this.taskOrder - o.taskOrder;
	}
}
