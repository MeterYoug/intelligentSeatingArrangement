# 智能排座系统项目进度

## 当前日期

2026-06-16

## 项目定位

面向老师的智能排座系统，优先建设 PC Web 管理端，方便老师在电脑上为班级学生排座。微信小程序暂定为后期扩展，不纳入第一版 MVP。

## 已确定方向

- 系统形态：Web 后台管理系统。
- 后端方向：已确认使用开源若依 Vue3 版本作为基础框架。
- 后端技术栈：Spring Boot + MyBatis / MyBatis Plus 风格，沿用若依生态。
- 前端方向：Vue3 + Vite + Element Plus，沿用若依 Vue3 前端架构。
- 核心业务模块：新增独立排座业务模块，不直接污染若依原有 `system` 模块。
- 排座算法方向：先采用规则评分 + 随机交换优化，不在第一版引入复杂 AI。

## 技术选型确认

已确认采用：

- 后端基础框架：若依 Vue3 对应的 Spring Boot 后端。
- 前端基础框架：若依 Vue3 前端。
- 开发方式：基于若依进行二次开发，新增独立排座业务模块。
- 模块边界：排座业务独立封装，避免和若依原有 `system` 模块强耦合。

## 本地源码状态

源码已手动下载到本地：

- 后端目录：`RuoYi-Vue-springboot3`
- 前端目录：`RuoYi-Vue3-master`

当前若依初始化 SQL：

- `RuoYi-Vue-springboot3/sql/ry_20260417.sql`
- `RuoYi-Vue-springboot3/sql/quartz.sql`

新增排座业务 SQL 草案：

- `RuoYi-Vue-springboot3/sql/seating_20260615.sql`

后端已识别到标准若依 Maven 多模块结构：

- `ruoyi-admin`
- `ruoyi-common`
- `ruoyi-framework`
- `ruoyi-generator`
- `ruoyi-quartz`
- `ruoyi-system`

前端已识别到 `package.json`，后续按 Vue3 前端项目启动。

## 本地启动状态

若依前后端分离基础项目已启动并验证联通：

- 后端端口：`8310`
- 前端端口：`8222`
- 后端验证码接口：`http://localhost:8310/captchaImage`
- 前端代理验证码接口：`http://localhost:8222/dev-api/captchaImage`

验证结果：

- 后端验证码接口返回 JSON 数据。
- 前端 Vite 开发服务可访问。
- 前端 `/dev-api` 代理可以正确转发到后端 `8310`。

## 后端模块接入状态

已创建并接入排座业务 Maven 模块：

- 模块目录：`RuoYi-Vue-springboot3/ruoyi-seating`
- 模块坐标：`com.ruoyi:ruoyi-seating:3.9.2`
- 已加入根 `pom.xml` 的 `modules`
- 已加入根 `pom.xml` 的 `dependencyManagement`
- 已加入 `ruoyi-admin/pom.xml` 依赖

已创建基础包结构：

- `com.ruoyi.seating.domain`
- `com.ruoyi.seating.mapper`
- `com.ruoyi.seating.service`
- `com.ruoyi.seating.service.impl`
- `com.ruoyi.seating.engine`
- `com.ruoyi.seating.engine.model`
- `com.ruoyi.seating.engine.rule`

验证结果：

- 执行 `mvn -DskipTests compile` 通过。
- Maven reactor 已识别 `ruoyi-seating`，构建顺序中位于 `ruoyi-admin` 之前。

## 2026-06-15 阶段记录

当前阶段：若依基础工程接入排座业务模块。

已完成：

- 确认采用前后端分离版本：后端 `RuoYi-Vue-springboot3`，前端 `RuoYi-Vue3-master`。
- 验证后端 `8310` 和前端 `8222` 联通。
- 完成排座业务模块设计文档。
- 完成排座业务数据库设计文档。
- 新增排座业务 SQL 草案：`RuoYi-Vue-springboot3/sql/seating_20260615.sql`。
- 创建 `ruoyi-seating` Maven 模块。
- 将 `ruoyi-seating` 接入根 `pom.xml` 和 `ruoyi-admin/pom.xml`。
- 创建排座模块基础包结构。

