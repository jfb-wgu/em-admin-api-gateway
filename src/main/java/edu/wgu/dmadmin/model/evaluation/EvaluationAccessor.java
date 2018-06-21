package edu.wgu.dmadmin.model.evaluation;

import java.util.Date;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

@Accessor
public interface EvaluationAccessor {

    @Query("SELECT evaluator_id, evaluation_id, aspects, comments, date_completed, status FROM dm.evaluation_by_id WHERE status = 'COMPLETED' and date_completed >= ? allow filtering")
    Result<EvaluationModel> getEvaluations(Date dateCompleted);
}
