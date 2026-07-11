from pathlib import Path

root = Path(r'D:\AI\project\intelligentSeatingArrangement')

# detailComparisonText.js
p = root / 'RuoYi-Vue3-master' / 'src' / 'views' / 'seating' / 'plan' / 'detailComparisonText.js'
text = p.read_text(encoding='utf-8')
needle = """export function formatAssignmentPosition(assignment = {}) {
  if (!assignment) {
    return \"未安排\"
  }

  if (assignment.seatCode) {
    return String(assignment.seatCode)
  }

  const rowIndex = Number(assignment.rowIndex)
  const colIndex = Number(assignment.colIndex)
  if (Number.isFinite(rowIndex) && Number.isFinite(colIndex)) {
    return `${rowIndex}-${colIndex}`
  }

  return \"未安排\"
}
"""
replacement = needle + """
export function formatAssignmentPositionReadable(assignment = {}) {
  if (!assignment) {
    return \"未安排\"
  }

  const rowIndex = Number(assignment.rowIndex)
  const colIndex = Number(assignment.colIndex)
  if (Number.isFinite(rowIndex) && Number.isFinite(colIndex)) {
    return `第${rowIndex}排第${colIndex}列`
  }

  if (assignment.seatCode) {
    return String(assignment.seatCode)
  }

  return \"未安排\"
}
"""
if needle not in text:
    raise SystemExit('needle not found in detailComparisonText.js')
p.write_text(text.replace(needle, replacement), encoding='utf-8')

# detail.vue
p = root / 'RuoYi-Vue3-master' / 'src' / 'views' / 'seating' / 'plan' / 'detail.vue'
text = p.read_text(encoding='utf-8')
text = text.replace('import { formatAssignmentPosition } from "./detailComparisonText"', 'import { formatAssignmentPositionReadable } from "./detailComparisonText"')
text = text.replace('{{ item.studentName }}：{{ item.from }} → {{ item.to }}', '{{ item.studentName }}：原 {{ item.from }} → 现 {{ item.to }}')
text = text.replace('const from = formatAssignmentPosition(comparison)\n    const to = formatAssignmentPosition(current)', 'const from = formatAssignmentPositionReadable(comparison)\n    const to = formatAssignmentPositionReadable(current)')
p.write_text(text, encoding='utf-8')

# tests
p = root / 'RuoYi-Vue3-master' / 'src' / 'views' / 'seating' / 'plan' / '__tests__' / 'detailComparisonText.test.js'
text = p.read_text(encoding='utf-8')
text = text.replace("import { formatAssignmentPosition } from '../detailComparisonText.js'", "import { formatAssignmentPosition, formatAssignmentPositionReadable } from '../detailComparisonText.js'")
append = """

test('formats readable assignment position from row and column', () => {
  assert.equal(
    formatAssignmentPositionReadable({ rowIndex: 3, colIndex: 2 }),
    '第3排第2列'
  )
})

test('falls back to seat code when readable row and column are unavailable', () => {
  assert.equal(
    formatAssignmentPositionReadable({ seatCode: '03-02' }),
    '03-02'
  )
})

test('falls back to unassigned when the readable assignment is missing position data', () => {
  assert.equal(formatAssignmentPositionReadable({ seatId: 88 }), '未安排')
  assert.equal(formatAssignmentPositionReadable(), '未安排')
})
"""
if "formatAssignmentPositionReadable" not in text:
    text = text + append
p.write_text(text, encoding='utf-8')

# docs/project-progress.md
p = root / 'docs' / 'project-progress.md'
text = p.read_text(encoding='utf-8')
marker = '## 2026-06-27 座位方案详情页 Excel 导出外圈标注补齐\n'
insert = '''## 2026-06-29 座位方案差异文案收口

已完成：

- 方案详情页的历史方案差异展示已改为“原 第x排第y列 → 现 第x排第y列”的表达方式，便于老师直接理解变化位置。
- 该文案与已补齐的外圈排号／列号保持同一口径，不再只显示简写数字差异。
- 前端生产构建已通过，页面展示可继续做真实浏览器回归。

'''
if marker not in text:
    raise SystemExit('marker not found in project-progress.md')
text = text.replace(marker, insert + marker, 1)
p.write_text(text, encoding='utf-8')

# ROADMAP.md
p = root / 'ROADMAP.md'
text = p.read_text(encoding='utf-8')
anchor = '- 已支持同班历史方案差异对比，显示学生座位变化。\n'
addition = '- 已将同班历史方案差异文案改为“原 第x排第y列 → 现 第x排第y列”，与外圈排号／列号展示保持一致。\n'
if anchor not in text:
    raise SystemExit('anchor not found in ROADMAP.md')
text = text.replace(anchor, anchor + addition, 1)
p.write_text(text, encoding='utf-8')
