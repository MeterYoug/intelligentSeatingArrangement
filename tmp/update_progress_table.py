from pathlib import Path
root = Path(r'D:\AI\project\intelligentSeatingArrangement')
p = root / 'docs' / 'project-progress.md'
text = p.read_text(encoding='utf-8')
old = '- 该文案与已补齐的外圈排号／列号保持同一口径，不再只显示简写数字差异。\n- 前端生产构建已通过，页面展示可继续做真实浏览器回归。\n'
new = '- 该文案与已补齐的外圈排号／列号保持同一口径，不再只显示简写数字差异。\n- 差异区已改为表格展示，便于老师快速扫读学生、原位置和现位置。\n- 前端生产构建已通过，页面展示可继续做真实浏览器回归。\n'
if old not in text:
    raise SystemExit('old block not found in project-progress.md')
text = text.replace(old, new, 1)
p.write_text(text, encoding='utf-8')
