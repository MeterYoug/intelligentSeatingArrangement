## 2026-06-29 座位方案差异文案收口

已完成：

- 方案详情页的历史方案差异展示已改为“原 第x排第y列 → 现 第x排第y列”的表达方式，便于老师直接理解变化位置。
- 该文案与已补齐的外圈排号／列号保持同一口径，不再只显示简写数字差异。
- 差异区已改为更紧凑的两列对照行展示，历史方案摘要也已收短，避免占用过多视觉空间。
- 前端生产构建已通过，页面展示可继续做真实浏览器回归。
- 2026-06-29 已继续回归撤销／重做链路，批量锁定、回退、再保存和成功提示均正常。
- 2026-06-29 已在真实浏览器中回归批量选择、批量锁定、保存、回退与再保存闭环，锁定与解锁后的按钮状态和保存成功提示均正常。
- 2026-06-29 已在真实浏览器中确认方案差异下拉可展开、可选中，并能回填差异明细。
- 2026-06-29 已去掉图片导出头部的导出时间字段，避免无业务意义的时间信息出现在导出结果里，待下次真实导出回归确认。
- 2026-06-29 已将 PDF 导出改为后端直接生成并下载，前端不再依赖隐藏 iframe 打印；后端 `mvn -DskipTests compile` 与前端 `npm run build:prod` 已通过，真实浏览器回归待后续补测。
- 2026-06-29 已在真实浏览器中点击「导出 PDF」，并在本机下载目录确认生成 `reg-copy-202606220237-教师视角-座位表.pdf`，PDF 导出链路已闭环。
- 2026-06-29 已在下载目录确认图片导出文件落地，图片导出链路可用。
- 2026-06-29 已补齐交付基线文档 `docs/delivery-baseline.md`，把演示数据、部署说明和稳定回归清单统一收口。
- 2026-06-29 已重新执行后端 `mvn -DskipTests compile` 与前端 `npm run build:prod`，两端验证均通过。
- 2026-06-29 已补充座位方案详情页动作状态回归测试，新增“启用中禁止确认”和“无座位布局禁止导出”两条覆盖，`node --test` 与前端构建均通过。

## 2026-06-27 座位方案详情页 Excel 导出外圈标注补齐

已完成：

- 详情页座位网格外圈已补齐排号和列号，与页面内座位定位保持一致。
- Excel 导出已同步加入外圈排号／列号标注，PNG 和 PDF 导出也保持同口径。
- 导出相关中文文案已恢复正常显示。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端编译成功。
- 前端生产构建成功。
- 本地后端启动时使用用户目录作为日志路径，解决 Windows 环境无法创建 `/home/ruoyi/logs` 的进程阻塞。

## 2026-06-27 ?????????????

????

- ?????????????????????????????????????????????
- ?????????????????????????????
- ???????????????????????

?????

```text
npm run build:prod
```

?????

- ?????????
- ??????????????????????

## 2026-06-27 ???????????

???????

- ??????????????????????????????????????????????????????
- ?????????????????????????????? ID??? `change` ?????????????????
- ??? `detailComparisonText` ???`node --test` ? `npm run build:prod` ?????
- ?????????????????????????????????????????????????

## 2026-06-27 前端生产构建与浏览器可用性复核

已完成：

- 已在 `RuoYi-Vue3-master` 下重新执行前端生产构建，`yarn build:prod` 成功通过。
- 已重新接通当前会话的内置浏览器实例，并确认当前详情页仍可在 `http://127.0.0.1:8222/seating/plan-detail/index/27` 打开。
- 通过真实页面复核，座位方案详情页的基础渲染、批量选择和保存相关交互仍可继续执行。

验证记录：

```text
yarn build:prod
Browser + current tab http://127.0.0.1:8222/seating/plan-detail/index/27
```

验证结果：

- 前端生产构建当前可通过。
- 内置浏览器实例当前可用，不再受“实例不可用”阻塞。
- 现阶段可以继续做详情页剩余交互和导出链路的真实回归。

## 2026-06-27 座位方案详情页批量锁定／解锁真实浏览器回归

已完成：

- 通过当前会话的内置浏览器实例，重新进入 `http://127.0.0.1:8222/seating/plan-detail/index/27` 并完成真实页面回归。
- 在已保存的锁定基线上，批量解锁前两格座位后，`保存调整` 按钮立即变为可点击，确认脏状态由当前快照自动驱动。
- 随后再将同一批座位批量锁回原基线，`保存调整` 按钮恢复禁用，页面返回干净状态。
- 本次回归同时确认批量选择、批量锁定、批量解锁、保存和回退提示都可在真实浏览器中正常工作。

验证记录：

```text
Browser + current tab http://127.0.0.1:8222/seating/plan-detail/index/27
```

验证结果：

- 真实浏览器实例可用。
- 批量解锁后保存按钮可点，说明这条路径的脏状态判断已恢复正常。
- 真实回归结束时，页面已被恢复为原始锁定基线，未留下脏状态。
## 2026-06-27 座位方案详情页脏状态自动计算修复记录

已完成：

- 将 `RuoYi-Vue3-master/src/views/seating/plan/detail.vue` 中的 `dirty` 改为自动计算，避免批量解锁后依赖手工同步导致的按钮状态滞后。
- `syncDirty()` 现在只负责清理调整结果提示，不再手工写回 `dirty`。
- `saveAssignments()` 和 `loadDetail()` 已移除对 `dirty` 的手工赋值，改由基线快照变化自动驱动保存按钮状态。

验证记录：

```text
node --test "RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailActionState.test.js" "RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailReadableText.test.js"
node -e "@vue/compiler-sfc parse + compileScript detail.vue"
```

验证结果：

- 相关单测 7 个用例全部通过。
- `detail.vue` 已通过 `@vue/compiler-sfc` 解析验证。
- 前端生产构建当前仍受仓库里已有的 Vite 打包配置问题影响，报出绝对路径 `index.html` emitted chunk 错误，需要单独处理构建配置后才能继续做全量构建验证。
## 2026-06-27 座位方案详情页批量解锁修复记录

已完成：

- 清理 `RuoYi-Vue3-master/src/views/seating/plan/detail.vue` 中前一轮调试残留，恢复详情页脚本可编译状态。
- 批量锁定／解锁、撤销／重做、保存调整、确认方案和导出相关函数已重新闭合并通过语法检查。
- 座位网格图片导出函数 `drawImageSeatGrid` 已补全闭合括号，避免 Vue 解析失败。

验证记录：

```text
node --test "RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailActionState.test.js" "RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailReadableText.test.js"
```

验证结果：

- 相关单测 7 个用例全部通过。
- `detail.vue` 已通过 `@vue/compiler-sfc` 解析验证。
- 本次未能继续真实浏览器回归，当前会话的浏览器动作被安全策略拦截。
# 慧排座项目进度

## 当前日期
2026-06-27

## 2026-06-27 搴т綅鏂规璇︽儏椤靛湪绾挎搷浣滈獙璇佽ˉ鍏?

