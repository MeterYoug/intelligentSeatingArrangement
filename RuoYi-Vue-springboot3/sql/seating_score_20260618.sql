-- ----------------------------
-- 智能排座成绩管理扩展
-- 需要先执行 seating_20260615.sql 和 seating_menu_20260615.sql
-- 本脚本不删除已有业务数据
-- ----------------------------

alter table seat_class
  add column school_stage varchar(16) default '' comment '学段：PRIMARY小学 JUNIOR初中 SENIOR高中' after grade_name,
  add column grade_code varchar(32) default '' comment '年级编码，例如 PRIMARY_4' after school_stage;

create table if not exists seat_subject (
  subject_id    bigint(20)   not null auto_increment comment '科目ID',
  school_stage  varchar(16)  not null                comment '学段：PRIMARY/JUNIOR/SENIOR',
  grade_code    varchar(32)  not null                comment '年级编码',
  subject_code  varchar(32)  not null                comment '科目编码',
  subject_name  varchar(32)  not null                comment '科目名称',
  sort_order    int(11)      default 0               comment '排序',
  is_required   char(1)      default '1'             comment '是否必考（0否 1是）',
  status        char(1)      default '0'             comment '状态（0正常 1停用）',
  remark        varchar(500) default null            comment '备注',
  primary key (subject_id),
  unique key uk_seat_subject_grade_code (grade_code, subject_code),
  key idx_seat_subject_grade (school_stage, grade_code)
) engine=innodb comment='成绩科目模板表';

create table if not exists seat_exam (
  exam_id                bigint(20)   not null auto_increment comment '考试ID',
  class_id               bigint(20)   not null                comment '班级ID',
  exam_name              varchar(64)  not null                comment '考试名称',
  exam_date              date                                  comment '考试日期',
  school_stage_snapshot  varchar(16)  default ''              comment '学段快照',
  grade_code_snapshot    varchar(32)  default ''              comment '年级编码快照',
  grade_name_snapshot    varchar(64)  default ''              comment '年级名称快照',
  subject_snapshot       json                                  comment '科目快照JSON',
  is_current             char(1)      default '0'             comment '是否当前考试（0否 1是）',
  status                 char(1)      default '0'             comment '状态（0正常 1停用）',
  del_flag               char(1)      default '0'             comment '删除标志（0存在 2删除）',
  create_by              varchar(64)  default ''              comment '创建者',
  create_time            datetime                             comment '创建时间',
  update_by              varchar(64)  default ''              comment '更新者',
  update_time            datetime                             comment '更新时间',
  remark                 varchar(500) default null            comment '备注',
  primary key (exam_id),
  key idx_seat_exam_class (class_id),
  key idx_seat_exam_current (class_id, is_current)
) engine=innodb comment='学生成绩考试批次表';

create table if not exists seat_student_score (
  score_id               bigint(20)    not null auto_increment comment '成绩ID',
  exam_id                bigint(20)    not null                comment '考试ID',
  class_id               bigint(20)    not null                comment '班级ID',
  student_id             bigint(20)    not null                comment '学生ID',
  student_no             varchar(32)   default ''              comment '学号快照',
  student_name_snapshot  varchar(64)   default ''              comment '学生姓名快照',
  subject_scores         json                                  comment '各科成绩JSON',
  total_score            decimal(8,2)  default null            comment '总分',
  class_rank             int(11)       default null            comment '班级排名',
  score_level            char(1)       default null            comment '成绩等级（A/B/C/D）',
  del_flag               char(1)       default '0'             comment '删除标志（0存在 2删除）',
  create_by              varchar(64)   default ''              comment '创建者',
  create_time            datetime                              comment '创建时间',
  update_by              varchar(64)   default ''              comment '更新者',
  update_time            datetime                              comment '更新时间',
  remark                 varchar(500)  default null            comment '备注',
  primary key (score_id),
  unique key uk_seat_student_score_exam_student (exam_id, student_id),
  key idx_seat_student_score_exam (exam_id),
  key idx_seat_student_score_class (class_id),
  key idx_seat_student_score_student (student_id)
) engine=innodb comment='学生成绩表';

insert into seat_subject (school_stage, grade_code, subject_code, subject_name, sort_order)
select 'PRIMARY', 'PRIMARY_1', 'CHINESE', '语文', 1 where not exists (select 1 from seat_subject where grade_code = 'PRIMARY_1' and subject_code = 'CHINESE');
insert into seat_subject (school_stage, grade_code, subject_code, subject_name, sort_order)
select 'PRIMARY', 'PRIMARY_1', 'MATH', '数学', 2 where not exists (select 1 from seat_subject where grade_code = 'PRIMARY_1' and subject_code = 'MATH');
insert into seat_subject (school_stage, grade_code, subject_code, subject_name, sort_order)
select 'PRIMARY', 'PRIMARY_1', 'ENGLISH', '英语', 3 where not exists (select 1 from seat_subject where grade_code = 'PRIMARY_1' and subject_code = 'ENGLISH');
insert into seat_subject (school_stage, grade_code, subject_code, subject_name, sort_order)
select 'PRIMARY', 'PRIMARY_1', 'SCIENCE', '科学', 4 where not exists (select 1 from seat_subject where grade_code = 'PRIMARY_1' and subject_code = 'SCIENCE');

