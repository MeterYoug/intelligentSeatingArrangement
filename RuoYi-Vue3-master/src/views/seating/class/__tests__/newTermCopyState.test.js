import test from 'node:test'
import assert from 'node:assert/strict'
import { createNewTermCopyForm, normalizeCopyRelations, nextSchoolYear, nextSemester, validateNewTermCopyForm } from '../newTermCopyState.js'

test('creates default new term copy form from source class', () => {
  const form = createNewTermCopyForm({ classId: 27, className: '五一一班', schoolYear: '2025-2026', semester: '1' })
  assert.equal(form.sourceClassId, 27)
  assert.equal(form.sourceClassName, '五一一班')
  assert.equal(form.schoolYear, '2025-2026')
  assert.equal(form.semester, '2')
  assert.equal(form.copyStudents, true)
  assert.equal(form.copyRelations, true)
})

test('advances school year when copying from second semester', () => {
  assert.equal(nextSchoolYear('2025-2026', '2'), '2026-2027')
  assert.equal(nextSchoolYear('2025-2026', '下学期'), '2026-2027')
  assert.equal(nextSemester('下学期'), '1')
})

test('disables relations when students are not copied', () => {
  const form = normalizeCopyRelations({ copyStudents: false, copyRelations: true })
  assert.equal(form.copyRelations, false)
})

test('validates required copy fields', () => {
  assert.equal(validateNewTermCopyForm({ className: '', schoolYear: '2026-2027', semester: '1' }), '目标班级名称不能为空')
  assert.equal(validateNewTermCopyForm({ className: '新班级', schoolYear: '', semester: '1' }), '目标学年不能为空')
  assert.equal(validateNewTermCopyForm({ className: '新班级', schoolYear: '2026-2027', semester: '' }), '目标学期不能为空')
})