- 宸叉纭牳瀵逛簡搴т綅鏂规璇︽儏椤电殑銆屾壒閲忛€夋嫨銆佹壒閲忛攣瀹氥€佹挙閿€銆侀噸鍋氥€嶉摼璺紝鍦ㄧ湡瀹炲墠绔唴锛屽彲閫氳繃鍗曞嚮搴т綅鍗℃涓婂崐鍖哄煙杩涘叆鎵归噺閫夋嫨銆?
- 骞堕€夋嫨 2 涓骇浣嶅悗鎵ц銆屾壒閲忛攣瀹氥€濊兘姝ｅ父鐢熸晥锛涜繑鍥炪€屾挙閿€銆嶅悗鍙仮澶嶄负鏈攣瀹氾紝銆岄噸鍋氥€嶅悗鍙堣兘鍐嶆鍥炲埌閿佸畾鐘舵€併€?
- 閫氳繃娴嬭瘯杩囩▼鍙互纭瘉锛屾湭鐐瑰嚮銆屼繚瀛樿皟鏁淬€濓紝鍥犳鏈鍙槸楠岃瘉鍓嶇浜や簰鐘舵€侊紝娌℃湁鏀瑰啓鍚庣淇濆瓨鎬併€?
- 方案详情页「保存调整」已在真实浏览器中验证：锁定两个座位后保存、刷新后回显一致；随后恢复为原始未锁定状态并再次刷新确认。
- 方案详情页在保存后的状态下仍可正常切换教师视角／学生视角，切换后座位状态和单选状态都符合预期。
- 鏈€鍚庝竴娆″皾璇曞埛鏂版椂锛屾祻瑙堝櫒浼氳瘽鍙戠敓浜嗛噸缃苟娌夐粯锛屾病鏈夌户缁ˉ楠岀粡杩囧埛鏂板悗鐨勫洖鏄惧眾鎬侊紝浣嗘湭淇濆瓨淇℃伅鏈鎻愪氦鍒板悗绔繚瀛樸€?

## 项目定位

「慧排座」面向老师，优先建设 PC Web 管理端，方便老师在电脑上为班级学生排座。微信小程序暂定为后期扩展，不纳入第一版 MVP。

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
- 慧排座第一版更需要快速验证业务闭环，而不是从零实现权限和后台框架。

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

1. 使用真实登录态核对 Excel、PNG 和 PDF 导出文件内容。
2. 梳理演示数据和 MVP 收尾清单。
3. 做一次完整 MVP 闭环验证：登录、建班级、导入学生、建教室、生成座位、微调、保存、确认和导出。

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

## 2026-06-17 座位方案 Excel 导出实现记录

当前阶段：阶段 7 导出和历史方案开始推进，当前优先补齐老师从方案详情页导出座位表的 Excel 能力。

已完成：

- 新增 `SeatPlanSeatExportRow` 导出模型，定义方案名称、班级、教室布局、座位行列、座位编号、座位状态、学生姓名、锁定状态和分配来源等列。
- 后端新增 `POST /seating/plan/{planId}/export-seat-table`，复用方案访问权限校验和 `seating:plan:export` 权限。
- 导出数据按 `seat_position` 的行列顺序生成，并合并当前方案 `seat_assignment` 分配结果。
- 导出内容覆盖普通座位、空座、过道、空位和不可用座位，便于老师核对完整教室布局。
- 前端方案详情页新增「导出 Excel」按钮，调用若依 `proxy.download` 下载当前方案座位表。
- 前端存在未保存人工调整时阻止导出，提示先保存调整，避免导出内容和页面状态不一致。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。
- 本次尚未做真实登录态下的浏览器下载联调。

下一步：

- 使用真实登录态验证空座编辑、未安排学生拖回、保存回显和 Excel 下载。
- 推进历史方案列表、复制和恢复能力。
- 继续补充 PDF / 图片导出能力。

## 2026-06-17 座位方案 Excel 导出版式修正记录

问题：

- 首版 Excel 导出使用注解式 `ExcelUtil` 输出，一行一个座位，结果更像数据明细清单，不像老师日常使用的座位表。

已修正：

- `POST /seating/plan/{planId}/export-seat-table` 改为使用 Apache POI 自定义写入二维网格。
- 顶部合并单元格展示方案名称、班级、教室布局和总评分。
- 按教室行列生成座位单元格，普通座位显示座位编号、学生姓名和锁定状态。
- 空座、过道、空位、不可用座位保留在对应行列位置，并用不同底色区分。
- 根据教室讲台位置在 Excel 中展示讲台，支持 `FRONT`、`BACK`、`LEFT`、`RIGHT`。

验证记录：

```text
mvn -DskipTests compile
```

验证结果：

- 后端 Maven 编译成功。
- 本次尚未做真实登录态下的浏览器下载和 Excel 内容人工核对。

## 2026-06-17 历史方案复制和恢复入口实现记录

当前阶段：阶段 7 导出和历史方案继续推进，已补齐历史方案复制为草稿、归档方案恢复启用的基础入口。

已完成：

- 后端新增 `POST /seating/plan/{planId}/copy`，沿用 `seating:plan:add` 权限，并复用方案访问权限校验。
- 复制方案会创建新的 `DRAFT` 草稿方案，方案类型为 `MANUAL`，名称追加「副本」和时间戳。
- 复制方案名称已按 `seat_plan.plan_name` 长度限制做截断保护，避免长方案名导致保存失败。
- 复制时同步复制原方案的座位分配记录和评分明细记录，生成新的主键，避免与原方案数据互相影响。
- 复制过程使用事务包裹，避免只复制方案、不复制分配或评分明细的中间状态。
- 前端 `src/api/seating/plan.js` 新增 `copyPlan(planId)`。
- 座位方案列表新增工具栏「复制方案」和行内「复制」入口，复制成功后跳转到新草稿方案详情页。
- 归档方案行内确认入口文案显示为「恢复」，仍复用现有确认启用逻辑，恢复后同班级原启用方案自动归档。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。
- 已由用户完成验收，复制方案、恢复启用和页面跳转满足当前阶段要求。

下一步：

- 继续补充 PDF / 图片导出能力。
- 梳理演示数据和 MVP 收尾清单。
- 做一次完整 MVP 闭环验证。

## 2026-06-17 座位方案图片导出实现记录

当前阶段：阶段 7 导出和历史方案继续推进，已补齐座位表 PNG 图片导出的第一版能力。

已完成：

- 座位方案详情页新增「导出图片」按钮，和「导出 Excel」放在同一个座位表操作区。
- 导出图片前检查 `dirty` 状态，存在未保存人工调整时提示先保存，避免导出旧数据。
- 前端使用 Canvas 根据 `seatRows`、`assignmentList`、`studentList` 和当前方案信息绘制 PNG，不新增依赖。
- PNG 顶部展示方案名称、班级、教室布局、总评分和导出时间。
- PNG 主体按教室布局二维网格绘制座位，保留讲台方向，区分普通座位、空座、过道、不可用座位和锁定座位。
- 已安排座位显示学生姓名和性别，锁定座位额外显示「已锁定」。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端 Vite 生产构建成功。
- 本次尚未做真实登录态下的浏览器 PNG 下载和图片内容人工核对。

下一步：

- 使用真实登录态核对 Excel 和 PNG 导出文件内容。
- 继续补充 PDF 导出能力。
- 梳理演示数据和 MVP 收尾清单。

## 2026-06-17 座位方案 PDF 导出实现记录

当前阶段：阶段 7 导出和历史方案继续推进，已补齐浏览器打印保存 PDF 的导出入口。

已完成：

