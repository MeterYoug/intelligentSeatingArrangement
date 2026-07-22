<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="教室布局ID" prop="classroomId">
        <el-input
          v-model="queryParams.classroomId"
          placeholder="请输入教室布局ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="行号，从1开始" prop="rowIndex">
        <el-input
          v-model="queryParams.rowIndex"
          placeholder="请输入行号，从1开始"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="列号，从1开始" prop="colIndex">
        <el-input
          v-model="queryParams.colIndex"
          placeholder="请输入列号，从1开始"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="座位编号" prop="seatCode">
        <el-input
          v-model="queryParams.seatCode"
          placeholder="请输入座位编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否可用" prop="isAvailable">
        <el-input
          v-model="queryParams.isAvailable"
          placeholder="请输入是否可用"
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
          v-hasPermi="['seating:position:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['seating:position:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['seating:position:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['seating:position:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="positionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="座位ID" align="center" prop="seatId" />
      <el-table-column label="教室布局ID" align="center" prop="classroomId" />
      <el-table-column label="行号，从1开始" align="center" prop="rowIndex" />
      <el-table-column label="列号，从1开始" align="center" prop="colIndex" />
      <el-table-column label="座位编号" align="center" prop="seatCode" />
      <el-table-column label="座位类型" align="center" prop="seatType" />
      <el-table-column label="是否可用" align="center" prop="isAvailable" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['seating:position:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['seating:position:remove']">删除</el-button>
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

    <!-- 添加或修改排座座位位置对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="positionRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="教室布局ID" prop="classroomId">
              <el-input v-model="form.classroomId" placeholder="请输入教室布局ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="行号，从1开始" prop="rowIndex">
              <el-input v-model="form.rowIndex" placeholder="请输入行号，从1开始" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="列号，从1开始" prop="colIndex">
              <el-input v-model="form.colIndex" placeholder="请输入列号，从1开始" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="座位编号" prop="seatCode">
              <el-input v-model="form.seatCode" placeholder="请输入座位编号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否可用" prop="isAvailable">
              <el-input v-model="form.isAvailable" placeholder="请输入是否可用" />
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

<script setup name="Position">
import { listPosition, getPosition, delPosition, addPosition, updatePosition } from "@/api/seating/position"

const { proxy } = getCurrentInstance()

const positionList = ref([])
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
    classroomId: undefined,
    rowIndex: undefined,
    colIndex: undefined,
    seatCode: undefined,
    seatType: undefined,
    isAvailable: undefined,
    status: undefined,
  },
  rules: {
    classroomId: [
      { required: true, message: "教室布局ID不能为空", trigger: "blur" }
    ],
    rowIndex: [
      { required: true, message: "行号，从1开始不能为空", trigger: "blur" }
    ],
    colIndex: [
      { required: true, message: "列号，从1开始不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询排座座位位置列表 */
function getList() {
  loading.value = true
  listPosition(queryParams.value).then(response => {
    positionList.value = response.rows
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
    seatId: null,
    classroomId: null,
    rowIndex: null,
    colIndex: null,
    seatCode: null,
    seatType: null,
    isAvailable: null,
    status: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("positionRef")
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
  ids.value = selection.map(item => item.seatId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加排座座位位置"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _seatId = row.seatId || ids.value
  getPosition(_seatId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改排座座位位置"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["positionRef"].validate(valid => {
    if (valid) {
      if (form.value.seatId != null) {
        updatePosition(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addPosition(form.value).then(() => {
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
  const _seatIds = row.seatId || ids.value
  proxy.$modal.confirm('是否确认删除排座座位位置编号为"' + _seatIds + '"的数据项？').then(function() {
    return delPosition(_seatIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('system/position/export', {
    ...queryParams.value
  }, `position_${new Date().getTime()}.xlsx`)
}

getList()
</script>
