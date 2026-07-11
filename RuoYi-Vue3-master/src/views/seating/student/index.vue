<template>
  <div class="student-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <p class="eyebrow">慧排座 · 学生管理</p>
        <h1>学生基础信息维护</h1>
        <p class="overview-text">
          维护班级学生、排座属性和导入入口，支撑后续排座和成绩同步流程。
        </p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['seating:student:add']">新增学生</el-button>
        <el-button icon="Upload" @click="handleImport" v-hasPermi="['seating:student:import']">导入学生</el-button>
        <el-button icon="Download" @click="handleExport" v-hasPermi="['seating:student:export']">导出数据</el-button>
      </div>
    </section>

    <el-card v-show="showSearch" shadow="never" class="panel-card search-card">
      <template #header>
        <div class="panel-header">
          <span>筛选条件</span>
          <small>按班级、学号、姓名、性别、排座属性和状态快速定位</small>
        </div>
      </template>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px" class="search-form">
        <el-form-item label="班级" prop="classId">
          <el-select
            v-model="queryParams.classId"
            placeholder="请选择班级"
            filterable
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in classOptions"
              :key="item.classId"
              :label="item.className"
              :value="item.classId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学号" prop="studentNo">
          <el-input
            v-model="queryParams.studentNo"
            placeholder="请输入学号"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="学生姓名" prop="studentName">
          <el-input
            v-model="queryParams.studentName"
            placeholder="请输入学生姓名"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-select
            v-model="queryParams.gender"
            placeholder="请选择性别"
            clearable
            style="width: 120px"
          >
            <el-option v-for="dict in sys_user_sex" :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="视力等级" prop="visionLevel">
          <el-select
            v-model="queryParams.visionLevel"
            placeholder="请选择视力等级"
            clearable
            style="width: 140px"
          >
            <el-option v-for="item in visionOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="成绩等级" prop="scoreLevel">
          <el-select
            v-model="queryParams.scoreLevel"
            placeholder="请选择成绩等级"
            clearable
            style="width: 140px"
          >
            <el-option v-for="item in scoreOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="纪律等级" prop="disciplineLevel">
          <el-select
            v-model="queryParams.disciplineLevel"
            placeholder="请选择纪律等级"
            clearable
            style="width: 140px"
          >
            <el-option v-for="item in disciplineOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="特殊需求" prop="specialNeed">
          <el-input
            v-model="queryParams.specialNeed"
            placeholder="请输入特殊需求"
            clearable
            @keyup.enter="handleQuery"
          />
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
          <span>学生列表</span>
          <small>支持修改、导入、删除和导出</small>
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
              v-hasPermi="['seating:student:add']"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="success"
              plain
              icon="Edit"
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['seating:student:edit']"
            >修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['seating:student:remove']"
            >删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="info"
              plain
              icon="Upload"
              @click="handleImport"
              v-hasPermi="['seating:student:import']"
            >导入</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="warning"
              plain
              icon="Download"
              @click="handleExport"
              v-hasPermi="['seating:student:export']"
            >导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </div>

      <el-table v-loading="loading" :data="studentList" @selection-change="handleSelectionChange" class="student-table">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="班级" align="center" prop="className" min-width="120" />
        <el-table-column label="学号" align="center" prop="studentNo" min-width="110" />
        <el-table-column label="学生姓名" align="center" prop="studentName" min-width="120" />
        <el-table-column label="性别" align="center" prop="gender" width="90">
          <template #default="scope">
            <dict-tag :options="sys_user_sex" :value="scope.row.gender" />
          </template>
        </el-table-column>
        <el-table-column label="身高（cm）" align="center" prop="heightCm" width="110" />
        <el-table-column label="视力等级" align="center" prop="visionLevel" width="110">
          <template #default="scope">{{ optionLabel(visionOptions, scope.row.visionLevel) }}</template>
        </el-table-column>
        <el-table-column label="成绩等级" align="center" prop="scoreLevel" width="110">
          <template #default="scope">{{ optionLabel(scoreOptions, scope.row.scoreLevel) }}</template>
        </el-table-column>
        <el-table-column label="纪律等级" align="center" prop="disciplineLevel" width="110">
          <template #default="scope">{{ optionLabel(disciplineOptions, scope.row.disciplineLevel) }}</template>
        </el-table-column>
        <el-table-column label="特殊需求" align="center" prop="specialNeed" min-width="140" show-overflow-tooltip />
        <el-table-column label="排序号" align="center" prop="sortNo" width="100" />
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
          <template #default="scope">
            <el-button type="primary" plain icon="Edit" class="row-action-btn" @click="handleUpdate(scope.row)" v-hasPermi="['seating:student:edit']">修改</el-button>
            <el-button type="danger" plain icon="Delete" class="row-action-btn" @click="handleDelete(scope.row)" v-hasPermi="['seating:student:remove']">删除</el-button>
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

    <el-dialog :title="title" v-model="open" width="760px" append-to-body class="student-dialog">
      <el-form ref="studentRef" :model="form" :rules="rules" label-width="88px" class="student-form">
        <el-row :gutter="16" class="student-form-grid">
          <el-col :span="12">
            <el-form-item label="班级" prop="classId">
              <el-select
                v-model="form.classId"
                placeholder="请选择班级"
                filterable
                clearable
                style="width: 100%"
              >
                <el-option
                  v-for="item in classOptions"
                  :key="item.classId"
                  :label="item.className"
                  :value="item.classId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学生姓名" prop="studentName">
              <el-input v-model="form.studentName" placeholder="请输入学生姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender" class="status-group">
                <el-radio v-for="dict in sys_user_sex" :key="dict.value" :value="dict.value">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身高" prop="heightCm">
              <el-input-number
                v-model="form.heightCm"
                :min="50"
                :max="250"
                :precision="1"
                :step="0.5"
                controls-position="right"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="视力等级" prop="visionLevel">
              <el-select v-model="form.visionLevel" placeholder="请选择视力等级" style="width: 100%">
                <el-option v-for="item in visionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="成绩等级" prop="scoreLevel">
              <el-select v-model="form.scoreLevel" placeholder="请选择成绩等级" clearable style="width: 100%">
                <el-option v-for="item in scoreOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纪律等级" prop="disciplineLevel">
              <el-select v-model="form.disciplineLevel" placeholder="请选择纪律等级" style="width: 100%">
                <el-option v-for="item in disciplineOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序号" prop="sortNo">
              <el-input-number v-model="form.sortNo" :min="0" :max="9999" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status" class="status-group">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="特殊需求" prop="specialNeed">
              <el-input
                v-model="form.specialNeed"
                type="textarea"
                :rows="2"
                placeholder="请输入座位、健康等特殊需求"
                maxlength="500"
                show-word-limit
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" maxlength="500" show-word-limit />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <excel-import-dialog
      ref="importStudentRef"
      title="学生导入"
      action="/seating/student/importData"
      template-action="/seating/student/importTemplate"
      template-file-name="student_template"
      update-support-label="是否按学号更新该班级已有学生"
      :extra-params="{ classId: importClassId }"
      :before-submit="validateImportClass"
      @success="getList"
    >
      <template #prepend>
        <el-form label-width="80px" class="student-import-form">
          <el-form-item label="班级" required>
            <el-select v-model="importClassId" placeholder="请选择班级" filterable style="width: 100%">
              <el-option
                v-for="item in classOptions"
                :key="item.classId"
                :label="item.className"
                :value="item.classId"
              />
            </el-select>
          </el-form-item>
        </el-form>
      </template>
    </excel-import-dialog>
  </div>