insert into seat_subject (school_stage, grade_code, subject_code, subject_name, sort_order)
select s.school_stage, s.grade_code, s.subject_code, s.subject_name, s.sort_order
from (
  select 'PRIMARY' school_stage, 'PRIMARY_2' grade_code, 'CHINESE' subject_code, '语文' subject_name, 1 sort_order union all
  select 'PRIMARY', 'PRIMARY_2', 'MATH', '数学', 2 union all
  select 'PRIMARY', 'PRIMARY_2', 'ENGLISH', '英语', 3 union all
  select 'PRIMARY', 'PRIMARY_2', 'SCIENCE', '科学', 4 union all
  select 'PRIMARY', 'PRIMARY_3', 'CHINESE', '语文', 1 union all
  select 'PRIMARY', 'PRIMARY_3', 'MATH', '数学', 2 union all
  select 'PRIMARY', 'PRIMARY_3', 'ENGLISH', '英语', 3 union all
  select 'PRIMARY', 'PRIMARY_3', 'SCIENCE', '科学', 4 union all
  select 'PRIMARY', 'PRIMARY_4', 'CHINESE', '语文', 1 union all
  select 'PRIMARY', 'PRIMARY_4', 'MATH', '数学', 2 union all
  select 'PRIMARY', 'PRIMARY_4', 'ENGLISH', '英语', 3 union all
  select 'PRIMARY', 'PRIMARY_4', 'SCIENCE', '科学', 4 union all
  select 'PRIMARY', 'PRIMARY_5', 'CHINESE', '语文', 1 union all
  select 'PRIMARY', 'PRIMARY_5', 'MATH', '数学', 2 union all
  select 'PRIMARY', 'PRIMARY_5', 'ENGLISH', '英语', 3 union all
  select 'PRIMARY', 'PRIMARY_5', 'SCIENCE', '科学', 4 union all
  select 'PRIMARY', 'PRIMARY_6', 'CHINESE', '语文', 1 union all
  select 'PRIMARY', 'PRIMARY_6', 'MATH', '数学', 2 union all
  select 'PRIMARY', 'PRIMARY_6', 'ENGLISH', '英语', 3 union all
  select 'PRIMARY', 'PRIMARY_6', 'SCIENCE', '科学', 4 union all
  select 'JUNIOR', 'JUNIOR_1', 'CHINESE', '语文', 1 union all
  select 'JUNIOR', 'JUNIOR_1', 'MATH', '数学', 2 union all
  select 'JUNIOR', 'JUNIOR_1', 'ENGLISH', '英语', 3 union all
  select 'JUNIOR', 'JUNIOR_1', 'POLITICS', '道德与法治', 4 union all
  select 'JUNIOR', 'JUNIOR_1', 'HISTORY', '历史', 5 union all
  select 'JUNIOR', 'JUNIOR_1', 'GEOGRAPHY', '地理', 6 union all
  select 'JUNIOR', 'JUNIOR_1', 'BIOLOGY', '生物', 7 union all
  select 'JUNIOR', 'JUNIOR_2', 'CHINESE', '语文', 1 union all
  select 'JUNIOR', 'JUNIOR_2', 'MATH', '数学', 2 union all
  select 'JUNIOR', 'JUNIOR_2', 'ENGLISH', '英语', 3 union all
  select 'JUNIOR', 'JUNIOR_2', 'POLITICS', '道德与法治', 4 union all
  select 'JUNIOR', 'JUNIOR_2', 'HISTORY', '历史', 5 union all
  select 'JUNIOR', 'JUNIOR_2', 'GEOGRAPHY', '地理', 6 union all
  select 'JUNIOR', 'JUNIOR_2', 'BIOLOGY', '生物', 7 union all
  select 'JUNIOR', 'JUNIOR_2', 'PHYSICS', '物理', 8 union all
  select 'JUNIOR', 'JUNIOR_3', 'CHINESE', '语文', 1 union all
  select 'JUNIOR', 'JUNIOR_3', 'MATH', '数学', 2 union all
  select 'JUNIOR', 'JUNIOR_3', 'ENGLISH', '英语', 3 union all
  select 'JUNIOR', 'JUNIOR_3', 'POLITICS', '道德与法治', 4 union all
  select 'JUNIOR', 'JUNIOR_3', 'HISTORY', '历史', 5 union all
  select 'JUNIOR', 'JUNIOR_3', 'PHYSICS', '物理', 6 union all
  select 'JUNIOR', 'JUNIOR_3', 'CHEMISTRY', '化学', 7 union all
  select 'SENIOR', 'SENIOR_1', 'CHINESE', '语文', 1 union all
  select 'SENIOR', 'SENIOR_1', 'MATH', '数学', 2 union all
  select 'SENIOR', 'SENIOR_1', 'ENGLISH', '英语', 3 union all
  select 'SENIOR', 'SENIOR_1', 'PHYSICS', '物理', 4 union all
  select 'SENIOR', 'SENIOR_1', 'CHEMISTRY', '化学', 5 union all
  select 'SENIOR', 'SENIOR_1', 'BIOLOGY', '生物', 6 union all
  select 'SENIOR', 'SENIOR_1', 'POLITICS', '政治', 7 union all
  select 'SENIOR', 'SENIOR_1', 'HISTORY', '历史', 8 union all
  select 'SENIOR', 'SENIOR_1', 'GEOGRAPHY', '地理', 9 union all
  select 'SENIOR', 'SENIOR_2', 'CHINESE', '语文', 1 union all
  select 'SENIOR', 'SENIOR_2', 'MATH', '数学', 2 union all
  select 'SENIOR', 'SENIOR_2', 'ENGLISH', '英语', 3 union all
  select 'SENIOR', 'SENIOR_2', 'PHYSICS', '物理', 4 union all
  select 'SENIOR', 'SENIOR_2', 'CHEMISTRY', '化学', 5 union all
  select 'SENIOR', 'SENIOR_2', 'BIOLOGY', '生物', 6 union all
  select 'SENIOR', 'SENIOR_2', 'POLITICS', '政治', 7 union all
  select 'SENIOR', 'SENIOR_2', 'HISTORY', '历史', 8 union all
  select 'SENIOR', 'SENIOR_2', 'GEOGRAPHY', '地理', 9 union all
  select 'SENIOR', 'SENIOR_3', 'CHINESE', '语文', 1 union all
  select 'SENIOR', 'SENIOR_3', 'MATH', '数学', 2 union all
  select 'SENIOR', 'SENIOR_3', 'ENGLISH', '英语', 3 union all
  select 'SENIOR', 'SENIOR_3', 'PHYSICS', '物理', 4 union all
  select 'SENIOR', 'SENIOR_3', 'CHEMISTRY', '化学', 5 union all
  select 'SENIOR', 'SENIOR_3', 'BIOLOGY', '生物', 6 union all
  select 'SENIOR', 'SENIOR_3', 'POLITICS', '政治', 7 union all
  select 'SENIOR', 'SENIOR_3', 'HISTORY', '历史', 8 union all
  select 'SENIOR', 'SENIOR_3', 'GEOGRAPHY', '地理', 9
) s
where not exists (
  select 1 from seat_subject t where t.grade_code = s.grade_code and t.subject_code = s.subject_code
);

