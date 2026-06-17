-- ----------------------------
-- 智能排座业务表
-- 适用于 RuoYi-Vue Spring Boot 3 版本
-- ----------------------------

drop table if exists seat_plan_score;
drop table if exists seat_assignment;
drop table if exists seat_plan;
drop table if exists seat_student_relation;
drop table if exists seat_rule;
drop table if exists seat_position;
drop table if exists seat_classroom;
drop table if exists seat_student;
drop table if exists seat_class;

-- ----------------------------
-- 1、班级表
-- ----------------------------
create table seat_class (
  class_id      bigint(20)   not null auto_increment comment '班级ID',
  class_name    varchar(64)  not null                comment '班级名称',
  grade_name    varchar(64)  default ''              comment '年级名称',
  school_year   varchar(20)  default ''              comment '学年',
  semester      char(1)      default '1'             comment '学期（1上学期 2下学期）',
  teacher_id    bigint(20)   default null            comment '负责老师ID',
  dept_id       bigint(20)   default null            comment '所属部门ID',
  status        char(1)      default '0'             comment '状态（0正常 1停用）',
  del_flag      char(1)      default '0'             comment '删除标志（0存在 2删除）',
  create_by     varchar(64)  default ''              comment '创建者',
  create_time   datetime                             comment '创建时间',
  update_by     varchar(64)  default ''              comment '更新者',
  update_time   datetime                             comment '更新时间',
  remark        varchar(500) default null            comment '备注',
  primary key (class_id),
  key idx_seat_class_teacher (teacher_id),
  key idx_seat_class_dept (dept_id)
) engine=innodb comment='排座班级表';

-- ----------------------------
-- 2、学生表
-- ----------------------------
create table seat_student (
  student_id        bigint(20)    not null auto_increment comment '学生ID',
  class_id          bigint(20)    not null                comment '班级ID',
  student_no        varchar(32)   default ''              comment '学号',
  student_name      varchar(64)   not null                comment '学生姓名',
  gender            char(1)       default '2'             comment '性别（0男 1女 2未知）',
  height_cm         decimal(5,2)  default null            comment '身高厘米',
  vision_level      char(1)       default '0'             comment '视力等级（0正常 1轻度 2中度 3重度）',
  score_level       char(1)       default null            comment '成绩等级（A/B/C/D）',
  discipline_level  char(1)       default '0'             comment '纪律等级（0正常 1关注 2重点关注）',
  special_need      varchar(255)  default ''              comment '特殊需求',
  sort_no           int(11)       default 0               comment '排序号',
  status            char(1)       default '0'             comment '状态（0正常 1停用）',
  del_flag          char(1)       default '0'             comment '删除标志（0存在 2删除）',
  create_by         varchar(64)   default ''              comment '创建者',
  create_time       datetime                              comment '创建时间',
  update_by         varchar(64)   default ''              comment '更新者',
  update_time       datetime                              comment '更新时间',
  remark            varchar(500)  default null            comment '备注',
  primary key (student_id),
  key idx_seat_student_class (class_id),
  key idx_seat_student_name (student_name),
  key idx_seat_student_no (class_id, student_no)
) engine=innodb comment='排座学生表';

-- ----------------------------
-- 3、教室布局表
-- ----------------------------
create table seat_classroom (
  classroom_id       bigint(20)   not null auto_increment comment '教室布局ID',
  class_id           bigint(20)   not null                comment '班级ID',
  classroom_name     varchar(64)  not null                comment '教室布局名称',
  row_count          int(11)      not null                comment '座位行数',
  col_count          int(11)      not null                comment '座位列数',
  platform_position  varchar(16)  default 'FRONT'         comment '讲台位置（FRONT/BACK/LEFT/RIGHT）',
  aisle_after_cols   varchar(100) default ''              comment '过道所在列后，逗号分隔',
  is_default         char(1)      default '0'             comment '是否默认（0否 1是）',
  status             char(1)      default '0'             comment '状态（0正常 1停用）',
  del_flag           char(1)      default '0'             comment '删除标志（0存在 2删除）',
  create_by          varchar(64)  default ''              comment '创建者',
  create_time        datetime                             comment '创建时间',
  update_by          varchar(64)  default ''              comment '更新者',
  update_time        datetime                             comment '更新时间',
  remark             varchar(500) default null            comment '备注',
  primary key (classroom_id),
  key idx_seat_classroom_class (class_id)
) engine=innodb comment='排座教室布局表';

