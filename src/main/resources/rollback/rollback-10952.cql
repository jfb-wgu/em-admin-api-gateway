drop materialized view dm.submission_by_status_and_task;
drop materialized VIEW dm.submission_by_status_group_and_task;
drop MATERIALIZED VIEW dm.submission_by_student_and_task;
drop MATERIALIZED VIEW dm.submission_by_student_and_assessment;
drop MATERIALIZED VIEW dm.submission_by_evaluator_and_task;
drop MATERIALIZED VIEW dm.submission_by_date_updated;
drop MATERIALIZED VIEW dm.task_by_id;
drop MATERIALIZED VIEW dm.task_by_assessment;

alter table dm.task_by_course drop supporting_documents;
alter table dm.submission_by_id drop attachments;
drop type if exists dm.attachment;

CREATE TYPE dm.attachment (
title text,
url text,
mime_type text,
size bigint
);

alter table dm.task_by_course add supporting_documents map<text, frozen<attachment>>;
alter table dm.submission_by_id add attachments map<text, frozen<attachment>>;

CREATE MATERIALIZED VIEW dm.submission_by_status_and_task AS
SELECT *
FROM dm.submission_by_id
WHERE task_id IS NOT NULL AND submission_id IS NOT NULL AND student_id IS NOT NULL AND attempt IS NOT NULL AND status IS NOT NULL
PRIMARY KEY (status, task_id, student_id, attempt, submission_id)
WITH CLUSTERING ORDER BY (task_id ASC, student_id ASC, attempt ASC, submission_id ASC)
AND bloom_filter_fp_chance = 0.01
AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
AND comment = ''
AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
AND crc_check_chance = 1.0
AND dclocal_read_repair_chance = 0.1
AND default_time_to_live = 0
AND gc_grace_seconds = 864000
AND max_index_interval = 2048
AND memtable_flush_period_in_ms = 0
AND min_index_interval = 128
AND read_repair_chance = 0.0
AND speculative_retry = '99PERCENTILE';

CREATE MATERIALIZED VIEW dm.submission_by_status_group_and_task AS
SELECT *
FROM dm.submission_by_id
WHERE task_id IS NOT NULL AND submission_id IS NOT NULL AND student_id IS NOT NULL AND attempt IS NOT NULL AND status_group IS NOT NULL
PRIMARY KEY (status_group, task_id, student_id, attempt, submission_id)
WITH CLUSTERING ORDER BY (task_id ASC, student_id ASC, attempt ASC, submission_id ASC)
AND bloom_filter_fp_chance = 0.01
AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
AND comment = ''
AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
AND crc_check_chance = 1.0
AND dclocal_read_repair_chance = 0.1
AND default_time_to_live = 0
AND gc_grace_seconds = 864000
AND max_index_interval = 2048
AND memtable_flush_period_in_ms = 0
AND min_index_interval = 128
AND read_repair_chance = 0.0
AND speculative_retry = '99PERCENTILE';

CREATE MATERIALIZED VIEW dm.submission_by_student_and_task AS
SELECT *
FROM dm.submission_by_id
WHERE student_id IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL AND attempt IS NOT NULL
PRIMARY KEY (student_id, task_id, attempt, submission_id)
WITH CLUSTERING ORDER BY (task_id ASC, attempt DESC, submission_id ASC)
AND bloom_filter_fp_chance = 0.01
AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
AND comment = ''
AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
AND crc_check_chance = 1.0
AND dclocal_read_repair_chance = 0.1
AND default_time_to_live = 0
AND gc_grace_seconds = 864000
AND max_index_interval = 2048
AND memtable_flush_period_in_ms = 0
AND min_index_interval = 128
AND read_repair_chance = 0.0
AND speculative_retry = '99PERCENTILE';

CREATE MATERIALIZED VIEW dm.submission_by_student_and_assessment AS
SELECT *
FROM dm.submission_by_id
WHERE task_id IS NOT NULL AND submission_id IS NOT NULL AND assessment_id IS NOT NULL AND student_id IS NOT NULL AND attempt IS NOT NULL
PRIMARY KEY ((student_id, assessment_id), task_id, attempt, submission_id)
WITH CLUSTERING ORDER BY (task_id ASC, attempt ASC, submission_id ASC)
AND bloom_filter_fp_chance = 0.01
AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
AND comment = ''
AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
AND crc_check_chance = 1.0
AND dclocal_read_repair_chance = 0.1
AND default_time_to_live = 0
AND gc_grace_seconds = 864000
AND max_index_interval = 2048
AND memtable_flush_period_in_ms = 0
AND min_index_interval = 128
AND read_repair_chance = 0.0
AND speculative_retry = '99PERCENTILE';

