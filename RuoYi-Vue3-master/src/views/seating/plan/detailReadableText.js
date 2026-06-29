const scoreDetailLabelMap = {
  required: '需满足数量',
  violations: '冲突数量',
  affected: '影响学生数',
  pairs: '同桌组合数',
  sameGenderPairs: '同性同桌数',
  sameLevelPairs: '同水平同桌数',
  adjacentPairs: '相邻组合数',
  preferredMissed: '未满足偏好数',
  assigned: '已安排人数',
  seed: '随机种子',
  conflictCount: '冲突数量',
  penalty: '扣分'
}

function parseDetailJson(detailJson) {
  if (!detailJson) {
    return null
  }
  try {
    return JSON.parse(detailJson)
  } catch (error) {
    return null
  }
}

function toWholeNumber(value) {
  const number = Number(value || 0)
  return Number.isFinite(number) ? number : 0
}

function fallbackScoreDetail(detailJson) {
  const detail = parseDetailJson(detailJson)
  if (!detail) {
    return detailJson || '-'
  }
  const entries = Object.entries(detail).filter(([key]) => key !== 'penalty')
  if (!entries.length) {
    return '-'
  }
  return entries.map(([key, value]) => `${scoreDetailLabelMap[key] || key}：${value}`).join('，')
}

function seatPositionOf(assignment, seatMap) {
  if (!assignment) {
    return '未安排'
  }
  const seat = seatMap.get(assignment.seatId)
  return seat?.seatCode || `${assignment.rowIndex} 排 ${assignment.colIndex} 列`
}

function positionsByNames(names, assignments, seatMap) {
  return names
    .map(name => {
      const assignment = assignments.find(item => item.studentNameSnapshot === name)
      return assignment ? `${name}（${seatPositionOf(assignment, seatMap)}）` : ''
    })
    .filter(Boolean)
}

export function formatPlanScoreDetailText(scoreItem = {}) {
  const detail = parseDetailJson(scoreItem.detailJson)
  if (!detail) {
    return scoreItem.detailJson || '-'
  }

  switch (scoreItem.ruleCode) {
    case 'FRONT_ROW':
      return `需坐前排学生 ${toWholeNumber(detail.required)} 人，其中 ${toWholeNumber(detail.violations)} 人未进入指定前排`
    case 'VISION_FRONT':
      return `${toWholeNumber(detail.affected)} 名视力关注学生已纳入靠前安排评估`
    case 'HEIGHT_BACK':
      return `${toWholeNumber(detail.affected)} 名高个学生已纳入靠后安排评估`
    case 'GENDER_BALANCE':
      return `共评估 ${toWholeNumber(detail.pairs)} 组同桌，其中 ${toWholeNumber(detail.sameGenderPairs)} 组为同性同桌`
    case 'SCORE_BALANCE':
      return `共评估 ${toWholeNumber(detail.pairs)} 组同桌，其中 ${toWholeNumber(detail.sameLevelPairs)} 组为同水平同桌`
    case 'DISCIPLINE_SCATTER':
      return `当前有 ${toWholeNumber(detail.adjacentPairs)} 组纪律关注学生相邻`
    case 'STUDENT_RELATION':
      return `学生关系约束中有 ${toWholeNumber(detail.violations)} 项硬冲突，${toWholeNumber(detail.preferredMissed)} 项偏好未满足`
    case 'CAPACITY':
      return `当前已安排 ${toWholeNumber(detail.assigned)} 名学生入座`
    case 'RANDOM_SEED':
      return `本次使用随机种子 ${toWholeNumber(detail.seed)}，硬冲突 ${toWholeNumber(detail.conflictCount)} 项`
    default:
      return fallbackScoreDetail(scoreItem.detailJson)
  }
}

export function formatPlanConflictText(conflict, assignments = [], seats = []) {
  const content = String(conflict || '')
  const seatMap = new Map((seats || []).map(item => [item.seatId, item]))

  const frontRowMatch = content.match(/^(.+?) 未能安排在前 (\d+) 排$/)
  if (frontRowMatch) {
    const [, studentName, frontRows] = frontRowMatch
    const assignment = assignments.find(item => item.studentNameSnapshot === studentName)
    return `${studentName} 需要安排在前 ${frontRows} 排内，当前座位是 ${seatPositionOf(assignment, seatMap)}`
  }

  const notDeskmateMatch = content.match(/^(.+?) 与 (.+?) 不能同桌$/)
  if (notDeskmateMatch) {
    const [, leftName, rightName] = notDeskmateMatch
    const left = assignments.find(item => item.studentNameSnapshot === leftName)
    const right = assignments.find(item => item.studentNameSnapshot === rightName)
    return `${leftName} 与 ${rightName} 被设置为不能同桌，当前座位分别是 ${seatPositionOf(left, seatMap)}、${seatPositionOf(right, seatMap)}`
  }

  const notAdjacentMatch = content.match(/^(.+?) 与 (.+?) 不能相邻$/)
  if (notAdjacentMatch) {
    const [, leftName, rightName] = notAdjacentMatch
    const left = assignments.find(item => item.studentNameSnapshot === leftName)
    const right = assignments.find(item => item.studentNameSnapshot === rightName)
    return `${leftName} 与 ${rightName} 被设置为不能相邻，当前座位分别是 ${seatPositionOf(left, seatMap)}、${seatPositionOf(right, seatMap)}`
  }

  const relatedNames = assignments
    .filter(item => item.studentNameSnapshot && content.includes(item.studentNameSnapshot))
    .map(item => item.studentNameSnapshot)
  const uniqueNames = [...new Set(relatedNames)]
  const positions = positionsByNames(uniqueNames, assignments, seatMap)
  return positions.length ? `${content}。当前座位：${positions.join('、')}` : content
}