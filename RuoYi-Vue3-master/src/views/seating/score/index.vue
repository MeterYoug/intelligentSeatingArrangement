<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="班级" prop="classId">
        <el-select v-model="queryParams.classId" placeholder="请选择班级" filterable clearable style="width: 180px" @change="handleClassChange">
          <el-option
            v-for="item in classOptions"
            :key="item.classId"
            :label="item.className"
            :value="item.classId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="考试" prop="examId">
        <el-select v-model="queryParams.examId" placeholder="请选择考试" filterable clearable style="width: 220px" @change="handleQuery">
          <el-option
            v-for="item in examOptions"
            :key="item.examId"
            :label="examLabel(item)"
            :value="item.examId"
          />
        </el-select>
      </el-form-item>
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAddExam" v-hasPermi="['seating:exam:add']">新增考试</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="Upload" :disabled="!currentExam" @click="handleImport" v-hasPermi="['seating:studentScore:import']">导入成绩</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Finished" :disabled="!currentExam" @click="handleSetCurrent" v-hasPermi="['seating:exam:edit']">设为当前</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Refresh" :disabled="!currentExam" @click="handleSyncLevel" v-hasPermi="['seating:studentScore:sync']">同步等级</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getScoreList"></right-toolbar>
    </el-row>

    <el-alert
      v-if="currentExam"
      class="mb8"
      type="info"
      :closable="false"
      show-icon
      :title="examSummary"
    />

    <el-table v-loading="loading" :data="scoreList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="班级" align="center" prop="className" min-width="120" />
      <el-table-column label="学号" align="center" prop="studentNo" />
      <el-table-column label="姓名" align="center" prop="studentNameSnapshot" />
      <el-table-column label="科目成绩" align="left" prop="subjectScores" min-width="260">
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
          <el-button link type="primary" icon="Edit" @click="handleUpdateScore(scope.row)" v-hasPermi="['seating:studentScore:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDeleteScore(scope.row)" v-hasPermi="['seating:studentScore:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getScoreList"
    />

    <el-dialog :title="examTitle" v-model="examOpen" width="520px" append-to-body>
      <el-form ref="examRef" :model="examForm" :rules="examRules" label-width="90px">
        <el-form-item label="班级" prop="classId">
          <el-select v-model="examForm.classId" placeholder="请选择班级" filterable style="width: 100%">
            <el-option
              v-for="item in classOptions"
              :key="item.classId"
              :label="classLabel(item)"
              :value="item.classId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="examForm.examName" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="考试日期" prop="examDate">
          <el-date-picker v-model="examForm.examDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择考试日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="examForm.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitExam">确 定</el-button>
          <el-button @click="examOpen = false">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="修改学生成绩" v-model="scoreOpen" width="520px" append-to-body>
      <el-form ref="scoreRef" :model="scoreForm" label-width="90px">
        <el-form-item label="学号">
          <el-input v-model="scoreForm.studentNo" disabled />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="scoreForm.studentNameSnapshot" disabled />
        </el-form-item>
        <el-form-item label="总分">
          <el-input-number v-model="scoreForm.totalScore" :min="0" :precision="2" controls-position="right" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="scoreForm.remark" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitScore">确 定</el-button>
          <el-button @click="scoreOpen = false">取 消</el-button>
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
      :extra-params="{ examId: queryParams.examId, classId: currentExam?.classId }"
      @success="getScoreList"
    />
  </div>
</template>

<script setup name="Score">
import ExcelImportDialog from "@/components/ExcelImportDialog"
import { listClass } from "@/api/seating/class"
import { listExam, addExam, setCurrentExam } from "@/api/seating/exam"
import { listStudentScore, getStudentScore, updateStudentScore, delStudentScore, syncStudentScoreLevel } from "@/api/seating/studentScore"

const { proxy } = getCurrentInstance()

const scoreLevelOptions = [
  { value: "A", label: "A" },
  { value: "B", label: "B" },
  { value: "C", label: "C" },
  { value: "D", label: "D" }
]

