<template>
  <el-empty :description="description" :image-size="64">
    <el-button v-if="actionText" type="primary" size="small" @click="$emit('action')">
      {{ actionText }}
    </el-button>
  </el-empty>
</template>

<script setup>
defineProps({
  description: {
    type: String,
    required: true
  },
  actionText: {
    type: String,
    default: ""
  }
})

defineEmits(["action"])
</script>
