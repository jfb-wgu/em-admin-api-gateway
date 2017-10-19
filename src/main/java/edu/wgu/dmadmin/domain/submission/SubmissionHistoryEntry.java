package edu.wgu.dmadmin.domain.submission;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import edu.wgu.dmadmin.model.submission.SubmissionModel;
import edu.wgu.dmadmin.util.StatusUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SubmissionHistoryEntry implements Comparable<SubmissionHistoryEntry> {

	@JsonInclude(value = Include.NON_EMPTY)
	String status;

	@JsonInclude(value = Include.NON_EMPTY)
	Date dateUpdated;

	@JsonInclude(value = Include.NON_EMPTY)
	UUID submissionId;

	@JsonInclude(value = Include.NON_EMPTY)
	int attempt;

	@JsonGetter("studentStatus")
	public String getStudentStatus() {
		return StatusUtil.getStudentStatus(status, attempt);
	}

	@Override
	public int compareTo(SubmissionHistoryEntry o) {
		return this.attempt - o.attempt;
	}
	
	public SubmissionHistoryEntry(SubmissionModel submission) {
		this.setStatus(submission.getStatus());
		this.setAttempt(submission.getAttempt());
		this.setSubmissionId(submission.getSubmissionId());
		this.setDateUpdated(submission.getDateUpdated());
	}
}