- 座位方案详情页新增「导出 PDF」按钮。
- PDF 导出复用当前座位表 Canvas 快照，避免维护两套可视化导出版式。
- 导出前复用 `canExportSnapshot()` 检查未保存调整和空布局，确保导出内容可用。
- 点击导出后打开独立打印页，使用 A4 横向页面展示座位表图片，并自动触发浏览器打印。
- 打印页标题和图片 `alt` 已做 HTML 转义，避免方案名称影响打印页结构。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端 Vite 生产构建成功。
- 本次尚未做真实登录态下的浏览器打印保存 PDF 和内容人工核对。

下一步：

- 使用真实登录态核对 Excel、PNG 和 PDF 导出文件内容。
- 梳理演示数据和 MVP 收尾清单。
- 做一次完整 MVP 闭环验证。

## 2026-06-18 项目进度同步记录

当前阶段：阶段 7 导出和历史方案已完成第一版能力建设，准备进入真实登录态导出核对、演示数据梳理和 MVP 收尾闭环验证。

已同步：

- `ROADMAP.md` 顶部当前状态已从阶段 5 更新为阶段 7。
- 阶段 6 状态已调整为基本完成，并补充拖拽换座、锁定座位、空座编辑、保存人工微调和评分刷新能力。
- 阶段 7 已补充当前完成项：确认启用、复制方案、恢复启用、Excel 导出、PNG 导出和 PDF 打印保存入口。
- 当前优先级已更新为导出内容核对、演示数据梳理和完整 MVP 闭环验证。

验证记录：

```text
文档同步，无代码变更，未执行编译或构建。
```

验证结果：

- 本次仅更新进度文档，不改变运行代码、数据库结构或配置。

## 2026-06-18 座位方案图片和 PDF 导出报错修复记录

问题：

- 点击座位方案详情页「导出图片」和「导出 PDF」没有反应。
- 前端控制台报错：`Uncaught ReferenceError: parseTime is not defined`。
- 调用链为 `exportSeatPdf` / `exportSeatImage` -> `buildSeatImageCanvas` -> `drawImageHeader`。

根因：

- `parseTime` 在若依中注册为 Vue 全局属性，模板可以直接访问。
- `detail.vue` 的 `script setup` 普通函数不能直接访问该全局模板变量，Canvas 导出函数运行时找不到 `parseTime`。

已完成：

- 在 `RuoYi-Vue3-master/src/views/seating/plan/detail.vue` 显式导入 `parseTime`。
- 导出图片和导出 PDF 的 Canvas 头部绘制逻辑继续使用若依原有时间格式化工具。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端 Vite 生产构建成功。
- 尚未做真实登录态下的浏览器 PNG 下载和 PDF 打印保存人工核对。

## 2026-06-18 座位方案详情页视角切换实现记录

当前阶段：阶段 7 导出和历史方案继续推进，座位方案详情页补充教师视角和学生视角切换能力。

已完成：

- 在座位方案详情页座位表操作区新增「教师视角 / 学生视角」切换控件。
- 默认使用「教师视角」，保持原有座位表展示方向不变。
- 新增展示用座位行列计算，切换到「学生视角」时对座位行列做反向展示。
- 新增展示用讲台方向计算，学生视角下 `FRONT` / `BACK`、`LEFT` / `RIGHT` 会同步反转。
- 拖拽换座、锁定、空座编辑和保存仍基于原始 `seatId`，视角切换不改变业务数据。
- PNG 和 PDF 导出复用当前展示视角，并在导出图片头部显示当前视角。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端 Vite 生产构建成功。
- 尚未做真实登录态下的页面切换、PNG 下载和 PDF 打印保存人工核对。

## 2026-06-18 座位方案 Excel 视角导出实现记录

当前阶段：阶段 7 导出和历史方案继续推进，Excel 导出已补齐跟随当前页面视角的能力。

已完成：

- 前端详情页导出 Excel 时会把当前 `viewMode` 提交给后端。
- 后端导出接口 `POST /seating/plan/{planId}/export-seat-table` 新增视角参数处理。
- 教师视角保持原有二维座位表顺序不变。
- 学生视角下，后端会反转座位表行列顺序，并同步反转讲台方向。
- Excel 标题摘要已增加「教师视角 / 学生视角」标识，和 PNG、PDF 导出保持一致。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。
- 尚未做真实登录态下的 Excel 下载和内容人工核对。

## 2026-06-18 成绩管理扩展实现记录

当前阶段：在现有 MVP 基础上扩展学生成绩管理能力，年级归属仍在班级管理中维护，成绩只绑定班级和考试批次。

已完成代码实现：

- 班级管理新增 `school_stage`、`grade_code` 字段映射，前端年级改为下拉选择，不再手动输入。
- 新增成绩扩展 SQL 草案：`RuoYi-Vue-springboot3/sql/seating_score_20260618.sql`。
- 新增科目模板表、考试批次表和学生成绩表的后端对象、Mapper、Service 和 Controller。
- 考试批次创建时从班级快照学段、年级和科目，成绩导入不再重复选择年级。
- 小学科目模板包含「科学」，初中和高中按学段年级使用不同科目列表。
- 成绩 Excel 模板按班级科目动态生成，导入时按学号匹配学生。
- 导入成绩后自动计算总分、班级排名和 A/B/C/D 成绩等级。
- 新增成绩等级同步能力，将某次考试等级写回 `seat_student.score_level`，供现有排座算法继续使用。
- 前端新增成绩管理页面和成绩相关 API。
- 通用 Excel 导入弹窗下载模板时已支持传递额外参数。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译通过。
- 前端 Vite 生产构建通过。
- 尚未导入 `seating_score_20260618.sql` 到真实数据库。
- 尚未在真实登录态下完成成绩菜单、考试创建、模板下载、成绩导入和等级同步联调。

下一步：

- 导入 `seating_score_20260618.sql` 后做真实页面联调。
- 使用一个小学班级验证模板包含「科学」。
- 导入成绩并同步等级后，重新生成座位方案验证现有成绩均衡规则仍可工作。

## 2026-06-18 MVP 闭环验证记录

当前阶段：阶段 7 导出和历史方案已完成真实登录态闭环验证，已进入 MVP 收尾前的兼容性收口。

已完成：

- 使用真实登录态跑通 `登录 -> 建班级 -> 建学生 -> 建教室 -> 保存布局 -> 生成方案 -> 人工交换并锁定 -> 确认方案 -> 当前视角导出 Excel -> 清理临时数据`。
- 闭环验证中，方案生成前后座位分配数量均为 `4`，人工调整后锁定座位数量为 `1`，方案确认后状态为 `ACTIVE`。
- 已验证 Excel 当前视角导出链路：教师视角导出文件只包含“教师视角”，学生视角导出文件只包含“学生视角”，且两个导出文件工作表 XML 不同。
- 已修复 `RuoYi-Vue-springboot3/ruoyi-seating/src/main/resources/mapper/seating/SeatClassMapper.xml` 的查询兼容问题，班级列表和按名称回查恢复可用。
- 所有验证产生的临时 `Codex*`、`Dbg*`、`ExpDbg*` 数据已清理完成。

本次暴露的兼容风险：

- 当前数据库 `seat_class.semester` 字段长度与页面录入习惯不一致，传“上学期”会报 `Data too long for column 'semester'`，真实联调需暂时使用 `1/2`。
- 当前班级扩展字段 `school_stage`、`grade_code` 代码已进入前后端，但数据库还未完成结构落库。这次只做了查询兼容，没有做 schema 变更。

