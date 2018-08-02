package edu.wgu.dmadmin.model.evaluation;

import java.util.Date;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface EvaluationAccessor {

    @Query("SELECT evaluator_id, evaluation_id, aspects, comments, date_completed, status, submission_id FROM dm.evaluation_by_id WHERE status = 'COMPLETED' and date_completed >= ? allow filtering")
    Result<EvaluationModel> getEvaluations(Date dateCompleted);
    
    @Query("SELECT evaluator_id, evaluation_id, aspects, comments, date_completed, date_updated, status, submission_id FROM dm.evaluation_by_id WHERE status = 'COMPLETED' and date_completed >= ? and date_completed <= ? allow filtering")
    Result<EvaluationModel> getEvaluations(Date startDate, Date endDate);
    
    @Query("SELECT evaluator_id, evaluation_id, aspects, comments, date_completed, date_updated, status, submission_id FROM dm.evaluation_by_id WHERE status = 'REVIEWED' and date_updated >= ? and date_updated <= ? allow filtering")
    Result<EvaluationModel> getReviewedEvaluations(Date startDate, Date endDate);
}
