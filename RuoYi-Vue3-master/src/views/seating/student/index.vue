<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
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

    <el-table v-loading="loading" :data="studentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="班级" align="center" prop="className" min-width="120" />
      <el-table-column label="学号" align="center" prop="studentNo" />
      <el-table-column label="学生姓名" align="center" prop="studentName" />
      <el-table-column label="性别" align="center" prop="gender">
        <template #default="scope">
          <dict-tag :options="sys_user_sex" :value="scope.row.gender" />
        </template>
      </el-table-column>
      <el-table-column label="身高（cm）" align="center" prop="heightCm" />
      <el-table-column label="视力等级" align="center" prop="visionLevel">
        <template #default="scope">{{ optionLabel(visionOptions, scope.row.visionLevel) }}</template>
      </el-table-column>
      <el-table-column label="成绩等级" align="center" prop="scoreLevel">
        <template #default="scope">{{ optionLabel(scoreOptions, scope.row.scoreLevel) }}</template>
      </el-table-column>
      <el-table-column label="纪律等级" align="center" prop="disciplineLevel">
        <template #default="scope">{{ optionLabel(disciplineOptions, scope.row.disciplineLevel) }}</template>
      </el-table-column>
      <el-table-column label="特殊需求" align="center" prop="specialNeed" show-overflow-tooltip />
      <el-table-column label="排序号" align="center" prop="sortNo" />
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['seating:student:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['seating:student:remove']">删除</el-button>
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

    <!-- 添加或修改排座学生对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="studentRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
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
          <el-col :span="24">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="学生姓名" prop="studentName">
              <el-input v-model="form.studentName" placeholder="请输入学生姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="性别" prop="gender">
              <el-radio-group v-model="form.gender">
                <el-radio v-for="dict in sys_user_sex" :key="dict.value" :value="dict.value">
                  {{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
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
          <el-col :span="24">
            <el-form-item label="视力等级" prop="visionLevel">
              <el-select v-model="form.visionLevel" placeholder="请选择视力等级" style="width: 100%">
                <el-option v-for="item in visionOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="成绩等级" prop="scoreLevel">
              <el-select v-model="form.scoreLevel" placeholder="请选择成绩等级" clearable style="width: 100%">
                <el-option v-for="item in scoreOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="纪律等级" prop="disciplineLevel">
              <el-select v-model="form.disciplineLevel" placeholder="请选择纪律等级" style="width: 100%">
                <el-option v-for="item in disciplineOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
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
            <el-form-item label="排序号" prop="sortNo">
              <el-input-number v-model="form.sortNo" :min="0" :max="9999" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">
                  {{ dict.label }}
                </el-radio>
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

/** 查询排座学生列表 */
function getList() {
  loading.value = true
  listStudent(queryParams.value).then(response => {
    studentList.value = response.rows
    total.value = response.total
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
    title.value = "添加排座学生"
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
    title.value = "修改排座学生"
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
  proxy.$modal.confirm('是否确认删除排座学生编号为"' + _studentIds + '"的数据项？').then(function() {
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

<style scoped>
.student-import-form {
  margin-bottom: 12px;
}
</style>