-- ----------------------------
-- 4、座位位置表
-- ----------------------------
create table seat_position (
  seat_id       bigint(20)   not null auto_increment comment '座位ID',
  classroom_id  bigint(20)   not null                comment '教室布局ID',
  row_index     int(11)      not null                comment '行号，从1开始',
  col_index     int(11)      not null                comment '列号，从1开始',
  seat_code     varchar(32)  default ''              comment '座位编号',
  seat_type     char(1)      default '0'             comment '座位类型（0普通 1空位 2过道）',
  is_available  char(1)      default '1'             comment '是否可用（0否 1是）',
  status        char(1)      default '0'             comment '状态（0正常 1停用）',
  create_by     varchar(64)  default ''              comment '创建者',
  create_time   datetime                             comment '创建时间',
  update_by     varchar(64)  default ''              comment '更新者',
  update_time   datetime                             comment '更新时间',
  remark        varchar(500) default null            comment '备注',
  primary key (seat_id),
  key idx_seat_position_classroom (classroom_id),
  unique key uk_seat_position_grid (classroom_id, row_index, col_index)
) engine=innodb comment='排座座位位置表';

-- ----------------------------
-- 5、排座规则表
-- ----------------------------
create table seat_rule (
  rule_id        bigint(20)   not null auto_increment comment '规则ID',
  class_id       bigint(20)   not null                comment '班级ID',
  rule_name      varchar(64)  not null                comment '规则名称',
  rule_category  varchar(16)  not null                comment '规则类别（HARD硬规则 SOFT软规则）',
  rule_code      varchar(64)  not null                comment '规则编码',
  rule_weight    int(11)      default 0               comment '规则权重',
  rule_config    json                                  comment '规则配置JSON',
  enabled        char(1)      default '1'             comment '是否启用（0否 1是）',
  status         char(1)      default '0'             comment '状态（0正常 1停用）',
  del_flag       char(1)      default '0'             comment '删除标志（0存在 2删除）',
  create_by      varchar(64)  default ''              comment '创建者',
  create_time    datetime                             comment '创建时间',
  update_by      varchar(64)  default ''              comment '更新者',
  update_time    datetime                             comment '更新时间',
  remark         varchar(500) default null            comment '备注',
  primary key (rule_id),
  key idx_seat_rule_class (class_id),
  key idx_seat_rule_code (rule_code)
) engine=innodb comment='排座规则表';

-- ----------------------------
-- 6、学生关系约束表
-- ----------------------------
create table seat_student_relation (
  relation_id     bigint(20)   not null auto_increment comment '关系ID',
  class_id        bigint(20)   not null                comment '班级ID',
  student_id      bigint(20)   not null                comment '学生ID',
  related_id      bigint(20)   not null                comment '关联学生ID',
  relation_type   varchar(32)  not null                comment '关系类型（NOT_DESKMATE/NOT_ADJACENT/PREFER_DESKMATE）',
  relation_weight int(11)      default 100             comment '关系权重',
  enabled         char(1)      default '1'             comment '是否启用（0否 1是）',
  create_by       varchar(64)  default ''              comment '创建者',
  create_time     datetime                             comment '创建时间',
  update_by       varchar(64)  default ''              comment '更新者',
  update_time     datetime                             comment '更新时间',
  remark          varchar(500) default null            comment '备注',
  primary key (relation_id),
  key idx_seat_relation_class (class_id),
  key idx_seat_relation_student (student_id),
  unique key uk_seat_relation_pair (class_id, student_id, related_id, relation_type)
) engine=innodb comment='排座学生关系约束表';

