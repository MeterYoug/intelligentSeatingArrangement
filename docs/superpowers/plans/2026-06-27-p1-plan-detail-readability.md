# Plan Detail Readability Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make score explanations and conflict messages on the seating plan detail page readable to teachers without changing back-end contracts.

**Architecture:** Add a pure front-end helper for translating rule details and conflict text, cover it with Node built-in tests first, then wire the Vue page to consume the helper with minimal template changes.

**Tech Stack:** Vue 3 SFC, Vite, Node built-in test runner, PowerShell verification commands

---

### Task 1: Add pure readability helper and failing tests

**Files:**
- Create: `RuoYi-Vue3-master/src/views/seating/plan/detailReadableText.js`
- Create: `RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailReadableText.test.js`

- [ ] Step 1: Write failing tests for score-detail translation and conflict translation.
- [ ] Step 2: Run `node --test "RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailReadableText.test.js"` and confirm it fails because the helper module does not exist yet.
- [ ] Step 3: Implement the minimal pure helper with known rule mappings and fallback behavior.
- [ ] Step 4: Re-run the same test command and confirm all tests pass.

### Task 2: Wire the helper into the detail page

**Files:**
- Modify: `RuoYi-Vue3-master/src/views/seating/plan/detail.vue`

- [ ] Step 1: Import the readability helper into the detail page.
- [ ] Step 2: Replace the score-detail formatter to consume the whole row instead of raw `detailJson`.
- [ ] Step 3: Replace the conflict formatter to use the new helper and keep existing seat-position fallback.
- [ ] Step 4: Rename the score-detail table column from `明细` to `说明`.

### Task 3: Verify and document

**Files:**
- Modify: `ROADMAP.md`
- Modify: `docs/project-progress.md`

- [ ] Step 1: Run `node --test "RuoYi-Vue3-master/src/views/seating/plan/__tests__/detailReadableText.test.js"`.
- [ ] Step 2: Run `npm run build:prod` in `RuoYi-Vue3-master`.
- [ ] Step 3: Update roadmap and progress docs with the completed P1 readability subtask and the remaining real-browser verification blocker.