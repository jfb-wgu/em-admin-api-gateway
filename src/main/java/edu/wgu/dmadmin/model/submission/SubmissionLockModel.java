package edu.wgu.dmadmin.model.submission;

import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_lock", readConsistency = "LOCAL_QUORUM", writeConsistency = "LOCAL_QUORUM")
public class SubmissionLockModel implements Comparable<SubmissionLockModel> {

	@Column(name = "submission_id")
	@PartitionKey(0)
	UUID submissionId;

	@Column(name = "user_id")
	@PartitionKey(1)
	String userId;

	@Column(name = "date_locked")
	Date dateLocked;

	@Column(name = "lock_id")
	@PartitionKey(2)
	UUID lockId;

	public SubmissionLockModel(UUID submissionId, String userId, UUID lockId) {
		this.setSubmissionId(submissionId);
		this.setUserId(userId);
		this.setLockId(lockId);
	}

	@Override
	public int compareTo(SubmissionLockModel o) {
		return Comparator.comparing(SubmissionLockModel::getDateLocked)
				.thenComparing(SubmissionLockModel::getLockId)
				.compare(this, o);
	}
}
