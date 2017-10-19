alter table dm.submission_by_evaluator add status_group text;
alter table dm.submission_by_id add status_group text;
alter table dm.pending_submission add status_group text;
alter table dm.task_by_id add aspect_count int;
alter table dm.task_by_id add date_created timestamp;
alter table dm.task_by_id add date_updated timestamp;
drop materialized view dm.submission_by_student;
CREATE MATERIALIZED VIEW dm.submission_by_student AS
    SELECT *
    FROM dm.submission_by_id
    WHERE student_id IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL AND attempt IS NOT NULL
    PRIMARY KEY (student_id, task_id, attempt, submission_id)
    WITH CLUSTERING ORDER BY (task_id ASC, attempt DESC, submission_id ASC);
CREATE TABLE dm.student_feedback (
    student_id text,
    student_rating_id uuid,
    assessment_code text,
    attempt int,
    comments text,
    date_rated timestamp,
    rating int,
    task_id uuid,
    PRIMARY KEY (student_id, student_rating_id)
) WITH CLUSTERING ORDER BY (student_rating_id ASC);