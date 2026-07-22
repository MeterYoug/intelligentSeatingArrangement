<template>
  <div class="app-container seating-page">
    <div class="seating-page-heading">
      <div>
        <h1>排座规则</h1>
        <p>设置硬规则和软规则，控制智能生成的约束与权重。</p>
      </div>
    </div>
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
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
          v-hasPermi="['seating:rule:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
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
          type="info"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['seating:rule:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ruleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="班级" align="center" prop="className" min-width="120" />
      <el-table-column label="规则名称" align="center" prop="ruleName" />
      <el-table-column label="规则类别" align="center" prop="ruleCategory">
        <template #default="scope">{{ optionLabel(categoryOptions, scope.row.ruleCategory) }}</template>
      </el-table-column>
      <el-table-column label="规则类型" align="center" prop="ruleCode">
        <template #default="scope">{{ ruleOptionLabel(scope.row.ruleCode) }}</template>
      </el-table-column>
      <el-table-column label="规则配置" align="center" prop="ruleConfig" min-width="160" show-overflow-tooltip>
        <template #default="scope">{{ formatRuleConfig(scope.row) }}</template>
      </el-table-column>
      <el-table-column label="规则权重" align="center" prop="ruleWeight" />
      <el-table-column label="是否启用" align="center" prop="enabled">
        <template #default="scope">{{ scope.row.enabled === "1" ? "是" : "否" }}</template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template #default="scope"><dict-tag :options="sys_normal_disable" :value="scope.row.status" /></template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['seating:rule:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['seating:rule:remove']">删除</el-button>
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
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改排座规则对话框 -->
    <el-dialog :title="title" v-model="open" width="620px" append-to-body>
      <el-form ref="ruleRef" :model="form" :rules="rules" label-width="100px">
        <el-row>
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
                <el-radio-group v-model="form.ruleCategory" @change="handleCategoryChange">
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
          <el-col :span="24">
            <el-form-item label="是否启用" prop="enabled">
              <el-radio-group v-model="form.enabled">
                <el-radio value="0">否</el-radio>
                <el-radio value="1">是</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio v-for="dict in sys_normal_disable" :key="dict.value" :value="dict.value">{{ dict.label }}</el-radio>
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

<style scoped>
.rule-category-field {
  width: 100%;
}

.rule-category-help {
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
  line-height: 20px;
}
</style>
