<template>
  <div class="classroom-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <p class="eyebrow">慧排座 · 教室布局</p>
        <h1>教室布局配置</h1>
        <p class="overview-text">
          维护班级对应的教室行列、讲台位置和座位类型，支撑排座生成与人工微调流程。
        </p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['seating:classroom:add']">新增布局</el-button>
      </div>
    </section>

    <el-card v-show="showSearch" shadow="never" class="panel-card search-card">
      <template #header>
        <div class="panel-header">
          <span>筛选条件</span>
          <small>按班级、布局名称、讲台位置和默认状态快速定位</small>
        </div>
      </template>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px" class="search-form">
        <el-form-item label="班级" prop="classId">
          <el-select v-model="queryParams.classId" placeholder="请选择班级" filterable clearable style="width: 180px">
            <el-option v-for="item in classOptions" :key="item.classId" :label="item.className" :value="item.classId" />
          </el-select>
        </el-form-item>
        <el-form-item label="教室布局名称" prop="classroomName">
          <el-input
            v-model="queryParams.classroomName"
            placeholder="请输入教室布局名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="讲台位置" prop="platformPosition">
          <el-select v-model="queryParams.platformPosition" placeholder="请选择讲台位置" clearable style="width: 140px">
            <el-option v-for="item in platformOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否默认" prop="isDefault">
          <el-select v-model="queryParams.isDefault" placeholder="请选择" clearable style="width: 100px">
            <el-option label="否" value="0" />
            <el-option label="是" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" plain icon="Search" @click="handleQuery">搜索</el-button>
          <el-button plain icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="panel-card table-card">
      <template #header>
        <div class="panel-header">
          <span>教室布局列表</span>
          <small>支持修改、删除和导出</small>
        </div>
      </template>
      <div class="table-toolbar">
        <el-row :gutter="10" class="toolbar-row">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="Plus"
              @click="handleAdd"
              v-hasPermi="['seating:classroom:add']"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="success"
              plain
              icon="Edit"
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['seating:classroom:edit']"
            >修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['seating:classroom:remove']"
            >删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="warning"
              plain
              icon="Download"
              @click="handleExport"
              v-hasPermi="['seating:classroom:export']"
            >导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </div>

      <el-table v-loading="loading" :data="classroomList" @selection-change="handleSelectionChange" class="classroom-table">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="班级" align="center" prop="className" min-width="120" />
        <el-table-column label="教室布局名称" align="center" prop="classroomName" min-width="160" />
        <el-table-column label="座位行数" align="center" prop="rowCount" width="100" />
        <el-table-column label="座位列数" align="center" prop="colCount" width="100" />
        <el-table-column label="讲台位置" align="center" prop="platformPosition" width="110">
          <template #default="scope">{{ optionLabel(platformOptions, scope.row.platformPosition) }}</template>
        </el-table-column>
        <el-table-column label="默认布局" align="center" prop="isDefault" width="100">
          <template #default="scope">{{ scope.row.isDefault === '1' ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template #default="scope"><dict-tag :options="sys_normal_disable" :value="scope.row.status" /></template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
          <template #default="scope">
            <el-button type="primary" plain icon="Edit" class="row-action-btn" @click="handleUpdate(scope.row)" v-hasPermi="['seating:classroom:edit']">修改</el-button>
            <el-button type="danger" plain icon="Delete" class="row-action-btn" @click="handleDelete(scope.row)" v-hasPermi="['seating:classroom:remove']">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <seating-table-empty-state
            :description="listEmptyDescription"
            :action-text="listEmptyActionText"
            @action="handleListEmptyAction"
          />
        </template>
      </el-table>

      <div class="table-footer">
        <pagination
          v-show="total > 0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </el-card>

    <el-dialog :title="title" v-model="open" width="1040px" append-to-body class="classroom-dialog">
      <div class="dialog-shell">
        <section class="dialog-section">
          <div class="dialog-section-header">
            <span>基础信息</span>
            <small>先确定班级和布局尺寸，再编辑座位画布</small>
          </div>
          <el-form ref="classroomRef" :model="form" :rules="rules" label-width="100px" class="classroom-form">
            <el-row :gutter="16" class="classroom-form-grid">
              <el-col :span="24">
                <el-form-item label="班级" prop="classId">
                  <el-select v-model="form.classId" placeholder="请选择班级" filterable style="width: 100%">
                    <el-option v-for="item in classOptions" :key="item.classId" :label="item.className" :value="item.classId" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="教室布局名称" prop="classroomName">
                  <el-input v-model="form.classroomName" placeholder="请输入教室布局名称" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="座位行数" prop="rowCount">
                  <el-input-number v-model="form.rowCount" :min="1" :max="30" controls-position="right" style="width: 100%" @change="handleGridSizeChange" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="座位列数" prop="colCount">
                  <el-input-number v-model="form.colCount" :min="1" :max="30" controls-position="right" style="width: 100%" @change="handleGridSizeChange" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="讲台位置" prop="platformPosition">
                  <el-radio-group v-model="form.platformPosition" class="status-group">
                    <el-radio v-for="item in platformOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="是否默认" prop="isDefault">
                  <el-radio-group v-model="form.isDefault" class="status-group">
                    <el-radio value="0">否</el-radio>
                    <el-radio value="1">是</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-radio-group v-model="form.status" class="status-group">
                    <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注" prop="remark">
                  <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </section>

        <section class="dialog-section layout-section">
          <div class="dialog-section-header">
            <span>布局画布</span>
            <small>点击格子切换座位、过道和不可用</small>
          </div>
          <div class="layout-editor">
            <div v-if="form.platformPosition === 'FRONT'" class="platform platform-horizontal">讲台</div>
            <div class="layout-body">
              <div v-if="form.platformPosition === 'LEFT'" class="platform platform-vertical">讲台</div>
              <div class="seat-grid-wrap">
                <div class="seat-grid" :style="gridStyle">
                  <button
                    v-for="cell in flatLayoutGrid"
                    :key="cell.rowIndex + '-' + cell.colIndex"
                    type="button"
                    class="seat-cell"
                    :class="'seat-type-' + cell.seatType"
                    @click="toggleSeatType(cell)"
                  >
                    {{ seatTypeMeta[cell.seatType].shortLabel }}
                  </button>
                </div>
              </div>
              <div v-if="form.platformPosition === 'RIGHT'" class="platform platform-vertical">讲台</div>
            </div>
            <div v-if="form.platformPosition === 'BACK'" class="platform platform-horizontal">讲台</div>
            <div class="layout-help">
              <div class="layout-tip">点击格子循环切换：座位、过道、不可用。</div>
              <div class="layout-legend">
                <span v-for="item in seatTypeLegend" :key="item.value" class="legend-item">
                  <i :class="'legend-dot seat-type-' + item.value"></i>{{ item.label }}
                </span>
              </div>
            </div>
          </div>
        </section>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup name="Classroom">
