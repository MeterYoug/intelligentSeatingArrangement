function normalizeState(input = {}) {
  const loading = Boolean(input.loading)
  const saving = Boolean(input.saving)
  const confirming = Boolean(input.confirming)
  const dirty = Boolean(input.dirty)
  const undoCount = Number(input.undoCount || 0)
  const redoCount = Number(input.redoCount || 0)
  const selectedSeatCount = Number(input.selectedSeatCount || 0)
  const seatCount = Number(input.seatCount || 0)
  const planStatus = input.planStatus || "DRAFT"
  const busyReason = loading ? "loading" : saving ? "saving" : confirming ? "confirming" : ""

  return {
    loading,
    saving,
    confirming,
    dirty,
    undoCount,
    redoCount,
    selectedSeatCount,
    seatCount,
    planStatus,
    busyReason,
    undoDisabled: !!busyReason || undoCount === 0,
    redoDisabled: !!busyReason || redoCount === 0,
    selectionToggleDisabled: !!busyReason,
    batchLockDisabled: !!busyReason || selectedSeatCount === 0,
    batchUnlockDisabled: !!busyReason || selectedSeatCount === 0,
    saveDisabled: !!busyReason || !dirty,
    confirmDisabled: !!busyReason || dirty || planStatus === "ACTIVE",
    exportDisabled: !!busyReason || dirty || seatCount === 0
  }
}

export function createPlanDetailActionState(input) {
  return normalizeState(input)
}

export function getPlanDetailBlockedMessage(action, state) {
  if (state.busyReason === "loading") return "方案正在加载，请稍后再试"
  if (state.busyReason === "saving") return "正在保存调整，请稍后再试"
  if (state.busyReason === "confirming") return "正在确认方案，请稍后再试"
  if (action === "save" && !state.dirty) return "当前没有待保存的调整"
  if (action === "confirm" && state.dirty) return "请先保存调整后再确认方案"
  if (action === "confirm" && state.planStatus === "ACTIVE") return "当前方案已启用，无需重复确认"
  if (action === "export" && state.dirty) return "请先保存调整后再导出"
  if (action === "export" && state.seatCount === 0) return "暂无座位布局可导出"
  if ((action === "batchLock" || action === "batchUnlock") && state.selectedSeatCount === 0) return "请先选择座位"
  if (action === "undo" && state.undoCount === 0) return "当前没有可撤销的调整"
  if (action === "redo" && state.redoCount === 0) return "当前没有可重做的调整"
  return ""
}
