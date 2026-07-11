# 慧排座前端

慧排座是面向老师的 PC Web 智能排座管理系统前端，提供班级、学生、成绩、教室布局、排座规则和座位方案管理能力。

## 技术栈

- Vue 3
- Vite
- Element Plus
- Pinia
- Vue Router

## 本地运行

```bash
npm install
npm run dev
```

开发服务默认运行在 `http://localhost:8222`，接口通过 `/dev-api` 代理到后端服务。

## 构建生产版本

```bash
npm run build:prod
```

## 主要功能

- 班级、学生和成绩管理
- 学生与成绩 Excel 导入
- 教室座位布局配置
- 排座规则配置与智能生成
- 座位方案拖拽微调、锁定、确认和历史管理
- Excel、图片与 PDF 导出

## 相关文档

- 项目说明：仓库根目录 `README.md`
- MVP 进度：仓库根目录 `ROADMAP.md`
- 模块设计：`docs/seating-module-design.md`
