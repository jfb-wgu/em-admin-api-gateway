package edu.wgu.dmadmin.domain.assessment;

import edu.wgu.dmadmin.domain.submission.Attachment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import edu.wgu.dmadmin.model.assessment.EvaluationModel;

@Data
@AllArgsConstructor
public class Evaluation {
	UUID evaluationId;
	String evaluatorId;
	UUID submissionId;
	String status;
	int minutesSpent;
	UUID taskId;
	int attempt;
	String studentId;
	Date dateStarted;
	Date dateCompleted;
	Date dateUpdated;
	
	List<Attachment> attachments;
	ScoreReport scoreReport;
	String evaluatorFirstName;
	String evaluatorLastName;
	
	public Evaluation(EvaluationModel model) {
		this.evaluationId = model.getEvaluationId();
		this.evaluatorId = model.getEvaluatorId();
		this.submissionId = model.getSubmissionId();
		this.status = model.getStatus();
		this.minutesSpent = model.getMinutesSpent();
		this.dateStarted = model.getDateStarted();
		this.dateCompleted = model.getDateCompleted();
		this.attachments = model.getAttachments();
		this.dateUpdated = model.getDateUpdated();
		this.taskId = model.getTaskId();
		this.attempt = model.getAttempt();
		this.studentId = model.getStudentId();
		this.evaluatorFirstName = model.getEvaluatorFirstName();
		this.evaluatorLastName = model.getEvaluatorLastName();
		this.scoreReport = new ScoreReport(model.getScoreReport());
	}
}
