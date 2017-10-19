package edu.wgu.dmadmin.domain.evaluator;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import edu.wgu.dmadmin.domain.assessment.ScoreReport;
import edu.wgu.dmadmin.domain.submission.Attachment;
import edu.wgu.dmadmin.domain.submission.WorkspaceSubmission;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.publish.TaskModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import lombok.Data;

@Data
public class EvaluatorWorkspace {
	private UUID submissionId;
	private String evaluatorId;
	private String courseName;
	private String courseCode;
	private String assessmentName;
	private String assessmentCode;
	private String taskName;
	private UUID taskId;
	private String studentId;
	private int attempt;
	private int originalityScore;
	private String comment;
	private String submissionStatus;
	private String evaluationStatus;
	private Date dateSubmitted;
	private Date dateEvaluationStarted;
	private Date dateEvaluationReleased;
	private String CRDNotes;
	private ScoreReport scoreReport;
	private List<Attachment> submittedFiles;
	private List<WorkspaceSubmission> previousSubmissions;
	
	public List<WorkspaceSubmission> getPreviousSubmissions() {
		Collections.sort(this.previousSubmissions);
		return previousSubmissions;
	}
	
	public EvaluatorWorkspace(SubmissionModel submission, TaskModel task, EvaluationModel evaluation) {
		this.setSubmissionId(submission.getSubmissionId());
		this.setEvaluatorId(submission.getEvaluatorId());
		this.setCourseName(submission.getCourseName());
		this.setCourseCode(submission.getCourseCode());
		this.setAssessmentName(submission.getAssessmentName());
		this.setAssessmentCode(submission.getAssessmentCode());
		this.setSubmissionStatus(submission.getStatus());
		this.setTaskName(task.getTaskName());
		this.setTaskId(task.getTaskId());
		this.setStudentId(submission.getStudentId());
		this.setAttempt(submission.getAttempt());
		this.setDateSubmitted(submission.getDateSubmitted());
		this.setComment(submission.getComments());
		this.setDateEvaluationStarted(evaluation.getDateStarted());
		this.setDateEvaluationReleased(evaluation.getDateCompleted());
		this.setEvaluationStatus(evaluation.getStatus());
		this.setCRDNotes(task.getCRDNotes());
		this.setScoreReport(new ScoreReport(evaluation.getScoreReport()));
		this.getScoreReport().setRubric(task.getRubric());
		this.setSubmittedFiles(submission.getAttachmentsNS().values().stream().map(attachment -> new Attachment(attachment, submission.getStudentId(), submission.getSubmissionId())).collect(Collectors.toList()));
	}
}
