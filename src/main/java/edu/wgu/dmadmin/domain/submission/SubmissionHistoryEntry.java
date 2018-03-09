package edu.wgu.dmadmin.domain.submission;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import edu.wgu.dmadmin.util.StatusUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(value = Include.NON_EMPTY)
public class SubmissionHistoryEntry implements Comparable<SubmissionHistoryEntry> {

	String status;
	Date dateUpdated;
	UUID submissionId;
	int attempt;

	@JsonGetter("studentStatus")
	public String getStudentStatus() {
		return StatusUtil.getStudentStatus(this.status, this.attempt);
	}

	@Override
	public int compareTo(SubmissionHistoryEntry o) {
		return this.attempt - o.attempt;
	}
	
	public SubmissionHistoryEntry(Submission submission) {
		this.setStatus(submission.getStatus());
		this.setAttempt(submission.getAttempt());
		this.setSubmissionId(submission.getSubmissionId());
		this.setDateUpdated(submission.getDateUpdated());
	}
}