验证记录：

```text
mvn -DskipTests compile
```

验证结果：

- 构建成功。
- `ruoyi-seating` 模块编译成功。
- `ruoyi-admin` 依赖 `ruoyi-seating` 后仍然编译成功。

下一步：

- 导入 `seating_20260615.sql`。
- 使用若依代码生成器生成排座基础 CRUD。
- 将生成代码整理到 `ruoyi-seating` 模块边界内。
- 配置智能排座菜单和权限。

## 2026-06-15 代码生成运行记录

当前阶段：排座基础 CRUD 已生成并确认可以正常运行。

已完成：

- 已导入排座业务 SQL 草案：`RuoYi-Vue-springboot3/sql/seating_20260615.sql`。
- 已使用若依代码生成器生成 `seat_` 业务表基础 CRUD。
- 已确认生成代码可以正常运行。
- 针对 `/system/assignment/list` 访问异常，已通过后端重新编译和重启运行态解决。

验证记录：

```text
mvn -DskipTests compile
```

验证结果：

- 构建成功。
- `ruoyi-seating` 模块重新编译成功。
- `ruoyi-admin` 依赖 `ruoyi-seating` 后重新编译成功。

待整理：

- 当前生成代码仍保留若依生成器默认的 `com.ruoyi.system` 包名、`/system/*` 接口路径和 `system:*` 权限标识。
- 后续需要按项目约定整理为 `com.ruoyi.seating`、`/seating/*` 和 `seating:*`。
- Controller 后续需要整理到 `ruoyi-admin` 的 `com.ruoyi.web.controller.seating`。

## 2026-06-15 班级管理业务化整理记录

当前阶段：班级管理页面完成第一轮业务化整理。

已完成：

- 班级管理新增和修改表单不再要求手填负责老师 ID、所属部门 ID、删除标志。
- 班级管理查询区不再按负责老师 ID、所属部门 ID 直接查询。
- 班级状态改为若依启用/停用字典控件展示。
- 前端提交班级表单时只提交班级名称、年级名称、学年、学期、状态和备注等业务可编辑字段。
- 后端新增班级时默认使用当前登录用户的 `userId` 写入 `teacher_id`，使用当前登录用户的 `deptId` 写入 `dept_id`，并写入 `create_by`、`del_flag=0`、默认 `status=0`。
- 后端修改班级时不允许前端覆盖 `teacher_id`、`dept_id`、`del_flag`、`create_by`、`create_time`。

验证记录：

```text
mvn -DskipTests compile
mvn -DskipTests package
npm run build:prod
GET /captchaImage
```

验证结果：

- 后端编译成功。
- 前端生产构建成功。

项目治理文档补齐：

- 新增根目录 `AGENTS.md`，记录项目协作规则、模块边界、前后端开发约定、验证命令和进度维护要求。
- 新增根目录 `ROADMAP.md`，记录 MVP 范围、阶段路线图、当前状态和下一步优先级。

## 2026-06-15 生成代码模块边界整理记录

当前阶段：排座生成代码已完成模块边界、接口路径和权限标识整理。

已完成：

- 将 9 组排座 domain、mapper、service 和 Mapper XML 从 `com.ruoyi.system`、`mapper/system` 整理到 `com.ruoyi.seating`、`mapper/seating`。
- 将 9 个排座 Controller 从 `ruoyi-seating` 整理到 `ruoyi-admin/src/main/java/com/ruoyi/web/controller/seating`。
- 将后端接口路径从 `/system/*` 统一为 `/seating/*`。
- 将后端和前端权限标识从 `system:*` 统一为 `seating:*`。
- 将前端 API 从 `src/api/system` 整理到 `src/api/seating`。
- 将前端页面从 `src/views/system` 整理到 `src/views/seating`。
- 新增菜单权限初始化脚本：`RuoYi-Vue-springboot3/sql/seating_menu_20260615.sql`。
- 菜单权限初始化脚本已由用户导入数据库。
- `智能排座` 一级菜单、权限和 5 个基础页面已验证可正常访问。

验证记录：