验证记录：

```text
mvn -DskipTests compile
POST /login
POST /seating/class
GET  /seating/class/list
POST /seating/student
GET  /seating/student/list
POST /seating/classroom
PUT  /seating/position/classroom/{classroomId}/layout
POST /seating/plan/generate
GET  /seating/assignment/list
PUT  /seating/assignment/plan/{planId}
PUT  /seating/plan/{planId}/confirm
POST /seating/plan/{planId}/export-seat-table?viewMode=TEACHER
POST /seating/plan/{planId}/export-seat-table?viewMode=STUDENT
DELETE /seating/plan/{planId}
DELETE /seating/classroom/{classroomId}
DELETE /seating/student/{studentId}
DELETE /seating/class/{classId}
```

验证结果：

- 后端 Maven 编译成功。
- 真实登录态闭环验证通过，当前“当前视角导出 Excel”可用。
- 班级列表恢复可用，但 `semester` 字段长度和 `school_stage` / `grade_code` 未落库仍需在 MVP 收尾前处理。

下一步：

1. 对齐 `seat_class` 的数据库结构、页面表单和后端字段语义。
2. 用真实浏览器页面补做教师视角 / 学生视角下的 PNG、PDF 手工导出核对。
3. 再决定是否推进成绩管理 SQL 落库和联调。

## 2026-06-18 当前进度同步：成绩管理待数据库导入和联调

当前状态：

- 成绩管理扩展代码已完成，包含考试批次、科目模板、学生成绩、Excel 动态模板导入和成绩等级同步。
- 班级管理已支持年级下拉选择，并保存 `school_stage`、`grade_code`、`grade_name`。
- 成绩模块不再选择年级，只绑定班级；考试创建时根据班级快照年级和科目。
- 小学成绩模板包含「科学」，初中、高中按学段和年级匹配科目。
- 已补充数据库设计文档和 SQL 草案。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端编译通过。
- 前端生产构建通过。
- 本次未执行真实数据库迁移。
- 本次未进行真实登录态页面联调。

下一步：

1. 导入 `RuoYi-Vue-springboot3/sql/seating_score_20260618.sql`。
2. 使用管理员刷新菜单权限，确认「成绩管理」菜单显示。
3. 新建考试批次并下载成绩模板。
4. 使用小学班级验证模板包含「科学」。
5. 导入成绩并同步等级，验证学生管理中的成绩等级更新。
6. 重新生成座位方案，确认成绩均衡规则仍按最新等级参与评分。

## 2026-06-18 成绩扩展 SQL 导入记录

当前状态：

- 用户已确认 `RuoYi-Vue-springboot3/sql/seating_score_20260618.sql` 导入数据库。
- 成绩管理扩展进入真实登录态联调阶段。
- 数据库侧应已包含：`seat_subject`、`seat_exam`、`seat_student_score`，以及 `seat_class.school_stage`、`seat_class.grade_code`。
- 菜单权限 SQL 已包含「成绩管理」菜单和考试、成绩相关权限。

下一步联调清单：

1. 重新登录后台，确认「成绩管理」菜单是否显示。
2. 在班级管理中确认班级年级字段是否完整。
3. 创建考试批次，检查考试是否自动带出班级年级和科目快照。
4. 下载成绩模板，确认小学模板包含「科学」。
5. 导入成绩，检查总分、排名和等级。
6. 同步成绩等级到学生档案。
7. 重新生成座位方案，验证成绩均衡规则联动。

待验证：

- 数据库导入后的接口可用性。
- 页面权限是否完整。
- 成绩导入事务回滚是否符合预期。

## 2026-06-18 班级管理学期枚举收口记录

当前阶段：MVP 收尾兼容性继续收口，先解决班级页字段和当前数据库结构不一致的问题。

已完成：

- 后端 `SeatClassMapper.xml` 已恢复 `school_stage`、`grade_code` 的真实字段映射。
- 后端 `SeatClassServiceImpl` 已新增班级数据归一化，统一把学期值收口为 `1/2`，并根据 `gradeCode` 自动补齐 `schoolStage`、`gradeName`。
- 前端班级管理页已重写为干净模板，修复原文件乱码造成的模板属性断裂问题。
- 班级管理页查询区、列表和编辑弹窗已统一使用年级下拉与学期下拉，列表中的学期会显示为「上学期 / 下学期」。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端编译成功。
- 前端构建成功。
- 班级管理页已具备和当前数据库字段一致的录入与展示方式。

下一步：

1. 用真实浏览器补做一次班级新增、编辑、列表筛选验证。
2. 选一个旧班级保存一次，确认 `grade_code`、`school_stage` 能被补齐。
3. 继续验证成绩模块是否继续正确复用班级年级快照。

## 2026-06-18 座位方案命名收口记录

当前阶段：阶段 7 导出和历史方案核心能力已完成，开始进入阶段 8 MVP 收尾。

已完成：

- 座位方案新增、修改和智能生成表单统一要求填写方案名称，前端限制最多 64 个字符。
- 后端统一处理方案名称首尾空格、空值和长度校验。
- 复制方案时增加副本名称输入，复制接口支持接收新的 `planName`。
- 未传副本名称时，后端仍会自动生成带时间戳的副本名称，保持接口兼容。
- Excel 和 PNG 下载文件名、PDF 打印标题统一使用“方案名称 + 当前视角 + 座位表”。
- 下载文件名会替换 Windows 不允许使用的字符。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端编译成功。
- 前端生产构建成功。
- 用户已确认成绩管理真实登录态联调和 Excel、PNG、PDF 导出内容校验完成。
- 本次新增的复制方案命名交互尚未做真实登录态页面操作验证。

下一步：

1. 使用真实登录态验证复制方案时填写新名称、列表回显和详情页标题。
2. 执行完整 MVP 闭环回归。
3. 收口菜单、权限、字典、初始化 SQL 和演示数据。

## 2026-06-18 MVP 闭环回归环境阻塞记录

本次目标：

- 执行 `登录 -> 生成并命名 -> 复制并指定名称 -> 微调保存 -> 确认启用 -> 导出 Excel、PNG、PDF` 的完整浏览器回归。

已完成：

- 检查本地端口和依赖服务状态。
- 后端执行 `mvn -DskipTests package` 成功。
- 前端开发服务在 `8222` 启动成功。
- Browser 插件不可用，按前端测试规范改用本机 Playwright。
- Playwright 复用本机 Edge 完成登录页截图检查，页面渲染正常。
- 实际启动最新后端并核对失败日志。
- 回归结束后已关闭本次启动的前端临时进程。

阻塞证据：

```text
MySQL 3306：未监听
Redis 6379：未监听
后端错误：Communications link failure
根因：连接 localhost:3306/intelligent_seating 被拒绝
```

未完成：

- 真实登录。
- 复制方案命名交互验证。
- 微调、保存和确认回归。
- Excel、PNG、PDF 导出回归。

重启后续跑步骤：

1. 确认 MySQL `3306` 和 Redis `6379` 已监听。
2. 启动后端并确认 `http://localhost:8310/captchaImage` 返回 200。
3. 启动前端并继续完整浏览器闭环回归。

## 2026-06-19 成绩管理新增考试显示修复记录

问题：

