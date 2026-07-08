<template>
  <div class="dashboard-page">
    <section class="hero-panel">
      <div class="hero-copy">
        <div class="hero-heading-row">
          <p class="eyebrow">慧排座后台</p>
        </div>
        <h1>班级排座与成绩管理</h1>
        <p class="overview-text">
          统一管理班级、学生、成绩、教室布局、排座规则和座位方案。
        </p>
      </div>
      <div class="overview-actions">
        <el-button type="primary" :icon="Grid" @click="goPage('/seating/plan')">生成方案</el-button>
        <el-button :icon="Upload" @click="goPage('/seating/student')">导入学生</el-button>
        <el-button :icon="DataAnalysis" @click="goPage('/seating/score')">成绩管理</el-button>
      </div>
    </section>

    <section class="module-grid">
      <button
        v-for="item in moduleCards"
        :key="item.path"
        class="module-card"
        type="button"
        @click="goPage(item.path)"
      >
        <span class="module-icon">
          <el-icon><component :is="item.icon" /></el-icon>
        </span>
        <span class="module-content">
          <strong>{{ item.title }}</strong>
          <small>{{ item.description }}</small>
          <span class="module-foot">{{ item.hint }}</span>
        </span>
      </button>
    </section>
  </div>
</template>

<script setup name="Index">
import { useRouter } from "vue-router"
import {
  DataAnalysis,
  Files,
  Grid,
  OfficeBuilding,
  School,
  SetUp,
  Upload,
  UserFilled
} from "@element-plus/icons-vue"

const router = useRouter()

const moduleCards = [
  {
    title: "班级管理",
    description: "维护学段、年级、学期和班级科目",
    hint: "维护班级基础信息",
    path: "/seating/class",
    icon: School
  },
  {
    title: "学生管理",
    description: "维护学生信息，支持 Excel 导入",
    hint: "批量导入、筛选、编辑",
    path: "/seating/student",
    icon: UserFilled
  },
  {
    title: "成绩管理",
    description: "考试批次、成绩导入和等级同步",
    hint: "成绩导入后同步等级",
    path: "/seating/score",
    icon: DataAnalysis
  },
  {
    title: "教室布局",
    description: "配置座位、过道和不可用位置",
    hint: "维护座位平面布局",
    path: "/seating/classroom",
    icon: OfficeBuilding
  },
  {
    title: "排座规则",
    description: "设置男女搭配、成绩均衡等规则",
    hint: "权重与启用状态清晰",
    path: "/seating/rule",
    icon: SetUp
  },
  {
    title: "座位方案",
    description: "生成方案、人工微调、确认和导出",
    hint: "生成、微调、导出一体化",
    path: "/seating/plan",
    icon: Files
  }
]

function goPage(path) {
  router.push(path)
}
</script>

<style scoped lang="scss">
.dashboard-page {
  min-height: calc(100vh - 84px);
  padding: 20px;
  background: linear-gradient(180deg, #f7f9fc 0%, #f5f7fb 100%);
  color: #1f2329;
}

.hero-panel {
  position: relative;
  overflow: hidden;
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

.hero-panel::after {
  position: absolute;
  inset: auto -40px -70px auto;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, rgba(90, 140, 255, 0.14) 0%, rgba(90, 140, 255, 0) 68%);
  content: "";
  pointer-events: none;
}

.hero-copy {
  position: relative;
  z-index: 1;
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 10px;
  min-width: 0;
}

.hero-heading-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 2px;
}

.eyebrow {
  margin: 0;
  color: #4d7eff;
  font-size: 13px;
  font-weight: 600;
  letter-spacing: 0.06em;
}

h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0;
}

.overview-text {
  margin: 0;
  color: #5f6b7a;
  font-size: 14px;
  line-height: 1.7;
}

.overview-actions {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 10px;
  min-width: 270px;
}

.overview-actions :deep(.el-button) {
  width: 100%;
  min-height: 38px;
  padding: 0 16px;
  border-radius: 10px;
  box-shadow: none;
  font-weight: 600;
  justify-content: flex-start;
}

.overview-actions :deep(.el-button--primary) {
  color: #fff;
  background: linear-gradient(135deg, #5a8cff 0%, #3e6bf5 100%);
  border-color: transparent;
  box-shadow: 0 8px 16px rgba(77, 126, 255, 0.18);
}

.overview-actions :deep(.el-button:not(.el-button--primary)) {
  color: #5f6b7a;
  background: #fff;
  border-color: #d8e0eb;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.module-card {
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 92px;
  padding: 18px;
  border: 1px solid #e1e7f0;
  border-radius: 14px;
  background: #ffffff;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease, background-color 0.18s ease;
}

.module-card:hover {
  border-color: #7ea8ff;
  background: #fbfdff;
  box-shadow: 0 10px 24px rgba(31, 35, 41, 0.07);
  transform: translateY(-1px);
}

.module-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  flex: 0 0 42px;
  border-radius: 12px;
  background: #edf4ff;
  color: #4d7eff;
  font-size: 22px;
}

.module-content {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 6px;
}

.module-content strong {
  color: #1f2329;
  font-size: 16px;
  font-weight: 700;
}

.module-content small {
  color: #5f6b7a;
  font-size: 13px;
  line-height: 1.5;
}

.module-foot {
  color: #8a94a6;
  font-size: 12px;
  line-height: 1.4;
}

@media (max-width: 1600px) {
  .hero-panel {
    align-items: stretch;
    flex-direction: column;
  }

  .overview-actions {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: flex-start;
    min-width: 0;
    width: 100%;
  }

  .overview-actions :deep(.el-button) {
    width: auto;
  }
}

@media (max-width: 1200px) {
  .module-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .dashboard-page {
    padding: 12px;
  }

  .hero-panel {
    align-items: stretch;
    flex-direction: column;
  }

  .overview-actions {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: flex-start;
    min-width: 0;
    width: 100%;
  }

  .overview-actions :deep(.el-button) {
    width: auto;
  }

  .module-grid {
    grid-template-columns: 1fr;
  }
}
</style>