</template>

<script setup name="Student">
import ExcelImportDialog from "@/components/ExcelImportDialog"
import { listStudent, getStudent, delStudent, addStudent, updateStudent } from "@/api/seating/student"
import { listClass } from "@/api/seating/class"

const { proxy } = getCurrentInstance()
const { sys_normal_disable, sys_user_sex } = proxy.useDict("sys_normal_disable", "sys_user_sex")

const visionOptions = [
  { value: "0", label: "正常" },
  { value: "1", label: "轻度" },
  { value: "2", label: "中度" },
  { value: "3", label: "重度" }
]
const scoreOptions = [
  { value: "A", label: "A" },
  { value: "B", label: "B" },
  { value: "C", label: "C" },
  { value: "D", label: "D" }
]
const disciplineOptions = [
  { value: "0", label: "正常" },
  { value: "1", label: "关注" },
  { value: "2", label: "重点关注" }
]

const studentList = ref([])
const classOptions = ref([])
const open = ref(false)
const importClassId = ref(null)
const loading = ref(true)
const listError = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    classId: undefined,
    studentNo: undefined,
    studentName: undefined,
    gender: undefined,
    heightCm: undefined,
    visionLevel: undefined,
    scoreLevel: undefined,
    disciplineLevel: undefined,
    specialNeed: undefined,
    sortNo: undefined,
    status: undefined,
  },
  rules: {
    classId: [
      { required: true, message: "请选择班级", trigger: "change" }
    ],
    studentName: [
      { required: true, message: "学生姓名不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

const hasActiveFilters = computed(() => Object.entries(queryParams.value).some(([key, value]) => !["pageNum", "pageSize"].includes(key) && value !== undefined && value !== null && value !== ""))
const listEmptyDescription = computed(() => listError.value ? "学生列表加载失败，请检查网络后重试。" : hasActiveFilters.value ? "没有找到符合条件的学生。" : "暂无学生，请先新增或导入学生。")
const listEmptyActionText = computed(() => listError.value ? "重新加载" : hasActiveFilters.value ? "重置筛选" : "新增学生")

function handleListEmptyAction() {
  if (listError.value) {
    getList()
  } else if (hasActiveFilters.value) {
    resetQuery()
  } else {
    handleAdd()
  }
}

/** 查询排座学生列表 */
function getList() {
  loading.value = true
  listError.value = false
  listStudent(queryParams.value).then(response => {
    studentList.value = response.rows
    total.value = response.total
  }).catch(() => {
    studentList.value = []
    total.value = 0
    listError.value = true
  }).finally(() => {
    loading.value = false
  })
}

/** 查询启用班级选项 */
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
    studentId: null,
    classId: null,
    studentNo: null,
    studentName: null,
    gender: "2",
    heightCm: null,
    visionLevel: "0",
    scoreLevel: null,
    disciplineLevel: "0",
    specialNeed: null,
    sortNo: null,
    status: "0",
    remark: null
  }
  proxy.resetForm("studentRef")
}

function buildSubmitData() {
  const submitData = { ...form.value }
  delete submitData.delFlag
  delete submitData.createBy
  delete submitData.createTime
  delete submitData.updateBy
  delete submitData.updateTime
  return submitData
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
  ids.value = selection.map(item => item.studentId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  getClassOptions().then(() => {
    open.value = true
    title.value = "新增学生"
  })
}

function handleImport() {
  importClassId.value = queryParams.value.classId || null
  getClassOptions().then(() => {
    proxy.$refs["importStudentRef"].open()
  })
}

function validateImportClass() {
  if (!importClassId.value) {
    proxy.$modal.msgError("请选择导入班级")
    return false
  }
  return true
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _studentId = row.studentId || ids.value
  Promise.all([getClassOptions(), getStudent(_studentId)]).then(([, response]) => {
    form.value = response.data
    open.value = true
    title.value = "修改学生"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["studentRef"].validate(valid => {
    if (valid) {
      const submitData = buildSubmitData()
      if (form.value.studentId != null) {
        updateStudent(submitData).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addStudent(submitData).then(() => {
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
  const _studentIds = row.studentId || ids.value
  proxy.$modal.confirm(`是否确认删除学生编号为“${_studentIds}”的数据项？`).then(function() {
    return delStudent(_studentIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('seating/student/export', {
    ...queryParams.value
  }, `student_${new Date().getTime()}.xlsx`)
}

getClassOptions()
getList()
</script>

<style scoped lang="scss">
.student-page {
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
  letter-spacing: 0;
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

.table-toolbar {
  margin-bottom: 16px;
}

.toolbar-row {
  align-items: center;
}

.student-table {
  border: 1px solid #edf1f6;
  border-radius: 12px;
  overflow: hidden;
}

.student-table :deep(.el-table__header-wrapper th) {
  background: #f9fbff;
  color: #1f2329;
}

.student-table :deep(.el-table__row:hover > td) {
  background: #fbfdff;
}

.student-table :deep(.el-tag) {
  border-radius: 999px;
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.hero-actions :deep(.el-button),
.table-toolbar :deep(.el-button),
.dialog-footer :deep(.el-button) {
  border-radius: 10px;
  box-shadow: none;
  font-weight: 600;
}

.hero-actions :deep(.el-button) {
  height: 36px;
  padding: 0 16px;
}

.hero-actions :deep(.el-button--primary) {
  background: linear-gradient(135deg, #5a8cff 0%, #3e6bf5 100%);
  border-color: transparent;
}

.hero-actions :deep(.el-button--primary:hover),
.hero-actions :deep(.el-button--primary:focus),
.hero-actions :deep(.el-button--primary:active) {
  background: linear-gradient(135deg, #6a97ff 0%, #4f7dff 100%);
  border-color: transparent;
}

.table-toolbar :deep(.el-button) {
  min-height: 34px;
  padding: 0 14px;
  background: #fff;
}

.table-toolbar :deep(.el-button--primary.is-plain) {
  color: #3f6ce8;
  background: #eef4ff;
  border-color: #d7e3ff;
}

.table-toolbar :deep(.el-button--success.is-plain) {
  color: #2f9d57;
  background: #eefaf2;
  border-color: #d8f1e0;
}

.table-toolbar :deep(.el-button--danger.is-plain) {
  color: #d94b4b;
  background: #fff1f1;
  border-color: #ffdada;
}

.table-toolbar :deep(.el-button--info.is-plain) {
  color: #52708a;
  background: #f3f7fb;
  border-color: #e1ebf4;
}

.table-toolbar :deep(.el-button--warning.is-plain) {
  color: #c98512;
  background: #fff8eb;
  border-color: #ffe8bd;
}



.student-dialog :deep(.el-dialog) {
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(31, 35, 41, 0.14);
}

.student-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 18px 22px 10px;
  border-bottom: 1px solid #eef2f7;
}

.student-dialog :deep(.el-dialog__title) {
  color: #1f2329;
  font-size: 16px;
  font-weight: 700;
}

.student-dialog :deep(.el-dialog__body) {
  padding: 18px 22px 14px;
}

.student-dialog :deep(.el-dialog__footer) {
  padding: 0 22px 20px;
}

.student-dialog :deep(.el-input__wrapper),
.student-dialog :deep(.el-select__wrapper),
.student-dialog :deep(.el-textarea__inner),
.student-dialog :deep(.el-input-number__wrapper) {
  border: 1px solid #d7e0ea;
  border-radius: 11px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  background: #ffffff;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.student-dialog :deep(.el-input__wrapper:hover),
.student-dialog :deep(.el-select__wrapper:hover),
.student-dialog :deep(.el-textarea__inner:hover),
.student-dialog :deep(.el-input-number__wrapper:hover) {
  border-color: #b8c7db;
}

.student-dialog :deep(.el-input.is-focus .el-input__wrapper),
.student-dialog :deep(.el-select__wrapper.is-focused),
.student-dialog :deep(.el-textarea__inner:focus),
.student-dialog :deep(.el-input-number.is-focus .el-input-number__wrapper) {
  border-color: #4c7df0;
  box-shadow: 0 0 0 3px rgba(76, 125, 240, 0.12), 0 1px 2px rgba(15, 23, 42, 0.04);
}

.student-dialog :deep(.el-input__inner),
.student-dialog :deep(.el-select__selected-item),
.student-dialog :deep(.el-textarea__inner) {
  color: #1f2937;
}

.student-dialog :deep(.el-input__inner::placeholder),
.student-dialog :deep(.el-textarea__inner::placeholder) {
  color: #a1adbb;
}

.student-form {
  display: block;
}

.student-form-grid {
  row-gap: 4px;
}

.status-group {
  width: 100%;
  min-height: 32px;
  display: flex;
  align-items: center;
  gap: 20px;
  flex-wrap: wrap;
}

.student-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.student-form :deep(.el-form-item__label) {
  color: #4f5b6d;
  font-weight: 600;
}

.student-form :deep(.el-form-item__content) {
  min-height: 40px;
}

.student-form :deep(.el-radio) {
  margin-right: 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.dialog-footer :deep(.el-button) {
  min-width: 88px;
  height: 34px;
  border-radius: 10px;
}

.dialog-footer :deep(.el-button--primary) {
  box-shadow: 0 8px 16px rgba(77, 126, 255, 0.18);
}

.student-import-form {
  margin-bottom: 12px;
}

@media (max-width: 1600px) {
  .hero-panel {
    align-items: stretch;
    flex-direction: column;
  }

  .hero-actions {
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .student-page {
    padding: 12px;
  }

  .table-toolbar {
    margin-bottom: 12px;
  }

  .table-footer {
    justify-content: stretch;
  }
}

.search-form :deep(.el-button) {
  min-height: 34px;
  padding: 0 14px;
  background: #fff;
  border-radius: 10px;
  box-shadow: none;
  font-weight: 600;
}

.search-form :deep(.el-button--primary.is-plain) {
  color: #3f6ce8;
  background: #eef4ff;
  border-color: #d7e3ff;
}
</style>








