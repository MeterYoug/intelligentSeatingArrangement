# 排座业务数据库设计

## 命名约定

- 表名前缀：`seat_`
- 主键：业务名 + `_id`，类型 `bigint`
- 状态字段：`status char(1)`，`0` 正常，`1` 停用
- 删除字段：`del_flag char(1)`，`0` 存在，`2` 删除
- 审计字段：沿用若依 `create_by`、`create_time`、`update_by`、`update_time`、`remark`

## 表清单

- `seat_class`：班级
- `seat_student`：学生
- `seat_classroom`：教室布局
- `seat_position`：座位位置
- `seat_rule`：排座规则
- `seat_student_relation`：学生关系约束
- `seat_plan`：座位方案
- `seat_assignment`：座位分配
- `seat_plan_score`：方案评分明细

## 关键关系

```text
seat_class 1 -- n seat_student
seat_class 1 -- n seat_classroom
seat_classroom 1 -- n seat_position
seat_class 1 -- n seat_rule
seat_class 1 -- n seat_student_relation
seat_class 1 -- n seat_plan
seat_plan 1 -- n seat_assignment
seat_plan 1 -- n seat_plan_score
```

## 设计取舍

- 不在第一版引入多学校多租户字段，先通过若依部门和老师账号隔离。
- 学生不复用 `sys_user`，避免学生端未上线前引入无意义账号。
- 规则表使用 `rule_config` JSON，便于快速扩展规则，不需要每加一条规则就改表。
- 座位分配保留快照字段，例如 `student_name_snapshot`，用于历史方案稳定展示。
- 座位表拆分为教室布局和座位位置，方便支持空座、过道和不规则教室。

## 核心字段

### seat_class

- `class_name`：班级名称，例如 `三年级一班`
- `grade_name`：年级，例如 `三年级`
- `school_year`：学年，例如 `2026-2027`
- `semester`：学期，`1` 上学期，`2` 下学期
- `teacher_id`：班主任或主要维护老师，关联 `sys_user.user_id`
- `dept_id`：所属部门或学校组织

### seat_student

- `student_name`：学生姓名
- `student_no`：学号
- `gender`：性别，沿用若依习惯，`0` 男，`1` 女，`2` 未知
- `height_cm`：身高
- `vision_level`：视力等级，`0` 正常，`1` 轻度近视，`2` 中度近视，`3` 重度近视
- `score_level`：成绩等级，`A`、`B`、`C`、`D`
- `discipline_level`：纪律关注等级，`0` 正常，`1` 需关注，`2` 重点关注
- `special_need`：特殊需求说明

### seat_rule

- `rule_category`：`HARD` 或 `SOFT`
- `rule_code`：规则编码，例如 `FRONT_REQUIRED`、`HEIGHT_BACK`、`AVOID_ADJACENT`
- `rule_weight`：软规则权重
- `rule_config`：JSON 配置

### seat_student_relation

- `relation_type`：`NOT_DESKMATE`、`NOT_ADJACENT`、`PREFER_DESKMATE`

### seat_plan / seat_assignment

- `seat_plan` 保存方案元数据。
- `seat_assignment` 保存学生到座位的分配。
- `seat_assignment.is_locked` 用于老师手动锁定座位，后续重新生成时保留。

## 规则配置示例

```json
{
  "frontRows": 2,
  "studentIds": [1001, 1002]
}
```

```json
{
  "maxSameGenderAdjacent": 2,
  "weight": 60
}
```

## 成绩管理扩展设计

成绩管理遵循边界：年级属于班级，成绩绑定班级，科目由班级年级推导。

### seat_class 扩展字段

- `school_stage`：学段，取值建议为 `PRIMARY`、`JUNIOR`、`SENIOR`。
- `grade_code`：年级编码，例如 `PRIMARY_4`、`JUNIOR_2`、`SENIOR_1`。
- `grade_name`：继续保留中文展示名称，例如「小学四年级」。

### seat_subject

科目模板表，用于根据学段和年级生成成绩导入模板。

- `school_stage`：学段。
- `grade_code`：年级编码。
- `subject_code`：科目编码。
- `subject_name`：科目名称。
- `sort_order`：导入模板列顺序。
- `is_required`：是否必考。
- `status`：状态。

小学默认科目包含：语文、数学、英语、科学。

### seat_exam

考试批次表。

- `class_id`：绑定班级。
- `exam_name`：考试名称。
- `exam_date`：考试日期。
- `school_stage_snapshot`：考试创建时的学段快照。
- `grade_code_snapshot`：考试创建时的年级编码快照。
- `grade_name_snapshot`：考试创建时的年级名称快照。
- `subject_snapshot`：考试创建时的科目快照 JSON。
- `is_current`：是否当前考试。

### seat_student_score

学生成绩表。

- `exam_id`：绑定考试批次。
- `class_id`：绑定班级。
- `student_id`：绑定学生。
- `student_no`：学号快照。
- `student_name_snapshot`：学生姓名快照。
- `subject_scores`：各科成绩 JSON。
- `total_score`：总分。
- `class_rank`：班级排名。
- `score_level`：成绩等级，取值 `A/B/C/D`。

导入成绩后，系统计算 `score_level`，并可同步回 `seat_student.score_level` 供排座算法使用。