```text
mvn clean compile -DskipTests
npm run build:prod
```

验证结果：

- 后端 Maven 全量清理编译成功。
- 前端生产构建成功。
- 源码中未发现旧 `com.ruoyi.system` 排座包、`/system/*` 排座接口或 `system:*` 排座权限引用。
- 清理后的构建产物中未发现旧排座 Controller、Mapper 或 service class。

下一步：

- 联调班级、学生、教室、规则、方案基础 CRUD。

## 2026-06-15 菜单权限和基础页面验证记录

已完成：

- `智能排座` 一级菜单可见。
- 班级管理、学生管理、教室布局、排座规则和座位方案 5 个基础页面可正常访问。
- 菜单权限配置已生效。

下一步：

- 联调班级、学生、教室、规则、方案基础 CRUD。
- 开始学生 Excel 导入和教室布局初始化。

## 2026-06-15 基础 CRUD 联调记录

已完成：

- 修正班级导出仍请求旧路径 `system/class/export` 的问题。
- 班级、学生、教室、规则、方案查询统一过滤 `del_flag=0`。
- 5 个基础模块删除统一改为写入 `del_flag=2`，不再物理删除业务记录。
- 使用临时数据完成 5 个模块新增、详情、修改和逻辑删除端到端验证。
- 临时数据逻辑删除后，5 个模块列表均不可见。
- 前端 `/dev-api` 代理访问排座接口正常。

验证记录：

```text
mvn clean package -DskipTests
npm run build:prod
```

验证结果：

- 后端完整打包成功。
- 后端在 `8310` 启动成功，数据库和 Mapper XML 加载正常。
- 前端生产构建成功。
- 5 个基础模块 CRUD 接口均返回业务码 `200`。

阶段结论：

- 阶段 3 已完成。
- 阶段 4 已完成，学生 Excel 导入、教室座位初始化和座位位置网格编辑已完成端到端验收。

## 2026-06-15 学生班级选择优化记录

已完成：

- 学生新增和修改表单中的班级 ID 输入框改为可搜索的班级下拉框。
- 下拉框仅加载启用状态的班级，并显示班级名称、提交班级 ID。
- 修正学生导出接口残留的旧 `/system/student/export` 路径。
- 查询区的班级、性别、视力等级、成绩等级和纪律等级改为选择控件。
- 新增和修改表单的性别、状态改为单选框，视力、成绩、纪律改为下拉框。
- 身高和排序号改为带范围限制的数字输入框，特殊需求改为多行文本。
- 学生列表隐藏内部学生 ID 和班级 ID，改为显示班级名称和中文枚举。
- 后端学生查询关联 `seat_class` 返回班级名称。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端编译成功。
- 前端生产构建成功。

## 2026-06-16 学生导入模板入口修复记录

问题：

- 学生管理点击导入后，控制台报 `proxy.$refs.importStudentRef.open is not a function`。
- 同时出现 `Failed to resolve component: excel-import-dialog`，页面未正确挂载导入弹窗组件，导致模板下载入口不可用。

已完成：

- 在学生管理页注册 `ExcelImportDialog` 组件。
- 恢复学生导入弹窗的打开能力和导入模板下载入口。
- 验证后端 `/seating/student/importTemplate` 接口可返回 Excel 模板文件。
- 完成学生 Excel 成功导入、同学号覆盖更新和失败回滚接口级验证。
- 导入验证产生的测试学生已通过业务删除接口清理。

验证记录：

```text
npm run build:prod
POST /seating/student/importTemplate
POST /seating/student/importData
DELETE /seating/student/{studentId}
```

验证结果：

- 前端生产构建成功。
- 模板接口返回 `200`，`Content-Type` 为 `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8`。
- 成功导入返回 `200`，提示 `导入成功，共 1 条`。
- 同学号覆盖更新返回 `200`，查询结果中的学生姓名更新为 `Codex Import Updated`。
- 失败用例返回 `500` 且包含 `导入失败，数据未写入`，失败文件中的有效行未落库。
- 测试学生清理接口返回 `200`。

## 2026-06-16 教室座位初始化验证记录

验证范围：

