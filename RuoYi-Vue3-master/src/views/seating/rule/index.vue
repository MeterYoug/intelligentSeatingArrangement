<template>
  <div class="rule-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <p class="eyebrow">慧排座 · 排座规则</p>
        <h1>规则基础信息维护</h1>
        <p class="overview-text">
          维护班级排座规则、规则类别和权重，支撑后续生成、评分和人工微调流程。
        </p>
      </div>
      <div class="hero-actions">
        <el-button type="primary" icon="Plus" @click="handleAdd" v-hasPermi="['seating:rule:add']">新增规则</el-button>
      </div>
    </section>

    <el-card v-show="showSearch" shadow="never" class="panel-card search-card">
      <template #header>
        <div class="panel-header">
          <span>筛选条件</span>
          <small>按班级、规则名称、规则类别和启用状态快速定位</small>
        </div>
      </template>
      <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="68px" class="search-form">
        <el-form-item label="班级" prop="classId">
          <el-select v-model="queryParams.classId" placeholder="请选择班级" filterable clearable style="width: 180px">
            <el-option v-for="item in classOptions" :key="item.classId" :label="item.className" :value="item.classId" />
          </el-select>
        </el-form-item>
        <el-form-item label="规则名称" prop="ruleName">
          <el-input
            v-model="queryParams.ruleName"
            placeholder="请输入规则名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="规则类别" prop="ruleCategory">
          <el-select v-model="queryParams.ruleCategory" placeholder="请选择规则类别" clearable style="width: 130px">
            <el-option v-for="item in categoryOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-select v-model="queryParams.enabled" placeholder="请选择" clearable style="width: 100px">
            <el-option label="否" value="0" />
            <el-option label="是" value="1" />
          </el-select>
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
          <span>规则列表</span>
          <small>支持修改、删除和导出</small>
        </div>
      </template>
      <div class="table-toolbar">
        <el-row :gutter="10" class="toolbar-row">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="Plus"
              @click="handleAdd"
              v-hasPermi="['seating:rule:add']"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="success"
              plain
              icon="Edit"
              :disabled="single"
              @click="handleUpdate"
              v-hasPermi="['seating:rule:edit']"
            >修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="Delete"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['seating:rule:remove']"
            >删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="warning"
              plain
              icon="Download"
              @click="handleExport"
              v-hasPermi="['seating:rule:export']"
            >导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </div>

      <el-table v-loading="loading" :data="ruleList" @selection-change="handleSelectionChange" class="rule-table">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="班级" align="center" prop="className" min-width="120" />
        <el-table-column label="规则名称" align="center" prop="ruleName" min-width="150" />
        <el-table-column label="规则类别" align="center" prop="ruleCategory" width="110">
          <template #default="scope">{{ optionLabel(categoryOptions, scope.row.ruleCategory) }}</template>
        </el-table-column>
        <el-table-column label="规则类型" align="center" prop="ruleCode" min-width="140">
          <template #default="scope">{{ ruleOptionLabel(scope.row.ruleCode) }}</template>
        </el-table-column>
        <el-table-column label="规则配置" align="center" prop="ruleConfig" min-width="180" show-overflow-tooltip>
          <template #default="scope">{{ formatRuleConfig(scope.row) }}</template>
        </el-table-column>
        <el-table-column label="规则权重" align="center" prop="ruleWeight" width="100" />
        <el-table-column label="是否启用" align="center" prop="enabled" width="100">
          <template #default="scope">{{ scope.row.enabled === '1' ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template #default="scope"><dict-tag :options="sys_normal_disable" :value="scope.row.status" /></template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" min-width="140" show-overflow-tooltip />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
          <template #default="scope">
            <el-button type="primary" plain icon="Edit" class="row-action-btn" @click="handleUpdate(scope.row)" v-hasPermi="['seating:rule:edit']">修改</el-button>
            <el-button type="danger" plain icon="Delete" class="row-action-btn" @click="handleDelete(scope.row)" v-hasPermi="['seating:rule:remove']">删除</el-button>
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

      <div class="table-footer">
        <pagination
          v-show="total > 0"
          :total="total"
          v-model:page="queryParams.pageNum"
          v-model:limit="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </el-card>

    <el-dialog :title="title" v-model="open" width="760px" append-to-body class="rule-dialog">
      <div class="dialog-shell">
        <section class="dialog-section">
          <div class="dialog-section-header">
            <span>基础信息</span>
            <small>先选班级，再确定规则类别和规则编码</small>
          </div>
          <el-form ref="ruleRef" :model="form" :rules="rules" label-width="100px" class="rule-form">
            <el-row :gutter="16" class="rule-form-grid">
              <el-col :span="24">
                <el-form-item label="班级" prop="classId">
                  <el-select v-model="form.classId" placeholder="请选择班级" filterable style="width: 100%" @change="handleClassChange">
                    <el-option v-for="item in classOptions" :key="item.classId" :label="item.className" :value="item.classId" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="规则名称" prop="ruleName">
                  <el-input v-model="form.ruleName" placeholder="请输入规则名称" />
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="规则类别" prop="ruleCategory">
                  <div class="rule-category-field">
                    <el-radio-group v-model="form.ruleCategory" class="status-group" @change="handleCategoryChange">
                      <el-radio v-for="item in categoryOptions" :key="item.value" :value="item.value">{{ item.label }}</el-radio>
                    </el-radio-group>
                    <div class="rule-category-help">
                      <div><strong>硬规则：</strong>必须满足，违反时会判定为冲突，需要调整规则或数据后再生成。</div>
                      <div><strong>软规则：</strong>尽量满足，通过权重参与评分，系统会优先优化但不保证全部达成。</div>
                    </div>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="规则编码" prop="ruleCode">
                  <el-select v-model="form.ruleCode" placeholder="请选择规则" filterable style="width: 100%" @change="handleRuleCodeChange">
                    <el-option
                      v-for="item in availableRuleOptions"
                      :key="item.value"
                      :label="item.label"
                      :value="item.value"
                    >
                      <span>{{ item.label }}</span>
                      <span style="float: right; color: #8492a6; font-size: 12px">{{ item.value }}</span>
                    </el-option>
                  </el-select>
                </el-form-item>
              </el-col>
              <template v-if="form.ruleCode === 'FRONT_ROW'">
                <el-col :span="24">
                  <el-form-item label="指定学生" prop="studentIds">
                    <el-select v-model="form.studentIds" placeholder="请选择学生" multiple filterable clearable style="width: 100%">
                      <el-option
                        v-for="item in studentOptions"
                        :key="item.studentId"
                        :label="studentLabel(item)"
                        :value="item.studentId"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="24">
                  <el-form-item label="前几排" prop="frontRows">
                    <el-input-number v-model="form.frontRows" :min="1" :max="10" controls-position="right" style="width: 100%" />
                  </el-form-item>
                </el-col>
              </template>
              <el-col :span="24">
                <el-form-item label="规则权重" prop="ruleWeight">
                  <el-input-number v-model="form.ruleWeight" :min="0" :max="1000" controls-position="right" style="width: 100%" />
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="是否启用" prop="enabled">
                  <el-radio-group v-model="form.enabled" class="status-group">
                    <el-radio value="0">否</el-radio>
                    <el-radio value="1">是</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="状态" prop="status">
                  <el-radio-group v-model="form.status" class="status-group">
                    <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
                  </el-radio-group>
                </el-form-item>
              </el-col>
              <el-col :span="24">
                <el-form-item label="备注" prop="remark">
                  <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </section>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup name="Rule">
