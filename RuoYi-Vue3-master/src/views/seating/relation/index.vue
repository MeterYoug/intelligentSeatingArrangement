<template>
  <div class="app-container relation-page">
    <el-form class="relation-search" :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="班级ID" prop="classId">
        <el-input
          v-model="queryParams.classId"
          placeholder="请输入班级ID"
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
      <el-form-item label="关联学生ID" prop="relatedId">
        <el-input
          v-model="queryParams.relatedId"
          placeholder="请输入关联学生ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="关系权重" prop="relationWeight">
        <el-input
          v-model="queryParams.relationWeight"
          placeholder="请输入关系权重"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否启用" prop="enabled">
        <el-input
          v-model="queryParams.enabled"
          placeholder="请输入是否启用"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" plain icon="Search" @click="handleQuery">搜索</el-button>
        <el-button plain icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="relation-toolbar mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['seating:relation:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['seating:relation:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['seating:relation:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['seating:relation:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table class="relation-table" v-loading="loading" :data="relationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="关系类型" align="center" prop="relationType" />
      <el-table-column label="关系权重" align="center" prop="relationWeight" />
      <el-table-column label="是否启用" align="center" prop="enabled" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button class="row-action-btn" plain type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['seating:relation:edit']">修改</el-button>
          <el-button class="row-action-btn" plain type="danger" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['seating:relation:remove']">删除</el-button>
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

    <!-- 添加或修改排座学生关系约束对话框 -->
    <el-dialog class="relation-dialog" :title="title" v-model="open" width="500px" append-to-body>
      <el-form class="relation-form" ref="relationRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="班级ID" prop="classId">
              <el-input v-model="form.classId" placeholder="请输入班级ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="学生ID" prop="studentId">
              <el-input v-model="form.studentId" placeholder="请输入学生ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关联学生ID" prop="relatedId">
              <el-input v-model="form.relatedId" placeholder="请输入关联学生ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="关系权重" prop="relationWeight">
              <el-input v-model="form.relationWeight" placeholder="请输入关系权重" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否启用" prop="enabled">
              <el-input v-model="form.enabled" placeholder="请输入是否启用" />
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

<script setup name="Relation">
import { listRelation, getRelation, delRelation, addRelation, updateRelation } from "@/api/seating/relation"

const { proxy } = getCurrentInstance()

