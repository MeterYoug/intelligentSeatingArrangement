import test from 'node:test'
import assert from 'node:assert/strict'
import { formatPlanScoreDetailText, formatPlanConflictText } from '../detailReadableText.js'

test('translates gender balance detail into teacher-readable text', () => {
  const row = {
    ruleCode: 'GENDER_BALANCE',
    detailJson: JSON.stringify({ pairs: 12, sameGenderPairs: 3, penalty: 15 })
  }

  assert.equal(formatPlanScoreDetailText(row), '共评估 12 组同桌，其中 3 组为同性同桌')
})

test('translates capacity detail into arranged-student summary', () => {
  const row = {
    ruleCode: 'CAPACITY',
    detailJson: JSON.stringify({ assigned: 48, penalty: 0 })
  }

  assert.equal(formatPlanScoreDetailText(row), '当前已安排 48 名学生入座')
})

test('translates front-row conflict into seat guidance', () => {
  const assignments = [
    { studentNameSnapshot: '张三', seatId: 1, rowIndex: 3, colIndex: 2 }
  ]
  const seats = [
    { seatId: 1, seatCode: '03-02' }
  ]

  assert.equal(
    formatPlanConflictText('张三 未能安排在前 2 排', assignments, seats),
    '张三 需要安排在前 2 排内，当前座位是 03-02'
  )
})

test('translates not-deskmate conflict into dual-seat guidance', () => {
  const assignments = [
    { studentNameSnapshot: '张三', seatId: 1, rowIndex: 1, colIndex: 1 },
    { studentNameSnapshot: '李四', seatId: 2, rowIndex: 1, colIndex: 2 }
  ]
  const seats = [
    { seatId: 1, seatCode: 'A1' },
    { seatId: 2, seatCode: 'A2' }
  ]

  assert.equal(
    formatPlanConflictText('张三 与 李四 不能同桌', assignments, seats),
    '张三 与 李四 被设置为不能同桌，当前座位分别是 A1、A2'
  )
})