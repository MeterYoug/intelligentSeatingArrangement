-- 智能排座菜单和基础 CRUD 权限
-- 使用固定高位 ID，避免与若依初始化菜单和代码生成器常用 ID 冲突。

insert into sys_menu
select 2000000, '智能排座', 0, 4, 'seating', null, '', '', 1, 0, 'M', '0', '0', '', 'table', 'admin', sysdate(), '', null, '智能排座目录'
where not exists (select 1 from sys_menu where menu_id = 2000000);

insert into sys_menu
select 2000001, '班级管理', 2000000, 1, 'class', 'seating/class/index', '', '', 1, 0, 'C', '0', '0', 'seating:class:list', 'peoples', 'admin', sysdate(), '', null, '班级管理菜单'
where not exists (select 1 from sys_menu where menu_id = 2000001);
insert into sys_menu
select 2000002, '学生管理', 2000000, 2, 'student', 'seating/student/index', '', '', 1, 0, 'C', '0', '0', 'seating:student:list', 'user', 'admin', sysdate(), '', null, '学生管理菜单'
where not exists (select 1 from sys_menu where menu_id = 2000002);
insert into sys_menu
select 2000003, '教室布局', 2000000, 3, 'classroom', 'seating/classroom/index', '', '', 1, 0, 'C', '0', '0', 'seating:classroom:list', 'tree-table', 'admin', sysdate(), '', null, '教室布局菜单'
where not exists (select 1 from sys_menu where menu_id = 2000003);
insert into sys_menu
select 2000004, '排座规则', 2000000, 4, 'rule', 'seating/rule/index', '', '', 1, 0, 'C', '0', '0', 'seating:rule:list', 'edit', 'admin', sysdate(), '', null, '排座规则菜单'
where not exists (select 1 from sys_menu where menu_id = 2000004);
insert into sys_menu
select 2000005, '座位方案', 2000000, 5, 'plan', 'seating/plan/index', '', '', 1, 0, 'C', '0', '0', 'seating:plan:list', 'form', 'admin', sysdate(), '', null, '座位方案菜单'
where not exists (select 1 from sys_menu where menu_id = 2000005);

insert into sys_menu
select 2000010, '班级查询', 2000001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:class:query', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000010);
insert into sys_menu
select 2000011, '班级新增', 2000001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:class:add', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000011);
insert into sys_menu
select 2000012, '班级修改', 2000001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:class:edit', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000012);
insert into sys_menu
select 2000013, '班级删除', 2000001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:class:remove', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000013);
insert into sys_menu
select 2000014, '班级导出', 2000001, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:class:export', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000014);

insert into sys_menu
select 2000020, '学生查询', 2000002, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:student:query', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000020);
insert into sys_menu
select 2000021, '学生新增', 2000002, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:student:add', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000021);
insert into sys_menu
select 2000022, '学生修改', 2000002, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:student:edit', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000022);
insert into sys_menu
select 2000023, '学生删除', 2000002, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:student:remove', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000023);
insert into sys_menu
select 2000024, '学生导出', 2000002, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:student:export', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000024);
insert into sys_menu
select 2000025, '学生导入', 2000002, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:student:import', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000025);

insert into sys_menu
select 2000030, '教室查询', 2000003, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:classroom:query', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000030);
insert into sys_menu
select 2000031, '教室新增', 2000003, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:classroom:add', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000031);
insert into sys_menu
select 2000032, '教室修改', 2000003, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:classroom:edit', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000032);
insert into sys_menu
select 2000033, '教室删除', 2000003, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:classroom:remove', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000033);
insert into sys_menu
select 2000034, '教室导出', 2000003, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:classroom:export', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000034);

insert into sys_menu
select 2000040, '规则查询', 2000004, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:rule:query', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000040);
insert into sys_menu
select 2000041, '规则新增', 2000004, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:rule:add', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000041);
insert into sys_menu
select 2000042, '规则修改', 2000004, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:rule:edit', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000042);
insert into sys_menu
select 2000043, '规则删除', 2000004, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:rule:remove', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000043);
insert into sys_menu
select 2000044, '规则导出', 2000004, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:rule:export', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000044);

insert into sys_menu
select 2000050, '方案查询', 2000005, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:plan:query', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000050);
insert into sys_menu
select 2000051, '方案新增', 2000005, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:plan:add', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000051);
insert into sys_menu
select 2000052, '方案修改', 2000005, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:plan:edit', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000052);
insert into sys_menu
select 2000053, '方案删除', 2000005, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:plan:remove', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000053);
insert into sys_menu
select 2000054, '方案导出', 2000005, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:plan:export', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000054);
insert into sys_menu
select 2000055, '方案生成', 2000005, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'seating:plan:generate', '#', 'admin', sysdate(), '', null, ''
where not exists (select 1 from sys_menu where menu_id = 2000055);