- 成绩管理页新增考试成功后，页面列表看起来没有变化。
- 代码排查发现新增接口只返回通用成功状态，前端无法拿到新考试 `examId`，因此新增后只刷新考试下拉，没有自动选中新考试，也没有刷新当前成绩列表。
- 当前页面表格承载的是成绩列表，考试刚创建但未导入成绩时本来没有成绩行，缺少明确空状态提示会造成误判。

已完成：

- `SeatExamController` 新增考试成功后返回带生成主键的 `SeatExam` 对象。
- `score/index.vue` 新增考试成功后自动切换到对应班级、刷新考试下拉、选中新建考试并刷新成绩列表。
- 成绩表格增加空状态文案：未选考试时提示先选择或新增考试，已选考试但无成绩时提示先导入成绩。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。
- 尚未执行真实登录态浏览器操作复核。

## 2026-06-19 成绩管理考试列表和班级科目修复记录

问题：

- 成绩管理页直接展示成绩明细，新增考试后不能直观看到考试批次列表。
- 新建考试的科目来自年级科目模板，不支持按班级配置科目。
- 真实页面中科目出现重复，说明历史科目来源缺少统一去重保护。

已完成：

- `seat_class` 新增 `subject_snapshot` 字段，迁移脚本为 `RuoYi-Vue-springboot3/sql/seating_class_subject_20260619.sql`。
- 班级管理页面新增科目多选，保存到班级科目快照，并在班级列表展示。
- 后端班级查询、插入、更新已映射 `subject_snapshot`。
- 新建考试优先使用班级科目生成 `seat_exam.subject_snapshot`。
- 成绩导入模板优先使用班级科目，考试科目和页面展示统一去重。
- 成绩管理页改为先展示考试批次列表，点击「查看成绩」后再展示该考试的成绩明细。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。
- 尚未执行数据库迁移脚本，尚未做真实登录态页面复核。

后续动作：

1. 执行 `RuoYi-Vue-springboot3/sql/seating_class_subject_20260619.sql`。
2. 用真实页面新增或编辑班级，确认科目保存和回显正常。
3. 新增考试，确认考试列表可见且科目来自班级配置。
4. 下载成绩模板，确认模板表头与班级科目一致。

## 2026-06-19 成绩明细独立页面调整记录

问题：

- 成绩管理主页面同时承载考试批次和成绩明细，新增考试后容易误判为没有展示考试列表。
- 用户明确要求「查看成绩」跳转到新页面展示成绩列表。

已完成：

- 用户已确认 `RuoYi-Vue-springboot3/sql/seating_class_subject_20260619.sql` 已导入数据库。
- `RuoYi-Vue3-master/src/views/seating/score/index.vue` 已收敛为考试批次列表页。
- 新增 `RuoYi-Vue3-master/src/views/seating/score/detail.vue`，独立展示单个考试的成绩列表。
- `RuoYi-Vue3-master/src/router/index.js` 新增隐藏路由 `/seating/score-detail/index/:examId`。
- 成绩详情页保留导入成绩、同步等级、查询、分页、修改和删除能力。
- 新增考试后刷新当前班级考试列表，并清空考试名称筛选，避免新增记录被旧筛选条件隐藏。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。
- 尚未进行真实登录态页面点击复核。

## 2026-06-19 学生导入弹窗交互调整记录

问题：

- 学生管理页导入学生时先弹出「选择导入班级」，再进入上传弹窗，流程多一步。
- 当前需求是把班级选择和上传文件放在一个小弹窗中，去掉「下一步」。

已完成：

- `RuoYi-Vue3-master/src/views/seating/student/index.vue` 已移除独立的「选择导入班级」弹窗。
- 学生导入弹窗内新增班级下拉，点击「导入」后直接进入同一个弹窗完成班级选择和文件上传。
- 打开导入弹窗时会默认带入当前查询栏已选择的班级。
- 提交上传前校验班级必选，未选择时提示「请选择导入班级」。
- `RuoYi-Vue3-master/src/components/ExcelImportDialog/index.vue` 新增顶部插槽和提交前校验钩子，供学生导入这种带前置字段的场景复用。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端 Vite 生产构建成功。
- 尚未进行真实登录态页面点击和实际文件上传复核。

## 2026-06-19 排座规则权重接入修复记录

问题：

- 用户只配置了「男女搭配均衡」且权重为 100，但方案详情仍展示多项未启用评分项。
- 总分按所有内置评分项简单平均，导致男女搭配扣 30 分后总分仍显示 96.67，容易误解为权重没有生效。
- 排座优化过程没有把 `GENDER_BALANCE`、`SCORE_BALANCE` 这类同桌搭配规则作为优化目标，只在生成后评分明细中扣分。

已完成：

- `SeatingEngine` 新增启用规则过滤，只按启用的 `seat_rule` 生成对应评分明细。
- 总分改为按启用规则的 `rule_weight` 加权平均。
- 软规则放置惩罚改为只在对应规则启用时生效，并按规则权重折算。
- 男女搭配和成绩强弱同桌搭配已纳入生成和交换优化的惩罚计算。
- 移除容量、随机种子这类非用户规则评分项对总分的稀释。

验证记录：

```text
mvn -DskipTests compile
```

验证结果：

- 后端 Maven 编译成功。
- 尚未在真实页面重新生成方案并复核男女搭配规则得分。

## 2026-06-19 过道布局同桌识别修复记录

问题：

- 方案详情页能看到 `C4-C5`、`C10-C11` 等位置存在同性同桌，但评分明细仍显示「同性同桌数：0」。
- 根因是后端 `sameDesk` 固定使用奇偶列判断同桌，只识别 `C1-C2`、`C3-C4`、`C5-C6` 这种连续座位布局。
- 当前教室布局包含过道列，真实同桌应按每行可用座位顺序两两配对，例如 `C1-C2`、`C4-C5`、`C7-C8`、`C10-C11`。

已完成：

- `SeatingEngine` 新增按完整座位布局生成的桌对索引。
- 同桌判断改为优先使用桌对索引，不再依赖固定奇偶列。
- 过道和不可用座位不参与桌对配对。
- 生成方案、保存人工调整后的重算评分、学生关系约束中的同桌判断已统一使用新逻辑。
- `SeatAssignmentServiceImpl` 在重算评分时传入完整教室座位布局，避免只根据已安排座位误判同桌。

验证记录：

```text
mvn -DskipTests compile
```

验证结果：

- 后端 Maven 编译成功。
- 尚未在真实页面重新生成或保存方案后复核评分明细。

## 2026-06-19 MVP 真实测试收口记录

测试结论：

- 用户已确认成绩管理完整闭环测试通过。
- 用户已确认学生导入单弹窗真实操作测试通过。
- 用户已确认排座规则回归测试通过。
- 用户已确认导出功能回归测试通过。

已覆盖范围：

- 班级科目保存和回显。
- 新增考试、考试列表、成绩详情页跳转。
- 成绩模板下载、成绩导入、同步等级。
- 同步等级后重新生成座位方案，验证成绩强弱均衡联动。
- 学生导入时在同一弹窗内选择班级和上传 Excel。
- 男女搭配、成绩强弱均衡、不能同桌规则。
- 带过道布局下的同桌识别和评分明细。
- 座位方案 Excel、图片、PDF 导出。
- 学生和成绩相关导出。

文档收口：

- 新增 `README.md`，补充项目介绍、本地运行、老师使用流程、导入说明、规则权重说明和常见问题。

最终验证：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。

下一步：

1. 提交本次 MVP 收口文档。
2. 进入体验增强阶段规划。

