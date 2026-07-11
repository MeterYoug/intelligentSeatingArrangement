# 学生管理页浅色后台风格刷新 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将学生管理页统一成与班级管理页一致的浅色卡片式后台风格，同时收轻按钮、弹窗和删除操作的视觉层级。

**Architecture:** 前端只改 `src/views/seating/student/index.vue`，保持接口和业务字段不变，重点重排页面骨架、搜索区、列表工具条和新增／编辑弹窗的布局。页面继续复用现有学生接口、班级下拉和 Excel 导入弹窗，只做视觉与交互密度收口，避免扩散到其他模块。

**Tech Stack:** Vue3、Element Plus、若依前端页面模式、`npm run build:prod`。

---

### Task 1: 重构学生管理页外层布局

**Files:**
- Modify: `RuoYi-Vue3-master/src/views/seating/student/index.vue`

- [ ] **Step 1: 把页面外层改成与班级管理一致的浅色卡片结构。**

```vue
<div class="student-page">
  <section class="hero-panel">
    <div class="hero-copy">
      <p class="eyebrow">慧排座 · 学生管理</p>
      <h1>学生基础信息维护</h1>
      <p class="overview-text">维护班级学生、排座属性和导入入口，支撑后续排座和成绩同步流程。</p>
    </div>
    <div class="hero-actions">
      <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['seating:student:add']">新增学生</el-button>
      <el-button icon="Upload" @click="handleImport" v-hasPermi="['seating:student:import']">导入学生</el-button>
      <el-button icon="Download" @click="handleExport" v-hasPermi="['seating:student:export']">导出数据</el-button>
    </div>
  </section>
</div>
```

- [ ] **Step 2: 将筛选区和列表区分别包进 `el-card`，并保留当前查询和导出逻辑。**

```vue
<el-card v-show="showSearch" shadow="never" class="panel-card search-card">...</el-card>
<el-card shadow="never" class="panel-card table-card">...</el-card>
```

- [ ] **Step 3: 让列表工具条按钮保持浅色后台风格，并把删除按钮改为明显危险态。**

```vue
<el-button type="danger" plain icon="Delete" ...>删除</el-button>
<el-button link type="danger" class="danger-link" icon="Delete" ...>删除</el-button>
```

- [ ] **Step 4: 跑前端构建，确认骨架重排没有破坏页面编译。**

Run: `npm run build:prod`

Expected: 通过，说明模板和样式写法正确。

### Task 2: 收轻新增／编辑弹窗

**Files:**
- Modify: `RuoYi-Vue3-master/src/views/seating/student/index.vue`

- [ ] **Step 1: 把学生弹窗改为两列紧凑布局，控制弹窗宽度。**

```vue
<el-dialog :title="title" v-model="open" width="760px" append-to-body class="student-dialog">
  <el-form ref="studentRef" :model="form" :rules="rules" label-width="88px" class="student-form">
    <el-row :gutter="16" class="student-form-grid">
      <el-col :span="12"><el-form-item label="班级" prop="classId">...</el-form-item></el-col>
      <el-col :span="12"><el-form-item label="学号" prop="studentNo">...</el-form-item></el-col>
      <el-col :span="12"><el-form-item label="学生姓名" prop="studentName">...</el-form-item></el-col>
      <el-col :span="12"><el-form-item label="性别" prop="gender">...</el-form-item></el-col>
      <el-col :span="12"><el-form-item label="身高" prop="heightCm">...</el-form-item></el-col>
      <el-col :span="12"><el-form-item label="视力等级" prop="visionLevel">...</el-form-item></el-col>
      <el-col :span="12"><el-form-item label="成绩等级" prop="scoreLevel">...</el-form-item></el-col>
      <el-col :span="12"><el-form-item label="纪律等级" prop="disciplineLevel">...</el-form-item></el-col>
      <el-col :span="12"><el-form-item label="排序号" prop="sortNo">...</el-form-item></el-col>
      <el-col :span="12"><el-form-item label="状态" prop="status">...</el-form-item></el-col>
      <el-col :span="24"><el-form-item label="特殊需求" prop="specialNeed">...</el-form-item></el-col>
      <el-col :span="24"><el-form-item label="备注" prop="remark">...</el-form-item></el-col>
    </el-row>
  </el-form>
</el-dialog>
```

- [ ] **Step 2: 把底部按钮顺序改成取消在前、确定在后，并保持主按钮视觉更强。**

```vue
<template #footer>
  <div class="dialog-footer">
    <el-button @click="cancel">取消</el-button>
    <el-button type="primary" @click="submitForm">确定</el-button>
  </div>
</template>
```

- [ ] **Step 3: 调整弹窗样式，让输入框、文本域和单选区更像班级页。**

```scss
.student-dialog :deep(.el-dialog) { border-radius: 18px; }
.student-form :deep(.el-form-item) { margin-bottom: 14px; }
.student-form :deep(.el-input__wrapper),
.student-form :deep(.el-select__wrapper),
.student-form :deep(.el-textarea__inner) { border-radius: 10px; }
```

- [ ] **Step 4: 跑前端构建，确认弹窗布局和样式语法正确。**

Run: `npm run build:prod`

Expected: 通过，说明弹窗改造没有引入编译错误。

### Task 3: 更新进度文档并做最终验证

**Files:**
- Modify: `ROADMAP.md`
- Modify: `docs/project-progress.md`

- [ ] **Step 1: 在项目进度里补一条学生管理页风格统一记录。**

```text
2026-07-01 已将学生管理页改造成与班级管理一致的浅色卡片式后台布局，并收轻了新增／编辑弹窗与删除按钮样式。
```

- [ ] **Step 2: 在 ROADMAP 的阶段 9 收口记录里补充学生页一致化进展。**

```text
2026-07-01 已开始按班级页同一视觉语言统一学生管理页，保持浅色卡片后台风格和危险删除态。
```

- [ ] **Step 3: 再跑一次前端构建，确认文档同步后没有遗漏代码改动。**

Run: `npm run build:prod`

Expected: 通过，表示学生页改造和进度同步都已收口。

