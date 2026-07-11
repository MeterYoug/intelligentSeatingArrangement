# Visible RuoYi Cleanup Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Remove RuoYi branding visible in the product UI, runtime metadata, and delivery documentation while retaining required license notices and internal framework identifiers.

**Architecture:** Unwire external RuoYi navigation components, replace exposed example text and runtime metadata, and replace the upstream frontend README with the product guide. Internal Java packages, Maven modules, licenses, and utility file names remain unchanged.

**Tech Stack:** Vue 3、Vite、Element Plus、Spring Boot 3、Springdoc OpenAPI、Maven。

## Global Constraints

- Keep both `LICENSE` files and their original copyright notices unchanged.
- Do not rename `com.ruoyi`, `ruoyi-*`, or existing project directories.
- Do not delete existing files; remove only active product-facing content.
- Verify with `npm run build:prod` and `mvn -DskipTests compile`.

---

### Task 1: Clean frontend product entry points

**Files:** `RuoYi-Vue3-master/src/layout/components/Navbar.vue`、`RuoYi-Vue3-master/src/views/tool/gen/index.vue`、`RuoYi-Vue3-master/src/views/tool/gen/genInfoForm.vue`、`RuoYi-Vue3-master/src/views/monitor/job/index.vue`、`RuoYi-Vue3-master/vite.config.js`

- [x] Remove navigation actions that open the upstream Git and documentation sites.
- [x] Replace visible download names and Java-package examples with product or neutral names.
- [x] Replace the scheduler class example and deployment comment.

### Task 2: Clean runtime metadata

**Files:** `RuoYi-Vue-springboot3/ruoyi-admin/src/main/resources/application.yml`、`RuoYi-Vue-springboot3/ruoyi-admin/src/main/resources/banner.txt`、`RuoYi-Vue-springboot3/ruoyi-admin/src/main/java/com/ruoyi/web/core/config/SwaggerConfig.java`

- [x] Set the application name, upload-path example, OpenAPI group, referer defaults, banner label, OpenAPI title, and description to 慧排座.

### Task 3: Clean delivery documentation and record verification

**Files:** `README.md`、`RuoYi-Vue3-master/README.md`、`ROADMAP.md`

- [x] Remove upstream framework marketing from product README files.
- [x] Record the completed cleanup in `ROADMAP.md` after both builds pass.
