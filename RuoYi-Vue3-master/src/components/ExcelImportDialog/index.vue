<template>
  <el-dialog :title="title" v-model="visible" :width="width" append-to-body @close="handleClose">
    <slot name="prepend"></slot>
    <el-upload ref="uploadRef" :limit="1" accept=".xlsx, .xls" :headers="headers" :action="uploadUrl" :disabled="isUploading" :on-progress="handleProgress" :on-change="handleFileChange" :on-remove="handleFileRemove" :on-success="handleSuccess" :on-error="handleError" :auto-upload="false" drag>
      <el-icon class="el-icon--upload"><upload-filled /></el-icon>
      <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      <template #tip>
        <div class="el-upload__tip text-center">
          <div class="el-upload__tip">
            <el-checkbox v-model="updateSupport"> {{ updateSupportLabel }} </el-checkbox>
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link v-if="templateUrl" type="primary" underline="never" style="font-size: 12px; vertical-align: baseline" @click="handleDownloadTemplate">下载模板</el-link>
        </div>
      </template>
    </el-upload>
    <div v-if="importErrors.length" class="import-error-summary">
      <el-alert title="导入未完成，文件数据没有写入。请修正以下问题后重新上传。" type="error" :closable="false" show-icon />
      <ul class="import-error-list">
        <li v-for="(item, index) in importErrors" :key="index">{{ item }}</li>
      </ul>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="handleSubmit">确 定</el-button>
        <el-button @click="visible = false">取 消</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { getToken } from '@/utils/auth'

const { proxy } = getCurrentInstance()

const props = defineProps({
  // 对话框标题
  title: {
    type: String,
    default: '数据导入'
  },
  // 对话框宽度
  width: {
    type: String,
    default: '400px'
  },
  // 上传接口地址（必传）
  action: {
    type: String,
    required: true
  },
  // 模板下载接口地址，不传则不显示下载模板链接
  templateAction: {
    type: String,
    default: ''
  },
  // 模板文件名前缀
  templateFileName: {
    type: String,
    default: 'template'
  },
  // 覆盖更新勾选框的说明文字
  updateSupportLabel: {
    type: String,
    default: '是否更新已经存在的数据'
  },
  extraParams: {
    type: Object,
    default: () => ({})
  },
  beforeSubmit: {
    type: Function,
    default: null
  }
})

const emit = defineEmits(['success', 'error'])

const uploadRef = ref(null)
const visible = ref(false)
const selectedFile = ref(null)
const isUploading = ref(false)
const updateSupport = ref(false)
const importErrors = ref([])
const headers = { Authorization: 'Bearer ' + getToken() }

const uploadUrl = computed(() => {
  const params = new URLSearchParams({
    updateSupport: updateSupport.value ? '1' : '0',
    ...props.extraParams
  })
  return import.meta.env.VITE_APP_BASE_API + props.action + '?' + params.toString()
})

const templateUrl = computed(() => !!props.templateAction)

// 打开对话框（供父组件通过 ref 调用）
function open() {
  updateSupport.value = false
  isUploading.value = false
  importErrors.value = []
  visible.value = true
  nextTick(() => {
    selectedFile.value = null
    uploadRef.value?.clearFiles()
  })
}

// 关闭时清理
function handleClose() {
  isUploading.value = false
  selectedFile.value = null
  importErrors.value = []
  uploadRef.value?.clearFiles()
}

// 下载模板
function handleDownloadTemplate() {
  proxy.download(props.templateAction, { ...props.extraParams }, `${props.templateFileName}_${new Date().getTime()}.xlsx`)
}

// 上传进度
function handleProgress() {
  isUploading.value = true
}

/** 文件选择处理 */
const handleFileChange = (file, fileList) => {
  selectedFile.value = file
  if (file.status === "ready") {
    importErrors.value = []
  }
}

/** 文件删除处理 */
const handleFileRemove = (file, fileList) => {
  selectedFile.value = null
}

// 上传成功
function handleSuccess(response) {
  if (response.code !== 200) {
    isUploading.value = false
    showImportErrors(response?.msg)
    emit('error', response)
    return
  }
  visible.value = false
  isUploading.value = false
  selectedFile.value = null
  uploadRef.value?.clearFiles()
  proxy.$alert("<div style='overflow:auto;overflow-x:hidden;max-height:70vh;padding:10px 20px 0;'>" + response.msg + '</div>', '导入结果', { dangerouslyUseHTMLString: true })
  emit('success')
}

function handleError(error) {
  isUploading.value = false
  showImportErrors(readImportError(error))
  emit('error', error)
}

function showImportErrors(message) {
  importErrors.value = parseImportErrors(message)
  proxy.$modal.msgError(importErrors.value[0] || "导入失败，请检查文件内容后重试。")
}

function readImportError(error) {
  const response = error?.response || error?.target?.response
  if (typeof response === "string") {
    try {
      return JSON.parse(response).msg || response
    } catch (e) {
      return response
    }
  }
  return response?.msg || error?.message || "网络异常，请检查服务连接后重试。"
}

function parseImportErrors(message) {
  const text = String(message || "导入失败，请检查文件内容后重试。")
    .replace(/<br\s*\/?>/gi, "\n")
    .replace(/<[^>]*>/g, "")
    .replace(/&nbsp;/gi, " ")
    .trim()
  return text.split(/\r?\n/).map(item => item.trim()).filter(Boolean)
}

// 提交上传
function handleSubmit() {
  if (props.beforeSubmit && props.beforeSubmit() === false) {
    return
  }
  const file = selectedFile.value
  if (!file || file.length === 0 || !file.name.toLowerCase().endsWith('.xls') && !file.name.toLowerCase().endsWith('.xlsx')) {
    proxy.$modal.msgError("请选择后缀为 “xls”或“xlsx”的文件。")
    return
  }
  uploadRef.value.submit()
}

defineExpose({ open })
</script>

<style scoped>
.import-error-summary {
  margin-top: 16px;
}

.import-error-list {
  margin: 8px 0 0;
  padding-left: 20px;
  line-height: 1.7;
}
</style>
