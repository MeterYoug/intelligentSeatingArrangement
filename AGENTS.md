# AGENTS.md

## 项目定位

本项目是面向老师的智能排座系统，第一版优先建设 PC Web 管理端。系统基于若依前后端分离版二次开发，重点交付班级、学生、教室布局、排座规则、座位方案、智能生成和人工微调能力。

微信小程序、学生端、家长端、多学校多租户、复杂 AI 对话式排座不纳入第一版 MVP。

## 代码目录

```text
.
├── RuoYi-Vue-springboot3   后端，若依 Spring Boot 3 多模块工程
├── RuoYi-Vue3-master       前端，若依 Vue3 + Vite + Element Plus 工程
└── docs                    项目设计与进度文档
```

关键文档：

- `docs/project-progress.md`：当前进度和阶段记录。
- `docs/seating-module-design.md`：排座模块后端、前端、权限和算法设计。
- `docs/seating-database-design.md`：排座业务表设计。
- `ROADMAP.md`：MVP 路线图和阶段验收标准。

## 后端开发约定

后端基础框架为 `RuoYi-Vue-springboot3`。

排座业务必须放在独立 Maven 模块：

```text
RuoYi-Vue-springboot3/ruoyi-seating
```

当前模块坐标：

```text
com.ruoyi:ruoyi-seating:3.9.2
```

排座业务包名使用：

```text
com.ruoyi.seating
```

推荐包结构：

```text
com.ruoyi.seating.domain
com.ruoyi.seating.mapper
com.ruoyi.seating.service
com.ruoyi.seating.service.impl
com.ruoyi.seating.engine
com.ruoyi.seating.engine.model
com.ruoyi.seating.engine.rule
```

Controller 沿用若依风格，放在 `ruoyi-admin`：

```text
RuoYi-Vue-springboot3/ruoyi-admin/src/main/java/com/ruoyi/web/controller/seating
```

Mapper XML 放在：

```text
RuoYi-Vue-springboot3/ruoyi-seating/src/main/resources/mapper/seating
```

约束：

- 不把排座业务写进 `ruoyi-system`。
- 不修改若依原有系统模块来承载业务逻辑，除非是菜单、权限、字典等必要配置。
- CRUD 优先使用若依代码生成器生成，再整理到 `ruoyi-seating` 模块边界内。
- 算法引擎不直接依赖 Controller，不直接读写数据库，只接收模型并返回结果。
- 业务表沿用若依审计字段：`create_by`、`create_time`、`update_by`、`update_time`、`remark`。
- 删除语义沿用若依习惯：`del_flag` 中 `0` 表示存在，`2` 表示删除。

## 前端开发约定

前端基础框架为 `RuoYi-Vue3-master`，使用 Vue3、Vite、Element Plus，并沿用若依现有页面结构、权限指令、请求封装和菜单体系。

排座页面放在：

```text
RuoYi-Vue3-master/src/views/seating
```

第一阶段页面：

```text
src/views/seating/class/index.vue
src/views/seating/student/index.vue
src/views/seating/classroom/index.vue
src/views/seating/rule/index.vue
src/views/seating/plan/index.vue
src/views/seating/plan/editor.vue
```

约束：

- 后台管理页面保持若依后台风格，不做营销型首页。
- 表格、表单、弹窗、权限按钮优先复用若依既有模式。
- 座位编辑页需要服务老师高频使用，优先保证清晰、稳定、可拖拽微调。

## 数据库约定

排座业务表名前缀统一为：

```text
seat_
```

当前 SQL 草案：

```text
RuoYi-Vue-springboot3/sql/seating_20260615.sql
```

核心表：

- `seat_class`
- `seat_student`
- `seat_classroom`
- `seat_position`
- `seat_rule`
- `seat_student_relation`
- `seat_plan`
- `seat_assignment`
- `seat_plan_score`

第一版不引入多学校多租户字段，先通过若依部门和老师账号隔离。

## 权限约定

排座权限标识统一使用 `seating:` 前缀，例如：

```text
seating:class:list
seating:student:import
seating:classroom:edit
seating:rule:list
seating:plan:generate
seating:plan:export
```

菜单结构：

```text
智能排座
├── 班级管理
├── 学生管理
├── 教室布局
├── 排座规则
└── 座位方案
```

## 本地端口

当前已验证端口：

```text
后端: http://localhost:8310
前端: http://localhost:8222
验证码: http://localhost:8310/captchaImage
前端代理: http://localhost:8222/dev-api/captchaImage
```

## 常用验证命令

后端编译：

```text
cd RuoYi-Vue-springboot3
mvn -DskipTests compile
```

前端启动：

```text
cd RuoYi-Vue3-master
npm run dev
```

验证要求：

- 后端改动至少执行 `mvn -DskipTests compile`。
- 前端改动至少确认 Vite 编译或页面可访问。
- 涉及前后端联调时，确认 `/dev-api` 代理仍指向后端 `8310`。

## 进度维护

完成关键阶段后，更新：

```text
docs/project-progress.md
```

如果阶段目标、优先级或 MVP 范围发生变化，同时更新：

```text
ROADMAP.md
```

