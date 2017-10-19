drop MATERIALIZED VIEW dm.evaluation_by_submission;
drop MATERIALIZED VIEW dm.evaluation_by_evaluator;


CREATE MATERIALIZED VIEW dm.evaluation_by_status_and_evaluator AS
       SELECT * FROM dm.evaluation_by_id
       WHERE evaluator_id IS NOT NULL AND submission_id IS NOT NULL
	   AND student_id IS NOT NULL AND task_id IS NOT NULL 
	   AND attempt IS NOT NULL AND evaluation_id IS NOT NULL 
	   AND status IS NOT NULL
		 PRIMARY KEY (status, evaluator_id, submission_id, student_id, task_id, attempt, evaluation_id);

CREATE MATERIALIZED VIEW dm.evaluation_by_evaluator AS
       SELECT * FROM dm.evaluation_by_id
       WHERE evaluator_id IS NOT NULL AND submission_id IS NOT NULL
	   AND student_id IS NOT NULL AND task_id IS NOT NULL 
	   AND attempt IS NOT NULL AND evaluation_id IS NOT NULL 
       PRIMARY KEY (evaluator_id, submission_id, student_id, task_id, attempt, evaluation_id);

CREATE MATERIALIZED VIEW dm.evaluation_by_status_and_submission AS
       SELECT * FROM dm.evaluation_by_id
       WHERE evaluator_id IS NOT NULL AND submission_id IS NOT NULL
	   AND student_id IS NOT NULL AND task_id IS NOT NULL 
	   AND attempt IS NOT NULL AND evaluation_id IS NOT NULL 
	   AND status IS NOT NULL
       PRIMARY KEY (status, submission_id, student_id, task_id, attempt, evaluator_id, evaluation_id);