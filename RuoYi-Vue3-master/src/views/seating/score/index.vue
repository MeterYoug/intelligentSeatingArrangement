<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
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
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAddExam" v-hasPermi="['seating:exam:add']">新增考试</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="handleQuery"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="examList">
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template #default="scope">
          <el-button link type="primary" icon="View" @click="handleViewScore(scope.row)">查看成绩</el-button>
          <el-button
            link
            type="primary"
            icon="Finished"
            :disabled="scope.row.isCurrent === '1'"
            @click="handleSetCurrent(scope.row)"
            v-hasPermi="['seating:exam:edit']"
          >设为当前</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog :title="title" v-model="open" width="520px" append-to-body>
      <el-form ref="examRef" :model="form" :rules="rules" label-width="90px">
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
        <el-form-item label="考试名称" prop="examName">
          <el-input v-model="form.examName" placeholder="请输入考试名称" />
        </el-form-item>
        <el-form-item label="考试日期" prop="examDate">
          <el-date-picker v-model="form.examDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择考试日期" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="open = false">取 消</el-button>
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

function getClassOptions() {
  return listClass({ status: "0" }).then(response => {
    classOptions.value = response.rows
  })
}

function getList() {
  loading.value = true
  listExam({ ...queryParams.value, status: "0" }).then(response => {
    examList.value = response.rows
    loading.value = false
  }).catch(() => {
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
