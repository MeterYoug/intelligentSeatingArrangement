# 排座业务模块设计

## 目标

在若依前后端分离版 Spring Boot 3 项目中新增独立排座业务模块，承载班级、学生、教室布局、排座规则、座位方案和算法能力。

## 后端目录

当前后端目录：

```text
RuoYi-Vue-springboot3
```

建议新增 Maven 模块：

```text
ruoyi-seating
```

新增后端模块后，根 `pom.xml` 增加：

```xml
<module>ruoyi-seating</module>
```

`ruoyi-admin/pom.xml` 增加依赖：

```xml
<dependency>
    <groupId>com.ruoyi</groupId>
    <artifactId>ruoyi-seating</artifactId>
</dependency>
```

根 `pom.xml` 的 `dependencyManagement` 增加：

```xml
<dependency>
    <groupId>com.ruoyi</groupId>
    <artifactId>ruoyi-seating</artifactId>
    <version>${ruoyi.version}</version>
</dependency>
```

## 包结构

建议包结构：

```text
ruoyi-seating
└── src/main/java/com/ruoyi/seating
    ├── domain
    ├── mapper
    ├── service
    ├── service/impl
    ├── engine
    ├── engine/model
    └── engine/rule
```

Controller 继续放在 `ruoyi-admin`：

```text
ruoyi-admin
└── src/main/java/com/ruoyi/web/controller/seating
    ├── SeatClassController.java
    ├── SeatStudentController.java
    ├── SeatClassroomController.java
    ├── SeatRuleController.java
    └── SeatPlanController.java
```

Mapper XML 放在：

```text
ruoyi-seating/src/main/resources/mapper/seating
```

该路径能被当前 MyBatis 配置 `classpath*:mapper/**/*Mapper.xml` 扫描到。

## 设计原则

- 不把排座业务写进 `ruoyi-system`，避免和系统权限、用户、菜单模块耦合。
- Controller 保持若依风格：`@RestController`、`@PreAuthorize`、`@Log`、`AjaxResult`、`TableDataInfo`。
- CRUD 先走若依代码生成器，复杂能力手写 Service 和算法引擎。
- 算法引擎不直接依赖 Controller，不直接读写数据库，只接收模型并返回结果，方便测试和后期替换。
- 所有业务表保留若依审计字段：`create_by`、`create_time`、`update_by`、`update_time`、`remark`。

## 权限标识

建议权限标识：

```text
seating:class:list
seating:class:query
seating:class:add
seating:class:edit
seating:class:remove
seating:class:export

seating:student:list
seating:student:query
seating:student:add
seating:student:edit
seating:student:remove
seating:student:import
seating:student:export

seating:classroom:list
seating:classroom:query
seating:classroom:add
seating:classroom:edit
seating:classroom:remove

seating:rule:list
seating:rule:query
seating:rule:add
seating:rule:edit
seating:rule:remove

seating:plan:list
seating:plan:query
seating:plan:add
seating:plan:edit
seating:plan:remove
seating:plan:generate
seating:plan:export
```

## 第一阶段 Controller

```text
GET    /seating/class/list
GET    /seating/class/{classId}
POST   /seating/class
PUT    /seating/class
DELETE /seating/class/{classIds}

GET    /seating/student/list
GET    /seating/student/{studentId}
POST   /seating/student
PUT    /seating/student
DELETE /seating/student/{studentIds}
POST   /seating/student/importData
POST   /seating/student/export

GET    /seating/classroom/list
GET    /seating/classroom/{classroomId}
POST   /seating/classroom
PUT    /seating/classroom
DELETE /seating/classroom/{classroomIds}

GET    /seating/rule/list
GET    /seating/rule/{ruleId}
POST   /seating/rule
PUT    /seating/rule
DELETE /seating/rule/{ruleIds}

GET    /seating/plan/list
GET    /seating/plan/{planId}
POST   /seating/plan/generate
PUT    /seating/plan/assignments
POST   /seating/plan/export
DELETE /seating/plan/{planIds}
```

## 算法引擎第一版

建议核心类：

```text
SeatingEngine
SeatingContext
SeatingResult
SeatScoreCalculator
HardRuleValidator
SoftRuleScorer
RandomSwapOptimizer
```

第一版算法流程：

1. 加载学生、座位、规则、历史方案。
2. 固定锁定座位和不可用座位。
3. 检查硬规则是否存在明显无解情况。
4. 生成初始座位分配。
5. 对方案评分。
6. 使用随机交换优化评分。
7. 保存最高分方案和规则命中情况。

## 前端页面

建议菜单结构：

```text
智能排座
├── 班级管理
├── 学生管理
├── 教室布局
├── 排座规则
└── 座位方案
```

第一阶段页面：

- `src/views/seating/class/index.vue`
- `src/views/seating/student/index.vue`
- `src/views/seating/classroom/index.vue`
- `src/views/seating/rule/index.vue`
- `src/views/seating/plan/index.vue`
- `src/views/seating/plan/editor.vue`

