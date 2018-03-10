package edu.wgu.dmadmin.model.submission;

import java.util.UUID;

import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface SubmissionAccessor {
	
    @Query("SELECT submission_id, task_id, attempt, student_id FROM dm.submission_by_student_and_task WHERE student_id = ? AND task_id = ? limit 1")
    SubmissionByStudentAndTaskModel getLastSubmissionByStudentAndTask(String studentId, UUID taskId);
}