import { listClassroom, getClassroom, delClassroom, addClassroom, updateClassroom } from "@/api/seating/classroom"
import { listClass } from "@/api/seating/class"
import { getClassroomLayout, saveClassroomLayout } from "@/api/seating/position"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const platformOptions = [
  { value: "FRONT", label: "前方" },
  { value: "BACK", label: "后方" },
  { value: "LEFT", label: "左侧" },
  { value: "RIGHT", label: "右侧" }
]

const classroomList = ref([])
const classOptions = ref([])
const layoutGrid = ref([])
const open = ref(false)
const loading = ref(true)
const listError = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const seatTypeCycle = ["0", "2", "1"]
const seatTypeMeta = {
  "0": { label: "座位", shortLabel: "座" },
  "1": { label: "不可用", shortLabel: "禁" },
  "2": { label: "过道", shortLabel: "道" }
}
const seatTypeLegend = [
  { value: "0", label: "座位" },
  { value: "2", label: "过道" },
  { value: "1", label: "不可用" }
]

const flatLayoutGrid = computed(() => layoutGrid.value.flat())
const gridStyle = computed(() => ({
  gridTemplateColumns: `repeat(${Number(form.value.colCount) || 1}, 52px)`
}))

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    classId: undefined,
    classroomName: undefined,
    rowCount: undefined,
    colCount: undefined,
    platformPosition: undefined,
    aisleAfterCols: undefined,
    isDefault: undefined,
    status: undefined,
  },
  rules: {
    classId: [
      { required: true, message: "请选择班级", trigger: "change" }
    ],
    classroomName: [
      { required: true, message: "教室布局名称不能为空", trigger: "blur" }
    ],
    rowCount: [
      { required: true, message: "座位行数不能为空", trigger: "blur" }
    ],
    colCount: [
      { required: true, message: "座位列数不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

const hasActiveFilters = computed(() => Object.entries(queryParams.value).some(([key, value]) => !["pageNum", "pageSize"].includes(key) && value !== undefined && value !== null && value !== ""))
const listEmptyDescription = computed(() => listError.value ? "教室布局列表加载失败，请检查网络后重试。" : hasActiveFilters.value ? "没有找到符合条件的教室布局。" : "暂无教室布局，请先新增教室布局。")
const listEmptyActionText = computed(() => listError.value ? "重新加载" : hasActiveFilters.value ? "重置筛选" : "新增教室布局")

function handleListEmptyAction() {
  if (listError.value) {
    getList()
  } else if (hasActiveFilters.value) {
    resetQuery()
  } else {
    handleAdd()
  }
}

/** 查询排座教室布局列表 */
function getList() {
  loading.value = true
  listError.value = false
  listClassroom(queryParams.value).then(response => {
    classroomList.value = response.rows
    total.value = response.total
  }).catch(() => {
    classroomList.value = []
    total.value = 0
    listError.value = true
  }).finally(() => {
    loading.value = false
  })
}

function getClassOptions() {
  return listClass({ status: "0" }).then(response => {
    classOptions.value = response.rows
  })
}

function optionLabel(options, value) {
  return options.find(item => item.value === value)?.label || "-"
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    classroomId: null,
    classId: null,
    classroomName: null,
    rowCount: 7,
    colCount: 8,
    platformPosition: "FRONT",
    aisleAfterCols: "",
    isDefault: "0",
    status: "0",
    remark: null
  }
  normalizeLayoutGrid()
  proxy.resetForm("classroomRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.classroomId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加排座教室布局"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _classroomId = row.classroomId || ids.value
  getClassroom(_classroomId).then(response => {
    form.value = response.data
    normalizeLayoutGrid()
    return getClassroomLayout(_classroomId)
  }).then(response => {
    applyLayoutPositions(response.data || [])
    open.value = true
    title.value = "修改排座教室布局"
  })
}

function handleGridSizeChange() {
  normalizeLayoutGrid()
}

function normalizeLayoutGrid() {
  const rowCount = Number(form.value.rowCount) || 0
  const colCount = Number(form.value.colCount) || 0
  const oldMap = new Map()
  layoutGrid.value.flat().forEach(cell => {
    oldMap.set(`${cell.rowIndex}-${cell.colIndex}`, cell)
  })
  const nextGrid = []
  for (let row = 1; row <= rowCount; row++) {
    const rowCells = []
    for (let col = 1; col <= colCount; col++) {
      const oldCell = oldMap.get(`${row}-${col}`)
      rowCells.push({
        rowIndex: row,
        colIndex: col,
        seatType: oldCell?.seatType || "0",
        status: oldCell?.status || "0",
        remark: oldCell?.remark || null
      })
    }
    nextGrid.push(rowCells)
  }
  layoutGrid.value = nextGrid
}

function applyLayoutPositions(positions) {
  if (!positions.length) {
    return
  }
  const positionMap = new Map()
  positions.forEach(item => {
    positionMap.set(`${item.rowIndex}-${item.colIndex}`, item)
  })
  layoutGrid.value = layoutGrid.value.map(rowCells => rowCells.map(cell => {
    const position = positionMap.get(`${cell.rowIndex}-${cell.colIndex}`)
    return position ? {
      rowIndex: cell.rowIndex,
      colIndex: cell.colIndex,
      seatType: position.seatType || "0",
      status: position.status || "0",
      remark: position.remark || null
    } : cell
  }))
}

function toggleSeatType(cell) {
  const index = seatTypeCycle.indexOf(cell.seatType)
  cell.seatType = seatTypeCycle[(index + 1) % seatTypeCycle.length]
}

function buildLayoutPayload() {
  return layoutGrid.value.flat().map(cell => ({
    rowIndex: cell.rowIndex,
    colIndex: cell.colIndex,
    seatType: cell.seatType,
    isAvailable: cell.seatType === "0" ? "1" : "0",
    status: cell.status || "0",
    remark: cell.remark
  }))
}

function deriveAisleAfterCols() {
  const rowCount = Number(form.value.rowCount) || 0
  const colCount = Number(form.value.colCount) || 0
  const aisleCols = []
  for (let col = 1; col <= colCount; col++) {
    let allAisle = rowCount > 0
    for (let row = 1; row <= rowCount; row++) {
      if (layoutGrid.value[row - 1]?.[col - 1]?.seatType !== "2") {
        allAisle = false
        break
      }
    }
    if (allAisle) {
      aisleCols.push(col)
    }
  }
  return aisleCols.join(",")
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["classroomRef"].validate(valid => {
    if (valid) {
      normalizeLayoutGrid()
      const submitData = { ...form.value }
      submitData.aisleAfterCols = deriveAisleAfterCols()
      delete submitData.className
      delete submitData.delFlag
      delete submitData.createBy
      delete submitData.createTime
      delete submitData.updateBy
      delete submitData.updateTime
      if (form.value.classroomId != null) {
        updateClassroom(submitData).then(() => {
          return saveClassroomLayout(form.value.classroomId, buildLayoutPayload())
        }).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addClassroom(submitData).then(response => {
          const classroomId = response.data.classroomId
          return saveClassroomLayout(classroomId, buildLayoutPayload())
        }).then(() => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _classroomIds = row.classroomId || ids.value
  proxy.$modal.confirm('是否确认删除排座教室布局编号为"' + _classroomIds + '"的数据项？').then(function() {
    return delClassroom(_classroomIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('seating/classroom/export', {
    ...queryParams.value
  }, `classroom_${new Date().getTime()}.xlsx`)
}

getClassOptions()
getList()
</script>
<style scoped lang="scss">
.classroom-page {
  min-height: calc(100vh - 84px);
  padding: 20px;
  background: linear-gradient(180deg, #f7f9fc 0%, #f5f7fb 100%);
  color: #1f2329;
}

.hero-panel {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 28px;
  margin-bottom: 16px;
  border: 1px solid #dfe5ef;
  border-radius: 16px;
  background: linear-gradient(135deg, #ffffff 0%, #f8fbff 100%);
  box-shadow: 0 12px 28px rgba(31, 35, 41, 0.04);
}

.hero-copy {
  min-width: 0;
}

.eyebrow {
  margin: 0 0 8px;
  color: #4d7eff;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

.hero-panel h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
}

.overview-text {
  margin: 10px 0 0;
  color: #5f6b7a;
  font-size: 14px;
  line-height: 1.7;
}

.hero-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
}

.panel-card {
  margin-bottom: 16px;
  border: 1px solid #e1e7f0;
  border-radius: 14px;
  box-shadow: 0 6px 18px rgba(31, 35, 41, 0.04);
}

.panel-card :deep(.el-card__header) {
  padding: 18px 20px 12px;
  border-bottom: 1px solid #eef2f7;
}

.panel-card :deep(.el-card__body) {
  padding: 20px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-weight: 700;
  color: #1f2329;
}

.panel-header small {
  color: #8a94a6;
  font-size: 12px;
  font-weight: 400;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  row-gap: 14px;
  column-gap: 16px;
}

.search-form :deep(.el-button),
.table-toolbar :deep(.el-button),
.dialog-footer :deep(.el-button) {
  border-radius: 10px;
  box-shadow: none;
  font-weight: 600;
}

.search-form :deep(.el-button) {
  min-height: 34px;
  padding: 0 14px;
  background: #fff;
}

.search-form :deep(.el-button--primary.is-plain) {
  color: #3f6ce8;
  background: #eef4ff;
  border-color: #d7e3ff;
}

.table-toolbar {
  margin-bottom: 16px;
}

.toolbar-row {
  align-items: center;
}

.classroom-table {
  border: 1px solid #edf1f6;
  border-radius: 12px;
  overflow: hidden;
}

.classroom-table :deep(.el-table__header-wrapper th) {
  background: #f9fbff;
  color: #1f2329;
}

.classroom-table :deep(.el-table__row:hover > td) {
  background: #fbfdff;
}

.row-action-btn {
  min-height: 28px;
  padding: 0 10px;
  border-radius: 8px;
}

.row-action-btn + .row-action-btn {
  margin-left: 10px;
}

.row-action-btn.is-plain.el-button--primary {
  color: #3f6ce8;
  background: #eef4ff;
  border-color: #d7e3ff;
}

.row-action-btn.is-plain.el-button--danger {
  color: #d93026;
  background: #fff1f1;
  border-color: #ffd4d4;
}

.row-action-btn.is-plain.el-button--danger:hover,
.row-action-btn.is-plain.el-button--danger:focus {
  color: #c62828;
  background: #ffe4e4;
  border-color: #ffbbbb;
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.classroom-dialog :deep(.el-dialog) {
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(31, 35, 41, 0.14);
}

.classroom-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 18px 22px 10px;
  border-bottom: 1px solid #eef2f7;
}

.classroom-dialog :deep(.el-dialog__title) {
  color: #1f2329;
  font-size: 16px;
  font-weight: 700;
}

.classroom-dialog :deep(.el-dialog__body) {
  padding: 18px 22px 14px;
}

.classroom-dialog :deep(.el-dialog__footer) {
  padding: 0 22px 20px;
}

.dialog-shell {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog-section {
  padding: 16px;
  border: 1px solid #e6ebf2;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #fbfcfe 100%);
}

.dialog-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.dialog-section-header span {
  color: #1f2329;
  font-size: 14px;
  font-weight: 700;
}

.dialog-section-header small {
  color: #8a94a6;
  font-size: 12px;
}

.classroom-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.classroom-form :deep(.el-form-item__label) {
  color: #4f5b6d;
  font-weight: 600;
}

.classroom-form :deep(.el-form-item__content) {
  min-height: 40px;
}

.classroom-form :deep(.el-input__wrapper),
.classroom-form :deep(.el-select__wrapper),
.classroom-form :deep(.el-textarea__inner),
.classroom-form :deep(.el-input-number__wrapper) {
  border: 1px solid #d7e0ea;
  border-radius: 11px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  background: #ffffff;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.classroom-form :deep(.el-input__wrapper:hover),
.classroom-form :deep(.el-select__wrapper:hover),
.classroom-form :deep(.el-textarea__inner:hover),
.classroom-form :deep(.el-input-number__wrapper:hover) {
  border-color: #b8c7db;
}

.classroom-form :deep(.el-input.is-focus .el-input__wrapper),
.classroom-form :deep(.el-select__wrapper.is-focused),
.classroom-form :deep(.el-textarea__inner:focus),
.classroom-form :deep(.el-input-number.is-focus .el-input-number__wrapper) {
  border-color: #4c7df0;
  box-shadow: 0 0 0 3px rgba(76, 125, 240, 0.12), 0 1px 2px rgba(15, 23, 42, 0.04);
}

.classroom-form :deep(.el-input__inner),
.classroom-form :deep(.el-select__selected-item),
.classroom-form :deep(.el-textarea__inner) {
  color: #1f2937;
}

.classroom-form :deep(.el-input__inner::placeholder),
.classroom-form :deep(.el-textarea__inner::placeholder) {
  color: #a1adbb;
}

.layout-editor {
  width: 100%;
}

.layout-body {
  display: flex;
  align-items: stretch;
  gap: 12px;
}

.seat-grid-wrap {
  flex: 1;
  max-width: 100%;
  overflow: auto;
  padding-bottom: 4px;
}

.seat-grid {
  display: grid;
  gap: 8px;
  width: max-content;
  padding: 12px;
  border: 1px solid #e5ebf3;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #f8fbff 100%);
}

.seat-cell {
  width: 52px;
  height: 46px;
  border: 1px solid #d9e0ea;
  border-radius: 10px;
  background: #ffffff;
  color: #5b6472;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.18s ease;
}

.seat-cell:hover {
  border-color: #3f6ce8;
  box-shadow: 0 4px 12px rgba(63, 108, 232, 0.12);
  transform: translateY(-1px);
}

.seat-type-0 {
  background: #ffffff;
  color: #5b6472;
}

.seat-type-1 {
  background: #ffefef;
  border-color: #f2c4c4;
  color: #d93026;
}

.seat-type-2 {
  background: #edf2f7;
  border-color: #d9e0ea;
  color: #7a8699;
}

.platform {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  background: linear-gradient(135deg, #5a8cff 0%, #3e6bf5 100%);
  color: #ffffff;
  font-weight: 600;
  box-shadow: 0 8px 16px rgba(77, 126, 255, 0.18);
}

.platform-horizontal {
  width: 180px;
  height: 34px;
  margin: 0 0 10px 0;
}

.platform-horizontal:last-child {
  margin: 10px 0 0 0;
}

.platform-vertical {
  width: 34px;
  min-height: 100px;
  writing-mode: vertical-rl;
  letter-spacing: 4px;
}

.layout-help {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 12px;
}

.layout-tip {
  color: #909399;
  font-size: 13px;
}

.layout-legend {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  color: #606266;
}

.legend-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.legend-dot {
  width: 16px;
  height: 16px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.status-group :deep(.el-radio) {
  margin-right: 16px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer :deep(.el-button) {
  min-width: 88px;
  height: 34px;
}

.dialog-footer :deep(.el-button--primary) {
  box-shadow: 0 8px 16px rgba(77, 126, 255, 0.18);
}

@media (max-width: 1600px) {
  .hero-panel {
    align-items: stretch;
  }

  .classroom-dialog :deep(.el-dialog) {
    width: min(1040px, calc(100vw - 40px));
  }
}

@media (max-width: 1280px) {
  .hero-panel {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-actions {
    justify-content: flex-start;
  }

  .layout-body {
    flex-direction: column;
  }

  .platform-vertical {
    width: 100%;
    min-height: 34px;
    writing-mode: horizontal-tb;
    letter-spacing: 2px;
  }

  .platform-horizontal {
    width: 100%;
  }
}
</style>
