# Plan Detail Action Feedback Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Unify disabled states and user feedback for high-frequency actions on the seating plan detail page.

**Architecture:** Extract button-state and guard-message logic into a pure helper module, cover it with Node built-in tests first, then wire the Vue page to consume the shared state. This keeps the change front-end only and avoids expanding business scope.

**Tech Stack:** Vue 3 SFC, Vite, Node built-in test runner, PowerShell verification commands

---

### Task 1: Add pure action-state helper and tests

**Files:**
- Create: `RuoYi-Vue3-master/src/views/seating/plan/detailActionState.js`
- Create: `RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailActionState.test.js`

- [ ] **Step 1: Write the failing test**

```javascript
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
```

- [ ] **Step 2: Run test to verify it fails**

Run: `node --test RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailActionState.test.js`
Expected: FAIL with module-not-found for `detailActionState.js`

- [ ] **Step 3: Write minimal implementation**

```javascript
function normalizeState(input) {
  const loading = Boolean(input.loading)
  const saving = Boolean(input.saving)
  const confirming = Boolean(input.confirming)
  const dirty = Boolean(input.dirty)
  const undoCount = Number(input.undoCount || 0)
  const redoCount = Number(input.redoCount || 0)
  const selectedSeatCount = Number(input.selectedSeatCount || 0)
  const seatCount = Number(input.seatCount || 0)
  const planStatus = input.planStatus || 'DRAFT'
  const busyReason = loading ? 'loading' : saving ? 'saving' : confirming ? 'confirming' : ''

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
    confirmDisabled: !!busyReason || dirty || planStatus === 'ACTIVE',
    exportDisabled: !!busyReason || dirty || seatCount === 0
  }
}

export function createPlanDetailActionState(input) {
  return normalizeState(input)
}

export function getPlanDetailBlockedMessage(action, state) {
  if (state.busyReason === 'loading') return '方案正在加载，请稍后再试'
  if (state.busyReason === 'saving') return '正在保存调整，请稍后再试'
  if (state.busyReason === 'confirming') return '正在确认方案，请稍后再试'
  if (action === 'save' && !state.dirty) return '当前没有待保存的调整'
  if (action === 'confirm' && state.dirty) return '请先保存调整后再确认方案'
  if (action === 'confirm' && state.planStatus === 'ACTIVE') return '当前方案已启用，无需重复确认'
  if (action === 'export' && state.dirty) return '请先保存调整后再导出'
  if (action === 'export' && state.seatCount === 0) return '暂无座位布局可导出'
  if ((action === 'batchLock' || action === 'batchUnlock') && state.selectedSeatCount === 0) return '请先选择座位'
  if (action === 'undo' && state.undoCount === 0) return '当前没有可撤销的调整'
  if (action === 'redo' && state.redoCount === 0) return '当前没有可重做的调整'
  return ''
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `node --test RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailActionState.test.js`
Expected: PASS

### Task 2: Wire shared action state into the page

**Files:**
- Modify: `RuoYi-Vue3-master/src/views/seating/plan/detail.vue`

- [ ] **Step 1: Write the failing integration expectation**

Expectation to enforce in code:

```javascript
// save should refuse while busy or clean
if (actionState.value.saveDisabled) {
  proxy.$modal.msgWarning(getPlanDetailBlockedMessage('save', actionState.value))
  return
}
```

- [ ] **Step 2: Run helper test again as red/guard check**

Run: `node --test RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailActionState.test.js`
Expected: PASS, helper contract unchanged before page wiring

- [ ] **Step 3: Write minimal page integration**

Required edits:

```javascript
import { createPlanDetailActionState, getPlanDetailBlockedMessage } from './detailActionState'

const actionState = computed(() => createPlanDetailActionState({
  loading: loading.value,
  saving: saving.value,
  confirming: confirming.value,
  dirty: dirty.value,
  undoCount: undoStack.value.length,
  redoCount: redoStack.value.length,
  selectedSeatCount: selectedSeatIds.value.length,
  seatCount: flatSeats.value.length,
  planStatus: plan.value.planStatus
}))

function guardPlanAction(action) {
  const message = getPlanDetailBlockedMessage(action, actionState.value)
  if (message) {
    proxy.$modal.msgWarning(message)
    return true
  }
  return false
}
```

Template updates:

```vue
<el-button :disabled="actionState.undoDisabled" @click="undoAdjustment">撤销</el-button>
<el-button :disabled="actionState.redoDisabled" @click="redoAdjustment">重做</el-button>
<el-button :disabled="actionState.selectionToggleDisabled" @click="toggleSelectionMode">...</el-button>
<el-button :disabled="actionState.batchLockDisabled" @click="batchSetLock('1')">批量锁定</el-button>
<el-button :disabled="actionState.batchUnlockDisabled" @click="batchSetLock('0')">批量解锁</el-button>
<el-button :disabled="actionState.exportDisabled" @click="exportSeatTable">导出 Excel</el-button>
<el-button :disabled="actionState.exportDisabled" @click="exportSeatImage">导出图片</el-button>
<el-button :disabled="actionState.exportDisabled" @click="exportSeatPdf">导出 PDF</el-button>
<el-button :disabled="actionState.confirmDisabled" @click="confirmCurrentPlan">确认方案</el-button>
<el-button :disabled="actionState.saveDisabled" @click="saveAssignments">保存调整</el-button>
```

Guard updates:

```javascript
function batchSetLock(isLocked) {
  if (guardPlanAction(isLocked === '1' ? 'batchLock' : 'batchUnlock')) return
  // existing body
}

function undoAdjustment() {
  if (guardPlanAction('undo')) return
  // existing body
}

function redoAdjustment() {
  if (guardPlanAction('redo')) return
  // existing body
}

function saveAssignments() {
  if (guardPlanAction('save')) return
  // existing body
}

function confirmCurrentPlan() {
  if (guardPlanAction('confirm')) return
  // existing body
}

function exportSeatTable() {
  if (guardPlanAction('export')) return
  // existing body
}
```

- [ ] **Step 4: Run verification**

Run: `npm run build:prod`
Expected: build succeeds

### Task 3: Final regression verification

**Files:**
- Modify: `docs/project-progress.md` if implementation status changes materially

- [ ] **Step 1: Run focused tests**

Run: `node --test RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailActionState.test.js`
Expected: PASS

- [ ] **Step 2: Run frontend build**

Run: `npm run build:prod`
Expected: PASS

- [ ] **Step 3: Record outcome**

If behavior changed as intended, append a progress note to `docs/project-progress.md` summarizing:
- shared action-state helper added
- page buttons now use unified disabled state
- warning messages now go through shared guard
