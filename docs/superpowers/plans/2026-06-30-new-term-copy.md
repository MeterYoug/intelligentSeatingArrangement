# 新学期复制创建 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为班级列表增加一条“新学期复制创建”闭环，默认复制学生、学生关系、排座规则和教室布局，同时明确不复制考试、成绩、座位方案、分配结果和方案评分。

**Architecture:** 后端在 `SeatClassController` 增加一个专用复制接口，由 `SeatClassServiceImpl` 在单个事务里完成源班级、学生、关系、规则和教室布局的复制。前端在班级列表页增加一个复制弹窗，并把复制表单逻辑抽到一个小的状态帮助模块里，方便用 `node --test` 直接验证默认值和校验规则。

**Tech Stack:** Spring Boot 3、MyBatis、`@Transactional`、Vue3、Element Plus、`node --test`、`npm run build:prod`。

---

### Task 1: 定义新学期复制请求和后端接口合同

**Files:**
- Create: `RuoYi-Vue-springboot3/ruoyi-seating/src/main/java/com/ruoyi/seating/domain/request/SeatClassCopyRequest.java`
- Modify: `RuoYi-Vue-springboot3/ruoyi-seating/src/main/java/com/ruoyi/seating/service/ISeatClassService.java`
- Modify: `RuoYi-Vue-springboot3/ruoyi-admin/src/main/java/com/ruoyi/web/controller/seating/SeatClassController.java`

- [ ] **Step 1: 写出请求对象和接口签名。**

```java
package com.ruoyi.seating.domain.request;

public class SeatClassCopyRequest
{
    private String className;
    private String schoolYear;
    private String semester;
    private Boolean copyStudents = Boolean.TRUE;
    private Boolean copyRelations = Boolean.TRUE;
    private Boolean copyRules = Boolean.TRUE;
    private Boolean copyClassroomLayout = Boolean.TRUE;

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getSchoolYear() { return schoolYear; }
    public void setSchoolYear(String schoolYear) { this.schoolYear = schoolYear; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public Boolean getCopyStudents() { return copyStudents; }
    public void setCopyStudents(Boolean copyStudents) { this.copyStudents = copyStudents; }
    public Boolean getCopyRelations() { return copyRelations; }
    public void setCopyRelations(Boolean copyRelations) { this.copyRelations = copyRelations; }
    public Boolean getCopyRules() { return copyRules; }
    public void setCopyRules(Boolean copyRules) { this.copyRules = copyRules; }
    public Boolean getCopyClassroomLayout() { return copyClassroomLayout; }
    public void setCopyClassroomLayout(Boolean copyClassroomLayout) { this.copyClassroomLayout = copyClassroomLayout; }
}
```

```java
SeatClass copyNewTerm(Long classId, SeatClassCopyRequest copyRequest, String operName);
```

```java
@PreAuthorize("@ss.hasPermi('seating:class:add')")
@Log(title = "鎺掑骇鐝骇", businessType = BusinessType.INSERT)
@PostMapping("/{classId}/copy-new-term")
public AjaxResult copyNewTerm(@PathVariable("classId") Long classId,
        @RequestBody SeatClassCopyRequest copyRequest)
{
    return success(seatClassService.copyNewTerm(classId, copyRequest, getUsername()));
}
```

- [ ] **Step 2: 先跑一次编译，确认现在会卡在服务实现缺失上。**

Run: `mvn -DskipTests compile`

Expected: 失败于 `SeatClassServiceImpl` 还没有 `copyNewTerm(...)` 实现，说明接口合同已经接到位。

- [ ] **Step 3: 补上接口声明后再编译一次。**

Run: `mvn -DskipTests compile`

Expected: 仍然失败，但错误会收敛到实现逻辑和辅助方法，说明控制器和请求对象已经接通。

- [ ] **Step 4: 提交这一小步。**

```bash
git add RuoYi-Vue-springboot3/ruoyi-seating/src/main/java/com/ruoyi/seating/domain/request/SeatClassCopyRequest.java
git add RuoYi-Vue-springboot3/ruoyi-seating/src/main/java/com/ruoyi/seating/service/ISeatClassService.java
git add RuoYi-Vue-springboot3/ruoyi-admin/src/main/java/com/ruoyi/web/controller/seating/SeatClassController.java
git commit -m "feat: 定义新学期复制接口"
```

### Task 2: 实现后端事务复制和复制规则

**Files:**
- Modify: `RuoYi-Vue-springboot3/ruoyi-seating/src/main/java/com/ruoyi/seating/service/impl/SeatClassServiceImpl.java`
- Create: `RuoYi-Vue-springboot3/ruoyi-seating/src/main/java/com/ruoyi/seating/service/support/SeatClassCopySupport.java`
- Create: `RuoYi-Vue-springboot3/ruoyi-seating/src/test/java/com/ruoyi/seating/service/support/SeatClassCopySupportTest.java`

