# 座位表行列标注实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在座位方案详情页的座位表外侧增加行号与列号标注，帮助老师直接读出每个座位的行列位置。

**Architecture:** 只改 `detail.vue` 的渲染结构、坐标计算和局部样式，不改座位数据结构、不改接口、不改导出逻辑。座位网格继续沿用现有的 `displaySeatRows` 和 `gridStyle`，行列标注与当前视角保持一致，学生视角下随网格一起翻转。

**Tech Stack:** Vue 3、Element Plus、现有若依前端工程、`npm run build:prod`

---

### Task 1: 给座位网格外层加行列标尺容器

**Files:**
- Modify: `D:/AI/project/intelligentSeatingArrangement/RuoYi-Vue3-master/src/views/seating/plan/detail.vue`

- [ ] **Step 1: 以现有 `.seat-grid` 结构为基础，包一层新的外框，顶部放列号，左侧放行号。**

```vue
<div class="seat-grid-shell">
  <div class="seat-grid-corner" aria-hidden="true"></div>
  <div class="seat-column-labels" :style="gridStyle" aria-hidden="true">
    <div
      v-for="label in displayColumnLabels"
      :key="`col-${label}`"
      class="seat-grid-label seat-column-label"
    >
      {{ label }}
    </div>
  </div>
  <div class="seat-row-labels" aria-hidden="true">
    <div
      v-for="label in displayRowLabels"
      :key="`row-${label}`"
      class="seat-grid-label seat-row-label"
    >
      {{ label }}
    </div>
  </div>
  <div class="seat-grid" :style="gridStyle">
    <!-- 现有 seat-cell 保持不变 -->
  </div>
</div>
```

- [ ] **Step 2: 确认页面上座位卡片、讲台、空座和不可用座位的视觉表现没有被这层包裹破坏。**

### Task 2: 计算当前视角下的行号和列号

**Files:**
- Modify: `D:/AI/project/intelligentSeatingArrangement/RuoYi-Vue3-master/src/views/seating/plan/detail.vue`

- [ ] **Step 1: 在 `displayFlatSeats` 之后补两个 computed，分别输出当前视角的行号和列号。**

```js
const displayRowLabels = computed(() => displaySeatRows.value.map((row, index) => row[0]?.rowIndex ?? index + 1))
const displayColumnLabels = computed(() => {
  const firstRow = displaySeatRows.value.find(row => row.length) || []
  return firstRow.map(seat => seat.colIndex)
})
```

- [ ] **Step 2: 让列号和行号跟随 `viewMode` 一起翻转，这样学生视角下的标尺与座位朝向一致。**

### Task 3: 补充行列标尺样式

**Files:**
- Modify: `D:/AI/project/intelligentSeatingArrangement/RuoYi-Vue3-master/src/views/seating/plan/detail.vue`

- [ ] **Step 1: 在现有 `.classroom-main` 和 `.seat-grid` 样式旁边增加标尺外框、列号、行号和角标样式。**

```css
.seat-grid-shell {
  display: grid;
  grid-template-columns: 40px auto;
  grid-template-rows: 28px auto;
  gap: 8px;
  align-items: start;
}

.seat-grid-corner {
  width: 40px;
  height: 28px;
}

.seat-column-labels {
  display: grid;
  gap: 8px;
}

.seat-row-labels {
  display: grid;
  gap: 8px;
  align-content: start;
}

.seat-grid-label {
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px dashed #dcdfe6;
  border-radius: 4px;
  background: #fafafa;
  color: #909399;
  font-size: 12px;
  font-weight: 600;
}

.seat-column-label {
  height: 28px;
}

.seat-row-label {
  height: 94px;
}
```

- [ ] **Step 2: 检查小屏宽度下是否仍然能横向滚动查看完整座位表。**

### Task 4: 构建验证

**Files:**
- None

- [ ] **Step 1: 运行前端生产构建，确认新增模板和样式没有编译错误。**

```bash
cd D:/AI/project/intelligentSeatingArrangement/RuoYi-Vue3-master
npm run build:prod
```

- [ ] **Step 2: 若构建通过，再在浏览器里确认座位表顶部和左侧的编号能正常显示。**
