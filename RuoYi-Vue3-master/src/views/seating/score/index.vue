<template>
  <div class="score-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <p class="eyebrow">慧排座 · 成绩管理</p>
        <h1>考试批次维护</h1>
        <p class="overview-text">
          按班级维护考试批次，支撑成绩导入、同步等级和查看成绩详情流程。
        </p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" icon="Plus" @click="handleAddExam" v-hasPermi="['seating:exam:add']">新增考试</el-button>
      </div>
    </section>

    <el-card v-show="showSearch" shadow="never" class="panel-card search-card">
      <template #header>
        <div class="panel-header">
          <span>筛选条件</span>
          <small>按班级和考试名称快速定位考试批次</small>
        </div>
      </template>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px" class="search-form">
        <el-form-item label="班级" prop="classId">
          <el-select v-model="queryParams.classId" placeholder="请选择班级" filterable clearable style="width: 180px">
            <el-option
              v-for="item in classOptions"
              :key="item.classId"
              :label="item.className"
              :value="item.classId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="queryParams.examName" placeholder="请输入考试名称" clearable @keyup.enter="handleQuery" />
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
          <span>考试列表</span>
          <small>支持查看成绩、设置当前考试和新增考试</small>
        </div>
      </template>
      <div class="table-toolbar">
        <el-row :gutter="10" class="toolbar-row">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="Plus"
              @click="handleAddExam"
              v-hasPermi="['seating:exam:add']"
            >新增</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </div>

      <el-table v-loading="loading" :data="examList" class="score-table">
        <el-table-column label="班级" align="center" prop="className" min-width="120" />
        <el-table-column label="考试名称" align="center" prop="examName" min-width="160" />
        <el-table-column label="考试日期" align="center" prop="examDate" width="120" />
        <el-table-column label="年级" align="center" prop="gradeNameSnapshot" width="120" />
        <el-table-column label="科目" align="left" prop="subjectSnapshot" min-width="320" show-overflow-tooltip>
          <template #default="scope">
            <el-space wrap>
              <el-tag v-for="subject in parseSubjectSnapshot(scope.row.subjectSnapshot)" :key="subject" size="small" effect="plain">
                {{ subject }}
              </el-tag>
            </el-space>
          </template>
        </el-table-column>
        <el-table-column label="当前" align="center" prop="isCurrent" width="90">
          <template #default="scope">
            <el-tag v-if="scope.row.isCurrent === '1'" type="success">是</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
          <template #default="scope">
            <el-button type="primary" plain icon="View" class="row-action-btn" @click="handleViewScore(scope.row)">查看成绩</el-button>
            <el-button
              type="warning"
              plain
              icon="Finished"
              class="row-action-btn"
              :disabled="scope.row.isCurrent === '1'"
              @click="handleSetCurrent(scope.row)"
              v-hasPermi="['seating:exam:edit']"
            >设为当前</el-button>
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

    </el-card>

    <el-dialog :title="title" v-model="open" width="560px" append-to-body class="score-dialog">
      <el-form ref="examRef" :model="form" :rules="rules" label-width="88px" class="score-form">
        <el-row :gutter="16" class="score-form-grid">
          <el-col :span="24">
            <el-form-item label="班级" prop="classId">
              <el-select v-model="form.classId" placeholder="请选择班级" filterable style="width: 100%">
                <el-option
                  v-for="item in classOptions"
                  :key="item.classId"
                  :label="classLabel(item)"
                  :value="item.classId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="考试名称" prop="examName">
              <el-input v-model="form.examName" placeholder="请输入考试名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="考试日期" prop="examDate">
              <el-date-picker v-model="form.examDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择考试日期" style="width: 100%" />
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
          <el-button @click="open = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup name="Score">
import { listClass } from "@/api/seating/class"
import { listExam, addExam, setCurrentExam } from "@/api/seating/exam"

const { proxy } = getCurrentInstance()
const router = useRouter()

const classOptions = ref([])
const examList = ref([])
const loading = ref(false)
const listError = ref(false)
const showSearch = ref(true)
const open = ref(false)
const title = ref("")

const data = reactive({
  queryParams: {
    classId: undefined,
    examName: undefined
  },
  form: {},
  rules: {
    classId: [{ required: true, message: "请选择班级", trigger: "change" }],
    examName: [{ required: true, message: "考试名称不能为空", trigger: "blur" }],
    examDate: [{ required: true, message: "请选择考试日期", trigger: "change" }]
  }
})

const { queryParams, form, rules } = toRefs(data)

const hasActiveFilters = computed(() => Object.values(queryParams.value).some(value => value !== undefined && value !== null && value !== ""))
const listEmptyDescription = computed(() => listError.value ? "考试列表加载失败，请检查网络后重试。" : hasActiveFilters.value ? "没有找到符合条件的考试。" : "暂无考试，请先新增考试。")
const listEmptyActionText = computed(() => listError.value ? "重新加载" : hasActiveFilters.value ? "重置筛选" : "新增考试")

function handleListEmptyAction() {
  if (listError.value) {
    getList()
  } else if (hasActiveFilters.value) {
    resetQuery()
  } else {
    handleAddExam()
  }
}

function getClassOptions() {
  return listClass({ status: "0" }).then(response => {
    classOptions.value = response.rows
  })
}

function getList() {
  loading.value = true
  listError.value = false
  listExam({ ...queryParams.value, status: "0" }).then(response => {
    examList.value = response.rows
  }).catch(() => {
    examList.value = []
    listError.value = true
  }).finally(() => {
    loading.value = false
  })
}

function handleQuery() {
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  getList()
}

function resetForm() {
  form.value = {
    examId: null,
    classId: queryParams.value.classId || null,
    examName: null,
    examDate: null,
    remark: null
  }
  proxy.resetForm("examRef")
}

