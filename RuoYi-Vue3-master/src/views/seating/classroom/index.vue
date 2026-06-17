<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
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
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
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

    <el-table v-loading="loading" :data="classroomList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="班级" align="center" prop="className" min-width="120" />
      <el-table-column label="教室布局名称" align="center" prop="classroomName" />
      <el-table-column label="座位行数" align="center" prop="rowCount" />
      <el-table-column label="座位列数" align="center" prop="colCount" />
      <el-table-column label="讲台位置" align="center" prop="platformPosition">
        <template #default="scope">{{ optionLabel(platformOptions, scope.row.platformPosition) }}</template>
      </el-table-column>
      <el-table-column label="默认布局" align="center" prop="isDefault">
        <template #default="scope">{{ scope.row.isDefault === "1" ? "是" : "否" }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope"><dict-tag :options="sys_normal_disable" :value="scope.row.status" /></template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['seating:classroom:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['seating:classroom:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改排座教室布局对话框 -->
    <el-dialog :title="title" v-model="open" width="920px" append-to-body>
      <el-form ref="classroomRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
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
          <el-col :span="24">
            <el-form-item label="座位行数" prop="rowCount">
              <el-input-number v-model="form.rowCount" :min="1" :max="30" controls-position="right" style="width: 100%" @change="handleGridSizeChange" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="座位列数" prop="colCount">
              <el-input-number v-model="form.colCount" :min="1" :max="30" controls-position="right" style="width: 100%" @change="handleGridSizeChange" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="讲台位置" prop="platformPosition">
              <el-radio-group v-model="form.platformPosition">
                <el-radio v-for="item in platformOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="布局编辑">
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
                <div class="layout-tip">点击格子切换：座位 → 过道 → 不可用。</div>
                <div class="layout-legend">
                  <span v-for="item in seatTypeLegend" :key="item.value" class="legend-item">
                    <i :class="'legend-dot seat-type-' + item.value"></i>{{ item.label }}
                  </span>
                </div>
              </div>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否默认" prop="isDefault">
              <el-radio-group v-model="form.isDefault">
                <el-radio value="0">否</el-radio>
                <el-radio value="1">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
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

/** 查询排座教室布局列表 */
function getList() {
  loading.value = true
  listClassroom(queryParams.value).then(response => {
    classroomList.value = response.rows
    total.value = response.total
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

<style scoped>
.layout-editor {
  width: 100%;
}

.layout-body {
  display: flex;
  align-items: stretch;
  gap: 10px;
}

.seat-grid-wrap {
  max-width: 100%;
  overflow: auto;
  padding-bottom: 4px;
}

.seat-grid {
  display: grid;
  gap: 8px;
}

.seat-cell {
  width: 52px;
  height: 46px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background: #ffffff;
  color: #606266;
  cursor: pointer;
  font-size: 14px;
}

.seat-cell:hover {
  border-color: #409eff;
}

.seat-type-0 {
  background: #ffffff;
  color: #606266;
}

.seat-type-1 {
  background: #f56c6c;
  border-color: #f56c6c;
  color: #ffffff;
}

.seat-type-2 {
  background: #c0c4cc;
  border-color: #c0c4cc;
  color: #ffffff;
}

.platform {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background: #409eff;
  color: #ffffff;
  font-weight: 600;
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

.layout-tip {
  margin-top: 10px;
  color: #909399;
  font-size: 13px;
}

.layout-legend {
  display: flex;
  gap: 18px;
  margin-top: 10px;
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
  border-radius: 3px;
}
</style>
