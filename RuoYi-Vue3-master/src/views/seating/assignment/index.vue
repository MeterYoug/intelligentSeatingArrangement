<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="方案ID" prop="planId">
        <el-input
          v-model="queryParams.planId"
          placeholder="请输入方案ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="班级ID" prop="classId">
        <el-input
          v-model="queryParams.classId"
          placeholder="请输入班级ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="教室布局ID" prop="classroomId">
        <el-input
          v-model="queryParams.classroomId"
          placeholder="请输入教室布局ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="座位ID" prop="seatId">
        <el-input
          v-model="queryParams.seatId"
          placeholder="请输入座位ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学生ID" prop="studentId">
        <el-input
          v-model="queryParams.studentId"
          placeholder="请输入学生ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="学生姓名快照" prop="studentNameSnapshot">
        <el-input
          v-model="queryParams.studentNameSnapshot"
          placeholder="请输入学生姓名快照"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="行号快照" prop="rowIndex">
        <el-input
          v-model="queryParams.rowIndex"
          placeholder="请输入行号快照"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="列号快照" prop="colIndex">
        <el-input
          v-model="queryParams.colIndex"
          placeholder="请输入列号快照"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否锁定" prop="isLocked">
        <el-input
          v-model="queryParams.isLocked"
          placeholder="请输入是否锁定"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分配来源" prop="assignSource">
        <el-input
          v-model="queryParams.assignSource"
          placeholder="请输入分配来源"
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
          v-hasPermi="['seating:assignment:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['seating:assignment:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['seating:assignment:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['seating:assignment:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="assignmentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="分配ID" align="center" prop="assignmentId" />
      <el-table-column label="方案ID" align="center" prop="planId" />
      <el-table-column label="班级ID" align="center" prop="classId" />
      <el-table-column label="教室布局ID" align="center" prop="classroomId" />
      <el-table-column label="座位ID" align="center" prop="seatId" />
      <el-table-column label="学生ID" align="center" prop="studentId" />
      <el-table-column label="学生姓名快照" align="center" prop="studentNameSnapshot" />
      <el-table-column label="行号快照" align="center" prop="rowIndex" />
      <el-table-column label="列号快照" align="center" prop="colIndex" />
      <el-table-column label="是否锁定" align="center" prop="isLocked" />
      <el-table-column label="分配来源" align="center" prop="assignSource" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['seating:assignment:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['seating:assignment:remove']">删除</el-button>
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

    <!-- 添加或修改排座分配对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="assignmentRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="方案ID" prop="planId">
              <el-input v-model="form.planId" placeholder="请输入方案ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="班级ID" prop="classId">
              <el-input v-model="form.classId" placeholder="请输入班级ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="教室布局ID" prop="classroomId">
              <el-input v-model="form.classroomId" placeholder="请输入教室布局ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="座位ID" prop="seatId">
              <el-input v-model="form.seatId" placeholder="请输入座位ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="学生ID" prop="studentId">
              <el-input v-model="form.studentId" placeholder="请输入学生ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="学生姓名快照" prop="studentNameSnapshot">
              <el-input v-model="form.studentNameSnapshot" placeholder="请输入学生姓名快照" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="行号快照" prop="rowIndex">
              <el-input v-model="form.rowIndex" placeholder="请输入行号快照" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="列号快照" prop="colIndex">
              <el-input v-model="form.colIndex" placeholder="请输入列号快照" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否锁定" prop="isLocked">
              <el-input v-model="form.isLocked" placeholder="请输入是否锁定" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="分配来源" prop="assignSource">
              <el-input v-model="form.assignSource" placeholder="请输入分配来源" />
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

<script setup name="Assignment">
import { listAssignment, getAssignment, delAssignment, addAssignment, updateAssignment } from "@/api/seating/assignment"

const { proxy } = getCurrentInstance()

const assignmentList = ref([])
const open = ref(false)
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
    planId: undefined,
    classId: undefined,
    classroomId: undefined,
    seatId: undefined,
    studentId: undefined,
    studentNameSnapshot: undefined,
    rowIndex: undefined,
    colIndex: undefined,
    isLocked: undefined,
    assignSource: undefined,
  },
  rules: {
    planId: [
      { required: true, message: "方案ID不能为空", trigger: "blur" }
    ],
    classId: [
      { required: true, message: "班级ID不能为空", trigger: "blur" }
    ],
    classroomId: [
      { required: true, message: "教室布局ID不能为空", trigger: "blur" }
    ],
    seatId: [
      { required: true, message: "座位ID不能为空", trigger: "blur" }
    ],
    rowIndex: [
      { required: true, message: "行号快照不能为空", trigger: "blur" }
    ],
    colIndex: [
      { required: true, message: "列号快照不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询排座分配列表 */
function getList() {
  loading.value = true
  listAssignment(queryParams.value).then(response => {
    assignmentList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    assignmentId: null,
    planId: null,
    classId: null,
    classroomId: null,
    seatId: null,
    studentId: null,
    studentNameSnapshot: null,
    rowIndex: null,
    colIndex: null,
    isLocked: null,
    assignSource: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("assignmentRef")
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
  ids.value = selection.map(item => item.assignmentId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加排座分配"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _assignmentId = row.assignmentId || ids.value
  getAssignment(_assignmentId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改排座分配"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["assignmentRef"].validate(valid => {
    if (valid) {
      if (form.value.assignmentId != null) {
        updateAssignment(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addAssignment(form.value).then(() => {
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
  const _assignmentIds = row.assignmentId || ids.value
  proxy.$modal.confirm('是否确认删除排座分配编号为"' + _assignmentIds + '"的数据项？').then(function() {
    return delAssignment(_assignmentIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('system/assignment/export', {
    ...queryParams.value
  }, `assignment_${new Date().getTime()}.xlsx`)
}

getList()
</script>