function handleAddExam() {
  resetForm()
  open.value = true
  title.value = "新增考试"
}

function submitForm() {
  proxy.$refs["examRef"].validate(valid => {
    if (!valid) return
    addExam(form.value).then(() => {
      proxy.$modal.msgSuccess("新增成功")
      open.value = false
      queryParams.value.classId = form.value.classId
      queryParams.value.examName = undefined
      getList()
    })
  })
}

function handleViewScore(row) {
  router.push("/seating/score-detail/index/" + row.examId)
}

function handleSetCurrent(row) {
  setCurrentExam(row.examId).then(() => {
    proxy.$modal.msgSuccess("已设为当前考试")
    getList()
  })
}

function classLabel(item) {
  return item.gradeName ? `${item.className}（${item.gradeName}）` : item.className
}

function parseSubjectSnapshot(value) {
  if (!value) return []
  try {
    return normalizeSubjects(JSON.parse(value))
  } catch (e) {
    return []
  }
}

function normalizeSubjects(subjects) {
  return [...new Set((subjects || []).map(item => String(item || "").trim()).filter(Boolean))]
}

getClassOptions()
getList()
</script>

<style scoped lang="scss">
.score-page {
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

.search-form :deep(.el-input__wrapper),
.search-form :deep(.el-select__wrapper),
.search-form :deep(.el-textarea__inner),
.search-form :deep(.el-input-number__wrapper) {
  min-height: 34px;
  border: 1px solid #d7e0ea;
  border-radius: 11px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  background: #ffffff;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.search-form :deep(.el-input__wrapper:hover),
.search-form :deep(.el-select__wrapper:hover),
.search-form :deep(.el-textarea__inner:hover),
.search-form :deep(.el-input-number__wrapper:hover) {
  border-color: #b8c7db;
}

.search-form :deep(.el-input.is-focus .el-input__wrapper),
.search-form :deep(.el-select__wrapper.is-focused),
.search-form :deep(.el-textarea__inner:focus),
.search-form :deep(.el-input-number.is-focus .el-input-number__wrapper) {
  border-color: #4c7df0;
  box-shadow: 0 0 0 3px rgba(76, 125, 240, 0.12), 0 1px 2px rgba(15, 23, 42, 0.04);
}

.search-form :deep(.el-input__inner),
.search-form :deep(.el-select__selected-item),
.search-form :deep(.el-textarea__inner) {
  color: #1f2937;
}

.search-form :deep(.el-input__inner::placeholder),
.search-form :deep(.el-textarea__inner::placeholder) {
  color: #a1adbb;
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

.score-table {
  border: 1px solid #edf1f6;
  border-radius: 12px;
  overflow: hidden;
}

.score-table :deep(.el-table__header-wrapper th) {
  background: #f9fbff;
  color: #1f2329;
}

.score-table :deep(.el-table__row:hover > td) {
  background: #fbfdff;
}

.score-table :deep(.el-tag) {
  border-radius: 999px;
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

.row-action-btn.is-plain.el-button--warning {
  color: #c98512;
  background: #fff8eb;
  border-color: #ffe8bd;
}

.row-action-btn.is-plain.el-button--warning:hover,
.row-action-btn.is-plain.el-button--warning:focus {
  color: #b8760e;
  background: #fff1d6;
  border-color: #ffd89c;
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.score-dialog :deep(.el-dialog) {
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(31, 35, 41, 0.14);
}

.score-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 18px 22px 10px;
  border-bottom: 1px solid #eef2f7;
}

.score-dialog :deep(.el-dialog__title) {
  color: #1f2329;
  font-size: 16px;
  font-weight: 700;
}

.score-dialog :deep(.el-dialog__body) {
  padding: 18px 22px 14px;
}

.score-dialog :deep(.el-dialog__footer) {
  padding: 0 22px 20px;
}

.score-dialog :deep(.el-input__wrapper),
.score-dialog :deep(.el-select__wrapper),
.score-dialog :deep(.el-textarea__inner),
.score-dialog :deep(.el-input-number__wrapper),
.score-dialog :deep(.el-date-editor) {
  border: 1px solid #d7e0ea;
  border-radius: 11px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  background: #ffffff;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.score-dialog :deep(.el-input__wrapper:hover),
.score-dialog :deep(.el-select__wrapper:hover),
.score-dialog :deep(.el-textarea__inner:hover),
.score-dialog :deep(.el-input-number__wrapper:hover),
.score-dialog :deep(.el-date-editor:hover) {
  border-color: #b8c7db;
}

.score-dialog :deep(.el-input.is-focus .el-input__wrapper),
.score-dialog :deep(.el-select__wrapper.is-focused),
.score-dialog :deep(.el-textarea__inner:focus),
.score-dialog :deep(.el-input-number.is-focus .el-input-number__wrapper),
.score-dialog :deep(.el-date-editor.is-focused) {
  border-color: #4c7df0;
  box-shadow: 0 0 0 3px rgba(76, 125, 240, 0.12), 0 1px 2px rgba(15, 23, 42, 0.04);
}

.score-dialog :deep(.el-input__inner),
.score-dialog :deep(.el-select__selected-item),
.score-dialog :deep(.el-textarea__inner) {
  color: #1f2937;
}

.score-dialog :deep(.el-input__inner::placeholder),
.score-dialog :deep(.el-textarea__inner::placeholder) {
  color: #a1adbb;
}

.score-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.score-form :deep(.el-form-item__label) {
  color: #4f5b6d;
  font-weight: 600;
}

.score-form :deep(.el-form-item__content) {
  min-height: 40px;
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
    flex-direction: column;
  }

  .hero-actions {
    justify-content: flex-start;
  }
}

@media (max-width: 768px) {
  .score-page {
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


