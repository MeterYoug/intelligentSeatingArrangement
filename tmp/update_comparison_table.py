from pathlib import Path
root = Path(r'D:\AI\project\intelligentSeatingArrangement')
p = root / 'RuoYi-Vue3-master' / 'src' / 'views' / 'seating' / 'plan' / 'detail.vue'
text = p.read_text(encoding='utf-8')
old = '''            <div v-if="comparisonPlanId" class="comparison-summary">
              <template v-if="comparisonDiffs.length">
                <div>与「{{ comparisonPlanName }}」相比，共有 {{ comparisonDiffs.length }} 名学生的座位发生变化。</div>
                <div v-for="item in comparisonDiffs.slice(0, 6)" :key="item.studentId" class="comparison-item">
                  {{ item.studentName }}：原 {{ item.from }} → 现 {{ item.to }}
                </div>
                <div v-if="comparisonDiffs.length > 6" class="comparison-more">其余 {{ comparisonDiffs.length - 6 }} 项变化未展开。</div>
              </template>
              <span v-else>与「{{ comparisonPlanName }}」相比，学生座位没有变化。</span>
            </div>
'''
new = '''            <div v-if="comparisonPlanId" class="comparison-summary">
              <template v-if="comparisonDiffs.length">
                <div class="comparison-summary-text">与「{{ comparisonPlanName }}」相比，共有 {{ comparisonDiffs.length }} 名学生的座位发生变化。</div>
                <el-table class="comparison-table" :data="comparisonDiffs" size="small" border max-height="260">
                  <el-table-column label="学生" prop="studentName" min-width="110" show-overflow-tooltip />
                  <el-table-column label="原位置" prop="from" min-width="120" show-overflow-tooltip />
                  <el-table-column label="现位置" prop="to" min-width="120" show-overflow-tooltip />
                </el-table>
              </template>
              <span v-else>与「{{ comparisonPlanName }}」相比，学生座位没有变化。</span>
            </div>
'''
if old not in text:
    raise SystemExit('comparison block not found')
text = text.replace(old, new, 1)
old_style = '''.comparison-summary {
  margin-top: 10px;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  color: #606266;
  font-size: 13px;
  line-height: 1.7;
}

.comparison-item {
  margin-top: 4px;
}

.comparison-more {
  margin-top: 4px;
  color: #909399;
}
'''
new_style = '''.comparison-summary {
  margin-top: 10px;
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  color: #606266;
  font-size: 13px;
  line-height: 1.7;
}

.comparison-summary-text {
  margin-bottom: 8px;
}

.comparison-table :deep(.el-table__cell) {
  padding-top: 6px;
  padding-bottom: 6px;
}
'''
if old_style not in text:
    raise SystemExit('comparison style block not found')
text = text.replace(old_style, new_style, 1)
p.write_text(text, encoding='utf-8')

p = root / 'docs' / 'project-progress.md'
text = p.read_text(encoding='utf-8')
marker = '- 已将同班历史方案差异文案改为“原 第x排第y列 → 现 第x排第y列”，与外圈排号／列号展示保持一致。\n'
addition = '- 已将同班历史方案差异区改成表格展示，便于老师直接扫读学生、原位置和现位置。\n'
if marker not in text:
    raise SystemExit('progress marker not found')
text = text.replace(marker, marker + addition, 1)
p.write_text(text, encoding='utf-8')

p = root / 'ROADMAP.md'
text = p.read_text(encoding='utf-8')
marker = '- 已将同班历史方案差异文案改为“原 第x排第y列 → 现 第x排第y列”，与外圈排号／列号展示保持一致。\n'
addition = '- 已将同班历史方案差异区改成表格展示，便于老师直接扫读学生、原位置和现位置。\n'
if marker not in text:
    raise SystemExit('roadmap marker not found')
text = text.replace(marker, marker + addition, 1)
p.write_text(text, encoding='utf-8')