## 2026-06-19 管理后台首页去若依内容记录

问题：

- 管理后台首页仍展示若依默认框架介绍、官网、QQ群、捐赠和更新日志，和当前慧排座定位不一致。

已完成：

- `RuoYi-Vue3-master/src/views/index.vue` 已替换为慧排座工作台。
- 首页入口聚焦班级管理、学生管理、成绩管理、教室布局、排座规则和座位方案。
- 首页新增排座流程、今日待办和规则说明等业务信息，不再展示若依默认内容。
- 已确认首页文件内未检索到若依默认介绍、官网、捐赠、QQ群和更新日志文案。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端 Vite 生产构建成功。
- 当前本地未安装 Playwright，未做浏览器截图复核。

## 2026-06-19 系统命名更新记录

已完成：

- 系统正式命名为「慧排座」。
- 浏览器标题、登录页标题、注册页标题、侧边栏 Logo 标题和页脚版权已统一使用「慧排座」。
- 管理后台首页工作台名称已更新为「慧排座工作台」。
- 前端 `package.json` 描述、作者和仓库地址已同步更新。
- `README.md`、`ROADMAP.md` 和项目进度文档已同步更新系统名称。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端 Vite 生产构建成功。

## 2026-06-19 Logo 接入记录

已完成：

- 新增 `RuoYi-Vue3-master/src/assets/logo/logo.svg`，作为「慧排座」后台侧边栏 Logo。
- 新增 `RuoYi-Vue3-master/public/favicon.svg`，作为浏览器图标。
- `RuoYi-Vue3-master/index.html` 已改为引用 `/favicon.svg`。
- 侧边栏 Logo 和表单构建工具示例 Logo 已改为引用新的 `logo.svg`。
- 旧的 `logo.png` 和 `favicon.ico` 暂时保留，未删除历史资源。

验证记录：

```text
npm run build:prod
```

验证结果：

- 前端 Vite 生产构建成功。
- 本地图片查看工具不支持直接预览 SVG，本次未做浏览器截图复核。

## 2026-06-20 MVP 收口提交与体验增强规划

收口结论：

- 阶段 8 MVP 收尾已完成，项目进入体验增强规划阶段。
- MVP 已覆盖班级、学生、成绩、教室布局、排座规则、智能生成、人工微调、历史方案与 Excel／PNG／PDF 导出。
- 真实操作回归已确认成绩管理、学生导入、排座规则和导出能力可用。

最终验证：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。

阶段 9 规划：

1. P0：优化空状态、表单校验、导入失败定位和操作反馈。
2. P1：提升座位编辑效率，包括撤销／重做、批量锁定／解锁和未安排学生快速定位。
3. P1：增强方案可解释性，展示规则得分原因、冲突位置和方案差异。
4. P2：增加班级、规则、教室布局模板及新学期复制能力。

范围约束：

- 保持 PC Web 管理端定位，不扩展小程序、家长端、多租户和 AI 对话式排座。
- 体验增强项在开发前必须明确教师任务、验收标准与数据影响。

## 2026-06-20 体验增强 P0 页面与异常场景盘点

盘点结论：

- 班级、学生、教室布局、排座规则和座位方案等列表页使用通用加载表格，但多数页面未区分首次无数据、筛选无结果和接口加载失败。
- 座位方案详情页已具备骨架屏、保存冲突提示和未安排学生空状态；但 `loadDetail` 任一请求失败时仅结束加载，页面缺少失败说明、重试和返回入口。
- 学生导入组件已覆盖班级前置校验、未选择文件和文件格式校验；上传失败、部分行失败的结果展示尚未形成统一交互规范。
- 方案生成、保存、确认和导出已有成功提示与部分前置校验，P0 实现时需验证失败后是否完整保留输入及未保存座位状态。

P0 实施范围：

1. 为班级、学生、教室布局、排座规则、座位方案、考试列表补齐差异化空状态和加载失败状态。
2. 为座位方案详情补齐加载失败提示、重试和返回方案列表操作。
3. 为学生、成绩导入补齐统一的上传失败与部分失败反馈，并保留可读的错误行信息。
4. 为生成、保存调整、确认方案、导出补齐失败后的状态保持验收。

验收场景：

1. 首次进入空列表时，页面明确说明可新增或导入何种数据。
2. 使用筛选条件无结果时，页面提示调整或重置筛选，不误导为系统故障。
3. 模拟列表或详情请求失败时，页面显示失败原因、重试按钮，并且不会永久停留在加载态。
4. 导入空文件、非 Excel 文件、服务端校验失败和部分行失败时，老师能够看到下一步处理方式。
5. 保存、确认或导出失败后，表单内容、当前座位拖拽结果和未保存状态保持不丢失。

本次不实施：

- 不修改后端接口、数据库结构或排座算法。
- 不提前实现 P1 的撤销／重做、批量锁定、评分解释和模板能力。

## 2026-06-20 体验增强 P0 第一轮实现记录

已完成：

- 新增 `SeatingTableEmptyState` 通用组件，并接入班级、学生、教室布局、排座规则、座位方案和考试列表。
- 列表页现在区分首次无数据、筛选无结果和接口加载失败，并提供新增、重置筛选或重新加载操作。
- 座位方案详情加载主数据或关联数据失败时，会退出骨架屏并显示重新加载、返回方案列表操作。
- `ExcelImportDialog` 已区分业务失败响应和网络上传失败；失败时保留导入弹窗及已选择班级，便于老师重新选择文件后重试。

验证记录：

```text
npm run build:prod
```

浏览器验证：

- Browser 插件在当前环境不可用，改用本机 Edge 的 Playwright 通道。
- 登录 `http://127.0.0.1:8222/login` 后，模拟座位方案列表接口 500，确认失败提示和「重新加载」按钮可见；恢复接口后点击重试，失败状态消失。
- 模拟座位方案详情主接口 500，确认失败页展示「重新加载」和「返回方案列表」。
- 模拟学生导入接口 500，确认错误提示出现，导入弹窗和已选班级保持可见。

验证结果：

- 前端 Vite 生产构建成功。
- 三个关键失败恢复路径通过浏览器验证。
- 模拟 500 产生的浏览器控制台错误为测试注入的预期结果，不是前端运行时错误。

待继续验证：

- 班级、学生、教室布局、排座规则和考试列表的真实接口失败场景。
- 成绩导入的部分行失败结果展示。
- 生成、保存调整、确认方案和导出失败后的状态保持。

## 2026-06-20 体验增强 P0 列表失败恢复补充验证

验证范围：

- 登录后依次进入班级、学生、教室布局、排座规则和考试列表。
- 每个列表页均模拟首次列表接口返回 HTTP 500，确认页面显示对应加载失败提示和「重新加载」操作。
- 点击重新加载后恢复真实接口，确认失败空状态消失并恢复列表数据。
- 将座位方案列表失败状态切换到 `390 × 844` 视口，确认失败说明和重试按钮仍可见、可操作。

验证结果：

- 六个核心列表页的加载失败与重试路径均通过浏览器验证。
- 模拟 500 的控制台错误均为测试注入的预期结果。

待继续验证：

- 成绩导入部分行失败的错误行展示。
- 生成、保存调整、确认方案和导出失败后，表单与未保存座位状态的保持。

## 2026-06-20 成绩导出功能补齐

已完成：

