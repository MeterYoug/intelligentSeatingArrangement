import test from 'node:test'
import assert from 'node:assert/strict'
import { createPlanDetailActionState, getPlanDetailBlockedMessage } from '../detailActionState.js'

test('disables confirm and export when there are unsaved adjustments', () => {
  const state = createPlanDetailActionState({
    loading: false,
    saving: false,
    confirming: false,
    dirty: true,
    undoCount: 1,
    redoCount: 0,
    selectedSeatCount: 2,
    seatCount: 12,
    planStatus: 'DRAFT'
  })

  assert.equal(state.saveDisabled, false)
  assert.equal(state.confirmDisabled, true)
  assert.equal(state.exportDisabled, true)
  assert.equal(getPlanDetailBlockedMessage('confirm', state), '请先保存调整后再确认方案')
  assert.equal(getPlanDetailBlockedMessage('export', state), '请先保存调整后再导出')
})

test('disables batch actions when no seat is selected', () => {
  const state = createPlanDetailActionState({
    loading: false,
    saving: false,
    confirming: false,
    dirty: false,
    undoCount: 0,
    redoCount: 0,
    selectedSeatCount: 0,
    seatCount: 12,
    planStatus: 'DRAFT'
  })

  assert.equal(state.batchLockDisabled, true)
  assert.equal(state.batchUnlockDisabled, true)
  assert.equal(getPlanDetailBlockedMessage('batchLock', state), '请先选择座位')
})

test('locks all high-frequency actions while save is in progress', () => {
  const state = createPlanDetailActionState({
    loading: false,
    saving: true,
    confirming: false,
    dirty: true,
    undoCount: 3,
    redoCount: 2,
    selectedSeatCount: 2,
    seatCount: 12,
    planStatus: 'DRAFT'
  })

  assert.equal(state.undoDisabled, true)
  assert.equal(state.redoDisabled, true)
  assert.equal(state.selectionToggleDisabled, true)
  assert.equal(state.batchLockDisabled, true)
  assert.equal(state.saveDisabled, true)
  assert.equal(getPlanDetailBlockedMessage('save', state), '正在保存调整，请稍后再试')
})
