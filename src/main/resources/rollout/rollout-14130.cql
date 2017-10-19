alter table dm.task_by_course add date_published timestamp;
alter table dm.task_by_course add date_retired timestamp;
alter table dm.task_by_course add publication_status text;

alter table dm.submission_by_id add review_evaluation_id UUID;

alter table dm.submission_by_id add pidm bigint;

CREATE INDEX role_idx ON dm.user_by_id ( roles );
CREATE INDEX perm_idx ON dm.user_by_id ( permissions );

CREATE TABLE dm.attachment_metadata (
	title text,
    submission_id uuid,
    student_id text,
    created_at timestamp,
    modified_at timestamp,
    upload_status text, 
    uploaded_bytes bigint, 
    size bigint,
    media_type text,
    path text,
    url text,
    is_url boolean,
    is_task_document boolean,
    PRIMARY KEY (student_id, submission_id, title));

CREATE MATERIALIZED VIEW dm.attachment_by_submission AS
    SELECT * FROM attachment_metadata
    WHERE student_id IS NOT NULL AND submission_id IS NOT NULL AND title IS NOT NULL
    PRIMARY KEY ((submission_id), student_id, title);
    
CREATE TABLE dm.submission_lock (
	submission_id UUID,
	user_id text,
	date_locked timestamp,
	lock_id UUID,
	PRIMARY KEY (submission_id, user_id, lock_id));
