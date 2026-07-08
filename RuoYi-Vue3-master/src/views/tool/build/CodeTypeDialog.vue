<template>
  <el-dialog class="gen-dialog" v-model="open" width="500px" title="选择生成类型" @open="onOpen" @close="onClose">
    <el-form ref="codeTypeForm" :model="formData" :rules="rules" label-width="100px" class="gen-form">
      <el-form-item label="生成类型" prop="type">
        <el-radio-group v-model="formData.type">
          <el-radio-button v-for="(item, index) in typeOptions" :key="index" :label="item.value">
            {{ item.label }}
          </el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="showFileName" label="文件名" prop="fileName">
        <el-input v-model="formData.fileName" placeholder="请输入文件名" clearable />
      </el-form-item>
    </el-form>

        <template #footer>
      <div class="dialog-footer">
        <el-button @click="onClose">取消</el-button>
        <el-button type="primary" @click="handelConfirm">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
const open = defineModel()
const props = defineProps({
  showFileName: Boolean
})
const emit = defineEmits(['confirm'])
const formData = ref({
  fileName: undefined,
  type: 'file'
})
const codeTypeForm = ref()
const rules = {
  fileName: [{
    required: true,
    message: '请输入文件名',
    trigger: 'blur'
  }],
  type: [{
    required: true,
    message: '生成类型不能为空',
    trigger: 'change'
  }]
}
const typeOptions = ref([
  {
    label: '页面',
    value: 'file'
  },
  {
    label: '弹窗',
    value: 'dialog'
  }
])
function onOpen() {
  if (props.showFileName) {
    formData.value.fileName = `${+new Date()}.vue`
  }
}
function onClose() {
  open.value = false
}
function handelConfirm() {
  codeTypeForm.value.validate(valid => {
    if (!valid) return
    emit('confirm', { ...formData.value })
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
.gen-form :deep(.el-select__wrapper),
.gen-form :deep(.el-radio-button__inner) {
  border: 1px solid #d7e0ea;
  border-radius: 10px;
  box-shadow: none;
  background: #fff;
}

.gen-form :deep(.el-input__wrapper:hover),
.gen-form :deep(.el-select__wrapper:hover),
.gen-form :deep(.el-radio-button__inner:hover) {
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