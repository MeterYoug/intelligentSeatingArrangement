-- 慧排座脱敏演示数据。
-- 前置条件：已执行基础 SQL、排座 SQL、成绩扩展 SQL 和班级科目扩展 SQL。
-- 本脚本只写入 900000 段虚构数据，不删除或修改既有业务数据，可重复执行。

start transaction;

insert ignore into seat_class
  (class_id, class_name, grade_name, school_stage, grade_code, subject_snapshot, school_year, semester,
   teacher_id, dept_id, status, del_flag, create_by, create_time, remark)
values
  (900001, '演示四年级一班', '四年级', 'PRIMARY', 'PRIMARY_4',
   json_array('语文', '数学', '英语', '科学'), '2026-2027', '1', 1, 100, '0', '0', 'demo', now(), '脱敏演示班级');

insert ignore into seat_classroom
  (classroom_id, class_id, classroom_name, row_count, col_count, platform_position, aisle_after_cols,
   is_default, status, del_flag, create_by, create_time, remark)
values
  (900002, 900001, '演示教室（2行4列）', 2, 4, 'FRONT', '', '1', '0', '0', 'demo', now(), '脱敏演示布局');

insert ignore into seat_position
  (seat_id, classroom_id, row_index, col_index, seat_code, seat_type, is_available, status, create_by, create_time)
values
  (900101, 900002, 1, 1, 'R1C1', '0', '1', '0', 'demo', now()),
  (900102, 900002, 1, 2, 'R1C2', '0', '1', '0', 'demo', now()),
  (900103, 900002, 1, 3, 'R1C3', '0', '1', '0', 'demo', now()),
  (900104, 900002, 1, 4, 'R1C4', '0', '1', '0', 'demo', now()),
  (900105, 900002, 2, 1, 'R2C1', '0', '1', '0', 'demo', now()),
  (900106, 900002, 2, 2, 'R2C2', '0', '1', '0', 'demo', now()),
  (900107, 900002, 2, 3, 'R2C3', '0', '1', '0', 'demo', now()),
  (900108, 900002, 2, 4, 'R2C4', '0', '1', '0', 'demo', now());

-- 姓名、学号和成绩均为虚构值，不可关联真实个人。
insert ignore into seat_student
  (student_id, class_id, student_no, student_name, gender, height_cm, vision_level, score_level,
   discipline_level, special_need, sort_no, status, del_flag, create_by, create_time, remark)
values
  (900011, 900001, 'DEMO-01', '演示学生01', '0', 138.00, '2', 'A', '0', '前排', 1, '0', '0', 'demo', now(), '虚构数据'),
  (900012, 900001, 'DEMO-02', '演示学生02', '1', 142.00, '0', 'B', '1', '', 2, '0', '0', 'demo', now(), '虚构数据'),
  (900013, 900001, 'DEMO-03', '演示学生03', '0', 150.00, '1', 'C', '0', '', 3, '0', '0', 'demo', now(), '虚构数据'),
  (900014, 900001, 'DEMO-04', '演示学生04', '1', 145.00, '0', 'D', '2', '', 4, '0', '0', 'demo', now(), '虚构数据'),
  (900015, 900001, 'DEMO-05', '演示学生05', '0', 155.00, '0', 'A', '0', '', 5, '0', '0', 'demo', now(), '虚构数据'),
  (900016, 900001, 'DEMO-06', '演示学生06', '1', 148.00, '3', 'B', '1', '前排', 6, '0', '0', 'demo', now(), '虚构数据');

insert into seat_rule
  (class_id, rule_name, rule_category, rule_code, rule_weight, rule_config, enabled, status, del_flag,
   create_by, create_time, remark)
select 900001, '近视学生靠前', 'SOFT', 'VISION_FRONT', 100, json_object(), '1', '0', '0', 'demo', now(), '脱敏演示规则'
where not exists (select 1 from seat_rule where class_id = 900001 and rule_code = 'VISION_FRONT' and del_flag = '0');