insert into sys_menu
select 2000006, '成绩管理', 2000000, 3, 'score', 'seating/score/index', '', '', 1, 0, 'C', '0', '0', 'seating:studentScore:list', 'education', 'admin', sysdate(), '', null, '成绩管理菜单'
where not exists (select 1 from sys_menu where menu_id = 2000006);

update sys_menu set order_num = 4 where menu_id = 2000003;
update sys_menu set order_num = 5 where menu_id = 2000004;
update sys_menu set order_num = 6 where menu_id = 2000005;

insert into sys_menu
select 2000060, '考试查询', 2000006, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:exam:list', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000060);
insert into sys_menu
select 2000061, '考试详情', 2000006, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:exam:query', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000061);
insert into sys_menu
select 2000062, '考试新增', 2000006, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:exam:add', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000062);
insert into sys_menu
select 2000063, '考试修改', 2000006, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:exam:edit', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000063);
insert into sys_menu
select 2000064, '考试删除', 2000006, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:exam:remove', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000064);

insert into sys_menu
select 2000070, '成绩查询', 2000006, 10, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:studentScore:query', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000070);
insert into sys_menu
select 2000071, '成绩列表', 2000006, 11, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:studentScore:list', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000071);
insert into sys_menu
select 2000072, '成绩新增', 2000006, 12, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:studentScore:add', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000072);
insert into sys_menu
select 2000073, '成绩修改', 2000006, 13, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:studentScore:edit', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000073);
insert into sys_menu
select 2000074, '成绩删除', 2000006, 14, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:studentScore:remove', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000074);
insert into sys_menu
select 2000075, '成绩导入', 2000006, 15, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:studentScore:import', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000075);
insert into sys_menu
select 2000076, '成绩导出', 2000006, 16, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:studentScore:export', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000076);
insert into sys_menu
select 2000077, '同步等级', 2000006, 17, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:studentScore:sync', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000077);
