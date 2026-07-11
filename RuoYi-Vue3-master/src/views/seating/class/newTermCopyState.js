export function normalizeSemester(semester) {
  const value = String(semester ?? '').trim()
  if (value === '1' || value === '上学期') {
    return '1'
  }
  if (value === '2' || value === '下学期') {
    return '2'
  }
  return value
}

export function nextSemester(semester) {
  return normalizeSemester(semester) === '1' ? '2' : '1'
}

export function nextSchoolYear(schoolYear, semester) {
  const value = String(schoolYear ?? '').trim()
  if (!value) {
    return ''
  }
  if (normalizeSemester(semester) !== '2') {
    return value
  }
  const parts = value.split('-')
  if (parts.length !== 2) {
    return value
  }
  const startYear = Number.parseInt(parts[0].trim(), 10)
  if (Number.isNaN(startYear)) {
    return value
  }
  return `${startYear + 1}-${startYear + 2}`
}

export function createNewTermCopyForm(source = {}) {
  const sourceSemester = normalizeSemester(source.semester)
  return {
    sourceClassId: source.classId ?? null,
    sourceClassName: source.className ?? '',
    sourceSchoolYear: source.schoolYear ?? '',
    sourceSemester,
    className: '',
    schoolYear: nextSchoolYear(source.schoolYear ?? '', sourceSemester),
    semester: nextSemester(sourceSemester),
    copyStudents: true,
    copyRelations: true,
    copyRules: true,
    copyClassroomLayout: true,
  }
}

export function normalizeCopyRelations(form) {
  if (!form.copyStudents) {
    form.copyRelations = false
  }
  return form
}

export function validateNewTermCopyForm(form) {
  if (!String(form.className ?? '').trim()) {
    return '目标班级名称不能为空'
  }
  if (!String(form.schoolYear ?? '').trim()) {
    return '目标学年不能为空'
  }
  if (!String(form.semester ?? '').trim()) {
    return '目标学期不能为空'
  }
  return ''
}