const classOptions = ref([])
const examOptions = ref([])
const scoreList = ref([])
const loading = ref(false)
const showSearch = ref(true)
const ids = ref([])
const total = ref(0)
const examOpen = ref(false)
const examTitle = ref("")
const scoreOpen = ref(false)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    classId: undefined,
    examId: undefined,
    studentNo: undefined,
    studentNameSnapshot: undefined,
    scoreLevel: undefined
  },
  examForm: {},
  scoreForm: {},
  examRules: {
    classId: [{ required: true, message: "请选择班级", trigger: "change" }],
    examName: [{ required: true, message: "考试名称不能为空", trigger: "blur" }],
    examDate: [{ required: true, message: "请选择考试日期", trigger: "change" }]
  }
})

const { queryParams, examForm, scoreForm, examRules } = toRefs(data)

const currentExam = computed(() => examOptions.value.find(item => item.examId === queryParams.value.examId))
const examSummary = computed(() => {
  const exam = currentExam.value
  if (!exam) return ""
  const subjects = parseSubjectSnapshot(exam.subjectSnapshot).join("、")
  return `当前考试：${exam.examName}；年级：${exam.gradeNameSnapshot || "-"}；科目：${subjects || "-"}`
})

function getClassOptions() {
  return listClass({ status: "0" }).then(response => {
    classOptions.value = response.rows
  })
}

function getExamOptions() {
  return listExam({ classId: queryParams.value.classId, status: "0" }).then(response => {
    examOptions.value = response.rows
    if (queryParams.value.examId && !examOptions.value.some(item => item.examId === queryParams.value.examId)) {
      queryParams.value.examId = undefined
    }
  })
}

function getScoreList() {
  loading.value = true
  listStudentScore(queryParams.value).then(response => {
    scoreList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

function handleClassChange() {
  queryParams.value.examId = undefined
  getExamOptions().then(handleQuery)
}

function handleQuery() {
  queryParams.value.pageNum = 1
  getScoreList()
}

function resetQuery() {
  proxy.resetForm("queryRef")
  examOptions.value = []
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.scoreId)
}

function resetExam() {
  examForm.value = {
    examId: null,
    classId: queryParams.value.classId || null,
    examName: null,
    examDate: null,
    remark: null
  }
  proxy.resetForm("examRef")
}

function handleAddExam() {
  resetExam()
  examOpen.value = true
  examTitle.value = "新增考试"
}

function submitExam() {
  proxy.$refs["examRef"].validate(valid => {
    if (!valid) return
    addExam(examForm.value).then(() => {
      proxy.$modal.msgSuccess("新增成功")
      examOpen.value = false
      queryParams.value.classId = examForm.value.classId
      getExamOptions()
    })
  })
}

function handleImport() {
  if (!currentExam.value) {
    proxy.$modal.msgError("请先选择考试")
    return
  }
  proxy.$refs["importScoreRef"].open()
}

function handleSetCurrent() {
  setCurrentExam(queryParams.value.examId).then(() => {
    proxy.$modal.msgSuccess("已设为当前考试")
    getExamOptions()
  })
}

function handleSyncLevel() {
  proxy.$modal.confirm("确认将当前考试成绩等级同步到学生档案吗？").then(() => {
    return syncStudentScoreLevel(queryParams.value.examId)
  }).then(response => {
    proxy.$modal.msgSuccess(`已同步 ${response.data} 名学生`)
  }).catch(() => {})
}

function handleUpdateScore(row) {
  getStudentScore(row.scoreId).then(response => {
    scoreForm.value = response.data
    scoreOpen.value = true
  })
}

function submitScore() {
  updateStudentScore(scoreForm.value).then(() => {
    proxy.$modal.msgSuccess("修改成功")
    scoreOpen.value = false
    getScoreList()
  })
}

function handleDeleteScore(row) {
  const scoreIds = row.scoreId || ids.value
  proxy.$modal.confirm('是否确认删除学生成绩编号为"' + scoreIds + '"的数据项？').then(() => {
    return delStudentScore(scoreIds)
  }).then(() => {
    getScoreList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

function classLabel(item) {
  return item.gradeName ? `${item.className}（${item.gradeName}）` : item.className
}

function examLabel(item) {
  return item.isCurrent === "1" ? `${item.examName}（当前）` : item.examName
}

function parseSubjectSnapshot(value) {
  if (!value) return []
  try {
    return JSON.parse(value)
  } catch (e) {
    return []
  }
}

function subjectScoreItems(value) {
  if (!value) return []
  try {
    return Object.entries(JSON.parse(value)).map(([name, score]) => ({ name, value: score }))
  } catch (e) {
    return []
  }
}

getClassOptions()
getExamOptions()
getScoreList()
</script>
