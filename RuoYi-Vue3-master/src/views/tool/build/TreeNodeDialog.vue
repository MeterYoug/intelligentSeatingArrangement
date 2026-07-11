<template>
  <div>
    <el-dialog class="gen-dialog" title="添加选项" v-model="open" width="800px" :close-on-click-modal="false" :modal-append-to-body="false"
      @open="onOpen" @close="onClose">
      <el-form ref="treeNodeForm" :model="formData" :rules="rules" label-width="100px" class="gen-form">
        <el-col :span="24">
          <el-form-item label="选项名" prop="label">
            <el-input v-model="formData.label" placeholder="请输入选项名" clearable />
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="选项值" prop="value">
            <el-input v-model="formData.value" placeholder="请输入选项值" clearable>
              <template #append>
                <el-select v-model="dataType" :style="{ width: '100px' }">
                  <el-option v-for="(item, index) in dataTypeOptions" :key="index" :label="item.label" :value="item.value"
                    :disabled="item.disabled" />
                </el-select>
              </template>

            </el-input>
          </el-form-item>
        </el-col>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="onClose">取消</el-button>
          <el-button type="primary" @click="handelConfirm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
const open = defineModel()
const emit = defineEmits(['confirm'])
const formData = ref({
  label: undefined,
  value: undefined
})
const rules = {
  label: [
    {
      required: true,
      message: '请输入选项名',
      trigger: 'blur'
    }
  ],
  value: [
    {
      required: true,
      message: '请输入选项值',
      trigger: 'blur'
    }
  ]
}
const dataType = ref('string')
const dataTypeOptions = ref([
  {
    label: '字符串',
    value: 'string'
  },
  {
    label: '数字',
    value: 'number'
  }
])
const id = ref(100)
const treeNodeForm = ref()

function onOpen() {
  formData.value = {
    label: undefined,
    value: undefined
  }
}

function onClose() {
  open.value = false
}

function handelConfirm() {
  treeNodeForm.value.validate(valid => {
    if (!valid) return
    if (dataType.value === 'number') {
      formData.value.value = parseFloat(formData.value.value)
    }
    formData.value.id = id.value++
    emit('commit', formData.value)
    onClose()
  })
}
</script>


<style scoped>
.gen-dialog :deep(.el-dialog) {
  border-radius: 18px;
  overflow: hidden;
  box-shadow: 0 24px 60px rgba(31, 35, 41, 0.14);
}

.gen-dialog :deep(.el-dialog__header) {
  margin-right: 0;
  padding: 18px 22px 10px;
  border-bottom: 1px solid #eef2f7;
}

.gen-dialog :deep(.el-dialog__title) {
  color: #1f2329;
  font-size: 16px;
  font-weight: 700;
}

.gen-dialog :deep(.el-dialog__body) {
  padding: 18px 22px 14px;
}

.gen-form :deep(.el-form-item) {
  margin-bottom: 16px;
}

.gen-form :deep(.el-form-item__label) {
  color: #4f5b6d;
  font-weight: 600;
}

.gen-form :deep(.el-input__wrapper),
.gen-form :deep(.el-select__wrapper) {
  border: 1px solid #d7e0ea;
  border-radius: 10px;
  box-shadow: none;
  background: #fff;
}

.gen-form :deep(.el-input__wrapper:hover),
.gen-form :deep(.el-select__wrapper:hover) {
  border-color: #9cb6ff;
}

.gen-form :deep(.el-input.is-focus .el-input__wrapper),
.gen-form :deep(.el-select__wrapper.is-focused) {
  border-color: #5a8cff;
  box-shadow: 0 0 0 2px rgba(90, 140, 255, 0.12);
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
  font-weight: 600;
}

.dialog-footer :deep(.el-button--primary) {
  box-shadow: 0 8px 16px rgba(77, 126, 255, 0.18);
}
</style>