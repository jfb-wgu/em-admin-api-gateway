package edu.wgu.dmadmin.factory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import edu.wgu.dmadmin.model.submission.SubmissionLockModel;
import edu.wgu.dmadmin.util.DateUtil;

import static org.junit.Assert.assertEquals;

public class SubmissionLockFactoryTest {
	
	SubmissionLockFactory factory = new SubmissionLockFactory();
	
	String userId = "user";
	UUID submissionId = UUID.randomUUID();
		
	@Test
	public void testGetSubmissionLock() {
		SubmissionLockModel lock = factory.getSubmissionLock(this.submissionId, this.userId);
		assertEquals(this.userId, lock.getUserId());
		assertEquals(this.submissionId, lock.getSubmissionId());
	}
	
	@Test
	public void testGetSubmissionLocks() throws InterruptedException {
		SubmissionLockModel lock1 = factory.getSubmissionLock(this.submissionId, this.userId);
		lock1.setDateLocked(DateUtil.getZonedNow());
		TimeUnit.SECONDS.sleep(1);
		SubmissionLockModel lock2 = factory.getSubmissionLock(UUID.randomUUID(), "second");
		lock2.setDateLocked(DateUtil.getZonedNow());
		
		List<SubmissionLockModel> locks = Arrays.asList(lock1, lock2);
		SubmissionLockModel winner = locks.stream().min(Comparator.naturalOrder()).get();
		
		assertEquals(this.userId, winner.getUserId());
		assertEquals(this.submissionId, winner.getSubmissionId());
	}
}