- 新增教室布局后，调用 `POST /seating/classroom/{classroomId}/initialize` 初始化座位。
- 查询 `GET /seating/position/list`，确认座位写入 `seat_position`。
- 重复初始化同一教室，确认先清空再重建，座位数量不会累加。

验证记录：

```text
POST /seating/classroom
POST /seating/classroom/{classroomId}/initialize
GET  /seating/position/list?classroomId={classroomId}
DELETE /seating/position/{seatIds}
DELETE /seating/classroom/{classroomId}
```

验证结果：

- 使用 3 行 4 列教室验证，初始化接口返回 `200`，返回数据为 `12`。
- 第一次初始化后查询座位数量为 `12`。
- 第二次初始化后查询座位数量仍为 `12`，未出现重复累加。
- 首个座位为 `rowIndex=1`、`colIndex=1`、`seatCode=R1C1`、`seatType=0`、`isAvailable=1`。
- 最后一个座位为 `rowIndex=3`、`colIndex=4`、`seatCode=R3C4`、`seatType=0`、`isAvailable=1`。
- 验证产生的座位和教室布局已通过业务接口清理，清理后座位和教室列表均为 `0`。

## 2026-06-16 教室布局网格编辑实现记录

已完成：

- 教室新增接口改为返回生成后的教室布局对象，前端可拿到 `classroomId` 后继续保存座位布局。
- 新增 `GET /seating/position/classroom/{classroomId}/layout`，按教室查询座位布局。
- 新增 `PUT /seating/position/classroom/{classroomId}/layout`，按教室覆盖保存整张座位布局。
- 座位查询、详情、新增、修改和删除接口补充教室归属校验，非管理员必须在有权班级范围内操作。
- 教室布局新增和修改弹窗改为网格化编辑，默认 7 行 8 列。
- 点击格子可按 `座位 -> 过道 -> 不可用` 循环切换，并显示讲台位置和图例。
- 保存教室时同步保存网格布局到 `seat_position`，过道整列会自动回写到 `aisle_after_cols` 用于兼容旧字段。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
POST /seating/classroom
PUT  /seating/position/classroom/{classroomId}/layout
GET  /seating/position/classroom/{classroomId}/layout
DELETE /seating/position/{seatIds}
DELETE /seating/classroom/{classroomId}
```

验证结果：

- 后端编译成功。
- 前端生产构建成功。
- 新增教室返回 `200`，响应中包含生成后的 `classroomId`。
- 2 行 3 列混合布局保存返回 `200`，返回数据为 `6`。
- 回显结果包含座位、过道和不可用三种类型：`0/1`、`2/0`、`1/0` 分别对应可用座位、过道、不可用。
- 回显座位编号按坐标生成，例如 `R1C1`、`R1C2`、`R1C3`。
- 验证产生的座位和教室布局已通过业务接口清理，教室清理后列表不可见。

## 2026-06-16 阶段 4 数据准备能力验收记录

验收范围：

- 老师可以批量导入学生。
- 导入失败时能看到明确错误。
- 老师可以创建一个可用于排座的教室布局。
- 座位位置数据能正确保存到 `seat_position`。

验收记录：

```text
POST /seating/class
POST /seating/student/importData
POST /seating/classroom
PUT  /seating/position/classroom/{classroomId}/layout
GET  /seating/position/classroom/{classroomId}/layout
DELETE /seating/student/{studentIds}
DELETE /seating/position/{seatIds}
DELETE /seating/classroom/{classroomId}
DELETE /seating/class/{classId}
```

验收结果：

- 临时班级创建返回 `200`。
- 学生 Excel 成功导入返回 `200`，提示 `导入成功，共 2 条`，查询学生数量为 `2`。
- 学生 Excel 失败用例返回 `500`，错误信息包含 `导入失败，数据未写入`，失败文件中的有效行未落库。
- 教室布局创建返回 `200`，响应中包含 `classroomId`。
- 3 行 4 列混合布局保存返回 `200`，保存数量为 `12`。
- 布局回显数量为 `12`，包含座位 `8` 个、不可用 `1` 个、过道 `3` 个。
- 可用状态回显正确：`isAvailable=1` 共 `8` 个，`isAvailable=0` 共 `4` 个。
- 验收产生的学生、座位、教室和班级均已通过业务接口清理，清理后对应列表均为 `0`。

阶段结论：

- 阶段 4 数据准备能力验收通过。
- 下一阶段进入第一版排座算法。

## 2026-06-16 第一版排座算法生成闭环实现记录

当前阶段：阶段 5 第一版排座算法生成接口已通过端到端运行验收。

已完成：

- 扩展排座算法输入模型，支持学生、座位、启用规则、学生关系、固定随机种子和优化次数。
- 扩展排座算法输出模型，支持座位分配、总评分、评分明细和硬规则冲突提示。
- 补充第一版硬规则处理：
  - 特殊需求包含“前排”或规则编码为 `FRONT_ROW` / `MUST_FRONT_ROW` / `STUDENT_FRONT_ROW` 的学生需坐前排。
  - 学生关系 `NOT_DESKMATE` 不允许同桌。
  - 学生关系 `NOT_ADJACENT` 不允许相邻。
  - 自动排座仅使用普通且可用的座位，过道和不可用座位不参与分配。
- 补充第一版软规则评分：
  - 近视学生尽量靠前。
  - 身高较高学生尽量靠后。
  - 男女搭配尽量均衡。
  - 成绩强弱尽量均衡。
  - 纪律关注学生尽量分散。
  - 学生关系 `PREFER_DESKMATE` 作为软约束评分。
- 实现固定随机种子的随机交换优化，便于同一输入复现生成结果。
- 新增 `POST /seating/plan/generate` 智能生成接口。
- 生成结果保存到 `seat_plan`、`seat_assignment` 和 `seat_plan_score`。
- 座位方案页面新增“一键生成”入口。
- 菜单初始化 SQL 补充 `seating:plan:generate` 权限项。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端生产构建成功。
- 本次尚未启动前后端服务做登录态接口运行验收。

待验证：

- 使用真实班级、学生和教室布局调用 `POST /seating/plan/generate`。
- 检查生成方案是否正确写入 `seat_plan`、`seat_assignment` 和 `seat_plan_score`。
- 验证硬规则冲突时是否回滚并返回清晰错误。
- 对同一输入使用固定随机种子验证结果可复现。

下一步：

- 做生成接口端到端运行验收。
- 进入座位方案详情页和人工微调交互设计与实现。

## 2026-06-16 教室布局操作入口精简记录

问题：

- 教室布局列表行内操作同时存在 `编辑布局` 和 `修改`，两者调用同一个修改弹窗，入口重复。

已完成：

- 移除行内 `编辑布局` 按钮。
- 保留 `修改` 按钮，继续承载教室基础信息和网格布局编辑能力。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端生产构建成功。

## 2026-06-16 排座规则表单业务化优化记录

问题：

- 排座规则新增和修改弹窗需要手工输入规则编码，老师不容易理解，也容易填错。
- 指定学生坐前排规则缺少可视化配置入口，需要依赖隐藏的 `rule_config` JSON。

已完成：

- 将规则编码输入框改为规则类型下拉选择。
- 支持按规则类别筛选可选规则。
- 新增前排规则配置：
  - 选择规则 `指定学生坐前排`。
  - 选择需要坐前排的学生。
  - 配置前几排。
- 保存前排规则时自动生成 `ruleConfig`，格式包含 `studentIds` 和 `frontRows`。
- 修改规则时自动解析已保存的 `ruleConfig` 并回显学生和前几排。
- 列表中规则编码改为显示业务名称，并补充规则配置摘要。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端生产构建成功。

## 2026-06-16 排座规则类别说明补充记录

已完成：

- 在排座规则新增和修改弹窗的「规则类别」下方补充硬规则、软规则说明。
- 说明硬规则必须满足，违反时会判定为冲突。
- 说明软规则通过权重参与评分，会优先优化但不保证全部达成。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端生产构建成功。

## 2026-06-16 第一版排座算法运行验收记录

验收范围：

- 使用真实登录态调用 `POST /seating/plan/generate`。
- 验证生成结果写入 `seat_plan`、`seat_assignment` 和 `seat_plan_score`。
- 验证硬规则冲突时返回明确错误，且不创建方案记录。
- 验证同一输入和固定随机种子可复现相同座位分配结果。

验收记录：

```text
POST /login
POST /seating/class
POST /seating/student
POST /seating/classroom
POST /seating/classroom/{classroomId}/initialize
POST /seating/plan/generate
GET  /seating/plan/list
GET  /seating/assignment/list
GET  /seating/score/list
POST /seating/rule
DELETE /seating/assignment/{assignmentIds}
DELETE /seating/score/{scoreIds}
DELETE /seating/plan/{planIds}
DELETE /seating/rule/{ruleIds}
DELETE /seating/student/{studentIds}
DELETE /seating/position/{seatIds}
DELETE /seating/classroom/{classroomIds}
DELETE /seating/class/{classIds}
```

验收结果：

- 正向生成链路通过：4 名学生、6 个可用座位生成 1 个座位方案。
- 生成接口返回 `planId` 和 `totalScore=99.89`。
- 生成结果查询到 1 条方案记录、4 条座位分配记录、9 条评分明细记录。
- 硬规则冲突用例通过：3 名学生都要求坐第 1 排，但第 1 排只有 2 个座位，接口返回 `500`，错误信息包含 `硬规则冲突` 和 `未能安排在前 1 排`。
- 硬规则冲突用例未创建方案记录。
- 固定随机种子复现通过：同一班级、教室、学生和 `seed=20260616` 连续生成两次，座位分配签名完全一致。
- 验收产生的临时班级、学生、教室、座位、规则、方案、分配和评分数据已通过业务接口清理，清理后 `Codex` 临时班级和方案查询数量均为 `0`。

阶段结论：

- 阶段 5 第一版排座算法生成接口端到端运行验收通过。
- 下一步进入座位方案详情页、评分明细展示和人工微调交互。

## 2026-06-16 座位方案详情页实现记录

当前阶段：阶段 6 座位表编辑和人工微调进行中。

已完成：

- 新增隐藏路由 `/seating/plan-detail/index/:planId`。
- 新增座位方案详情页 `RuoYi-Vue3-master/src/views/seating/plan/detail.vue`。
- 座位方案列表新增「查看」入口。
- 一键生成座位方案成功后自动跳转到详情页。
- 详情页加载方案详情、教室座位布局、座位分配和评分明细。
- 详情页按教室布局渲染座位网格，区分已分配座位、空座、过道和不可用座位。
- 详情页展示班级、教室布局、方案状态、总评分、生成时间和评分明细。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端生产构建成功。

待办：

- 做详情页真实浏览器访问验收。
- 支持拖拽交换学生座位。
- 支持锁定和解锁座位。
- 支持修改后保存座位分配。

## 2026-06-16 座位方案详情页可读性优化记录

问题：

- 评分明细中的详情字段显示为英文 JSON key，不适合面向中国教师使用。
- 座位表讲台位置靠左，视觉上不像教室正中讲台。

已完成：

- 将评分明细中的 `penalty`、`required`、`affected`、`seed` 等字段映射为中文展示。
- 将前方和后方讲台调整为相对座位表居中显示。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端生产构建成功。

## 2026-06-16 座位方案详情页讲台位置修正记录

问题：

- 讲台居中是相对整个座位表区域居中，不是相对教室座位网格居中。

已完成：

- 将讲台放入与座位网格同一个教室主体容器。
- 前方和后方讲台现在相对教室座位网格居中。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端生产构建成功。

## 选择若依的原因

- 项目前期大量能力属于标准后台管理能力，例如登录、权限、菜单、角色、字典、Excel 导入导出。
- 若依可以降低基础框架搭建成本，把开发重点放在排座业务建模和算法上。
- 智能排座系统第一版更需要快速验证业务闭环，而不是从零实现权限和后台框架。

## 架构边界

建议新增独立业务模块：

```text
ruoyi-seating
```

该模块负责：

- 班级相关扩展数据
- 学生排座属性
- 教室座位布局
- 排座规则
- 座位方案
- 座位分配结果
- 排座算法引擎

若依原有模块主要负责：

- 用户登录
- 权限控制
- 菜单管理
- 系统配置
- 字典管理
- 操作日志

## 第一版 MVP 范围

第一版优先实现：

- 老师登录
- 班级管理
- 学生管理
- Excel 导入学生
- 教室座位布局配置
- 学生属性维护
- 基础排座规则配置
- 一键智能生成座位表
- 拖拽微调座位
- 保存历史座位方案
- 导出 Excel / PDF / 图片

暂不实现：

- 微信小程序
- 学生端
- 家长端
- 多学校多租户
- 复杂 AI 对话式排座
- 复杂审批流

## 核心排座规则

硬规则：

- 指定学生必须坐前排
- 指定学生不能同桌
- 指定学生不能相邻
- 指定座位不可用
- 已锁定座位不能被自动调整

软规则：

- 近视学生尽量靠前
- 身高较高学生尽量靠后
- 男女搭配尽量均衡
- 成绩强弱尽量均衡
- 纪律较差学生尽量分散
- 尽量减少频繁换座

## 排座算法初步方案

第一阶段使用可解释的约束优化思路：

1. 读取班级学生、教室布局、规则配置和历史座位方案。
2. 先处理锁定座位、不可用座位和必须满足的硬规则。
3. 生成一个初始座位方案。
4. 根据软规则对方案进行评分。
5. 通过随机交换学生座位持续优化评分。
6. 输出得分最高的座位方案。
7. 返回规则满足情况和冲突提示，方便老师人工调整。

## 初步数据模型

核心表：

- `classes`：班级
- `students`：学生
- `classrooms`：教室布局
- `seats`：座位定义
- `seating_rules`：排座规则
- `student_relations`：学生关系约束
- `seating_plans`：座位方案
- `seat_assignments`：座位分配结果

## 初步 API 方向

```text
GET    /api/v1/classes
POST   /api/v1/classes

