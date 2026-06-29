from pathlib import Path
root = Path(r'D:\AI\project\intelligentSeatingArrangement')
p = root / 'ROADMAP.md'
text = p.read_text(encoding='utf-8')
needle = '- 已将同班历史方案差异文案改为“原 第x排第y列 → 现 第x排第y列”，与外圈排号／列号展示保持一致。\n'
if needle in text:
    text = text.replace(needle, needle + '- 已将同班历史方案差异区改成更紧凑的两列对照行展示，便于老师快速扫读学生、原位置和现位置。\n', 1)
else:
    marker = '- 已支持同班历史方案差异对比，显示学生座位变化。\n'
    if marker not in text:
        raise SystemExit('roadmap insertion marker not found')
    text = text.replace(marker, marker + '- 已将同班历史方案差异区改成更紧凑的两列对照行展示，便于老师快速扫读学生、原位置和现位置。\n', 1)
p.write_text(text, encoding='utf-8')