- 成绩详情页新增「导出成绩」按钮，受 `seating:studentScore:export` 权限控制。
- 导出调用既有 `POST /seating/student-score/export` 接口，携带当前考试、班级和页面筛选条件。
- 导出文件名使用「考试名称_成绩.xlsx」。

验证记录：

```text
npm run build:prod
```

浏览器验证：

- 登录后进入成绩管理，打开考试成绩详情页。
- 点击「导出成绩」，浏览器成功下载 `3_成绩.xlsx`。
- 验证过程中未发现前端控制台错误。

## 2026-06-20 成绩学号排序与导出排序

已完成：

- 成绩详情页学号列支持服务端升序／降序排序，默认按学号升序展示。
- 导出区域新增「按学号排序导出」复选框，默认勾选；取消后沿用原有排名顺序。
- 后端通过 `studentNoOrder` 白名单参数切换排序，仅允许 `ASC` 与 `DESC`，不接受任意排序字段。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。
- 浏览器已确认成绩详情页出现学号排序列和「按学号排序导出」控件，无前端控制台错误。

待验证：

- 重启后端后，用包含多条成绩的考试确认学号升序、降序和按学号导出顺序。

## 2026-06-20 学号末尾数字自然排序修复

问题：

- 学号字段为字符串，直接排序会出现 `1、10、11、…、2` 的字典序结果。

已完成：

- 学号排序改为提取末尾连续数字后按数值升序／降序排列。
- 数字后缀相同的学号按完整学号兜底排序；没有数字后缀的学号排在最后。
- 页面学号排序和「按学号排序导出」复用同一后端排序逻辑。

验证记录：

```text
mvn -DskipTests compile
```

验证结果：

- 后端 Maven 编译成功。

待验证：

- 重启后端后，用实际学号验证 `五十-1、五十-2、五十-10` 的页面和导出顺序。

## 2026-06-20 成绩总分升降序排序

已完成：

- 成绩详情页总分列支持服务端升序／降序排序，空总分固定排在最后。
- 切换总分排序时会清除学号排序；切换学号排序时会清除总分排序。
- 勾选「按学号排序导出」时按学号末尾数字正序导出；取消勾选后沿用当前列表排序，包括总分升序或降序。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。

待验证：

- 重启后端后，用包含多条不同总分的考试确认总分升序、降序和导出顺序。

## 2026-06-22 体验增强 P0 与 P1 第一轮实现

已完成：

- 用户确认成绩详情页学号自然排序、总分升降序及导出顺序已在真实数据下校验通过。
- 通用 Excel 导入弹窗保留失败后的弹窗和文件选择，并展示逐行错误信息；成绩导入服务改为返回纯文本错误行，避免 HTML 换行内容被上传组件截断。
- 座位方案详情支持撤销／重做、批量选择座位、批量锁定／解锁，以及未安排学生按姓名或学号筛选。
- 座位方案详情支持选择同班历史方案，展示每位学生的前后座位差异；硬规则冲突提示会附带当前座位位置。
- 修复了新增编辑工具后，座位区工具栏在 1440 像素桌面视口溢出的布局问题。

验证记录：

```text
mvn -DskipTests compile
npm run build:prod
Playwright mocked API regression
```

验证结果：

- 后端 Maven 编译成功。
- 前端 Vite 生产构建成功。
- 模拟成绩导入失败时，逐行错误信息可见，导入弹窗和已选文件仍保留。
- 模拟保存调整失败时，保存按钮仍可用，未保存座位状态未丢失。
- 方案详情页的批量锁定、撤销、未安排学生筛选和历史方案差异展示均通过浏览器交互验证。

补充验证：

- 已在真实登录态下进入方案详情，浏览器拦截确认方案、Excel 导出、图片导出和 PDF 导出并返回失败结果。
- 四个失败场景后，方案详情仍可见，草稿状态和确认按钮保持可用，未向后端执行确认或导出操作。

## 2026-06-22 座位方案真实浏览器回归补充记录

已完成：

- 本地联调环境已恢复：MySQL `3306` 正常监听；后端以显式 Redis `6380` 参数启动成功；`/captchaImage` 返回 `200`。
- 使用 Playwright 复用本机 Edge，在真实登录态下完成 `登录 -> 一键生成并命名 -> 列表搜索 -> 复制并指定名称 -> 详情页拖拽交换 -> 保存调整 -> 学生视角导出 Excel / 图片 / PDF -> 返回列表核对` 的非破坏性浏览器回归。
- 回归过程中生成草稿方案 `reg-gen-202606220237`，复制得到草稿方案 `reg-copy-202606220237`。
- 复制后详情页可见新名称；返回列表后可按新名称搜索到该方案，状态显示为“草稿”。
- 详情页拖拽交换 `暴昊城` 与 `何梓涵` 后，「保存调整」按钮成功进入可点击状态；保存成功后按钮恢复禁用。
- 学生视角导出命名已核对：Excel 文件名为 `reg-copy-202606220237-学生视角-座位表.xlsx`，图片文件名为 `reg-copy-202606220237-学生视角-座位表.png`，PDF 预览弹窗标题为 `reg-copy-202606220237-学生视角-座位表`。

验证记录：

```text
http://127.0.0.1:8222/login
Playwright + Edge：真实登录、生成、复制、拖拽保存、导出、列表回显
```

验证结果：

- 登录页、首页、座位方案列表、方案详情页渲染正常，无空白页或前端错误覆盖层。
- 浏览器控制台未出现业务错误；仅出现 Element Plus `el-radio` 组件 `label` 兼作 `value` 的废弃警告。
- 生成、复制、保存调整与 Excel / 图片 / PDF 导出均已完成真实浏览器验证。

待确认：

- 「确认启用当前方案」的真实成功回归尚未执行。当前数据库只有一个班级，确认新方案会自动归档现有 `ACTIVE` 方案，属于会改变现有业务状态的操作。
- 本次产生的 `reg-gen-202606220237` 与 `reg-copy-202606220237` 两个临时草稿方案仍保留在本地数据库，尚未清理。

## 2026-06-22 座位方案确认启用真实回归记录

已完成：

- 在用户明确允许影响当前启用方案后，使用真实浏览器对 `reg-copy-202606220237` 执行了详情页「确认方案」操作。
- 确认弹窗提交后，页面显示“确认成功”，方案详情中的状态文案变为“启用”，绿色确认按钮消失。
- 浏览器回归后立即用接口核对状态：`planId=27` 已由 `DRAFT` 更新为 `ACTIVE`，`activeTime=2026-06-22`；原启用方案 `planId=14` 已自动切换为 `ARCHIVED`。
- 已补充列表页浏览器证据：`reg-copy-202606220237` 在列表中显示“启用”，原方案 `2-副本-20260617123557` 在列表中显示“归档”。

验证记录：

```text
Playwright + Edge：详情页点击确认方案
PUT /seating/plan/27/confirm -> 200
GET /seating/plan/list?pageNum=1&pageSize=100
```

验证结果：

- 真实“确认启用当前方案”链路验证通过。
- 同班级原启用方案自动归档逻辑验证通过。
- 浏览器控制台无业务异常，仍只有 Element Plus `el-radio` 的废弃警告。

当前状态：

- 当前启用方案已切换为 `reg-copy-202606220237`。
- `reg-gen-202606220237` 仍作为临时草稿方案保留在库中，尚未清理。

## 2026-06-22 座位方案视角切换告警修复

问题：

