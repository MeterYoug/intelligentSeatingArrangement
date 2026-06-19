<template>
  <div class="app-container">
    <el-page-header title="返回" @back="goBack">
      <template #content>
        <span>{{ plan.planName || "座位方案详情" }}</span>
      </template>
    </el-page-header>

    <el-skeleton :loading="loading" animated>
      <template #template>
        <el-skeleton-item variant="p" style="width: 30%" />
        <el-skeleton-item variant="rect" style="height: 360px; margin-top: 16px" />
      </template>
      <template #default>
        <div class="plan-summary">
          <div class="summary-item">
            <span class="summary-label">班级</span>
            <span class="summary-value">{{ plan.className || "-" }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">教室布局</span>
            <span class="summary-value">{{ plan.classroomName || "-" }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">方案状态</span>
            <span class="summary-value">{{ optionLabel(statusOptions, plan.planStatus) }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">总评分</span>
            <span class="summary-value">{{ plan.totalScore ?? "-" }}</span>
          </div>
          <div class="summary-item">
            <span class="summary-label">生成时间</span>
            <span class="summary-value">{{ parseTime(plan.generatedAt, "{y}-{m}-{d}") || "-" }}</span>
          </div>
        </div>

        <el-alert
          v-if="adjustResult && adjustResult.conflicts && adjustResult.conflicts.length"
          class="adjust-alert"
          title="当前人工调整存在硬规则冲突"
          type="warning"
          :closable="false"
          show-icon
        >
          <div v-for="item in adjustResult.conflicts" :key="item">{{ item }}</div>
        </el-alert>
        <el-alert
          v-else-if="adjustResult"
          class="adjust-alert"
          :title="`调整已保存，最新总分 ${adjustResult.totalScore ?? '-'}，${formatScoreChange(adjustResult.scoreChange)}`"
          type="success"
          :closable="false"
          show-icon
        />

        <div class="detail-layout">
          <section class="seat-section">
            <div class="section-header">
              <div class="section-title">座位表</div>
              <div class="section-actions">
                <el-radio-group v-model="viewMode" size="small" class="view-toggle">
                  <el-radio-button label="TEACHER">教师视角</el-radio-button>
                  <el-radio-button label="STUDENT">学生视角</el-radio-button>
                </el-radio-group>
                <el-button icon="Download" @click="exportSeatTable">导出 Excel</el-button>
                <el-button icon="Picture" @click="exportSeatImage">导出图片</el-button>
                <el-button icon="Printer" @click="exportSeatPdf">导出 PDF</el-button>
                <el-button v-if="plan.planStatus !== 'ACTIVE'" type="success" icon="CircleCheck" :loading="confirming" @click="confirmCurrentPlan">确认方案</el-button>
                <el-button type="primary" icon="Check" :loading="saving" :disabled="!dirty" @click="saveAssignments">保存调整</el-button>
              </div>
            </div>
            <div class="classroom-view">
              <div class="classroom-body">
                <div v-if="viewPlatformPosition === 'LEFT'" class="platform platform-vertical">讲台</div>
                <div class="classroom-main">
                  <div v-if="viewPlatformPosition === 'FRONT'" class="platform platform-horizontal">讲台</div>
                  <div class="seat-grid" :style="gridStyle">
                    <div
                      v-for="seat in displayFlatSeats"
                      :key="seat.rowIndex + '-' + seat.colIndex"
                      class="seat-cell"
                      :class="seatClass(seat)"
                      :draggable="canDrag(seat)"
                      @dragstart="handleDragStart(seat)"
                      @dragend="resetDragging"
                      @dragover.prevent
                      @drop.prevent="handleDrop(seat)"
                    >
                      <template v-if="seat.seatType === '0' && seat.isAvailable === '1'">
                        <div class="seat-code">{{ seat.seatCode || seat.rowIndex + '-' + seat.colIndex }}</div>
                        <div v-if="currentAssignment(seat)" class="student-name">
                          <span :class="['gender-tag', genderClass(currentAssignment(seat))]">
                            {{ genderLabel(currentAssignment(seat)) }}
                          </span>
                          <span class="student-text">{{ currentAssignment(seat).studentNameSnapshot }}</span>
                        </div>
                        <div v-else class="student-name">空座</div>
                        <div v-if="currentAssignment(seat)" class="seat-actions">
                          <el-button
                            class="seat-action-button"
                            link
                            :icon="currentAssignment(seat)?.isLocked === '1' ? 'Lock' : 'Unlock'"
                            @click.stop="toggleLock(seat)"
                          >
                            {{ currentAssignment(seat)?.isLocked === "1" ? "已锁定" : "锁定" }}
                          </el-button>
                          <el-button
                            class="seat-action-button"
                            link
                            type="danger"
                            icon="Close"
                            @click.stop="clearSeat(seat)"
                          >
                            空座
                          </el-button>
                        </div>
                      </template>
                      <template v-else>
                        <div class="seat-placeholder">{{ seat.seatType === "2" ? "过道" : "不可用" }}</div>
                      </template>
                    </div>
                  </div>
                  <div v-if="viewPlatformPosition === 'BACK'" class="platform platform-horizontal">讲台</div>
                </div>
                <div v-if="viewPlatformPosition === 'RIGHT'" class="platform platform-vertical">讲台</div>
              </div>
            </div>
          </section>

          <section class="score-section">
            <div class="section-title">未安排学生</div>
            <div class="unassigned-panel">
              <div v-if="unassignedStudents.length" class="student-pool">
                <div
                  v-for="student in unassignedStudents"
                  :key="student.studentId"
                  class="student-chip"
                  draggable="true"
                  @dragstart="handleStudentDragStart(student)"
                  @dragend="resetDragging"
                >
                  <span :class="['gender-tag', genderClassByValue(student.gender)]">{{ genderLabelByValue(student.gender) }}</span>
                  <span class="student-chip-name">{{ student.studentName }}</span>
                </div>
              </div>
              <el-empty v-else description="暂无未安排学生" :image-size="48" />
            </div>
            <div class="section-title">评分明细</div>
            <el-table :data="scoreList" size="small" border>
              <el-table-column label="规则" prop="ruleName" min-width="130" show-overflow-tooltip />
              <el-table-column label="得分" prop="scoreValue" width="80" align="center" />
              <el-table-column label="扣分" prop="penaltyValue" width="80" align="center" />
              <el-table-column label="明细" min-width="180" show-overflow-tooltip>
                <template #default="scope">{{ formatScoreDetail(scope.row.detailJson) }}</template>
              </el-table-column>
            </el-table>
          </section>
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup name="SeatingPlanDetail">
import { getPlan, confirmPlan, exportSeatTableUrl } from "@/api/seating/plan"
import { listAssignment, savePlanAssignments } from "@/api/seating/assignment"
import { listScore } from "@/api/seating/score"
import { getClassroomLayout } from "@/api/seating/position"
import { listStudent } from "@/api/seating/student"
import { parseTime } from "@/utils/ruoyi"

const route = useRoute()
const router = useRouter()
const { proxy } = getCurrentInstance()

const statusOptions = [
  { value: "DRAFT", label: "草稿" },
  { value: "ACTIVE", label: "启用" },
  { value: "ARCHIVED", label: "归档" }
]

const loading = ref(true)
const saving = ref(false)
const confirming = ref(false)
const dirty = ref(false)
const draggingSeatId = ref(null)
const draggingStudentId = ref(null)
const plan = ref({})
const seatRows = ref([])
const assignmentList = ref([])
const scoreList = ref([])
const adjustResult = ref(null)
const studentList = ref([])
const viewMode = ref("TEACHER")

const assignmentMap = computed(() => {
  const map = new Map()
  assignmentList.value.forEach(item => {
    map.set(item.seatId, item)
  })
  return map
})

const studentMap = computed(() => {
  const map = new Map()
  studentList.value.forEach(item => {
    map.set(item.studentId, item)
  })
  return map
})

const assignedStudentIds = computed(() => new Set(assignmentList.value.map(item => item.studentId)))
const unassignedStudents = computed(() => studentList.value.filter(item => item.status === "0" && !assignedStudentIds.value.has(item.studentId)))
const flatSeats = computed(() => seatRows.value.flat())
const displaySeatRows = computed(() => {
  if (viewMode.value === "STUDENT") {
    return seatRows.value.slice().reverse().map(row => row.slice().reverse())
  }
  return seatRows.value
})
const displayFlatSeats = computed(() => displaySeatRows.value.flat())
const maxColCount = computed(() => displaySeatRows.value.reduce((max, row) => Math.max(max, row.length), 1))
const gridStyle = computed(() => ({
  gridTemplateColumns: `repeat(${maxColCount.value}, minmax(88px, 112px))`
}))
const platformPosition = computed(() => plan.value.platformPosition || "FRONT")
const viewPlatformPosition = computed(() => viewMode.value === "STUDENT" ? reversePlatformPosition(platformPosition.value) : platformPosition.value)
const viewModeLabel = computed(() => viewMode.value === "STUDENT" ? "学生视角" : "教师视角")

function optionLabel(options, value) {
  return options.find(item => item.value === value)?.label || "-"
}

function goBack() {
  router.push("/seating/plan")
}

function reversePlatformPosition(position) {
  const reverseMap = {
    FRONT: "BACK",
    BACK: "FRONT",
    LEFT: "RIGHT",
    RIGHT: "LEFT"
  }
  return reverseMap[position] || "FRONT"
}

function seatClass(seat) {
  if (seat.seatType === "2") {
    return "seat-aisle"
  }
  if (seat.seatType !== "0" || seat.isAvailable !== "1") {
    return "seat-disabled"
  }
  const assignment = currentAssignment(seat)
  if (!assignment) {
    return "seat-empty"
  }
  return assignment.isLocked === "1" ? "seat-assigned seat-locked" : "seat-assigned"
}

function currentAssignment(seat) {
  return assignmentMap.value.get(seat.seatId)
}

function assignmentGender(assignment) {
  return studentMap.value.get(assignment?.studentId)?.gender || "2"
}

function genderLabel(assignment) {
  return genderLabelByValue(assignmentGender(assignment))
}

function genderLabelByValue(gender) {
  if (gender === "0") {
    return "男"
  }
  if (gender === "1") {
    return "女"
  }
  return "未知"
}

function genderClass(assignment) {
  return genderClassByValue(assignmentGender(assignment))
}

function genderClassByValue(gender) {
  if (gender === "0") {
    return "gender-male"
  }
  if (gender === "1") {
    return "gender-female"
  }
  return "gender-unknown"
}

function canDrag(seat) {
  const assignment = currentAssignment(seat)
  return !!assignment && assignment.isLocked !== "1"
}

function handleDragStart(seat) {
  if (!canDrag(seat)) {
    draggingSeatId.value = null
    draggingStudentId.value = null
    return
  }
  draggingSeatId.value = seat.seatId
  draggingStudentId.value = null
}

function handleStudentDragStart(student) {
  draggingStudentId.value = student.studentId
  draggingSeatId.value = null
}

function handleDrop(targetSeat) {
  if ((!draggingSeatId.value && !draggingStudentId.value) || draggingSeatId.value === targetSeat.seatId) {
    return
  }
  if (targetSeat.seatType !== "0" || targetSeat.isAvailable !== "1") {
    proxy.$modal.msgWarning("只能调整到可用座位")
    resetDragging()
    return
  }
  const targetAssignment = currentAssignment(targetSeat)
  if (draggingStudentId.value) {
    if (targetAssignment) {
      proxy.$modal.msgWarning("请先将目标座位设为空座")
      resetDragging()
      return
    }
    const student = unassignedStudents.value.find(item => item.studentId === draggingStudentId.value)
    if (!student) {
      resetDragging()
      return
    }
    const assignment = {
      assignmentId: null,
      planId: plan.value.planId,
      classId: plan.value.classId,
      classroomId: plan.value.classroomId,
      studentId: student.studentId,
      studentNameSnapshot: student.studentName,
      isLocked: "0",
      assignSource: "MANUAL"
    }
    applySeatToAssignment(assignment, targetSeat)
    assignmentList.value = [...assignmentList.value, assignment]
    markDirty()
    resetDragging()
    return
  }

  const sourceSeat = flatSeats.value.find(item => item.seatId === draggingSeatId.value)
  const sourceAssignment = sourceSeat ? currentAssignment(sourceSeat) : null
  if (!sourceAssignment) {
    resetDragging()
    return
  }
  if (sourceAssignment.isLocked === "1" || targetAssignment?.isLocked === "1") {
    proxy.$modal.msgWarning("锁定座位不能参与调整")
    resetDragging()
    return
  }
  applySeatToAssignment(sourceAssignment, targetSeat)
  if (targetAssignment && sourceSeat) {
    applySeatToAssignment(targetAssignment, sourceSeat)
  }
  assignmentList.value = [...assignmentList.value]
  markDirty()
  resetDragging()
}

function applySeatToAssignment(assignment, seat) {
  assignment.seatId = seat.seatId
  assignment.rowIndex = seat.rowIndex
  assignment.colIndex = seat.colIndex
  assignment.assignSource = "MANUAL"
}

function toggleLock(seat) {
  const assignment = currentAssignment(seat)
  if (!assignment) {
    return
  }
  assignment.isLocked = assignment.isLocked === "1" ? "0" : "1"
  assignmentList.value = [...assignmentList.value]
  markDirty()
}

function clearSeat(seat) {
  const assignment = currentAssignment(seat)
  if (!assignment) {
    return
  }
  if (assignment.isLocked === "1") {
    proxy.$modal.msgWarning("锁定座位不能设为空座")
    return
  }
  proxy.$modal.confirm(`确认将 ${assignment.studentNameSnapshot} 移出座位？`).then(() => {
    assignmentList.value = assignmentList.value.filter(item => item !== assignment)
    markDirty()
  }).catch(() => {})
}

function markDirty() {
  dirty.value = true
  adjustResult.value = null
}

function resetDragging() {
  draggingSeatId.value = null
  draggingStudentId.value = null
}

function saveAssignments() {
  saving.value = true
  const planId = route.params.planId
  const payload = assignmentList.value.map(item => ({
    assignmentId: item.assignmentId || null,
    studentId: item.studentId,
    seatId: item.seatId,
    isLocked: item.isLocked === "1" ? "1" : "0"
  }))
  savePlanAssignments(planId, payload).then(response => {
    adjustResult.value = response.data || null
    if (adjustResult.value?.conflicts?.length) {
      proxy.$modal.msgWarning("保存成功，但存在硬规则冲突")
    } else {
      proxy.$modal.msgSuccess("保存成功")
    }
    dirty.value = false
    loadDetail()
  }).finally(() => {
    saving.value = false
  })
}

function confirmCurrentPlan() {
  if (dirty.value) {
    proxy.$modal.msgWarning("请先保存调整后再确认方案")
    return
  }
  confirming.value = true
  proxy.$modal.confirm('确认启用当前方案？同班级原启用方案会自动归档。').then(function() {
    return confirmPlan(plan.value.planId)
  }).then(() => {
    proxy.$modal.msgSuccess("确认成功")
    loadDetail()
  }).finally(() => {
    confirming.value = false
  }).catch(() => {})
}

function exportSeatTable() {
  if (dirty.value) {
    proxy.$modal.msgWarning("请先保存调整后再导出")
    return
  }
  const planId = route.params.planId
  proxy.download(exportSeatTableUrl(planId), { viewMode: viewMode.value }, buildExportFilename("xlsx"))
}

function exportSeatImage() {
  if (!canExportSnapshot()) {
    return
  }

  const canvas = buildSeatImageCanvas()
  canvas.toBlob(blob => {
    if (!blob) {
      proxy.$modal.msgError("导出图片失败")
      return
    }
    downloadBlob(blob, buildExportFilename("png"))
  }, "image/png")
}

function exportSeatPdf() {
  if (!canExportSnapshot()) {
    return
  }

  const canvas = buildSeatImageCanvas()
  const imageUrl = canvas.toDataURL("image/png")
  const printWindow = window.open("", "_blank")
  if (!printWindow) {
    proxy.$modal.msgWarning("浏览器阻止了打印窗口，请允许弹窗后重试")
    return
  }
  printWindow.document.write(buildPrintHtml(imageUrl))
  printWindow.document.close()
}

function canExportSnapshot() {
  if (dirty.value) {
    proxy.$modal.msgWarning("请先保存调整后再导出")
    return false
  }
  if (!seatRows.value.length) {
    proxy.$modal.msgWarning("暂无座位布局可导出")
    return false
  }
  return true
}

function buildSeatImageCanvas() {
  const scale = window.devicePixelRatio || 2
  const padding = 32
  const titleHeight = 84
  const cellWidth = 148
  const cellHeight = 92
  const gap = 10
  const platformGap = 12
  const platformSize = 36
  const rows = displaySeatRows.value
  const maxCols = maxColCount.value
  const gridWidth = maxCols * cellWidth + Math.max(maxCols - 1, 0) * gap
  const gridHeight = rows.length * cellHeight + Math.max(rows.length - 1, 0) * gap
  const platform = viewPlatformPosition.value
  const isHorizontalPlatform = ["FRONT", "BACK"].includes(platform)
  const isVerticalPlatform = ["LEFT", "RIGHT"].includes(platform)
  const width = padding * 2 + gridWidth + (isVerticalPlatform ? platformSize + platformGap : 0)
  const height = padding * 2 + titleHeight + gridHeight + (isHorizontalPlatform ? platformSize + platformGap : 0)
  const canvas = document.createElement("canvas")
  canvas.width = width * scale
  canvas.height = height * scale
  canvas.style.width = `${width}px`
  canvas.style.height = `${height}px`
  const ctx = canvas.getContext("2d")
  ctx.scale(scale, scale)

  drawImageBackground(ctx, width, height)
  drawImageHeader(ctx, padding)

  const gridX = padding + (platform === "LEFT" ? platformSize + platformGap : 0)
  const gridY = padding + titleHeight + (platform === "FRONT" ? platformSize + platformGap : 0)
  drawImagePlatform(ctx, gridX, gridY, gridWidth, gridHeight, platformSize)
  drawImageSeatGrid(ctx, rows, gridX, gridY, cellWidth, cellHeight, gap)
  return canvas
}

function drawImageBackground(ctx, width, height) {
  ctx.fillStyle = "#ffffff"
  ctx.fillRect(0, 0, width, height)
  ctx.strokeStyle = "#dcdfe6"
  ctx.lineWidth = 1
  ctx.strokeRect(0.5, 0.5, width - 1, height - 1)
}

function drawImageHeader(ctx, padding) {
  ctx.fillStyle = "#303133"
  ctx.font = "600 22px Arial, Microsoft YaHei, sans-serif"
  drawTextWithEllipsis(ctx, plan.value.planName || "座位方案", padding, padding + 4, 520)

  ctx.fillStyle = "#606266"
  ctx.font = "14px Arial, Microsoft YaHei, sans-serif"
  const meta = [
    `班级：${plan.value.className || "-"}`,
    `教室：${plan.value.classroomName || "-"}`,
    `视角：${viewModeLabel.value}`,
    `总评分：${plan.value.totalScore ?? "-"}`,
    `导出时间：${parseTime(new Date(), "{y}-{m}-{d} {h}:{i}")}`
  ].join("    ")
  drawTextWithEllipsis(ctx, meta, padding, padding + 38, 860)
}

function drawImagePlatform(ctx, gridX, gridY, gridWidth, gridHeight, platformSize) {
  if (viewPlatformPosition.value === "FRONT") {
    drawPlatformRect(ctx, gridX + gridWidth / 2 - 120, gridY - platformSize - 12, 240, platformSize, "讲台")
  } else if (viewPlatformPosition.value === "BACK") {
    drawPlatformRect(ctx, gridX + gridWidth / 2 - 120, gridY + gridHeight + 12, 240, platformSize, "讲台")
  } else if (viewPlatformPosition.value === "LEFT") {
    drawPlatformRect(ctx, gridX - platformSize - 12, gridY, platformSize, gridHeight, "讲台")
  } else if (viewPlatformPosition.value === "RIGHT") {
    drawPlatformRect(ctx, gridX + gridWidth + 12, gridY, platformSize, gridHeight, "讲台")
  }
}

function drawPlatformRect(ctx, x, y, width, height, text) {
  drawRoundRect(ctx, x, y, width, height, 8, "#409eff", "#409eff")
  ctx.fillStyle = "#ffffff"
  ctx.font = "600 15px Arial, Microsoft YaHei, sans-serif"
  ctx.textAlign = "center"
  ctx.textBaseline = "middle"
  ctx.fillText(text, x + width / 2, y + height / 2)
  ctx.textAlign = "left"
  ctx.textBaseline = "alphabetic"
}

function drawImageSeatGrid(ctx, rows, gridX, gridY, cellWidth, cellHeight, gap) {
  rows.forEach((row, rowIndex) => {
    row.forEach((seat, colIndex) => {
      const x = gridX + colIndex * (cellWidth + gap)
      const y = gridY + rowIndex * (cellHeight + gap)
      drawImageSeat(ctx, seat, x, y, cellWidth, cellHeight)
    })
  })
}

function drawImageSeat(ctx, seat, x, y, width, height) {
  const assignment = currentAssignment(seat)
  const style = imageSeatStyle(seat, assignment)
  drawRoundRect(ctx, x, y, width, height, 8, style.background, style.border)

  ctx.fillStyle = style.subText
  ctx.font = "12px Arial, Microsoft YaHei, sans-serif"
  drawTextWithEllipsis(ctx, seat.seatCode || `${seat.rowIndex}-${seat.colIndex}`, x + 10, y + 18, width - 20)

  ctx.fillStyle = style.mainText
  ctx.font = "600 17px Arial, Microsoft YaHei, sans-serif"
  if (seat.seatType !== "0" || seat.isAvailable !== "1") {
    drawCenteredText(ctx, seat.seatType === "2" ? "过道" : "不可用", x, y + 6, width, height)
    return
  }
  if (!assignment) {
    drawCenteredText(ctx, "空座", x, y + 8, width, height)
    return
  }

  const student = `${genderLabel(assignment)}  ${assignment.studentNameSnapshot || "-"}`
  drawTextWithEllipsis(ctx, student, x + 10, y + 48, width - 20)
  if (assignment.isLocked === "1") {
    ctx.fillStyle = "#b88230"
    ctx.font = "12px Arial, Microsoft YaHei, sans-serif"
    ctx.fillText("已锁定", x + 10, y + 72)
  }
}

function imageSeatStyle(seat, assignment) {
  if (seat.seatType === "2") {
    return { background: "#f4f4f5", border: "#c0c4cc", mainText: "#909399", subText: "#909399" }
  }
  if (seat.seatType !== "0" || seat.isAvailable !== "1") {
    return { background: "#fef0f0", border: "#f56c6c", mainText: "#f56c6c", subText: "#f56c6c" }
  }
  if (!assignment) {
    return { background: "#ffffff", border: "#dcdfe6", mainText: "#606266", subText: "#909399" }
  }
  if (assignment.isLocked === "1") {
    return { background: "#fdf6ec", border: "#e6a23c", mainText: "#303133", subText: "#909399" }
  }
  return { background: "#ecf5ff", border: "#409eff", mainText: "#303133", subText: "#909399" }
}

function drawCenteredText(ctx, text, x, y, width, height) {
  ctx.textAlign = "center"
  ctx.textBaseline = "middle"
  ctx.fillText(text, x + width / 2, y + height / 2)
  ctx.textAlign = "left"
  ctx.textBaseline = "alphabetic"
}

function drawTextWithEllipsis(ctx, text, x, y, maxWidth) {
  const content = String(text || "")
  if (ctx.measureText(content).width <= maxWidth) {
    ctx.fillText(content, x, y)
    return
  }
  let clipped = content
  while (clipped.length > 0 && ctx.measureText(`${clipped}...`).width > maxWidth) {
    clipped = clipped.slice(0, -1)
  }
  ctx.fillText(`${clipped}...`, x, y)
}

function drawRoundRect(ctx, x, y, width, height, radius, fill, stroke) {
  const safeRadius = Math.min(radius, width / 2, height / 2)
  ctx.beginPath()
  ctx.moveTo(x + safeRadius, y)
  ctx.lineTo(x + width - safeRadius, y)
  ctx.quadraticCurveTo(x + width, y, x + width, y + safeRadius)
  ctx.lineTo(x + width, y + height - safeRadius)
  ctx.quadraticCurveTo(x + width, y + height, x + width - safeRadius, y + height)
  ctx.lineTo(x + safeRadius, y + height)
  ctx.quadraticCurveTo(x, y + height, x, y + height - safeRadius)
  ctx.lineTo(x, y + safeRadius)
  ctx.quadraticCurveTo(x, y, x + safeRadius, y)
  ctx.closePath()
  ctx.fillStyle = fill
  ctx.fill()
  ctx.strokeStyle = stroke
  ctx.stroke()
}

function downloadBlob(blob, filename) {
  const url = URL.createObjectURL(blob)
  const link = document.createElement("a")
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

function buildPrintHtml(imageUrl) {
  const title = escapeHtml(buildExportTitle())
  return `<!doctype html>
<html>
<head>
  <meta charset="utf-8">
  <title>${title}</title>
  <style>
    @page { size: A4 landscape; margin: 10mm; }
    * { box-sizing: border-box; }
    body { margin: 0; font-family: Arial, "Microsoft YaHei", sans-serif; color: #303133; background: #ffffff; }
    .print-page { width: 100%; }
    img { display: block; width: 100%; height: auto; }
    @media print { body { print-color-adjust: exact; -webkit-print-color-adjust: exact; } }
  </style>
</head>
<body>
  <div class="print-page">
    <img src="${imageUrl}" alt="${title}">
  </div>
  <script>
    window.onload = function() {
      window.focus();
      window.print();
    };
  <\/script>
</body>
</html>`
}

function buildExportFilename(extension) {
  return `${sanitizeFilename(buildExportTitle())}.${extension}`
}

function buildExportTitle() {
  return `${plan.value.planName || "座位方案"}-${viewModeLabel.value}-座位表`
}

function sanitizeFilename(value) {
  const filename = String(value || "座位方案")
    .replace(/[\\/:*?"<>|]/g, "_")
    .replace(/[.\s]+$/g, "")
  return filename || "座位方案"
}

function escapeHtml(value) {
  return String(value || "")
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;")
    .replace(/'/g, "&#39;")
}

function formatScoreChange(scoreChange) {
  const value = Number(scoreChange || 0)
  if (value > 0) {
    return `总分提升 ${value.toFixed(2)}`
  }
  if (value < 0) {
    return `总分下降 ${Math.abs(value).toFixed(2)}`
  }
  return "总分不变"
}

function normalizeSeats(positions) {
  const rowMap = new Map()
  positions
    .slice()
    .sort((left, right) => left.rowIndex - right.rowIndex || left.colIndex - right.colIndex)
    .forEach(item => {
      if (!rowMap.has(item.rowIndex)) {
        rowMap.set(item.rowIndex, [])
      }
      rowMap.get(item.rowIndex).push(item)
    })
  seatRows.value = Array.from(rowMap.keys())
    .sort((left, right) => left - right)
    .map(rowIndex => rowMap.get(rowIndex))
}

function formatScoreDetail(detailJson) {
  if (!detailJson) {
    return "-"
  }
  try {
    const detail = JSON.parse(detailJson)
    return Object.keys(detail).map(key => `${scoreDetailLabel(key)}：${detail[key]}`).join("，")
  } catch (e) {
    return detailJson
  }
}

function scoreDetailLabel(key) {
  const labelMap = {
    required: "需满足数量",
    violations: "冲突数量",
    affected: "影响学生数",
    pairs: "同桌组合数",
    sameGenderPairs: "同性同桌数",
    sameLevelPairs: "同水平同桌数",
    adjacentPairs: "相邻组合数",
    preferredMissed: "未满足偏好数",
    assigned: "已安排人数",
    seed: "随机种子",
    conflictCount: "冲突数量",
    penalty: "扣分"
  }
  return labelMap[key] || key
}

function loadDetail() {
  loading.value = true
  const planId = route.params.planId
  getPlan(planId).then(response => {
    plan.value = response.data || {}
    return Promise.all([
      getClassroomLayout(plan.value.classroomId),
      listAssignment({ planId, pageNum: 1, pageSize: 1000 }),
      listScore({ planId, pageNum: 1, pageSize: 1000 }),
      listStudent({ classId: plan.value.classId, pageNum: 1, pageSize: 1000 })
    ])
  }).then(([layoutResponse, assignmentResponse, scoreResponse, studentResponse]) => {
    normalizeSeats(layoutResponse.data || [])
    assignmentList.value = assignmentResponse.rows || []
    scoreList.value = scoreResponse.rows || []
    studentList.value = studentResponse.rows || []
    dirty.value = false
  }).finally(() => {
    loading.value = false
  })
}

loadDetail()
</script>

<style scoped>
.plan-summary {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
  margin: 18px 0;
  padding: 14px 16px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #ffffff;
}

.summary-item {
  min-width: 0;
}

.summary-label {
  display: block;
  color: #909399;
  font-size: 12px;
  line-height: 18px;
}

.summary-value {
  display: block;
  margin-top: 4px;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.detail-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 420px;
  gap: 16px;
  align-items: start;
}

.adjust-alert {
  margin-bottom: 16px;
}

.seat-section,
.score-section {
  min-width: 0;
  padding: 16px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #ffffff;
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.section-title {
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

.score-section .section-title + .section-title {
  margin-top: 16px;
}

.section-actions {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  flex: 0 0 auto;
}

.view-toggle {
  margin-right: 4px;
}

.classroom-view {
  min-width: 0;
}

.classroom-body {
  display: flex;
  align-items: stretch;
  gap: 10px;
}

.classroom-main {
  max-width: 100%;
  overflow: auto;
  padding-bottom: 4px;
}

.seat-grid {
  display: grid;
  gap: 8px;
}

.seat-cell {
  width: 100%;
  min-width: 88px;
  height: 94px;
  padding: 6px;
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  background: #ffffff;
  color: #606266;
  overflow: hidden;
}

.seat-cell[draggable="true"] {
  cursor: move;
}

.seat-assigned {
  border-color: #409eff;
  background: #ecf5ff;
}

.seat-locked {
  border-color: #e6a23c;
  background: #fdf6ec;
}

.seat-empty {
  border-style: dashed;
  background: #ffffff;
}

.seat-disabled {
  border-color: #f56c6c;
  background: #fef0f0;
  color: #f56c6c;
}

.seat-aisle {
  border-color: #c0c4cc;
  background: #f4f4f5;
  color: #909399;
}

.seat-code {
  color: #909399;
  font-size: 12px;
  line-height: 16px;
}

.student-name {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 4px;
  color: #303133;
  font-size: 14px;
  font-weight: 600;
  line-height: 18px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.student-text {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.gender-tag {
  flex: 0 0 auto;
  min-width: 24px;
  height: 16px;
  padding: 0 4px;
  border-radius: 3px;
  font-size: 11px;
  font-weight: 500;
  line-height: 16px;
  text-align: center;
}

.gender-male {
  background: #e8f3ff;
  color: #1677ff;
}

.gender-female {
  background: #fff0f6;
  color: #c41d7f;
}

.gender-unknown {
  background: #f4f4f5;
  color: #909399;
}

.seat-actions {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-top: 2px;
  overflow: hidden;
}

.seat-action-button {
  height: 20px;
  padding: 0;
  font-size: 12px;
}

.seat-action-button + .seat-action-button {
  margin-left: 0;
}

.unassigned-panel {
  margin: 10px 0 16px;
  padding: 10px;
  border: 1px dashed #dcdfe6;
  border-radius: 6px;
  background: #fafafa;
}

.student-pool {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  max-height: 150px;
  overflow: auto;
}

.student-chip {
  display: inline-flex;
  align-items: center;
  max-width: 180px;
  height: 28px;
  padding: 0 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #ffffff;
  color: #303133;
  cursor: move;
}

.student-chip-name {
  min-width: 0;
  margin-left: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.seat-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  font-size: 13px;
}

.platform {
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 6px;
  background: #409eff;
  color: #ffffff;
  font-weight: 600;
}

.platform-horizontal {
  width: 180px;
  height: 34px;
  margin: 0 auto 10px auto;
}

.platform-horizontal:last-child {
  margin: 10px auto 0 auto;
}

.platform-vertical {
  width: 34px;
  min-height: 100px;
  writing-mode: vertical-rl;
  letter-spacing: 4px;
}

@media (max-width: 1200px) {
  .detail-layout {
    grid-template-columns: 1fr;
  }
}
</style>
