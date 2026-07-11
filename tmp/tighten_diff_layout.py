from pathlib import Path
import re
root = Path(r'D:\AI\project\intelligentSeatingArrangement')

p = root / 'RuoYi-Vue3-master' / 'src' / 'views' / 'seating' / 'plan' / 'detail.vue'
text = p.read_text(encoding='utf-8')
pattern = re.compile(r'''<div v-if="comparisonPlanId" class="comparison-summary">\s*<template v-if="comparisonDiffs.length">\s*<div class="comparison-summary-text">与「\{\{ comparisonPlanName \}\}」相比，共有 \{\{ comparisonDiffs.length \}\} 名学生的座位发生变化。<\/div>\s*<el-table class="comparison-table" :data="comparisonDiffs" size="small" border max-height="260">\s*<el-table-column label="学生" prop="studentName" min-width="110" show-overflow-tooltip \/>\s*<el-table-column label="原位置" prop="from" min-width="120" show-overflow-tooltip \/>\s*<el-table-column label="现位置" prop="to" min-width="120" show-overflow-tooltip \/>\s*<\/el-table>\s*<\/template>\s*<span v-else>与「\{\{ comparisonPlanName \}\}」相比，学生座位没有变化。<\/span>\s*<\/div>''', re.S)
replacement = '''<div v-if="comparisonPlanId" class="comparison-summary">
              <template v-if="comparisonDiffs.length">
                <div class="comparison-summary-text">与「{{ comparisonPlanName }}」相比，共有 {{ comparisonDiffs.length }} 名学生的座位发生变化。</div>
                <div class="comparison-list">
                  <div v-for="item in comparisonDiffs" :key="item.studentId" class="comparison-row">
                    <div class="comparison-student" :title="item.studentName">{{ item.studentName }}</div>
                    <div class="comparison-position comparison-position-from">
                      <span class="comparison-position-label">原</span>
                      <span class="comparison-position-value" :title="item.from">{{ item.from }}</span>
                    </div>
                    <div class="comparison-position comparison-position-to">
                      <span class="comparison-position-label">现</span>
                      <span class="comparison-position-value" :title="item.to">{{ item.to }}</span>
                    </div>
                  </div>
                </div>
              </template>
              <span v-else>与「{{ comparisonPlanName }}」相比，学生座位没有变化。</span>
            </div>'''
text, count = pattern.subn(replacement, text, count=1)
if count != 1:
    raise SystemExit(f'comparison block replacement count={count}')
style_old = re.compile(r'''\.comparison-summary-text \{\s*  margin-bottom: 8px;\s*\}\s*\s*\.comparison-table :deep\(\.el-table__cell\) \{\s*  padding-top: 6px;\s*  padding-bottom: 6px;\s*\}''', re.S)
style_new = '''.comparison-summary-text {
  margin-bottom: 8px;
}

.comparison-list {
  display: grid;
  gap: 8px;
}

.comparison-row {
  display: grid;
  grid-template-columns: minmax(96px, 1.2fr) minmax(120px, 1fr) minmax(120px, 1fr);
  gap: 8px;
  align-items: center;
  padding: 8px 10px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fafafa;
}

.comparison-student,
.comparison-position-value {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.comparison-student {
  color: #303133;
  font-weight: 600;
}

.comparison-position {
  display: flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
}

.comparison-position-label {
  flex: 0 0 auto;
  min-width: 24px;
  height: 20px;
  border-radius: 10px;
  background: #f4f4f5;
  color: #606266;
  font-size: 12px;
  font-weight: 600;
  line-height: 20px;
  text-align: center;
}

.comparison-position-to .comparison-position-label {
  background: #ecf5ff;
  color: #1677ff;
}'''
text, count2 = style_old.subn(style_new, text, count=1)
if count2 != 1:
    raise SystemExit(f'style block replacement count={count2}')
p.write_text(text, encoding='utf-8')

p = root / 'docs' / 'project-progress.md'
text = p.read_text(encoding='utf-8')
old = '- 差异区已改为表格展示，便于老师快速扫读学生、原位置和现位置。\n'
new = '- 差异区已改为更紧凑的两列对照行展示，便于老师快速扫读学生、原位置和现位置。\n'
if old not in text:
    raise SystemExit('progress line not found')
text = text.replace(old, new, 1)
p.write_text(text, encoding='utf-8')

p = root / 'ROADMAP.md'
text = p.read_text(encoding='utf-8')
old = '- 已将同班历史方案差异区改成表格展示，便于老师直接扫读学生、原位置和现位置。\n'
new = '- 已将同班历史方案差异区改成更紧凑的两列对照行展示，便于老师直接扫读学生、原位置和现位置。\n'
if old not in text:
    raise SystemExit('roadmap line not found')
text = text.replace(old, new, 1)
p.write_text(text, encoding='utf-8')