-- ----------------------------
-- 7、座位方案表
-- ----------------------------
create table seat_plan (
  plan_id       bigint(20)    not null auto_increment comment '方案ID',
  class_id      bigint(20)    not null                comment '班级ID',
  classroom_id  bigint(20)    not null                comment '教室布局ID',
  plan_name     varchar(64)   not null                comment '方案名称',
  plan_type     varchar(16)   default 'AUTO'          comment '方案类型（AUTO自动 MANUAL手动）',
  plan_status   varchar(16)   default 'DRAFT'         comment '方案状态（DRAFT草稿 ACTIVE启用 ARCHIVED归档）',
  total_score   decimal(8,2)  default 0               comment '方案总评分',
  generated_at  datetime                              comment '生成时间',
  active_time   datetime                              comment '启用时间',
  del_flag      char(1)       default '0'             comment '删除标志（0存在 2删除）',
  create_by     varchar(64)   default ''              comment '创建者',
  create_time   datetime                              comment '创建时间',
  update_by     varchar(64)   default ''              comment '更新者',
  update_time   datetime                              comment '更新时间',
  remark        varchar(500)  default null            comment '备注',
  primary key (plan_id),
  key idx_seat_plan_class (class_id),
  key idx_seat_plan_classroom (classroom_id)
) engine=innodb comment='排座方案表';

-- ----------------------------
-- 8、座位分配表
-- ----------------------------
create table seat_assignment (
  assignment_id         bigint(20)   not null auto_increment comment '分配ID',
  plan_id               bigint(20)   not null                comment '方案ID',
  class_id              bigint(20)   not null                comment '班级ID',
  classroom_id          bigint(20)   not null                comment '教室布局ID',
  seat_id               bigint(20)   not null                comment '座位ID',
  student_id            bigint(20)   default null            comment '学生ID',
  student_name_snapshot varchar(64)  default ''              comment '学生姓名快照',
  row_index             int(11)      not null                comment '行号快照',
  col_index             int(11)      not null                comment '列号快照',
  is_locked             char(1)      default '0'             comment '是否锁定（0否 1是）',
  assign_source         varchar(16)  default 'AUTO'          comment '分配来源（AUTO自动 MANUAL手动）',
  create_by             varchar(64)  default ''              comment '创建者',
  create_time           datetime                             comment '创建时间',
  update_by             varchar(64)  default ''              comment '更新者',
  update_time           datetime                             comment '更新时间',
  remark                varchar(500) default null            comment '备注',
  primary key (assignment_id),
  key idx_seat_assignment_plan (plan_id),
  key idx_seat_assignment_student (student_id),
  unique key uk_seat_assignment_seat (plan_id, seat_id),
  unique key uk_seat_assignment_student (plan_id, student_id)
) engine=innodb comment='排座分配表';

-- ----------------------------
-- 9、方案评分明细表
-- ----------------------------
create table seat_plan_score (
  score_id       bigint(20)    not null auto_increment comment '评分ID',
  plan_id        bigint(20)    not null                comment '方案ID',
  rule_code      varchar(64)   not null                comment '规则编码',
  rule_name      varchar(64)   default ''              comment '规则名称',
  score_value    decimal(8,2)  default 0               comment '评分值',
  penalty_value  decimal(8,2)  default 0               comment '扣分值',
  detail_json    json                                  comment '评分明细JSON',
  create_time    datetime                              comment '创建时间',
  primary key (score_id),
  key idx_seat_plan_score_plan (plan_id),
  key idx_seat_plan_score_rule (rule_code)
) engine=innodb comment='排座方案评分明细表';
