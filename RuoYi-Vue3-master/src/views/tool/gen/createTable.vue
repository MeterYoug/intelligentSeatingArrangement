<template>
  <!-- 创建表 -->
  <el-dialog class="gen-dialog" title="创建表" v-model="visible" width="800px" top="5vh" append-to-body>
    <span>创建表语句(支持多个建表语句)：</span>
    <el-input class="sql-input" type="textarea" :rows="10" placeholder="请输入建表 SQL" v-model="content"></el-input>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" @click="handleImportTable">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { createTable } from "@/api/tool/gen"

const visible = ref(false)
const content = ref("")
const { proxy } = getCurrentInstance()
const emit = defineEmits(["ok"])

/** 显示弹框 */
function show() {
  visible.value = true
}

/** 导入按钮操作 */
function handleImportTable() {
  if (content.value === "") {
    proxy.$modal.msgError("请输入建表语句")
    return
  }
  createTable({ sql: content.value, tplWebType: 'element-plus' }).then(res => {
    proxy.$modal.msgSuccess(res.msg)
    if (res.code === 200) {
      visible.value = false
      emit("ok")
    }
  })
}

defineExpose({
  show,
})
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
  color: #4f5b6d;
}

.gen-dialog :deep(.sql-input .el-textarea__inner) {
  min-height: 240px !important;
  border: 1px solid #d7e0ea;
  border-radius: 12px;
  box-shadow: none;
  background: #fff;
  color: #243041;
}

.gen-dialog :deep(.sql-input .el-textarea__inner:hover) {
  border-color: #9cb6ff;
}

.gen-dialog :deep(.sql-input .el-textarea__inner:focus) {
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
