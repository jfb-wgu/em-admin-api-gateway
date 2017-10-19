package edu.wgu.dmadmin.model.feedback;

import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface StudentFeedbackAccessor {
    @Query("SELECT * FROM dm.student_feedback WHERE student_id = ?")
    Result<StudentFeedbackModel> getAllFeedbackForStudent(String studentId);
}
