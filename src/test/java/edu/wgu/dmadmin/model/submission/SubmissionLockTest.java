package edu.wgu.dmadmin.model.submission;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SubmissionLockTest {
	
	UUID lockID1;
	UUID lockID2;
	UUID lockID3;
	UUID lockID4;
	UUID submissionID;
	
	Date locked1;
	Date locked2;
	Date locked3;
	Date locked4;
	
	List<SubmissionLockModel> locks = new ArrayList<SubmissionLockModel>();
		
	@Before
	public void initialize() {
		
		lockID1 = UUID.randomUUID();
		lockID2 = UUID.randomUUID();
		lockID3 = UUID.randomUUID();
		lockID4 = UUID.randomUUID();
		submissionID = UUID.randomUUID();
		
		Calendar twoDaysAgo = Calendar.getInstance();
		twoDaysAgo.add(Calendar.DATE, -2);
		locked1 = twoDaysAgo.getTime();
		
		twoDaysAgo.add(Calendar.HOUR, 8);
		locked2 = twoDaysAgo.getTime();
		
		twoDaysAgo.add(Calendar.HOUR, 8);
		locked3 = twoDaysAgo.getTime();
		
		twoDaysAgo.add(Calendar.HOUR, 8);
		locked4 = twoDaysAgo.getTime();
		
		SubmissionLockModel lock1 = new SubmissionLockModel(submissionID, "user1", locked4, lockID1);
		SubmissionLockModel lock2 = new SubmissionLockModel(submissionID, "user2", locked1, lockID2);
		SubmissionLockModel lock3 = new SubmissionLockModel(submissionID, "user3", locked3, lockID3);
		SubmissionLockModel lock4 = new SubmissionLockModel(submissionID, "user5", locked2, lockID4);
		
		locks.add(lock1);
		locks.add(lock2);
		locks.add(lock3);
		locks.add(lock4);
	}
	
	@Test
	public void testLockWinner() {
		SubmissionLockModel winner = locks.stream().min(Comparator.naturalOrder()).get();
		assertEquals(winner.getLockId(), lockID2);
		assertEquals(winner.getDateLocked(), locked1);
	}
}