insert into seat_rule
  (class_id, rule_name, rule_category, rule_code, rule_weight, rule_config, enabled, status, del_flag,
   create_by, create_time, remark)
select 900001, '高个学生靠后', 'SOFT', 'HEIGHT_BACK', 100, json_object(), '1', '0', '0', 'demo', now(), '脱敏演示规则'
where not exists (select 1 from seat_rule where class_id = 900001 and rule_code = 'HEIGHT_BACK' and del_flag = '0');

insert into seat_rule
  (class_id, rule_name, rule_category, rule_code, rule_weight, rule_config, enabled, status, del_flag,
   create_by, create_time, remark)
select 900001, '男女搭配均衡', 'SOFT', 'GENDER_BALANCE', 100, json_object(), '1', '0', '0', 'demo', now(), '脱敏演示规则'
where not exists (select 1 from seat_rule where class_id = 900001 and rule_code = 'GENDER_BALANCE' and del_flag = '0');

insert into seat_rule
  (class_id, rule_name, rule_category, rule_code, rule_weight, rule_config, enabled, status, del_flag,
   create_by, create_time, remark)
select 900001, '成绩强弱均衡', 'SOFT', 'SCORE_BALANCE', 100, json_object(), '1', '0', '0', 'demo', now(), '脱敏演示规则'
where not exists (select 1 from seat_rule where class_id = 900001 and rule_code = 'SCORE_BALANCE' and del_flag = '0');

insert ignore into seat_student_relation
  (relation_id, class_id, student_id, related_id, relation_type, relation_weight, enabled, create_by, create_time, remark)
values
  (900301, 900001, 900011, 900012, 'NOT_DESKMATE', 100, '1', 'demo', now(), '脱敏演示关系');

insert ignore into seat_exam
  (exam_id, class_id, exam_name, exam_date, school_stage_snapshot, grade_code_snapshot, grade_name_snapshot,
   subject_snapshot, is_current, status, del_flag, create_by, create_time, remark)
values
  (900003, 900001, '演示期中考试', '2026-06-15', 'PRIMARY', 'PRIMARY_4', '四年级',
   json_array('语文', '数学', '英语', '科学'), '1', '0', '0', 'demo', now(), '脱敏演示考试');

insert ignore into seat_student_score
  (score_id, exam_id, class_id, student_id, student_no, student_name_snapshot, subject_scores, total_score,
   class_rank, score_level, del_flag, create_by, create_time, remark)
values
  (900201, 900003, 900001, 900011, 'DEMO-01', '演示学生01', json_object('语文', 94, '数学', 96, '英语', 92, '科学', 95), 377, 1, 'A', '0', 'demo', now(), '虚构成绩'),
  (900202, 900003, 900001, 900012, 'DEMO-02', '演示学生02', json_object('语文', 90, '数学', 88, '英语', 91, '科学', 89), 358, 2, 'A', '0', 'demo', now(), '虚构成绩'),
  (900203, 900003, 900001, 900013, 'DEMO-03', '演示学生03', json_object('语文', 85, '数学', 87, '英语', 84, '科学', 86), 342, 3, 'B', '0', 'demo', now(), '虚构成绩'),
  (900204, 900003, 900001, 900014, 'DEMO-04', '演示学生04', json_object('语文', 80, '数学', 78, '英语', 82, '科学', 79), 319, 4, 'B', '0', 'demo', now(), '虚构成绩'),
  (900205, 900003, 900001, 900015, 'DEMO-05', '演示学生05', json_object('语文', 76, '数学', 74, '英语', 77, '科学', 75), 302, 5, 'C', '0', 'demo', now(), '虚构成绩'),
  (900206, 900003, 900001, 900016, 'DEMO-06', '演示学生06', json_object('语文', 70, '数学', 72, '英语', 69, '科学', 71), 282, 6, 'C', '0', 'demo', now(), '虚构成绩');

commit;
