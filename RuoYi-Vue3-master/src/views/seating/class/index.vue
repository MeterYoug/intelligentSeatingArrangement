<template>
  <div class="app-container seating-page">
    <div class="seating-page-heading">
      <div>
        <h1>班级管理</h1>
        <p>维护班级、年级、学期和科目，为学生与成绩建立基础数据。</p>
      </div>
    </div>
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
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
          v-hasPermi="['seating:class:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
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
          type="info"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['seating:class:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="classList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="班级ID" align="center" prop="classId" />
      <el-table-column label="班级名称" align="center" prop="className" />
      <el-table-column label="年级" align="center" prop="gradeName" />
      <el-table-column label="科目" align="left" prop="subjectSnapshot" min-width="220" show-overflow-tooltip>
        <template #default="scope">
          <el-space wrap>
            <el-tag v-for="subject in parseSubjectSnapshot(scope.row.subjectSnapshot)" :key="subject" size="small" effect="plain">
              {{ subject }}
            </el-tag>
          </el-space>
        </template>
      </el-table-column>
      <el-table-column label="学年" align="center" prop="schoolYear" />
      <el-table-column label="学期" align="center" prop="semester">
        <template #default="scope">
          <span>{{ optionLabel(semesterOptions, scope.row.semester) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['seating:class:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['seating:class:remove']">删除</el-button>
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

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="classRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="班级名称" prop="className">
              <el-input v-model="form.className" placeholder="请输入班级名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
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
          <el-col :span="24">
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
          <el-col :span="24">
            <el-form-item label="学年" prop="schoolYear">
              <el-input v-model="form.schoolYear" placeholder="请输入学年" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
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
          <el-col :span="24">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
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
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确定</el-button>
          <el-button @click="cancel">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Class">
import { listClass, getClass, delClass, addClass, updateClass } from "@/api/seating/class"

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

getList()
</script>
