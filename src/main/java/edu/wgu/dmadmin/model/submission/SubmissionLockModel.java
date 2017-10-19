package edu.wgu.dmadmin.model.submission;

import java.util.Date;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(keyspace = "dm", name = "submission_lock", readConsistency = "QUORUM", writeConsistency = "QUORUM")
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
		if (this.getDateLocked().equals(o.getDateLocked())) {
			return this.getLockId().compareTo(o.getLockId());
		} else {
			return this.getDateLocked().compareTo(o.getDateLocked());
		}
	}
}
