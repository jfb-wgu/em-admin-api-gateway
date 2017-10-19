package edu.wgu.dmadmin.domain.submission;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import edu.wgu.dmadmin.domain.assessment.Comment;
import edu.wgu.dmadmin.model.assessment.EvaluationModel;
import edu.wgu.dmadmin.model.submission.SubmissionModel;
import lombok.Data;

@Data
public class WorkspaceSubmission implements Comparable<WorkspaceSubmission> {
    String status;
    Date dateSubmitted;
    
    UUID submissionId;
    int attempt;
    int studentFiles;
    List<Attachment> submittedFiles;
    String comments;
    List<Comment> evaluatorComments;
    
    public WorkspaceSubmission(SubmissionModel submission, EvaluationModel evaluation) {
    	this.setStatus(submission.getStatus());
    	this.setDateSubmitted(submission.getDateSubmitted());
    	this.setSubmissionId(submission.getSubmissionId());
    	this.setAttempt(submission.getAttempt());
    	this.setStudentFiles(submission.getAttachmentsNS().size());
    	this.setSubmittedFiles(submission.getAttachmentsNS().values().stream().map(attach -> new Attachment(attach, submission.getStudentId(), submission.getSubmissionId())).collect(Collectors.toList()));
    	this.setComments(submission.getComments());
    	this.evaluatorComments = evaluation.getScoreReport().getComments().values().stream().filter(c -> c.getAttempt() == submission.getAttempt()).map(c -> new Comment(c)).collect(Collectors.toList());
    }
    
    @Override
    public int compareTo(WorkspaceSubmission o) {
        return o.attempt - this.attempt;
    }
}
