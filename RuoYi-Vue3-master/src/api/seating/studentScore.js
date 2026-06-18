import request from '@/utils/request'

export function listStudentScore(query) {
  return request({
    url: '/seating/student-score/list',
    method: 'get',
    params: query
  })
}

export function getStudentScore(scoreId) {
  return request({
    url: '/seating/student-score/' + scoreId,
    method: 'get'
  })
}

export function addStudentScore(data) {
  return request({
    url: '/seating/student-score',
    method: 'post',
    data
  })
}

export function updateStudentScore(data) {
  return request({
    url: '/seating/student-score',
    method: 'put',
    data
  })
}

export function delStudentScore(scoreId) {
  return request({
    url: '/seating/student-score/' + scoreId,
    method: 'delete'
  })
}

export function syncStudentScoreLevel(examId) {
  return request({
    url: '/seating/student-score/exam/' + examId + '/sync-student-level',
    method: 'post'
  })
}
