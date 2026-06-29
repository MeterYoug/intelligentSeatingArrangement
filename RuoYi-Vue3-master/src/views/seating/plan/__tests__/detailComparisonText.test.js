import test from 'node:test'
import assert from 'node:assert/strict'
import { formatAssignmentPosition, formatAssignmentPositionReadable } from '../detailComparisonText.js'

test('formats assignment position with seat code first', () => {
  assert.equal(
    formatAssignmentPosition({ seatCode: '03-02', rowIndex: 3, colIndex: 2 }),
    '03-02'
  )
})

test('formats assignment position from row and column when seat code is absent', () => {
  assert.equal(
    formatAssignmentPosition({ rowIndex: 3, colIndex: 2 }),
    '3-2'
  )
})

test('falls back to unassigned when the assignment is missing position data', () => {
  assert.equal(formatAssignmentPosition({ seatId: 88 }), '未安排')
  assert.equal(formatAssignmentPosition(), '未安排')
})