GET    /api/v1/classes/{classId}/students
POST   /api/v1/classes/{classId}/students/import
PATCH  /api/v1/students/{studentId}

GET    /api/v1/classes/{classId}/classroom
PUT    /api/v1/classes/{classId}/classroom

GET    /api/v1/classes/{classId}/rules
PUT    /api/v1/classes/{classId}/rules

POST   /api/v1/classes/{classId}/seating-plans/generate
GET    /api/v1/classes/{classId}/seating-plans
GET    /api/v1/seating-plans/{planId}
PATCH  /api/v1/seating-plans/{planId}/assignments
POST   /api/v1/seating-plans/{planId}/export
```

## 下一步计划

1. 在座位方案详情页实现拖拽交换学生座位。
2. 支持锁定和解锁座位。
3. 支持保存人工微调后的座位分配。

## 2026-06-16 座位方案人工微调实现记录

当前阶段：阶段 6 座位表编辑和人工微调继续推进，拖拽交换、锁定座位和保存人工微调已完成接口级验收。

已完成：

- 在座位方案详情页增加学生座位拖拽交互，支持拖到空座位和与其他学生交换座位。
- 在座位卡片增加锁定和解锁操作，锁定座位不可作为拖拽源或交换目标。
- 增加「保存调整」按钮，页面存在未保存调整时才可提交。
- 前端新增 `savePlanAssignments(planId, data)`，调用 `PUT /seating/assignment/plan/{planId}` 保存人工微调。
- 后端新增方案维度保存分配接口，并复用方案和班级权限校验。
- 后端服务层保存前校验分配归属、目标座位归属、座位类型、可用状态、重复座位和未提交分配占用。
- 后端保存交换座位时采用事务内临时释放再写回，避免 `plan_id + seat_id` 唯一索引中间态冲突。

验证记录：

```text
mvn -DskipTests compile
mvn -DskipTests package
npm run build:prod
POST /login
POST /seating/plan
POST /seating/assignment
PUT  /seating/assignment/plan/{planId}
GET  /seating/assignment/list
DELETE /seating/assignment/{assignmentIds}
DELETE /seating/plan/{planId}
```

验证结果：

- 后端 Maven 编译成功，完整打包成功，并已使用新 jar 启动验证。
- 前端 Vite 生产构建成功。

## 2026-06-16 座位方案确认实现记录

当前阶段：阶段 6 座位表编辑和人工微调继续推进，座位方案确认启用能力已完成编译、打包和启动验收。

已完成：

- 后端新增方案确认接口 `PUT /seating/plan/{planId}/confirm`，沿用 `seating:plan:edit` 权限。
- 确认前复用方案和班级权限校验，并校验方案必须已有座位分配。
- 确认当前方案后，将同班级其他 `ACTIVE` 方案更新为 `ARCHIVED`，当前方案更新为 `ACTIVE` 并写入启用时间。
- 前端 `plan.js` 新增 `confirmPlan(planId)` 请求封装。
- 座位方案列表支持单选后点击「确认方案」，也支持行内确认非启用方案。
- 座位方案详情页支持确认当前方案；若存在未保存人工微调，会提示先保存调整。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 后端完整打包成功，并已使用新 jar 启动到 `8310`。
- `http://localhost:8310/captchaImage` 返回 `200`。
- 前端 Vite 生产构建成功。

