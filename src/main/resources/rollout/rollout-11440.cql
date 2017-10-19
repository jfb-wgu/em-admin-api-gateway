drop table dm.activity_log_by_user;
CREATE TABLE dm.activity_log_by_user (
    user_id text,
    activity_date timestamp,
    log_id uuid,
    item_id uuid,
    method text,
    PRIMARY KEY (user_id, activity_date, log_id));