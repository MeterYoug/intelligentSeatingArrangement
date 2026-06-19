-- 班级科目配置扩展。
-- 执行前请确认 `seat_class` 尚未存在 `subject_snapshot` 字段。

alter table seat_class
  add column subject_snapshot json null comment '班级科目快照JSON' after grade_code;