下一步：

- 增加方案导出能力，优先支持老师直接导出当前正式座位表。
- 临时方案验证中，两个学生座位成功互换，锁定状态成功保存，保存后的 `assignSource` 为 `MANUAL`。
- 验证完成后，临时方案和临时分配已通过业务接口清理。

下一步：

- 补充方案详情页的空座、不可用座位编辑能力。
- 补充人工微调后的规则冲突或评分变化提示。
- 推进座位方案导出和历史方案能力。

## 2026-06-16 人工微调评分提示实现记录

当前阶段：阶段 6 座位表编辑和人工微调继续推进，人工微调保存后的评分刷新与冲突提示已完成接口级验收。

已完成：

- 排座引擎新增基于当前分配结果的复评估入口。
- 人工微调保存后自动复算硬规则冲突、软规则评分和方案总分。
- 保存后刷新 `seat_plan_score`，并同步更新 `seat_plan.total_score`。
- 保存接口返回评分变化和冲突列表，前端详情页据此展示中文成功或警告提示。

验证记录：

```text
mvn -DskipTests compile
mvn -DskipTests package
npm run build:prod
PUT /seating/assignment/plan/{planId}
GET /seating/score/list
GET /seating/plan/{planId}
```

验证结果：

- 后端 Maven 编译和完整打包成功。
- 前端 Vite 生产构建成功。
- 临时方案人工微调保存后返回评分结果，并生成 9 条评分明细。
- 验证完成后，临时方案、临时分配和临时评分明细已清理。

