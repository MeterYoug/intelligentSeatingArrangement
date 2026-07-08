<template>
  <div class="app-container position-page">
    <el-form class="position-search" :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
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
        <el-button type="primary" plain icon="Search" @click="handleQuery">搜索</el-button>
        <el-button plain icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="position-toolbar mb8">
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
          type="success"
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
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['seating:position:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table class="position-table" v-loading="loading" :data="positionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="行号，从1开始" align="center" prop="rowIndex" />
      <el-table-column label="列号，从1开始" align="center" prop="colIndex" />
      <el-table-column label="座位编号" align="center" prop="seatCode" />
      <el-table-column label="座位类型" align="center" prop="seatType" />
      <el-table-column label="是否可用" align="center" prop="isAvailable" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button class="row-action-btn" plain type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['seating:position:edit']">修改</el-button>
          <el-button class="row-action-btn" plain type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['seating:position:remove']">删除</el-button>
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
    <el-dialog class="position-dialog" :title="title" v-model="open" width="500px" append-to-body>
      <el-form class="position-form" ref="positionRef" :model="form" :rules="rules" label-width="100px">
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
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
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

<style scoped>
.position-page {
  min-height: calc(100vh - 84px);
  padding: 24px 24px 28px;
  background:
    radial-gradient(circle at top right, rgba(91, 140, 255, 0.08), transparent 30%),
    linear-gradient(180deg, #f6f8fc 0%, #f9fbff 42%, #ffffff 100%);
}

.position-search,
.position-toolbar,
.position-table {
  background: #fff;
}

.position-search {
  padding: 18px 20px 6px;
  margin-bottom: 16px;
  border: 1px solid #eef2f8;
  border-radius: 18px;
  box-shadow: 0 12px 32px rgba(31, 35, 41, 0.05);
}

.position-search :deep(.el-form-item) {
  margin-right: 16px;
  margin-bottom: 14px;
}

.position-search :deep(.el-form-item__label) {
  color: #556174;
  font-weight: 600;
}

.position-search :deep(.el-input__wrapper),
.position-search :deep(.el-select__wrapper),
.position-search :deep(.el-textarea__inner),
.position-search :deep(.el-input-number__wrapper) {
  min-height: 34px;
  border: 1px solid #d7e0ea;
  border-radius: 10px;
  box-shadow: none;
  background: #fff;
}

.position-search :deep(.el-input__wrapper:hover),
.position-search :deep(.el-select__wrapper:hover),
.position-search :deep(.el-textarea__inner:hover),
.position-search :deep(.el-input-number__wrapper:hover) {
  border-color: #9cb6ff;
  box-shadow: 0 0 0 1px rgba(90, 140, 255, 0.12);
}

.position-search :deep(.el-input.is-focus .el-input__wrapper),
.position-search :deep(.el-select__wrapper.is-focused),
.position-search :deep(.el-textarea__inner:focus),
.position-search :deep(.el-input-number.is-focus .el-input-number__wrapper) {
  border-color: #5a8cff;
  box-shadow: 0 0 0 2px rgba(90, 140, 255, 0.12);
}

.position-search :deep(.el-input__inner),
.position-search :deep(.el-select__selected-item),
.position-search :deep(.el-textarea__inner) {
  color: #243041;
}

.position-search :deep(.el-input__inner::placeholder),
.position-search :deep(.el-textarea__inner::placeholder) {
  color: #a2afbf;
}

.position-search :deep(.el-button) {
  min-height: 34px;
  padding: 0 14px;
  border-radius: 10px;
  box-shadow: none;
  font-weight: 600;
}

.position-search :deep(.el-button--primary) {
  color: #fff;
  background: linear-gradient(135deg, #5a8cff 0%, #3e6bf5 100%);
  border-color: transparent;
}

.position-search :deep(.el-button:not(.el-button--primary)) {
  color: #5f6b7a;
  background: #fff;
  border-color: #d8e0eb;
}

.position-toolbar {
  margin-bottom: 0 !important;
  padding: 14px 18px 10px;
  border: 1px solid #eef2f8;
  border-bottom: none;
  border-radius: 18px 18px 0 0;
  box-shadow: 0 10px 28px rgba(31, 35, 41, 0.05);
}

.position-toolbar :deep(.el-button) {
  min-height: 34px;
  padding: 0 14px;
  border-radius: 10px;
  box-shadow: none;
  font-weight: 600;
}

.position-toolbar :deep(.el-button--primary.is-plain) {
  color: #3f6ce8;
  background: #eef4ff;
  border-color: #d7e3ff;
}

.position-toolbar :deep(.el-button--success.is-plain) {
  color: #2f9d57;
  background: #eefaf2;
  border-color: #d8f1e0;
}

.position-toolbar :deep(.el-button--danger.is-plain) {
  color: #d94b4b;
  background: #fff1f1;
  border-color: #ffdada;
}

.position-toolbar :deep(.el-button--warning.is-plain) {
  color: #c98512;
  background: #fff8eb;
  border-color: #ffe8bd;
}

.position-table {
  border: 1px solid #eef2f8;
  border-top: none;
  border-radius: 0 0 18px 18px;
  overflow: hidden;
  box-shadow: 0 18px 40px rgba(31, 35, 41, 0.05);
}

.position-table :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.position-table :deep(.el-table__header-wrapper th) {
  background: #f7f9fc;
  color: #1f2329;
  font-weight: 600;
}

.position-table :deep(.el-table__cell) {
  color: #3c4655;
}

.position-table :deep(.row-action-btn) {
  min-height: 28px;
  padding: 0 10px;
  border-radius: 8px;
  font-weight: 600;
}

.position-table :deep(.row-action-btn + .row-action-btn) {
  margin-left: 10px;
}

.position-table :deep(.row-action-btn.el-button--primary) {
  color: #3f6ce8;
}

.position-table :deep(.row-action-btn.el-button--danger) {
  color: #d94b4b;
}

.position-table :deep(.row-action-btn.el-button--danger:hover),
.position-table :deep(.row-action-btn.el-button--danger:focus) {
  color: #c92a2a;
}

.position-dialog :deep(.el-dialog) {
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(31, 35, 41, 0.14);
}

.position-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 18px 22px 10px;
  border-bottom: 1px solid #eef2f7;
}

.position-dialog :deep(.el-dialog__title) {
  color: #1f2329;
  font-size: 16px;
  font-weight: 700;
}

.position-dialog :deep(.el-dialog__body) {
  padding: 18px 22px 14px;
}

.position-dialog :deep(.el-form-item) {
  margin-bottom: 14px;
}

.position-dialog :deep(.el-form-item__label) {
  color: #4f5b6d;
  font-weight: 600;
}

.position-dialog :deep(.el-input__wrapper),
.position-dialog :deep(.el-select__wrapper),
.position-dialog :deep(.el-textarea__inner),
.position-dialog :deep(.el-input-number__wrapper) {
  border: 1px solid #d7e0ea;
  border-radius: 10px;
  box-shadow: none;
  background: #fff;
}

.position-dialog :deep(.el-input__wrapper:hover),
.position-dialog :deep(.el-select__wrapper:hover),
.position-dialog :deep(.el-textarea__inner:hover),
.position-dialog :deep(.el-input-number__wrapper:hover) {
  border-color: #9cb6ff;
  box-shadow: 0 0 0 1px rgba(90, 140, 255, 0.12);
}

.position-dialog :deep(.el-input.is-focus .el-input__wrapper),
.position-dialog :deep(.el-select__wrapper.is-focused),
.position-dialog :deep(.el-textarea__inner:focus),
.position-dialog :deep(.el-input-number.is-focus .el-input-number__wrapper) {
  border-color: #5a8cff;
  box-shadow: 0 0 0 2px rgba(90, 140, 255, 0.12);
}

.position-form :deep(.el-form-item__content) {
  min-height: 40px;
}

.position-dialog :deep(.el-input__inner),
.position-dialog :deep(.el-select__selected-item),
.position-dialog :deep(.el-textarea__inner) {
  color: #243041;
}

.position-dialog :deep(.el-input__inner::placeholder),
.position-dialog :deep(.el-textarea__inner::placeholder) {
  color: #a2afbf;
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

@media (max-width: 1200px) {
  .position-page {
    padding: 16px;
  }

  .position-search {
    padding: 16px 16px 4px;
  }

  .position-toolbar {
    padding: 12px 16px 8px;
  }
}
</style>