- 进入座位方案详情页时，浏览器控制台持续输出 Element Plus `el-radio` 的废弃警告。
- 排查确认告警源自教师／学生视角切换控件，当前写法使用 `label` 作为值，不符合新版 Element Plus 推荐方式。

已完成：

- 将 `RuoYi-Vue3-master/src/views/seating/plan/detail.vue` 中的两个 `el-radio-button` 从 `label=\"TEACHER/STUDENT\"` 改为 `value=\"TEACHER/STUDENT\"`。
- 保持页面交互、视角切换语义和现有数据状态不变，只消除控制台弃用告警。

验证记录：

```text
npm run build:prod
Playwright + Edge：登录后打开 /seating/plan-detail/index/27，采集 console warning / error
```

验证结果：

- 前端 Vite 生产构建成功。
- 方案详情页仍正常显示当前启用方案 `reg-copy-202606220237` 及“启用”状态。
- 页面控制台 `warnings=[]`、`errors=[]`，本次 `el-radio` 废弃警告已消失。

## 2026-06-22 当前进度同步：MVP 已完成，进入阶段 9 收口

当前结论：

- 「慧排座」第一版 MVP 主闭环已经完成。
- 已完成真实浏览器回归验证，核心链路包括：登录、生成方案、复制方案、拖拽微调、保存调整、确认启用、Excel／PNG／PDF 导出。
- 当前工作重点已从核心功能建设，转为阶段 9「体验增强」收口。

阶段状态：

- 阶段 8「MVP 收尾」已完成。
- 阶段 9 `P0` 已完成。
- 阶段 9 `P1` 已完成第一轮实现。
- 阶段 9 `P2` 仍处于规划中，尚未进入正式开发收口。

阶段 9 已完成内容：

- 已补齐班级、学生、教室布局、排座规则、座位方案、考试列表的空状态、失败状态和重试入口。
- 已补齐座位方案详情加载失败提示、重新加载和返回列表入口。
- 已补齐学生与成绩导入失败反馈，并保留导入上下文。
- 已补充生成、保存调整、确认方案、导出失败后的状态保持验证。
- 已支持撤销／重做。
- 已支持批量选择座位、批量锁定／解锁。
- 已支持未安排学生按姓名或学号快速筛选。
- 已支持同班历史方案差异对比，并补充硬规则冲突位置提示。
- 已修复座位编辑工具栏在常见桌面宽度下的布局溢出问题。
- 已修复方案详情页教师／学生视角切换的前端废弃告警。

当前待收口事项：

- 继续统一批量操作、撤销／重做、保存调整等高频交互的反馈文案与禁用条件。
- 补充基于真实业务数据的高频操作回归，而不只依赖模拟失败场景。
- 继续优化评分明细、规则解释和冲突提示的教师可读性。
- 补齐演示数据、部署说明和稳定回归清单，形成后续演示与交付基线。

下一步优先级：

1. 完成阶段 9 `P1` 收口。
2. 补齐演示数据、部署说明和稳定回归清单。
3. 在 `P1` 稳定后，再进入阶段 9 `P2`，推进模板复用与新学期复制能力。

风险提示：

- `ROADMAP.md` 如不同步更新，会继续落后于本进度文档的实际状态。
- 阶段 9 已实现内容较多，如不尽快统一验收口径，后续容易出现“功能已做、状态不清”的问题。

## 2026-06-26 P1 收口：座位方案详情高频交互反馈统一

已完成：

- 已将本轮 P1 收口设计与实施计划写入 `docs/superpowers/specs/2026-06-26-p1-plan-detail-action-feedback-design.md` 与 `docs/superpowers/plans/2026-06-26-p1-plan-detail-action-feedback.md`。
- 新增 `RuoYi-Vue3-master/src/views/seating/plan/detailActionState.js`，统一描述撤销、重做、批量锁定／解锁、保存、确认、导出的禁用状态与阻断文案。
- `RuoYi-Vue3-master/src/views/seating/plan/detail.vue` 已接入统一动作守卫；高频按钮在加载中、保存中、确认中、未保存调整、未选择座位等场景下，前端反馈已保持一致。
- 已补充保存失败与确认失败的分离提示，避免老师在高频操作时收到错误的反馈文案。
- 新增 `RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailActionState.test.js`，覆盖未保存调整、未选择座位、保存进行中三类核心动作状态。

验证记录：

```text
node --test "RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailActionState.test.js"
npm run build:prod
```

验证结果：

- 前端动作状态单测通过，3 个用例全部通过。
- 前端 Vite 生产构建成功，方案详情页本轮收口改动未引入编译错误。

当前剩余：

- 仍需补一轮基于真实业务数据的高频交互回归，重点核对撤销／重做、批量锁定／解锁、保存、确认、导出在连续操作下的提示是否符合老师预期。
- 评分明细、规则解释和冲突提示的教师可读性收口仍未完成，继续保留在阶段 9 `P1` 待办内。
## 2026-06-27 P1 收口：座位方案详情老师可读性增强

已完成：

- 已将本轮老师可读性收口设计与实施计划写入 `docs/superpowers/specs/2026-06-27-p1-plan-detail-readability-design.md` 与 `docs/superpowers/plans/2026-06-27-p1-plan-detail-readability.md`。
- 新增 `RuoYi-Vue3-master/src/views/seating/plan/detailReadableText.js`，将常见评分明细和硬规则冲突文本翻译为老师可直接理解的说明句。
- `RuoYi-Vue3-master/src/views/seating/plan/detail.vue` 已接入新解释层；评分表列名由“明细”调整为“说明”，常见规则项不再直接暴露 `required`、`pairs`、`affected` 等技术键名。
- 前排约束、不能同桌、不能相邻三类高频冲突，现已优先展示“规则含义 + 当前座位”的业务化提示。
- 新增 `RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailReadableText.test.js`，覆盖评分说明翻译和冲突提示翻译的核心场景。

验证记录：

```text
node --test "RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailActionState.test.js" "RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailReadableText.test.js"
npm run build:prod
```

验证结果：

- 前端纯逻辑测试共 7 个用例全部通过。
- 前端 Vite 生产构建成功，本轮收口未引入编译错误。

当前阻塞：

- 原计划补做一轮真实浏览器高频交互回归，但当前会话内置浏览器实例不可用：`agent.browsers.list()` 返回空数组，无法继续按既定浏览器回归路径执行。
- 本轮未改用其他未确认的浏览器控制方式绕过，先将阻塞记录到文档，等待后续具备可用浏览器实例后再补回归证据。

当前剩余：

- 仍需补一轮基于真实业务数据的高频交互回归，确认撤销／重做、批量锁定／解锁、保存、确认、导出在连续操作下的提示是否符合老师预期。
- 如后续老师反馈仍觉得说明不够直观，再基于真实使用反馈微调解释句，而不是继续暴露技术字段。
## 2026-06-27 浏览器实例可用性复核记录

已完成：

- 按当前会话的浏览器控制流程重新测试了内置浏览器实例可用性。
- 已复核 `iab` 实例获取结果与浏览器实例列表。

验证记录：

```text
agent.browsers.get("iab")
agent.browsers.list()
```

验证结果：

- `agent.browsers.get("iab")` 返回 `Browser is not available: iab`。
- `agent.browsers.list()` 返回空数组 `[]`。
- 结论是当前会话仍无可用内置浏览器实例，无法继续执行真实页面回归。