下一步：

- 补充方案详情页的空座和不可用座位编辑边界设计。
- 推进座位方案导出和历史方案能力。

## 2026-06-16 座位卡性别标识实现记录

已完成：

- 详情页通过学生列表补充座位分配的性别展示信息。
- 座位卡学生姓名前增加轻量性别标签，便于老师快速查看男女分布。
- 性别标识不改变座位卡主状态颜色，避免影响锁定和不可用状态判断。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端 Vite 生产构建成功。

## 2026-06-17 方案详情页空座编辑实现记录

当前阶段：阶段 6 座位表编辑和人工微调继续推进，方案内空座编辑和未安排学生回填已完成代码实现。

已完成代码实现：

- 座位方案详情页支持将已分配学生移出座位，形成空座。
- 详情页新增未安排学生列表，用于承接被移出的学生和当前方案未分配的学生。
- 未安排学生可拖拽回空座，重新加入座位方案。
- 锁定座位禁止设为空座；已占用座位不允许直接被未安排学生覆盖。
- 后端 `PUT /seating/assignment/plan/{planId}` 支持保存时删除未提交的旧分配，并插入新增的学生分配。
- 保存后继续刷新方案评分和硬规则冲突提示。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。
- 本次尚未做真实登录态下的页面拖拽和保存接口联调。

下一步：

- 使用真实登录态验证空座编辑、未安排学生拖回和保存后的数据回显。
- 推进座位方案导出能力，优先支持 Excel。