CREATE MATERIALIZED VIEW dm.submission_by_evaluator_and_task AS
SELECT *
FROM dm.submission_by_id
WHERE student_id IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL AND attempt IS NOT NULL AND evaluator_id IS NOT NULL
PRIMARY KEY (evaluator_id, task_id, attempt, student_id, submission_id)
WITH CLUSTERING ORDER BY (task_id ASC, attempt DESC, student_id ASC, submission_id ASC)
AND bloom_filter_fp_chance = 0.01
AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
AND comment = ''
AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
AND crc_check_chance = 1.0
AND dclocal_read_repair_chance = 0.1
AND default_time_to_live = 0
AND gc_grace_seconds = 864000
AND max_index_interval = 2048
AND memtable_flush_period_in_ms = 0
AND min_index_interval = 128
AND read_repair_chance = 0.0
AND speculative_retry = '99PERCENTILE';

CREATE MATERIALIZED VIEW dm.submission_by_date_updated AS
SELECT *
FROM dm.submission_by_id
WHERE student_id IS NOT NULL AND task_id IS NOT NULL AND submission_id IS NOT NULL AND attempt IS NOT NULL AND date_updated IS NOT NULL
PRIMARY KEY (task_id, date_updated, attempt, student_id, submission_id)
WITH CLUSTERING ORDER BY (date_updated ASC, attempt DESC, student_id ASC, submission_id ASC)
AND bloom_filter_fp_chance = 0.01
AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
AND comment = ''
AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
AND crc_check_chance = 1.0
AND dclocal_read_repair_chance = 0.1
AND default_time_to_live = 0
AND gc_grace_seconds = 864000
AND max_index_interval = 2048
AND memtable_flush_period_in_ms = 0
AND min_index_interval = 128
AND read_repair_chance = 0.0
AND speculative_retry = '99PERCENTILE';

CREATE MATERIALIZED VIEW dm.task_by_assessment AS
SELECT *
FROM dm.task_by_course
WHERE task_id IS NOT NULL AND assessment_id IS NOT NULL AND course_id IS NOT NULL
PRIMARY KEY (assessment_id, course_id, task_id)
WITH CLUSTERING ORDER BY (course_id ASC, task_id ASC)
AND bloom_filter_fp_chance = 0.01
AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
AND comment = ''
AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
AND crc_check_chance = 1.0
AND dclocal_read_repair_chance = 0.1
AND default_time_to_live = 0
AND gc_grace_seconds = 864000
AND max_index_interval = 2048
AND memtable_flush_period_in_ms = 0
AND min_index_interval = 128
AND read_repair_chance = 0.0
AND speculative_retry = '99PERCENTILE';

CREATE MATERIALIZED VIEW dm.task_by_id AS
SELECT *
FROM dm.task_by_course
WHERE task_id IS NOT NULL AND assessment_id IS NOT NULL AND course_id IS NOT NULL
PRIMARY KEY (task_id, assessment_id, course_id)
WITH CLUSTERING ORDER BY (assessment_id ASC, course_id ASC)
AND bloom_filter_fp_chance = 0.01
AND caching = {'keys': 'ALL', 'rows_per_partition': 'NONE'}
AND comment = ''
AND compaction = {'class': 'org.apache.cassandra.db.compaction.SizeTieredCompactionStrategy', 'max_threshold': '32', 'min_threshold': '4'}
AND compression = {'chunk_length_in_kb': '64', 'class': 'org.apache.cassandra.io.compress.LZ4Compressor'}
AND crc_check_chance = 1.0
AND dclocal_read_repair_chance = 0.1
AND default_time_to_live = 0
AND gc_grace_seconds = 864000
AND max_index_interval = 2048
AND memtable_flush_period_in_ms = 0
AND min_index_interval = 128
AND read_repair_chance = 0.0
AND speculative_retry = '99PERCENTILE';