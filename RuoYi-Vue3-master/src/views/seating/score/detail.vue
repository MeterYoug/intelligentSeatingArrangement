<template>
  <div class="app-container">
    <el-page-header @back="goBack">
      <template #content>
        <span>{{ exam.examName || "考试成绩" }}</span>
      </template>
      <template #extra>
        <el-space>
          <el-button icon="Upload" type="primary" plain @click="handleImport" v-hasPermi="['seating:studentScore:import']">导入成绩</el-button>
          <el-button icon="Download" type="warning" plain @click="handleExport" v-hasPermi="['seating:studentScore:export']">导出成绩</el-button>
          <el-checkbox v-model="exportByStudentNo">按学号排序导出</el-checkbox>
          <el-button icon="Finished" plain :disabled="exam.isCurrent === '1'" @click="handleSetCurrent" v-hasPermi="['seating:exam:edit']">设为当前</el-button>
          <el-button icon="Refresh" plain @click="handleSyncLevel" v-hasPermi="['seating:studentScore:sync']">同步等级</el-button>
        </el-space>
      </template>
    </el-page-header>

    <el-descriptions class="exam-summary" :column="4" border>
      <el-descriptions-item label="班级">{{ exam.className || "-" }}</el-descriptions-item>
      <el-descriptions-item label="年级">{{ exam.gradeNameSnapshot || "-" }}</el-descriptions-item>
      <el-descriptions-item label="考试日期">{{ exam.examDate || "-" }}</el-descriptions-item>
      <el-descriptions-item label="当前考试">
        <el-tag v-if="exam.isCurrent === '1'" type="success">是</el-tag>
        <span v-else>-</span>
      </el-descriptions-item>
      <el-descriptions-item label="科目" :span="4">
        <el-space wrap>
          <el-tag v-for="subject in subjects" :key="subject" size="small" effect="plain">{{ subject }}</el-tag>
          <span v-if="subjects.length === 0">-</span>
        </el-space>
      </el-descriptions-item>
      <el-descriptions-item label="备注" :span="4">{{ exam.remark || "-" }}</el-descriptions-item>
    </el-descriptions>

    <el-form :model="queryParams" ref="queryRef" :inline="true" class="score-query" label-width="68px">
      <el-form-item label="学号" prop="studentNo">
        <el-input v-model="queryParams.studentNo" placeholder="请输入学号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="姓名" prop="studentNameSnapshot">
        <el-input v-model="queryParams.studentNameSnapshot" placeholder="请输入姓名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="等级" prop="scoreLevel">
        <el-select v-model="queryParams.scoreLevel" placeholder="成绩等级" clearable style="width: 120px">
          <el-option v-for="item in scoreLevelOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="scoreList" :empty-text="emptyText" :default-sort="{ prop: 'studentNo', order: 'ascending' }" @selection-change="handleSelectionChange" @sort-change="handleSortChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="学号" align="center" prop="studentNo" min-width="110" sortable="custom" />
      <el-table-column label="姓名" align="center" prop="studentNameSnapshot" min-width="110" />
      <el-table-column label="科目成绩" align="left" prop="subjectScores" min-width="300">
        <template #default="scope">
          <el-space wrap>
            <el-tag v-for="item in subjectScoreItems(scope.row.subjectScores)" :key="item.name" size="small" effect="plain">
              {{ item.name }}：{{ item.value }}
            </el-tag>
          </el-space>
        </template>
      </el-table-column>
      <el-table-column label="总分" align="center" prop="totalScore" width="100" />
      <el-table-column label="排名" align="center" prop="classRank" width="90" />
      <el-table-column label="等级" align="center" prop="scoreLevel" width="90" />
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['seating:studentScore:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['seating:studentScore:remove']">删除</el-button>
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

    <el-dialog title="修改学生成绩" v-model="open" width="520px" append-to-body>
      <el-form ref="scoreRef" :model="form" label-width="90px">
        <el-form-item label="学号">
          <el-input v-model="form.studentNo" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.studentNameSnapshot" disabled />
        </el-form-item>
        <el-form-item label="总分">
          <el-input-number v-model="form.totalScore" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="open = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <excel-import-dialog
      ref="importScoreRef"
      title="成绩导入"
      action="/seating/student-score/importData"
      template-action="/seating/student-score/importTemplate"
      template-file-name="score_template"
      update-support-label="是否按学号更新该考试已有成绩"
      :extra-params="{ examId: queryParams.examId, classId: exam.classId }"
      @success="getList"
    />
  </div>
</template>

<script setup name="ScoreDetail">
import ExcelImportDialog from "@/components/ExcelImportDialog"
import { getExam, setCurrentExam } from "@/api/seating/exam"
import { listStudentScore, getStudentScore, updateStudentScore, delStudentScore, syncStudentScoreLevel } from "@/api/seating/studentScore"

const { proxy } = getCurrentInstance()
const route = useRoute()
const router = useRouter()

const scoreLevelOptions = [
  { value: "A", label: "A" },
  { value: "B", label: "B" },
  { value: "C", label: "C" },
  { value: "D", label: "D" }
]

const exam = ref({})
const scoreList = ref([])
const loading = ref(false)
const ids = ref([])
const total = ref(0)
const open = ref(false)
const exportByStudentNo = ref(true)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    examId: Number(route.params.examId),
    classId: undefined,
    studentNo: undefined,
    studentNameSnapshot: undefined,
    scoreLevel: undefined,
    studentNoOrder: "ASC"
  },
  form: {}
})

