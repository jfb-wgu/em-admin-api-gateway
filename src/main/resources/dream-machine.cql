DROP KEYSPACE "dm";
// for local installs
//CREATE KEYSPACE "dm" WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

// for full clusters
CREATE KEYSPACE dm WITH replication = {'class': 'NetworkTopologyStrategy', '27': '5'}  AND durable_writes = true;

CREATE TYPE dm.anchor (
	name text,
	description text,
	score int
);

CREATE TYPE dm.aspect (
	name text,
	description text,
	anchors list<frozen<anchor>>,
	passing_score int,
	aspect_order int,
	lr_url text
);

CREATE TYPE dm.rubric (
	name text,
	description text,
	aspects list<frozen<aspect>>
);

CREATE TYPE dm.attachment (
    title text,
    url text,
    mime_type text,
    size bigint,
		is_url boolean,
    is_task_document boolean
);

CREATE TYPE dm.competency (
	code text,
	name text,
	description text
);
    
CREATE TABLE dm.task_by_course (
	course_name text,
	course_code text,
	course_id bigint,
    assessment_name text,
    assessment_code text,
    assessment_date text,
    assessment_type text,
    assessment_order int,
    assessment_id UUID,
    task_name text,
    task_id UUID,
    task_order int,
    avg_time_required int,
    description text,
    competencies list<frozen<competency>>,
    supporting_documents map<text, frozen<attachment>>,
    introduction text,
    scenario text,
    notes text,
    requirements text,
    crd_notes text,
    rubric frozen<rubric>,
    aspect_count int,
    web_links list<text>,
    originality_minimum int,
    originality_warning int,
    date_created timestamp,
    date_updated timestamp,
    date_published timestamp,
    date_retired timestamp,
    publication_status text,
    PRIMARY KEY (course_id, assessment_id, task_id)
);

CREATE MATERIALIZED VIEW dm.task_by_id AS
       SELECT * FROM dm.task_by_course
       WHERE task_id IS NOT NULL AND assessment_id IS NOT NULL AND course_id IS NOT NULL
       PRIMARY KEY (task_id, assessment_id, course_id);
       
CREATE MATERIALIZED VIEW dm.task_by_assessment AS
       SELECT * FROM dm.task_by_course
       WHERE task_id IS NOT NULL AND assessment_id IS NOT NULL AND course_id IS NOT NULL
       PRIMARY KEY (assessment_id, course_id, task_id);
    
CREATE TYPE dm.comment (
	comment_id UUID,
	user_id text,
	first_name text,
	last_name text,
	date_created timestamp,
	date_updataed timestamp,
	comments text,
	attempt int,
	score int,
	type text
);

CREATE TYPE dm.score (
	name text,
	passing_score int,
	assigned_score int,
	previous_score int,
	comments map<UUID,frozen<comment>>
);

CREATE TYPE dm.score_report (
	name text,
	description text,
	scores map<text, frozen<score>>,
	comments map<UUID,frozen<comment>>,
	passed boolean
);

CREATE TABLE dm.evaluation_by_id (
	evaluation_id uuid,
	evaluator_id text,
	submission_id uuid,
	task_id uuid,
	student_id text,
	attempt int,
	status text,
	minutes_spent int,
	date_started timestamp,
	date_completed timestamp,
	date_updated timestamp,
	attachments list<frozen<attachment>>,
	score_report frozen<score_report>,
	evaluator_first_name text,
	evaluator_last_name text,
	PRIMARY KEY (evaluation_id, evaluator_id, submission_id, student_id, task_id, attempt)
);

CREATE MATERIALIZED VIEW dm.evaluation_by_evaluator AS
       SELECT * FROM dm.evaluation_by_id
       WHERE evaluator_id IS NOT NULL AND submission_id IS NOT NULL
	   AND student_id IS NOT NULL AND task_id IS NOT NULL 
	   AND attempt IS NOT NULL AND evaluation_id IS NOT NULL 
	   AND status IS NOT NULL
	   PRIMARY KEY (evaluator_id, status, submission_id, student_id, task_id, attempt, evaluation_id);

CREATE MATERIALIZED VIEW dm.evaluation_by_submission AS
       SELECT * FROM dm.evaluation_by_id
       WHERE evaluator_id IS NOT NULL AND submission_id IS NOT NULL
	   AND student_id IS NOT NULL AND task_id IS NOT NULL 
	   AND attempt IS NOT NULL AND evaluation_id IS NOT NULL 
       PRIMARY KEY (submission_id, evaluator_id, student_id, task_id, attempt, evaluation_id);

CREATE TABLE dm.eval_attach_metadata (
	title text,
    evaluation_id uuid,
    evaluator_id text,
    created_at timestamp,
    modified_at timestamp,
    upload_status text,
    uploaded_bytes bigint,
    size bigint,
    media_type text,
    path text,
    url text,
    is_url boolean,
    PRIMARY KEY (evaluator_id, evaluation_id, title));

