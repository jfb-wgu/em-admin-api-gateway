DROP INDEX dm.role_idx;
DROP INDEX dm.perm_idx;

DROP TABLE dm.submission_lock;
DROP MATERIALIZED VIEW dm.attachment_by_submission;
DROP TABLE dm.attachment_metadata;

DROP MATERIALIZED VIEW dm.submission_by_status_group_and_task ;

DROP MATERIALIZED VIEW dm.submission_by_status_and_task ;

DROP MATERIALIZED VIEW dm.submission_by_student_and_assessment ;

DROP MATERIALIZED VIEW dm.submission_by_student_and_task ;

DROP MATERIALIZED VIEW dm.submission_by_evaluator_and_task ;

DROP MATERIALIZED VIEW dm.submission_by_date_updated ;

alter table dm.submission_by_id DROP pidm;
alter table dm.submission_by_id DROP review_evaluation_id UUID;

CREATE MATERIALIZED VIEW dm.submission_by_status_group_and_task AS
SELECT * FROM dm.submission_by_id
WHERE task_id IS NOT NULL AND submission_id IS NOT NULL
AND student_id IS NOT NULL AND attempt IS NOT NULL
AND status_group IS NOT NULL
PRIMARY KEY (status_group, task_id, student_id, attempt, submission_id);

CREATE MATERIALIZED VIEW dm.submission_by_status_and_task AS
SELECT * FROM dm.submission_by_id
WHERE task_id IS NOT NULL AND submission_id IS NOT NULL
AND student_id IS NOT NULL AND attempt IS NOT NULL
AND status IS NOT NULL
PRIMARY KEY (status, task_id, student_id, attempt, submission_id);

CREATE MATERIALIZED VIEW dm.submission_by_student_and_assessment AS
SELECT * FROM dm.submission_by_id
WHERE task_id IS NOT NULL AND submission_id IS NOT NULL
AND assessment_id IS NOT NULL AND student_id IS NOT NULL AND attempt IS NOT NULL
PRIMARY KEY ((student_id, assessment_id), task_id, attempt, submission_id);

CREATE MATERIALIZED VIEW dm.submission_by_student_and_task AS
SELECT * FROM submission_by_id
WHERE student_id IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL
AND attempt IS NOT NULL
PRIMARY KEY (student_id, task_id, attempt, submission_id)
WITH CLUSTERING
  ORDER BY (attempt DESC);

CREATE MATERIALIZED VIEW dm.submission_by_evaluator_and_task AS
SELECT * FROM submission_by_id
WHERE student_id IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL
AND attempt IS NOT NULL AND evaluator_id IS NOT NULL
PRIMARY KEY (evaluator_id, task_id, attempt, student_id, submission_id)
WITH CLUSTERING
  ORDER BY (attempt DESC);

CREATE MATERIALIZED VIEW dm.submission_by_date_updated AS
SELECT * FROM submission_by_id
WHERE student_id IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL
AND attempt IS NOT NULL AND date_updated IS NOT NULL
PRIMARY KEY (task_id, date_updated, attempt, student_id, submission_id)
WITH CLUSTERING
  ORDER BY (attempt DESC);

DROP MATERIALIZED VIEW dm.task_by_id;

DROP MATERIALIZED VIEW dm.task_by_assessment;

alter table dm.task_by_course DROP date_published timestamp;
alter table dm.task_by_course DROP date_retired timestamp;
alter table dm.task_by_course DROP publication_status text;

CREATE MATERIALIZED VIEW dm.task_by_id AS
SELECT * FROM dm.task_by_course
WHERE task_id IS NOT NULL AND assessment_id IS NOT NULL AND course_id IS NOT NULL
PRIMARY KEY (task_id, assessment_id, course_id);

CREATE MATERIALIZED VIEW dm.task_by_assessment AS
SELECT * FROM dm.task_by_course
WHERE task_id IS NOT NULL AND assessment_id IS NOT NULL AND course_id IS NOT NULL
PRIMARY KEY (assessment_id, course_id, task_id);