- [ ] **Step 1: 先把“下一学期”与教室来源选择逻辑写成可测帮助类。**

```java
package com.ruoyi.seating.service.support;

import org.apache.commons.lang3.StringUtils;

public final class SeatClassCopySupport
{
    private SeatClassCopySupport()
    {
    }

    public static String nextSchoolYear(String schoolYear, String semester)
    {
        if (StringUtils.isBlank(schoolYear))
        {
            return "";
        }
        if ("1".equals(semester))
        {
            return schoolYear;
        }
        if ("2".equals(semester))
        {
            String[] parts = schoolYear.split("-");
            if (parts.length == 2)
            {
                try
                {
                    int startYear = Integer.parseInt(parts[0]);
                    return (startYear + 1) + "-" + (startYear + 2);
                }
                catch (NumberFormatException ignored)
                {
                    return schoolYear;
                }
            }
        }
        return schoolYear;
    }

    public static String nextSemester(String semester)
    {
        return "1".equals(semester) ? "2" : "1";
    }
}
```

```java
package com.ruoyi.seating.service.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class SeatClassCopySupportTest
{
    @Test
    void nextSemesterShouldMoveForwardOneTerm()
    {
        assertEquals("2", SeatClassCopySupport.nextSemester("1"));
        assertEquals("1", SeatClassCopySupport.nextSemester("2"));
    }

    @Test
    void nextSchoolYearShouldAdvanceWhenCopyingFromSecondTerm()
    {
        assertEquals("2026-2027", SeatClassCopySupport.nextSchoolYear("2025-2026", "2"));
    }
}
```

- [ ] **Step 2: 先跑帮助类测试，确认它们还会失败。**

Run: `mvn -Dtest=SeatClassCopySupportTest test`

Expected: 失败，因为帮助类还只是壳子。

- [ ] **Step 3: 在 `SeatClassServiceImpl` 里实现事务复制。**

关键实现要点：

```java
@Override
@Transactional(rollbackFor = Exception.class)
public SeatClass copyNewTerm(Long classId, SeatClassCopyRequest copyRequest, String operName)
{
    SeatClass source = selectSeatClassByClassId(classId);
    if (source == null)
    {
        throw new ServiceException("班级不存在");
    }
    if (copyRequest == null)
    {
        throw new ServiceException("复制参数不能为空");
    }
    SeatClass copiedClass = buildCopiedClass(source, copyRequest, operName);
    SeatClassroom copiedClassroom = copyClassroomLayoutIfNeeded(source, copiedClass, copyRequest, operName);
    Map<Long, Long> studentIdMap = copyStudentsIfNeeded(source, copiedClass, copyRequest, operName);
    copyRelationsIfNeeded(source, copiedClass, copyRequest, studentIdMap, operName);
    copyRulesIfNeeded(source, copiedClass, copyRequest, operName);
    return copiedClass;
}
```

实现细节要包含：

- 复制时继承 `schoolStage`、`gradeCode`、`gradeName`、`subjectSnapshot`、`teacherId` 和 `deptId`。
- 目标班级默认启用，`delFlag` 为存在态。
- 教室布局只复制默认且启用的主布局；找不到时，如果用户勾选了布局复制就直接抛错。
- 学生关系必须依赖新学生 ID 映射重建，不能直接拷贝旧 ID。
- 不复制考试、成绩、座位方案、分配结果和方案评分。

- [ ] **Step 4: 跑帮助类测试和后端编译，确认复制逻辑接通。**

Run:

```text
mvn -Dtest=SeatClassCopySupportTest test
mvn -DskipTests compile
```

Expected:

- 帮助类测试通过。
- 后端编译通过。

- [ ] **Step 5: 提交后端复制实现。**

```bash
git add RuoYi-Vue-springboot3/ruoyi-seating/src/main/java/com/ruoyi/seating/service/impl/SeatClassServiceImpl.java
git add RuoYi-Vue-springboot3/ruoyi-seating/src/main/java/com/ruoyi/seating/service/support/SeatClassCopySupport.java
git add RuoYi-Vue-springboot3/ruoyi-seating/src/test/java/com/ruoyi/seating/service/support/SeatClassCopySupportTest.java
git commit -m "feat: 实现新学期复制后端逻辑"
```

### Task 3: 提供前端复制表单状态和接口调用

**Files:**
- Create: `RuoYi-Vue3-master/src/views/seating/class/newTermCopyState.js`
- Create: `RuoYi-Vue3-master/src/views/seating/class/__tests__/newTermCopyState.test.js`
- Modify: `RuoYi-Vue3-master/src/api/seating/class.js`

- [ ] **Step 1: 把复制表单默认值和校验抽出来。**