CREATE TYPE dm.referral (
	type text,
	URL text,
	status text,
	created_by text,
	date_created timestamp,
	creator_comments text,
	reviewed_by text,
	date_reviewed timestamp,
	reviewer_comments text,
	return_to_creator boolean
);

CREATE TABLE dm.submission_by_id (
	submission_id uuid,
	student_id text,
    attempt int,
    task_id UUID,
    pidm bigint,
    task_name text,
    aspect_count int,
    course_code text,
    course_name text,
    assessment_id uuid,
    assessment_code text,
    assessment_name text,
    comments text,
    internal_comments map<UUID,frozen<comment>>,
    attachments map<text, frozen<attachment>>,
    status text,
    status_group text,
    date_created timestamp,
    date_submitted timestamp,
    date_estimated timestamp,
    date_started timestamp,
	date_updated timestamp,
	date_completed timestamp,
	evaluator_id text,
	evaluator_first_name text,
	evaluator_last_name text,
	evaluation_id UUID,
	referrals list<frozen<referral>>,
	previous_submission_id UUID,
	previous_evaluation_id UUID,
	review_evaluation_id UUID,
	PRIMARY KEY (submission_id, student_id, task_id, attempt)
);

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
       WITH CLUSTERING ORDER BY (attempt DESC);

CREATE MATERIALIZED VIEW dm.submission_by_evaluator_and_task AS
       SELECT * FROM submission_by_id
       WHERE student_id IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL
       AND attempt IS NOT NULL AND evaluator_id IS NOT NULL
       PRIMARY KEY (evaluator_id, task_id, attempt, student_id, submission_id)
       WITH CLUSTERING ORDER BY (attempt DESC);

CREATE MATERIALIZED VIEW dm.submission_by_date_updated AS
       SELECT * FROM submission_by_id
       WHERE student_id IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL
       AND attempt IS NOT NULL AND date_updated IS NOT NULL
       PRIMARY KEY (task_id, date_updated, attempt, student_id, submission_id)
       WITH CLUSTERING ORDER BY (attempt DESC);

CREATE TABLE dm.status_log_by_assessment (
	log_id uuid,
	activity_date timestamp,
	course_code text,
	assessment_id UUID,
	task_id UUID,
	submission_id uuid,
	user_id text,
	student_id text,
	old_status text,
	new_status text,
	PRIMARY KEY (assessment_id, activity_date, task_id, student_id, submission_id, log_id)
) WITH CLUSTERING ORDER BY (activity_date DESC);
      
CREATE MATERIALIZED VIEW dm.status_log_by_student AS
    SELECT * FROM dm.status_log_by_assessment
    WHERE student_id IS NOT NULL AND assessment_id IS NOT NULL AND task_id IS NOT NULL 
    AND submission_id IS NOT NULL AND activity_date IS NOT NULL AND log_id IS NOT NULL
	PRIMARY KEY (student_id, submission_id, activity_date, assessment_id, task_id, log_id)
	WITH CLUSTERING ORDER BY (activity_date DESC);

CREATE TABLE dm.activity_log_by_user (
	log_id uuid,
	activity_date timestamp,
	user_id text,
	method text,
	item_id uuid,
	PRIMARY KEY (user_id, activity_date, log_id)
) WITH CLUSTERING ORDER BY (activity_date DESC);

CREATE TABLE dm.mime_type (
	type_id UUID,
	extension text,
	application text,
	mime_type text,
	description text,
	PRIMARY KEY (extension, type_id)
);

CREATE TABLE dm.student_feedback (
	student_rating_id UUID,
	student_id text,
	rating int,
	comments text,
	assessment_code text,
	task_id UUID,
	attempt int,
	date_rated timestamp,
	PRIMARY KEY (student_id, student_rating_id)
);

CREATE TABLE dm.user_by_id (
    user_id text,
    first_name text,
    last_name text,
    last_login timestamp,
    teams set<UUID>,
    roles set<UUID>,
    permissions set<text>,
    tasks set<UUID>,
    landings set<text>,
    employee_id text,
	PRIMARY KEY (user_id)
);

CREATE INDEX role_idx ON dm.user_by_id ( roles );
CREATE INDEX perm_idx ON dm.user_by_id ( permissions );

CREATE MATERIALIZED VIEW dm.user_by_last_name AS
    SELECT * FROM dm.user_by_id
    WHERE user_id IS NOT NULL AND last_name IS NOT NULL
	PRIMARY KEY (last_name, user_id);

CREATE MATERIALIZED VIEW dm.user_by_first_name AS
    SELECT * FROM dm.user_by_id
    WHERE user_id IS NOT NULL AND first_name IS NOT NULL
	PRIMARY KEY (first_name, user_id);

CREATE TABLE dm.permission (
	permission_id UUID,
	permission text,
	permission_type text,
	permission_description text,
	landing text,
	date_created timestamp,
	date_updated timestamp,
	PRIMARY KEY (permission_id, permission)
);

CREATE TABLE dm.role (
	role_id UUID,
	role text,
	role_description text,
	permissions set<UUID>,
	date_created timestamp,
	date_updated timestamp,
	PRIMARY KEY (role_id, role)
);

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