import { listRule, getRule, delRule, addRule, updateRule } from "@/api/seating/rule"
import { listClass } from "@/api/seating/class"
import { listStudent } from "@/api/seating/student"

const { proxy } = getCurrentInstance()
const { sys_normal_disable } = proxy.useDict("sys_normal_disable")

const categoryOptions = [
  { value: "HARD", label: "硬规则" },
  { value: "SOFT", label: "软规则" }
]
const ruleOptions = [
  { value: "FRONT_ROW", label: "指定学生坐前排", category: "HARD", defaultWeight: 100 },
  { value: "VISION_FRONT", label: "近视学生靠前", category: "SOFT", defaultWeight: 80 },
  { value: "HEIGHT_BACK", label: "高个学生靠后", category: "SOFT", defaultWeight: 60 },
  { value: "GENDER_BALANCE", label: "男女搭配均衡", category: "SOFT", defaultWeight: 40 },
  { value: "SCORE_BALANCE", label: "成绩强弱均衡", category: "SOFT", defaultWeight: 40 },
  { value: "DISCIPLINE_SCATTER", label: "纪律关注学生分散", category: "SOFT", defaultWeight: 70 }
]

const ruleList = ref([])
const classOptions = ref([])
const studentOptions = ref([])
const open = ref(false)
const loading = ref(true)
const listError = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const availableRuleOptions = computed(() => ruleOptions.filter(item => item.category === form.value.ruleCategory))

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    classId: undefined,
    ruleName: undefined,
    ruleCategory: undefined,
    ruleCode: undefined,
    ruleWeight: undefined,
    ruleConfig: undefined,
    enabled: undefined,
    status: undefined,
  },
  rules: {
    classId: [
      { required: true, message: "请选择班级", trigger: "change" }
    ],
    ruleName: [
      { required: true, message: "规则名称不能为空", trigger: "blur" }
    ],
    ruleCategory: [
      { required: true, message: "规则类别不能为空", trigger: "blur" }
    ],
    ruleCode: [
      { required: true, message: "规则编码不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

const hasActiveFilters = computed(() => Object.entries(queryParams.value).some(([key, value]) => !["pageNum", "pageSize"].includes(key) && value !== undefined && value !== null && value !== ""))
const listEmptyDescription = computed(() => listError.value ? "排座规则列表加载失败，请检查网络后重试。" : hasActiveFilters.value ? "没有找到符合条件的排座规则。" : "暂无排座规则，请先新增规则。")
const listEmptyActionText = computed(() => listError.value ? "重新加载" : hasActiveFilters.value ? "重置筛选" : "新增规则")

function handleListEmptyAction() {
  if (listError.value) {
    getList()
  } else if (hasActiveFilters.value) {
    resetQuery()
  } else {
    handleAdd()
  }
}

/** 查询排座规则列表 */
function getList() {
  loading.value = true
  listError.value = false
  listRule(queryParams.value).then(response => {
    ruleList.value = response.rows
    total.value = response.total
  }).catch(() => {
    ruleList.value = []
    total.value = 0
    listError.value = true
  }).finally(() => {
    loading.value = false
  })
}

function getClassOptions() {
  return listClass({ status: "0" }).then(response => {
    classOptions.value = response.rows
  })
}

function optionLabel(options, value) {
  return options.find(item => item.value === value)?.label || "-"
}

function ruleOptionLabel(value) {
  return ruleOptions.find(item => item.value === value)?.label || value
}

function studentLabel(student) {
  return student.studentNo ? `${student.studentName}（${student.studentNo}）` : student.studentName
}

function getStudentOptions(classId) {
  studentOptions.value = []
  if (!classId) {
    return Promise.resolve()
  }
  return listStudent({ classId, status: "0", pageNum: 1, pageSize: 1000 }).then(response => {
    studentOptions.value = response.rows || []
  })
}

function handleClassChange(classId) {
  form.value.studentIds = []
  getStudentOptions(classId)
}

function handleCategoryChange() {
  form.value.ruleCode = null
  form.value.ruleName = null
  form.value.ruleConfig = null
  form.value.studentIds = []
  form.value.frontRows = 2
}

function handleRuleCodeChange(ruleCode) {
  const option = ruleOptions.find(item => item.value === ruleCode)
  if (!option) {
    return
  }
  form.value.ruleName = option.label
  form.value.ruleWeight = option.defaultWeight
  if (ruleCode !== "FRONT_ROW") {
    form.value.studentIds = []
    form.value.frontRows = null
    form.value.ruleConfig = null
  } else {
    form.value.frontRows = form.value.frontRows || 2
  }
}

function parseRuleConfig(ruleConfig) {
  if (!ruleConfig) {
    return {}
  }
  try {
    return JSON.parse(ruleConfig)
  } catch (e) {
    return {}
  }
}

function applyRuleConfig(rule) {
  const config = parseRuleConfig(rule.ruleConfig)
  rule.studentIds = Array.isArray(config.studentIds) ? config.studentIds : []
  rule.frontRows = config.frontRows || 2
}

function buildRuleConfig() {
  if (form.value.ruleCode !== "FRONT_ROW") {
    return null
  }
  return JSON.stringify({
    studentIds: form.value.studentIds || [],
    frontRows: form.value.frontRows || 2
  })
}

function formatRuleConfig(row) {
  if (row.ruleCode !== "FRONT_ROW") {
    return "-"
  }
  const config = parseRuleConfig(row.ruleConfig)
  const count = Array.isArray(config.studentIds) ? config.studentIds.length : 0
  return `前${config.frontRows || 2}排，${count}名学生`
}

/** 取消按钮 */
function cancel() {
  open.value = false
  reset()
}

/** 表单重置 */
function reset() {
  form.value = {
    ruleId: null,
    classId: null,
    ruleName: null,
    ruleCategory: "SOFT",
    ruleCode: null,
    ruleWeight: null,
    ruleConfig: null,
    studentIds: [],
    frontRows: 2,
    enabled: "1",
    status: "0",
    remark: null
  }
  proxy.resetForm("ruleRef")
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
  ids.value = selection.map(item => item.ruleId)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加排座规则"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _ruleId = row.ruleId || ids.value
  getRule(_ruleId).then(response => {
    form.value = response.data
    applyRuleConfig(form.value)
    getStudentOptions(form.value.classId).then(() => {
      open.value = true
      title.value = "修改排座规则"
    })
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["ruleRef"].validate(valid => {
    if (valid) {
      const submitData = { ...form.value }
      if (submitData.ruleCode === "FRONT_ROW" && (!submitData.studentIds || submitData.studentIds.length === 0)) {
        proxy.$modal.msgError("请选择需要坐前排的学生")
        return
      }
      submitData.ruleConfig = buildRuleConfig()
      delete submitData.studentIds
      delete submitData.frontRows
      delete submitData.className
      delete submitData.delFlag
      delete submitData.createBy
      delete submitData.createTime
      delete submitData.updateBy
      delete submitData.updateTime
      if (form.value.ruleId != null) {
        updateRule(submitData).then(() => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addRule(submitData).then(() => {
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
  const _ruleIds = row.ruleId || ids.value
  proxy.$modal.confirm('是否确认删除排座规则编号为"' + _ruleIds + '"的数据项？').then(function() {
    return delRule(_ruleIds)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('seating/rule/export', {
    ...queryParams.value
  }, `rule_${new Date().getTime()}.xlsx`)
}

getClassOptions()
getList()
</script>
<style scoped lang="scss">
.rule-page {
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

.rule-table {
  border: 1px solid #edf1f6;
  border-radius: 12px;
  overflow: hidden;
}

.rule-table :deep(.el-table__header-wrapper th) {
  background: #f9fbff;
  color: #1f2329;
}

.rule-table :deep(.el-table__row:hover > td) {
  background: #fbfdff;
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

.row-action-btn.is-plain.el-button--danger {
  color: #d93026;
  background: #fff1f1;
  border-color: #ffd4d4;
}

.row-action-btn.is-plain.el-button--danger:hover,
.row-action-btn.is-plain.el-button--danger:focus {
  color: #c62828;
  background: #ffe4e4;
  border-color: #ffbbbb;
}

.table-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.rule-dialog :deep(.el-dialog) {
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(31, 35, 41, 0.14);
}

.rule-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 18px 22px 10px;
  border-bottom: 1px solid #eef2f7;
}

.rule-dialog :deep(.el-dialog__title) {
  color: #1f2329;
  font-size: 16px;
  font-weight: 700;
}

.rule-dialog :deep(.el-dialog__body) {
  padding: 18px 22px 14px;
}

.rule-dialog :deep(.el-dialog__footer) {
  padding: 0 22px 20px;
}

.dialog-shell {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.dialog-section {
  padding: 16px;
  border: 1px solid #e6ebf2;
  border-radius: 14px;
  background: linear-gradient(180deg, #ffffff 0%, #fbfcfe 100%);
}

.dialog-section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.dialog-section-header span {
  color: #1f2329;
  font-size: 14px;
  font-weight: 700;
}

.dialog-section-header small {
  color: #8a94a6;
  font-size: 12px;
}

.rule-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.rule-form :deep(.el-form-item__label) {
  color: #4f5b6d;
  font-weight: 600;
}

.rule-form :deep(.el-form-item__content) {
  min-height: 40px;
}

.rule-form :deep(.el-input__wrapper),
.rule-form :deep(.el-select__wrapper),
.rule-form :deep(.el-textarea__inner),
.rule-form :deep(.el-input-number__wrapper) {
  border: 1px solid #d7e0ea;
  border-radius: 11px;
  box-shadow: 0 1px 2px rgba(15, 23, 42, 0.04);
  background: #ffffff;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, background 0.18s ease;
}

.rule-form :deep(.el-input__wrapper:hover),
.rule-form :deep(.el-select__wrapper:hover),
.rule-form :deep(.el-textarea__inner:hover),
.rule-form :deep(.el-input-number__wrapper:hover) {
  border-color: #b8c7db;
}

.rule-form :deep(.el-input.is-focus .el-input__wrapper),
.rule-form :deep(.el-select__wrapper.is-focused),
.rule-form :deep(.el-textarea__inner:focus),
.rule-form :deep(.el-input-number.is-focus .el-input-number__wrapper) {
  border-color: #4c7df0;
  box-shadow: 0 0 0 3px rgba(76, 125, 240, 0.12), 0 1px 2px rgba(15, 23, 42, 0.04);
}

.rule-form :deep(.el-input__inner),
.rule-form :deep(.el-select__selected-item),
.rule-form :deep(.el-textarea__inner) {
  color: #1f2937;
}

.rule-form :deep(.el-input__inner::placeholder),
.rule-form :deep(.el-textarea__inner::placeholder) {
  color: #a1adbb;
}

.rule-category-field {
  width: 100%;
}

.rule-category-help {
  margin-top: 8px;
  padding: 12px 14px;
  border: 1px solid #e5ebf3;
  border-radius: 12px;
  background: #f8fbff;
  color: #6a7483;
  font-size: 12px;
  line-height: 1.7;
}

.rule-category-help strong {
  color: #445063;
}

.status-group :deep(.el-radio) {
  margin-right: 16px;
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
  }

  .rule-dialog :deep(.el-dialog) {
    width: min(760px, calc(100vw - 40px));
  }
}

@media (max-width: 1280px) {
  .hero-panel {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-actions {
    justify-content: flex-start;
  }
}
</style>
