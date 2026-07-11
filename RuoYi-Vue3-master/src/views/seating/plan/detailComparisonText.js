export function formatAssignmentPosition(assignment = {}) {
  if (!assignment) {
    return "未安排"
  }

  if (assignment.seatCode) {
    return String(assignment.seatCode)
  }

  const rowIndex = Number(assignment.rowIndex)
  const colIndex = Number(assignment.colIndex)
  if (Number.isFinite(rowIndex) && Number.isFinite(colIndex)) {
    return `${rowIndex}-${colIndex}`
  }

  return "未安排"
}

export function formatAssignmentPositionReadable(assignment = {}) {
  if (!assignment) {
    return "未安排"
  }

  const rowIndex = Number(assignment.rowIndex)
  const colIndex = Number(assignment.colIndex)
  if (Number.isFinite(rowIndex) && Number.isFinite(colIndex)) {
    return `第${rowIndex}排第${colIndex}列`
  }

  if (assignment.seatCode) {
    return String(assignment.seatCode)
  }

  return "未安排"
}
