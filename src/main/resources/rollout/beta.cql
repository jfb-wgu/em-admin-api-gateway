truncate dm.evaluation_by_id;
truncate dm.submission_by_id;
truncate dm.status_log_by_assessment;
truncate dm.activity_log_by_user;
truncate dm.student_feedback;
truncate dream_machine_upload.attachment_metadata;

delete from dm.task_by_course where course_id = 6940002 and assessment_id = e0cb8717-4925-4318-85c1-799265efb52d and task_id = bc1682b7-02cd-4ed4-b2cd-59bc25a17f0c;
delete from dm.task_by_course where course_id = 7200002 and assessment_id = 4e599bce-6b72-425d-959c-be309aea1f2a and task_id = 53fb2086-e096-4857-bfba-de1a8afbd04b;
delete from dm.task_by_course where course_id = 6860001 and assessment_id = 09e3e608-5e64-4a35-8f26-0422a7f40092 and task_id = 80ee5294-0026-4183-94a9-d4e91ea5396a;
delete from dm.task_by_course where course_id = 6940001 and assessment_id = 2f9eacfb-3168-47fb-907d-3f248569cb0e and task_id = 9dbb4ee3-f544-460f-953e-56adf2f16fe4;
delete from dm.task_by_course where course_id = 7560005 and assessment_id = 7e3bae38-90ec-4aab-b1ce-dd4d8f26ca1e and task_id = 887f2402-494f-428f-8cc3-2c56366c5097;
delete from dm.task_by_course where course_id = 7560005 and assessment_id = 7e3bae38-90ec-4aab-b1ce-dd4d8f26ca1e and task_id = a9c5c633-6378-4ad5-99cd-26df46b4da15;
delete from dm.task_by_course where course_id = 7560005 and assessment_id = 7e3bae38-90ec-4aab-b1ce-dd4d8f26ca1e and task_id = 1d82fb04-4350-457b-9785-cd2b45549103;