const relationList = ref([])
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
    classId: undefined,
    studentId: undefined,
    relatedId: undefined,
    relationType: undefined,
    relationWeight: undefined,
    enabled: undefined,
  },
  rules: {
    classId: [
      { required: true, message: "班级ID不能为空", trigger: "blur" }
    ],
    studentId: [
      { required: true, message: "学生ID不能为空", trigger: "blur" }
    ],
    relatedId: [
      { required: true, message: "关联学生ID不能为空", trigger: "blur" }
    ],
    relationType: [
      { required: true, message: "关系类型不能为空", trigger: "change" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询排座学生关系约束列表 */
function getList() {
  loading.value = true
  listRelation(queryParams.value).then(response => {
    relationList.value = response.rows
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
    relationId: null,
    classId: null,
    studentId: null,
    relatedId: null,
    relationType: null,
    relationWeight: null,
    enabled: null,
    createBy: null,
    createTime: null,
    updateBy: null,
    updateTime: null,
    remark: null
  }
  proxy.resetForm("relationRef")
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
  ids.value = selection.map(item => item.relationId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加排座学生关系约束"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _relationId = row.relationId || ids.value
  getRelation(_relationId).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改排座学生关系约束"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["relationRef"].validate(valid => {
    if (valid) {
      if (form.value.relationId != null) {
        updateRelation(form.value).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addRelation(form.value).then(() => {
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
  const _relationIds = row.relationId || ids.value
  proxy.$modal.confirm('是否确认删除排座学生关系约束编号为"' + _relationIds + '"的数据项？').then(function() {
    return delRelation(_relationIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('system/relation/export', {
    ...queryParams.value
  }, `relation_${new Date().getTime()}.xlsx`)
}

getList()
</script>

<style scoped>
.relation-page {
  min-height: calc(100vh - 84px);
  padding: 24px 24px 28px;
  background:
    radial-gradient(circle at top right, rgba(91, 140, 255, 0.08), transparent 30%),
    linear-gradient(180deg, #f6f8fc 0%, #f9fbff 42%, #ffffff 100%);
}

.relation-search,
.relation-toolbar,
.relation-table {
  background: #fff;
}

.relation-search {
  padding: 18px 20px 6px;
  margin-bottom: 16px;
  border: 1px solid #eef2f8;
  border-radius: 18px;
  box-shadow: 0 12px 32px rgba(31, 35, 41, 0.05);
}

.relation-search :deep(.el-form-item) {
  margin-right: 16px;
  margin-bottom: 14px;
}

.relation-search :deep(.el-form-item__label) {
  color: #556174;
  font-weight: 600;
}

.relation-search :deep(.el-input__wrapper),
.relation-search :deep(.el-select__wrapper),
.relation-search :deep(.el-textarea__inner),
.relation-search :deep(.el-input-number__wrapper) {
  min-height: 34px;
  border: 1px solid #d7e0ea;
  border-radius: 10px;
  box-shadow: none;
  background: #fff;
}

.relation-search :deep(.el-input__wrapper:hover),
.relation-search :deep(.el-select__wrapper:hover),
.relation-search :deep(.el-textarea__inner:hover),
.relation-search :deep(.el-input-number__wrapper:hover) {
  border-color: #9cb6ff;
  box-shadow: 0 0 0 1px rgba(90, 140, 255, 0.12);
}

.relation-search :deep(.el-input.is-focus .el-input__wrapper),
.relation-search :deep(.el-select__wrapper.is-focused),
.relation-search :deep(.el-textarea__inner:focus),
.relation-search :deep(.el-input-number.is-focus .el-input-number__wrapper) {
  border-color: #5a8cff;
  box-shadow: 0 0 0 2px rgba(90, 140, 255, 0.12);
}

.relation-search :deep(.el-input__inner),
.relation-search :deep(.el-select__selected-item),
.relation-search :deep(.el-textarea__inner) {
  color: #243041;
}

.relation-search :deep(.el-input__inner::placeholder),
.relation-search :deep(.el-textarea__inner::placeholder) {
  color: #a2afbf;
}

.relation-search :deep(.el-button) {
  min-height: 34px;
  padding: 0 14px;
  border-radius: 10px;
  box-shadow: none;
  font-weight: 600;
}

.relation-search :deep(.el-button--primary) {
  color: #fff;
  background: linear-gradient(135deg, #5a8cff 0%, #3e6bf5 100%);
  border-color: transparent;
}

.relation-search :deep(.el-button:not(.el-button--primary)) {
  color: #5f6b7a;
  background: #fff;
  border-color: #d8e0eb;
}

.relation-toolbar {
  margin-bottom: 0 !important;
  padding: 14px 18px 10px;
  border: 1px solid #eef2f8;
  border-bottom: none;
  border-radius: 18px 18px 0 0;
  box-shadow: 0 10px 28px rgba(31, 35, 41, 0.05);
}

.relation-toolbar :deep(.el-button) {
  min-height: 34px;
  padding: 0 14px;
  border-radius: 10px;
  box-shadow: none;
  font-weight: 600;
}

.relation-toolbar :deep(.el-button--primary.is-plain) {
  color: #3f6ce8;
  background: #eef4ff;
  border-color: #d7e3ff;
}

.relation-toolbar :deep(.el-button--success.is-plain) {
  color: #2f9d57;
  background: #eefaf2;
  border-color: #d8f1e0;
}

.relation-toolbar :deep(.el-button--danger.is-plain) {
  color: #d94b4b;
  background: #fff1f1;
  border-color: #ffdada;
}

.relation-toolbar :deep(.el-button--warning.is-plain) {
  color: #c98512;
  background: #fff8eb;
  border-color: #ffe8bd;
}

.relation-table {
  border: 1px solid #eef2f8;
  border-top: none;
  border-radius: 0 0 18px 18px;
  overflow: hidden;
  box-shadow: 0 18px 40px rgba(31, 35, 41, 0.05);
}

.relation-table :deep(.el-table__inner-wrapper::before) {
  display: none;
}

.relation-table :deep(.el-table__header-wrapper th) {
  background: #f7f9fc;
  color: #1f2329;
  font-weight: 600;
}

.relation-table :deep(.el-table__cell) {
  color: #3c4655;
}

.relation-table :deep(.row-action-btn) {
  min-height: 28px;
  padding: 0 10px;
  border-radius: 8px;
  font-weight: 600;
}

.relation-table :deep(.row-action-btn + .row-action-btn) {
  margin-left: 10px;
}

.relation-table :deep(.row-action-btn.el-button--primary) {
  color: #3f6ce8;
}

.relation-table :deep(.row-action-btn.el-button--danger) {
  color: #d94b4b;
}

.relation-table :deep(.row-action-btn.el-button--danger:hover),
.relation-table :deep(.row-action-btn.el-button--danger:focus) {
  color: #c92a2a;
}

.relation-dialog :deep(.el-dialog) {
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(31, 35, 41, 0.14);
}

.relation-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 18px 22px 10px;
  border-bottom: 1px solid #eef2f7;
}

.relation-dialog :deep(.el-dialog__title) {
  color: #1f2329;
  font-size: 16px;
  font-weight: 700;
}

.relation-dialog :deep(.el-dialog__body) {
  padding: 18px 22px 14px;
}

.relation-dialog :deep(.el-form-item) {
  margin-bottom: 14px;
}

.relation-dialog :deep(.el-form-item__label) {
  color: #4f5b6d;
  font-weight: 600;
}

.relation-dialog :deep(.el-input__wrapper),
.relation-dialog :deep(.el-select__wrapper),
.relation-dialog :deep(.el-textarea__inner),
.relation-dialog :deep(.el-input-number__wrapper) {
  border: 1px solid #d7e0ea;
  border-radius: 10px;
  box-shadow: none;
  background: #fff;
}

.relation-dialog :deep(.el-input__wrapper:hover),
.relation-dialog :deep(.el-select__wrapper:hover),
.relation-dialog :deep(.el-textarea__inner:hover),
.relation-dialog :deep(.el-input-number__wrapper:hover) {
  border-color: #9cb6ff;
  box-shadow: 0 0 0 1px rgba(90, 140, 255, 0.12);
}

.relation-dialog :deep(.el-input.is-focus .el-input__wrapper),
.relation-dialog :deep(.el-select__wrapper.is-focused),
.relation-dialog :deep(.el-textarea__inner:focus),
.relation-dialog :deep(.el-input-number.is-focus .el-input-number__wrapper) {
  border-color: #5a8cff;
  box-shadow: 0 0 0 2px rgba(90, 140, 255, 0.12);
}

.relation-form :deep(.el-form-item__content) {
  min-height: 40px;
}

.relation-dialog :deep(.el-input__inner),
.relation-dialog :deep(.el-select__selected-item),
.relation-dialog :deep(.el-textarea__inner) {
  color: #243041;
}

.relation-dialog :deep(.el-input__inner::placeholder),
.relation-dialog :deep(.el-textarea__inner::placeholder) {
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
  .relation-page {
    padding: 16px;
  }

  .relation-search {
    padding: 16px 16px 4px;
  }

  .relation-toolbar {
    padding: 12px 16px 8px;
  }
}
</style>



