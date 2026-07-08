<template>
  <div class="class-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <p class="eyebrow">慧排座 · 班级管理</p>
        <h1>班级基础信息维护</h1>
        <p class="overview-text">
          维护年级、学年、学期和班级科目，支撑后续导入、排座和新学期复制流程。
        </p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['seating:class:add']">新增班级</el-button>
        <el-button icon="Download" @click="handleExport" v-hasPermi="['seating:class:export']">导出数据</el-button>
      </div>
    </section>

    <el-card v-show="showSearch" shadow="never" class="panel-card search-card">
      <template #header>
        <div class="panel-header">
          <span>筛选条件</span>
          <small>按班级名称、年级、学年、学期和状态快速定位</small>
        </div>
      </template>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px" class="search-form">
        <el-form-item label="班级名称" prop="className">
          <el-input
            v-model="queryParams.className"
            placeholder="请输入班级名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="年级" prop="gradeCode">
          <el-select v-model="queryParams.gradeCode" placeholder="请选择年级" clearable filterable style="width: 160px">
            <el-option
              v-for="item in gradeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="学年" prop="schoolYear">
          <el-input
            v-model="queryParams.schoolYear"
            placeholder="请输入学年"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="queryParams.semester" placeholder="请选择学期" clearable style="width: 160px">
            <el-option
              v-for="item in semesterOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
            <el-option
              v-for="dict in sys_normal_disable"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
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
          <span>班级列表</span>
          <small>支持修改、新学期复制、删除和导出</small>
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
              v-hasPermi="['seating:class:add']"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="success"
              plain
              icon="Edit"
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['seating:class:edit']"
            >修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['seating:class:remove']"
            >删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="warning"
              plain
              icon="Download"
              @click="handleExport"
              v-hasPermi="['seating:class:export']"
            >导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </div>

      <el-table v-loading="loading" :data="classList" @selection-change="handleSelectionChange" class="class-table">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="班级名称" align="center" prop="className" min-width="150" />
        <el-table-column label="年级" align="center" prop="gradeName" min-width="120" />
        <el-table-column label="科目" align="left" prop="subjectSnapshot" min-width="220" show-overflow-tooltip>
          <template #default="scope">
            <el-space wrap>
              <el-tag v-for="subject in parseSubjectSnapshot(scope.row.subjectSnapshot)" :key="subject" size="small" effect="plain">
                {{ subject }}
              </el-tag>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column label="学年" align="center" prop="schoolYear" min-width="110" />
        <el-table-column label="学期" align="center" prop="semester" width="100">
          <template #default="scope">
            <span>{{ optionLabel(semesterOptions, scope.row.semester) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template #default="scope">
            <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" min-width="120" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
          <template #default="scope">
            <el-button type="primary" plain icon="Edit" class="row-action-btn" @click="handleUpdate(scope.row)" v-hasPermi="['seating:class:edit']">修改</el-button>
            <el-button type="warning" plain icon="CopyDocument" class="row-action-btn" @click="handleCopyNewTerm(scope.row)" v-hasPermi="['seating:class:add']">新学期复制</el-button>
            <el-button type="danger" plain icon="Delete" class="row-action-btn" @click="handleDelete(scope.row)" v-hasPermi="['seating:class:remove']">删除</el-button>
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

        <el-dialog :title="title" v-model="open" width="680px" append-to-body class="class-dialog">
      <el-form ref="classRef" :model="form" :rules="rules" label-width="72px" class="class-form">
        <el-row :gutter="16" class="class-form-grid">
          <el-col :span="12">
            <el-form-item label="班级名称" prop="className">
              <el-input v-model="form.className" placeholder="请输入班级名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级" prop="gradeCode">
              <el-select v-model="form.gradeCode" placeholder="请选择年级" filterable style="width: 100%" @change="handleGradeChange">
                <el-option
                  v-for="item in gradeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="科目" prop="subjectNames">
              <el-select v-model="form.subjectNames" multiple filterable placeholder="请选择班级科目" style="width: 100%">
                <el-option
                  v-for="item in subjectOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学年" prop="schoolYear">
              <el-input v-model="form.schoolYear" placeholder="请输入学年" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="学期" prop="semester">
              <el-select v-model="form.semester" placeholder="请选择学期" style="width: 100%">
                <el-option
                  v-for="item in semesterOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status" class="status-group">
                <el-radio
                  v-for="dict in sys_normal_disable"
                  :key="dict.value"
                  :value="dict.value"
                >{{ dict.label }}</el-radio>
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
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="新学期复制" v-model="copyOpen" width="640px" append-to-body class="copy-dialog">
      <el-form :model="copyForm" label-width="110px">
        <el-alert
          title="默认会复制学生、学生关系、排座规则和教室布局；考试、成绩、座位方案、分配结果和方案评分不会复制。"
          type="info"
          :closable="false"
          class="mb16"
        />
        <el-row :gutter="16">
          <el-col :span="24">
            <el-form-item label="源班级">
              <el-input :model-value="copyForm.sourceClassName || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="源班级ID">
              <el-input :model-value="copyForm.sourceClassId == null ? '-' : String(copyForm.sourceClassId)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="源学年">
              <el-input :model-value="copyForm.sourceSchoolYear || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="源学期">
              <el-input :model-value="optionLabel(semesterOptions, copyForm.sourceSemester)" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="目标班级名称">
              <el-input v-model="copyForm.className" placeholder="请输入目标班级名称" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标学年">
              <el-input v-model="copyForm.schoolYear" placeholder="请输入目标学年" clearable />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标学期">
              <el-select v-model="copyForm.semester" placeholder="请选择目标学期" style="width: 100%">
                <el-option
                  v-for="item in semesterOptions"
                  :key="`copy-${item.value}`"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="复制内容">
              <el-space wrap>
                <el-checkbox v-model="copyForm.copyStudents" @change="handleCopyStudentsChange">复制学生</el-checkbox>
                <el-checkbox v-model="copyForm.copyRelations" :disabled="!copyForm.copyStudents">复制学生关系</el-checkbox>
                <el-checkbox v-model="copyForm.copyRules">复制排座规则</el-checkbox>
                <el-checkbox v-model="copyForm.copyClassroomLayout">复制教室布局</el-checkbox>
              </el-space>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" :loading="copySubmitting" @click="submitCopyForm">确定复制</el-button>
          <el-button @click="copyOpen = false">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Class">
import { listClass, getClass, delClass, addClass, updateClass, copyClassNewTerm } from "@/api/seating/class"
import { createNewTermCopyForm, normalizeCopyRelations, validateNewTermCopyForm } from "./newTermCopyState.js"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const classList = ref([])
const open = ref(false)
const loading = ref(true)
const listError = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const copyOpen = ref(false)
const copySubmitting = ref(false)
const copyForm = ref(createNewTermCopyForm())

const gradeOptions = [
  { value: "PRIMARY_1", label: "小学一年级", stage: "PRIMARY" },
  { value: "PRIMARY_2", label: "小学二年级", stage: "PRIMARY" },
  { value: "PRIMARY_3", label: "小学三年级", stage: "PRIMARY" },
  { value: "PRIMARY_4", label: "小学四年级", stage: "PRIMARY" },
  { value: "PRIMARY_5", label: "小学五年级", stage: "PRIMARY" },
  { value: "PRIMARY_6", label: "小学六年级", stage: "PRIMARY" },
  { value: "JUNIOR_1", label: "初中一年级", stage: "JUNIOR" },
  { value: "JUNIOR_2", label: "初中二年级", stage: "JUNIOR" },
  { value: "JUNIOR_3", label: "初中三年级", stage: "JUNIOR" },
  { value: "SENIOR_1", label: "高中一年级", stage: "SENIOR" },
  { value: "SENIOR_2", label: "高中二年级", stage: "SENIOR" },
  { value: "SENIOR_3", label: "高中三年级", stage: "SENIOR" }
]

const semesterOptions = [
  { value: "1", label: "上学期" },
  { value: "2", label: "下学期" }
]

const subjectOptions = ["语文", "数学", "英语", "科学", "道德与法治", "历史", "地理", "生物", "物理", "化学", "政治"]

const primarySubjects = ["语文", "数学", "英语", "科学"]
const juniorSubjects = ["语文", "数学", "英语", "道德与法治", "历史", "地理", "生物"]
const seniorSubjects = ["语文", "数学", "英语", "物理", "化学", "生物", "政治", "历史", "地理"]

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    className: undefined,
    gradeCode: undefined,
    schoolYear: undefined,
    semester: undefined,
    status: undefined,
  },
  rules: {
    className: [
      { required: true, message: "班级名称不能为空", trigger: "blur" }
    ],
    gradeCode: [
      { required: true, message: "请选择年级", trigger: "change" }
    ],
    subjectNames: [
      { required: true, message: "请选择班级科目", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

const hasActiveFilters = computed(() => Object.entries(queryParams.value).some(([key, value]) => !["pageNum", "pageSize"].includes(key) && value !== undefined && value !== null && value !== ""))
const listEmptyDescription = computed(() => listError.value ? "班级列表加载失败，请检查网络后重试。" : hasActiveFilters.value ? "没有找到符合条件的班级。" : "暂无班级，请先新增班级。")
const listEmptyActionText = computed(() => listError.value ? "重新加载" : hasActiveFilters.value ? "重置筛选" : "新增班级")

function handleListEmptyAction() {
  if (listError.value) {
    getList()
  } else if (hasActiveFilters.value) {
    resetQuery()
  } else {
    handleAdd()
  }
}

function getList() {
  loading.value = true
  listError.value = false
  listClass(queryParams.value).then(response => {
    classList.value = response.rows
    total.value = response.total
  }).catch(() => {
    classList.value = []
    total.value = 0
    listError.value = true
  }).finally(() => {
    loading.value = false
  })
}

function cancel() {
  open.value = false
  reset()
}

function reset() {
  form.value = {
    classId: null,
    className: null,
    gradeName: null,
    gradeCode: null,
    schoolStage: null,
    subjectSnapshot: null,
    subjectNames: [],
    schoolYear: null,
    semester: null,
    status: "0",
    remark: null
  }
  proxy.resetForm("classRef")
}

function handleGradeChange(value) {
  const grade = gradeOptions.find(item => item.value === value)
  form.value.gradeName = grade?.label || null
  form.value.schoolStage = grade?.stage || null
  form.value.subjectNames = defaultSubjects(value)
}

function optionLabel(options, value) {
  return options.find(item => item.value === value)?.label || value || "-"
}

function normalizeLoadedGrade() {
  if (!form.value.gradeCode && form.value.gradeName) {
    const grade = gradeOptions.find(item => item.label === form.value.gradeName)
    if (grade) {
      form.value.gradeCode = grade.value
      form.value.schoolStage = grade.stage
    }
  }
  if (form.value.semester === "上学期") {
    form.value.semester = "1"
  } else if (form.value.semester === "下学期") {
    form.value.semester = "2"
  }
  form.value.subjectNames = parseSubjectSnapshot(form.value.subjectSnapshot)
  if (!form.value.subjectNames.length) {
    form.value.subjectNames = defaultSubjects(form.value.gradeCode)
  }
}

function buildSubmitData() {
  const grade = gradeOptions.find(item => item.value === form.value.gradeCode)
  form.value.gradeName = grade?.label || null
  form.value.schoolStage = grade?.stage || null
  return {
    classId: form.value.classId,
    className: form.value.className,
    gradeName: form.value.gradeName,
    gradeCode: form.value.gradeCode,
    schoolStage: form.value.schoolStage,
    subjectSnapshot: JSON.stringify(normalizeSubjects(form.value.subjectNames)),
    schoolYear: form.value.schoolYear,
    semester: form.value.semester,
    status: form.value.status,
    remark: form.value.remark
  }
}

function defaultSubjects(gradeCode) {
  const grade = gradeOptions.find(item => item.value === gradeCode)
  if (!grade) {
    return [...primarySubjects]
  }
  if (grade.stage === "PRIMARY") {
    return [...primarySubjects]
  }
  if (grade.stage === "JUNIOR") {
    const subjects = [...juniorSubjects]
    if (gradeCode !== "JUNIOR_1") {
      subjects.push("物理")
    }
    if (gradeCode === "JUNIOR_3") {
      subjects.push("化学")
    }
    return normalizeSubjects(subjects)
  }
  return [...seniorSubjects]
}

function parseSubjectSnapshot(value) {
  if (!value) {
    return []
  }
  try {
    return normalizeSubjects(JSON.parse(value))
  } catch (e) {
    return []
  }
}

function normalizeSubjects(subjects) {
  return [...new Set((subjects || []).map(item => String(item || "").trim()).filter(Boolean))]
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.classId)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function handleAdd() {
  reset()
  open.value = true
  title.value = "新增排座班级"
}

function handleUpdate(row) {
  reset()
  const classId = row.classId || ids.value
  getClass(classId).then(response => {
    form.value = response.data
    normalizeLoadedGrade()
    open.value = true
    title.value = "修改排座班级"
  })
}

function submitForm() {
  proxy.$refs["classRef"].validate(valid => {
    if (!valid) {
      return
    }
    const submitData = buildSubmitData()
    if (form.value.classId != null) {
      updateClass(submitData).then(() => {
        proxy.$modal.msgSuccess("修改成功")
        open.value = false
        getList()
      })
    } else {
      addClass(submitData).then(() => {
        proxy.$modal.msgSuccess("新增成功")
        open.value = false
        getList()
      })
    }
  })
}

function handleDelete(row) {
  const classIds = row.classId || ids.value
  proxy.$modal.confirm(`是否确认删除班级编号为“${classIds}”的数据项？`).then(function() {
    return delClass(classIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function handleExport() {
  proxy.download("seating/class/export", {
    ...queryParams.value
  }, `class_${new Date().getTime()}.xlsx`)
}

function handleCopyNewTerm(row) {
  copyForm.value = createNewTermCopyForm(row)
  copyOpen.value = true
}

function handleCopyStudentsChange() {
  normalizeCopyRelations(copyForm.value)
}

function buildCopySubmitData() {
  const normalized = normalizeCopyRelations(copyForm.value)
  return {
    className: String(normalized.className ?? '').trim(),
    schoolYear: String(normalized.schoolYear ?? '').trim(),
    semester: String(normalized.semester ?? '').trim(),
    copyStudents: normalized.copyStudents,
    copyRelations: normalized.copyRelations,
    copyRules: normalized.copyRules,
    copyClassroomLayout: normalized.copyClassroomLayout,
  }
}

function submitCopyForm() {
  const errorMessage = validateNewTermCopyForm(copyForm.value)
  if (errorMessage) {
    proxy.$modal.msgError(errorMessage)
    return
  }
  copySubmitting.value = true
  copyClassNewTerm(copyForm.value.sourceClassId, buildCopySubmitData()).then(response => {
    proxy.$modal.msgSuccess("复制成功")
    copyOpen.value = false
    getList()
    if (response?.data?.classId) {
      ids.value = [response.data.classId]
      single.value = true
      multiple.value = false
    }
  }).finally(() => {
    copySubmitting.value = false
  })
}

getList()
</script>

<style scoped lang="scss">
.class-page {
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


.table-toolbar {
  margin-bottom: 16px;
}

.toolbar-row {
  align-items: center;
}

.class-table {
  border: 1px solid #edf1f6;
  border-radius: 12px;
  overflow: hidden;
}

.class-table :deep(.el-table__header-wrapper th) {
  background: #f9fbff;
  color: #1f2329;
}

.class-table :deep(.el-table__row:hover > td) {
  background: #fbfdff;
}

.class-table :deep(.el-tag) {
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

.table-toolbar :deep(.el-button--warning.is-plain) {
  color: #c98512;
  background: #fff8eb;
  border-color: #ffe8bd;
}

.class-table :deep(.row-action-btn) {
  min-height: 28px;
  padding: 0 10px;
  border-radius: 8px;
  font-weight: 600;
}

.class-table :deep(.row-action-btn + .row-action-btn) {
  margin-left: 10px;
}

.class-table :deep(.row-action-btn.is-plain.el-button--primary) {
  color: #3f6ce8;
  background: #eef4ff;
  border-color: #d7e3ff;
}

.class-table :deep(.row-action-btn.is-plain.el-button--warning) {
  color: #c98512;
  background: #fff8eb;
  border-color: #ffe8bd;
}

.class-table :deep(.row-action-btn.is-plain.el-button--danger) {
  color: #d94b4b;
  background: #fff1f1;
  border-color: #ffdada;
}

.class-table :deep(.row-action-btn.is-plain.el-button--danger:hover),
.class-table :deep(.row-action-btn.is-plain.el-button--danger:focus) {
  color: #c92a2a;
  background: #ffe7e7;
  border-color: #ffcaca;
}

.class-dialog :deep(.el-dialog),
.copy-dialog :deep(.el-dialog) {
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(31, 35, 41, 0.14);
}

.class-dialog :deep(.el-dialog__header),
.copy-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 18px 22px 10px;
  border-bottom: 1px solid #eef2f7;
}

.class-dialog :deep(.el-dialog__title),
.copy-dialog :deep(.el-dialog__title) {
  color: #1f2329;
  font-size: 16px;
  font-weight: 700;
}

.class-dialog :deep(.el-dialog__body),
.copy-dialog :deep(.el-dialog__body) {
  padding: 18px 22px 14px;
}

.class-dialog :deep(.el-dialog__footer),
.copy-dialog :deep(.el-dialog__footer) {
  padding: 0 22px 20px;
}

.class-dialog :deep(.el-input__wrapper),
.copy-dialog :deep(.el-input__wrapper),
.class-dialog :deep(.el-select__wrapper),
.copy-dialog :deep(.el-select__wrapper),
.class-dialog :deep(.el-textarea__inner),
.copy-dialog :deep(.el-textarea__inner) {
  border-radius: 10px;
  box-shadow: none;
  background: #fbfcfe;
}

.class-form {
  display: block;
}

.class-form-grid {
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

.class-form :deep(.el-form-item) {
  margin-bottom: 14px;
}

.class-form :deep(.el-form-item__label) {
  color: #556174;
  font-weight: 600;
}

.class-form :deep(.el-radio) {
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
  .class-page {
    padding: 12px;
  }

  .table-toolbar {
    margin-bottom: 12px;
  }

  .table-footer {
    justify-content: stretch;
  }
}
</style>