const { queryParams, form } = toRefs(data)

const subjects = computed(() => parseSubjectSnapshot(exam.value.subjectSnapshot))
const emptyText = computed(() => exam.value.examId ? "当前考试暂无成绩，请先导入成绩" : "考试不存在或加载失败")

function loadExam() {
  if (!queryParams.value.examId) {
    proxy.$modal.msgError("缺少考试编号")
    return
  }
  getExam(queryParams.value.examId).then(response => {
    exam.value = response.data || {}
    queryParams.value.classId = exam.value.classId
    getList()
  })
}

function getList() {
  if (!queryParams.value.examId) return
  loading.value = true
  listStudentScore({
    pageNum: queryParams.value.pageNum,
    pageSize: queryParams.value.pageSize,
    examId: queryParams.value.examId,
    classId: queryParams.value.classId,
    studentNo: queryParams.value.studentNo,
    studentNameSnapshot: queryParams.value.studentNameSnapshot,
    scoreLevel: queryParams.value.scoreLevel,
    studentNoOrder: queryParams.value.studentNoOrder
  }).then(response => {
    scoreList.value = response.rows
    total.value = response.total
    loading.value = false
  }).catch(() => {
    loading.value = false
  })
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  queryParams.value.studentNoOrder = "ASC"
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.scoreId)
}

function handleSortChange({ prop, order }) {
  if (prop !== "studentNo") return
  queryParams.value.studentNoOrder = order === "descending" ? "DESC" : "ASC"
  handleQuery()
}

function handleImport() {
  if (!exam.value.examId) {
    proxy.$modal.msgError("考试信息未加载")
    return
  }
  proxy.$refs["importScoreRef"].open()
}

function handleExport() {
  if (!exam.value.examId) {
    proxy.$modal.msgError("考试信息未加载")
    return
  }
  proxy.download("seating/student-score/export", {
    examId: queryParams.value.examId,
    classId: queryParams.value.classId,
    studentNo: queryParams.value.studentNo,
    studentNameSnapshot: queryParams.value.studentNameSnapshot,
    scoreLevel: queryParams.value.scoreLevel,
    studentNoOrder: exportByStudentNo.value ? "ASC" : undefined
  }, `${exam.value.examName || "学生成绩"}_成绩.xlsx`)
}

function handleSetCurrent() {
  if (!exam.value.examId) return
  setCurrentExam(exam.value.examId).then(() => {
    proxy.$modal.msgSuccess("已设为当前考试")
    loadExam()
  })
}

function handleSyncLevel() {
  if (!exam.value.examId) return
  proxy.$modal.confirm("确认将当前考试成绩等级同步到学生档案吗？").then(() => {
    return syncStudentScoreLevel(exam.value.examId)
  }).then(response => {
    proxy.$modal.msgSuccess(`已同步 ${response.data} 名学生`)
  }).catch(() => {})
}

function handleUpdate(row) {
  getStudentScore(row.scoreId).then(response => {
    form.value = response.data
    open.value = true
  })
}

function submitForm() {
  updateStudentScore(form.value).then(() => {
    proxy.$modal.msgSuccess("修改成功")
    open.value = false
    getList()
  })
}

function handleDelete(row) {
  const scoreIds = row.scoreId || ids.value
  proxy.$modal.confirm('是否确认删除学生成绩编号为"' + scoreIds + '"的数据项？').then(() => {
    return delStudentScore(scoreIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function goBack() {
  router.push("/seating/score")
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

function subjectScoreItems(value) {
  if (!value) return []
  try {
    return Object.entries(JSON.parse(value)).map(([name, score]) => ({ name, value: score }))
  } catch (e) {
    return []
  }
}

loadExam()
</script>

<style scoped>
.exam-summary {
  margin-top: 16px;
}

.score-query {
  margin-top: 16px;
}
</style>