```js
export function createNewTermCopyForm(source = {}) {
  return {
    sourceClassId: source.classId ?? null,
    sourceClassName: source.className ?? "",
    className: "",
    schoolYear: source.schoolYear ?? "",
    semester: source.semester === "1" ? "2" : "1",
    copyStudents: true,
    copyRelations: true,
    copyRules: true,
    copyClassroomLayout: true,
  }
}

export function normalizeCopyRelations(form) {
  if (!form.copyStudents) {
    form.copyRelations = false
  }
  return form
}

export function validateNewTermCopyForm(form) {
  if (!String(form.className || "").trim()) return "目标班级名称不能为空"
  if (!String(form.schoolYear || "").trim()) return "目标学年不能为空"
  if (!String(form.semester || "").trim()) return "目标学期不能为空"
  return ""
}
```

```js
import { strict as assert } from "node:assert"
import { createNewTermCopyForm, normalizeCopyRelations, validateNewTermCopyForm } from "../newTermCopyState.js"

assert.equal(createNewTermCopyForm({ classId: 27, className: "五一一班", semester: "1" }).semester, "2")
assert.equal(normalizeCopyRelations({ copyStudents: false, copyRelations: true }).copyRelations, false)
assert.equal(validateNewTermCopyForm({ className: "", schoolYear: "2026-2027", semester: "1" }), "目标班级名称不能为空")
```

- [ ] **Step 2: 先跑纯逻辑测试，确认默认值和依赖关系都对。**

Run: `node --test "RuoYi-Vue3-master/src/views/seating/class/__tests__/newTermCopyState.test.js"`

Expected: 失败，直到状态模块实现完成。

- [ ] **Step 3: 增加 API 调用。**

```js
export function copyClassNewTerm(classId, data) {
  return request({
    url: `/seating/class/${classId}/copy-new-term`,
    method: "post",
    data,
  })
}
```

- [ ] **Step 4: 跑前端构建，确认 API 文件能被打包。**

Run: `npm run build:prod`

Expected: 通过，说明状态模块和 API 语法正确。

- [ ] **Step 5: 提交前端基础支撑。**

```bash
git add RuoYi-Vue3-master/src/views/seating/class/newTermCopyState.js
git add RuoYi-Vue3-master/src/views/seating/class/__tests__/newTermCopyState.test.js
git add RuoYi-Vue3-master/src/api/seating/class.js
git commit -m "feat: 增加新学期复制前端支撑"
```

### Task 4: 接入班级列表弹窗并完成端到端验证

**Files:**
- Modify: `RuoYi-Vue3-master/src/views/seating/class/index.vue`

- [ ] **Step 1: 在班级列表行操作区加入“新学期复制”入口，并挂上弹窗。**

弹窗需要包含：

- 源班级信息只读展示。
- 目标班级名称输入框。
- 目标学年输入或选择。
- 目标学期选择器。
- 四个默认勾选项：
  - 复制学生。
  - 复制学生关系。
  - 复制排座规则。
  - 复制教室布局。

提交时调用 `copyClassNewTerm(sourceClassId, formData)`。

- [ ] **Step 2: 提交成功后刷新列表，并优先选中新班级。**

```js
copyClassNewTerm(sourceClassId, submitData).then((response) => {
  proxy.$modal.msgSuccess("复制成功")
  getList()
  if (response.data?.classId) {
    queryParams.value.classId = response.data.classId
    getList()
  }
})
```

- [ ] **Step 3: 跑前端构建，确认列表页接入无语法问题。**

Run: `npm run build:prod`

Expected: 通过。

- [ ] **Step 4: 用真实浏览器验证完整闭环。**

验证步骤：

1. 打开班级管理列表。
2. 选择一个已有班级。
3. 打开“新学期复制”弹窗。
4. 创建一个新班级。
5. 确认新班级里的学生、规则和教室布局被带过去。
6. 确认考试、成绩和排座方案没有被复制。

- [ ] **Step 5: 把本次实现相关的文档状态更新到最新。**

更新目标文件：

- `ROADMAP.md`
- `docs/project-progress.md`

然后提交最终实现：

```bash
git add RuoYi-Vue3-master/src/views/seating/class/index.vue
git add ROADMAP.md
git add docs/project-progress.md
git commit -m "feat: 完成新学期复制创建"
```

## 覆盖检查

这份计划覆盖了 spec 的每个核心点：

- 班级列表入口，由 Task 4 负责。
- 复制弹窗默认勾选项，由 Task 3 和 Task 4 负责。
- 后端事务复制，由 Task 1 和 Task 2 负责。
- 明确不复制考试、成绩和方案数据，由 Task 2 的实现规则负责。
- 前端构建与真实浏览器验证，由 Task 3 和 Task 4 负责。

## 自检规则

执行过程中如果发现以下情况，要先停一下再改：

- 目标班级的默认学期推导规则和 spec 冲突。
- 教室布局来源不清楚，出现多布局复制歧义。
- 学生关系映射没有把旧 ID 全部替换掉。
- 前端弹窗逻辑开始侵入班级页面之外的其他页面。


