DROP MATERIALIZED VIEW dm.submission_by_status; 
DROP MATERIALIZED VIEW dm.submission_by_student;
DROP MATERIALIZED VIEW dm.submission_by_student_assessment;
DROP MATERIALIZED VIEW dm.pending_submission_by_assessment;
DROP MATERIALIZED VIEW dm.pending_submission_by_course;
DROP MATERIALIZED VIEW dm.task_by_assessment;
DROP MATERIALIZED VIEW dm.task_by_course;

DROP TABLE dm.student_feedback;

alter table dm.submission_by_evaluator drop status_group;
alter table dm.submission_by_id drop status_group;
alter table dm.pending_submission drop status_group;
alter table dm.task_by_id drop aspect_count;
alter table dm.task_by_id drop date_created;
alter table dm.task_by_id drop date_updated;

CREATE MATERIALIZED VIEW dm.submission_by_status AS
    SELECT *
    FROM dm.submission_by_evaluator
    WHERE status IS NOT NULL AND date_submitted IS NOT NULL AND submission_id IS NOT NULL AND evaluator_id IS NOT NULL
    PRIMARY KEY (status, date_submitted, evaluator_id, submission_id)
    WITH CLUSTERING ORDER BY (date_submitted ASC, evaluator_id ASC, submission_id ASC);
    
CREATE MATERIALIZED VIEW dm.submission_by_student AS
    SELECT *
    FROM dm.submission_by_id
    WHERE student_id IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL AND attempt IS NOT NULL
    PRIMARY KEY (student_id, task_id, attempt, submission_id)
    WITH CLUSTERING ORDER BY (task_id ASC, attempt ASC, submission_id ASC);

CREATE MATERIALIZED VIEW dm.submission_by_student_assessment AS
    SELECT *
    FROM dm.submission_by_id
    WHERE task_id IS NOT NULL AND submission_id IS NOT NULL AND assessment_id IS NOT NULL AND student_id IS NOT NULL AND attempt IS NOT NULL
    PRIMARY KEY ((student_id, assessment_id), task_id, submission_id, attempt)
    WITH CLUSTERING ORDER BY (task_id ASC, submission_id ASC, attempt ASC);
    
CREATE MATERIALIZED VIEW dm.pending_submission_by_assessment AS
    SELECT *
    FROM dm.pending_submission
    WHERE course_code IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL AND assessment_code IS NOT NULL AND date_submitted IS NOT NULL
    PRIMARY KEY (assessment_code, task_id, course_code, date_submitted, submission_id)
    WITH CLUSTERING ORDER BY (task_id ASC, course_code ASC, date_submitted ASC, submission_id ASC);

CREATE MATERIALIZED VIEW dm.pending_submission_by_course AS
    SELECT *
    FROM dm.pending_submission
    WHERE course_code IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL AND assessment_code IS NOT NULL AND date_submitted IS NOT NULL
    PRIMARY KEY (course_code, task_id, assessment_code, date_submitted, submission_id)
    WITH CLUSTERING ORDER BY (task_id ASC, assessment_code ASC, date_submitted ASC, submission_id ASC);

CREATE MATERIALIZED VIEW dm.task_by_assessment AS
    SELECT *
    FROM dm.task_by_id
    WHERE task_id IS NOT NULL AND assessment_id IS NOT NULL AND course_id IS NOT NULL
    PRIMARY KEY (assessment_id, course_id, task_id)
    WITH CLUSTERING ORDER BY (course_id ASC, task_id ASC);
    
CREATE MATERIALIZED VIEW dm.task_by_course AS
    SELECT *
    FROM dm.task_by_id
    WHERE task_id IS NOT NULL AND assessment_id IS NOT NULL AND course_id IS NOT NULL
    PRIMARY KEY (course_id, assessment_id, task_id)
    WITH CLUSTERING ORDER BY (assessment_id ASC, task_id ASC);