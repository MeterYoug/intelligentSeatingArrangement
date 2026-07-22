<template>
  <div class="dashboard-page">
    <section class="overview-band">
      <div>
        <p class="eyebrow">慧排座工作台</p>
        <h1>老师日常排座与成绩管理</h1>
        <p class="overview-text">
          围绕班级、学生、成绩、教室布局、排座规则和座位方案完成一套闭环管理。
        </p>
      </div>
      <div class="overview-actions">
        <el-button type="primary" :icon="Grid" @click="goPage('/seating/plan')">生成座位方案</el-button>
        <el-button :icon="Upload" @click="goPage('/seating/student')">导入学生</el-button>
        <el-button :icon="DataAnalysis" @click="goPage('/seating/score')">管理成绩</el-button>
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
        </span>
      </button>
    </section>

    <el-row :gutter="16" class="dashboard-row">
      <el-col :xs="24" :lg="15">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>排座流程</span>
              <el-tag type="info" effect="plain">MVP 已验证</el-tag>
            </div>
          </template>
          <el-steps :active="6" finish-status="success" align-center>
            <el-step title="班级" description="学段、年级、科目" />
            <el-step title="学生" description="导入基础信息" />
            <el-step title="成绩" description="导入并同步等级" />
            <el-step title="教室" description="维护座位和过道" />
            <el-step title="规则" description="配置权重" />
            <el-step title="方案" description="生成、微调、导出" />
          </el-steps>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="9">
        <el-card shadow="never" class="panel-card">
          <template #header>
            <div class="panel-header">
              <span>今日待办</span>
            </div>
          </template>
          <ul class="todo-list">
            <li v-for="item in todoItems" :key="item">
              <el-icon><CircleCheck /></el-icon>
              <span>{{ item }}</span>
            </li>
          </ul>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="dashboard-row">
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel-card compact-panel">
          <template #header>
            <div class="panel-header">
              <span>规则说明</span>
            </div>
          </template>
          <p>
            仅启用的排座规则会参与评分。总分按规则权重加权，未启用规则不会稀释结果。
          </p>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel-card compact-panel">
          <template #header>
            <div class="panel-header">
              <span>成绩联动</span>
            </div>
          </template>
          <p>
            成绩导入后执行同步等级，再重新生成座位方案，成绩强弱均衡规则才会使用最新等级。
          </p>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card shadow="never" class="panel-card compact-panel">
          <template #header>
            <div class="panel-header">
              <span>同桌识别</span>
            </div>
          </template>
          <p>
            带过道布局按每行可用座位从左到右两两配对，过道和不可用座位不参与同桌判断。
          </p>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup name="Index">
import { useRouter } from "vue-router"
import {
  CircleCheck,
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
  { title: "班级管理", description: "维护学段、年级、学期和班级科目", path: "/seating/class", icon: School },
  { title: "学生管理", description: "维护学生信息，支持 Excel 导入", path: "/seating/student", icon: UserFilled },
  { title: "成绩管理", description: "考试批次、成绩导入和等级同步", path: "/seating/score", icon: DataAnalysis },
  { title: "教室布局", description: "配置座位、过道和不可用位置", path: "/seating/classroom", icon: OfficeBuilding },
  { title: "排座规则", description: "设置男女搭配、成绩均衡等规则", path: "/seating/rule", icon: SetUp },
  { title: "座位方案", description: "生成方案、人工微调、确认和导出", path: "/seating/plan", icon: Files }
]

const todoItems = [
  "先确认班级、学生和教室布局数据完整",
  "导入成绩后同步等级，再生成新方案",
  "确认方案前检查评分明细和未安排学生",
  "确认方案后按需导出 Excel、图片或 PDF"
]

function goPage(path) {
  router.push(path)
}
</script>

<style scoped lang="scss">
.dashboard-page {
  min-height: calc(100vh - 84px);
  padding: 18px;
  background: #f5f7fb;
  color: #1f2d3d;
}

.overview-band {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 24px;
  margin-bottom: 16px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #ffffff;
}

.eyebrow {
  margin: 0 0 8px;
  color: #409eff;
  font-size: 13px;
  font-weight: 600;
}

h1 {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0;
}

.overview-text {
  margin: 10px 0 0;
  color: #606266;
  font-size: 14px;
  line-height: 1.7;
}

.overview-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 10px;
}

.module-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
  margin-bottom: 16px;
}

.module-card {
  display: flex;
  align-items: center;
  gap: 14px;
  min-height: 92px;
  padding: 18px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #ffffff;
  text-align: left;
  cursor: pointer;
  transition: border-color 0.18s ease, box-shadow 0.18s ease, transform 0.18s ease;
}

.module-card:hover {
  border-color: #409eff;
  box-shadow: 0 8px 24px rgba(31, 45, 61, 0.08);
  transform: translateY(-1px);
}

.module-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  flex: 0 0 42px;
  border-radius: 8px;
  background: #ecf5ff;
  color: #409eff;
  font-size: 22px;
}

.module-content {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 6px;
}

.module-content strong {
  color: #303133;
  font-size: 16px;
  font-weight: 700;
}

.module-content small {
  color: #606266;
  font-size: 13px;
  line-height: 1.5;
}

.dashboard-row {
  margin-bottom: 16px;
}

.panel-card {
  height: 100%;
  border-radius: 8px;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  font-weight: 700;
}

.todo-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 0;
  margin: 0;
  list-style: none;
}

.todo-list li {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: #606266;
  line-height: 1.6;
}

.todo-list .el-icon {
  margin-top: 3px;
  color: #67c23a;
}

.compact-panel p {
  margin: 0;
  color: #606266;
  line-height: 1.8;
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

  .overview-band {
    align-items: stretch;
    flex-direction: column;
  }

  .overview-actions {
    justify-content: flex-start;
  }

  .module-grid {
    grid-template-columns: 1fr;
  }
}
</style>
