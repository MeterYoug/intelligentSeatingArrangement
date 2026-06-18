import request from '@/utils/request'

export function listExam(query) {
  return request({
    url: '/seating/exam/list',
    method: 'get',
    params: query
  })
}

export function getExam(examId) {
  return request({
    url: '/seating/exam/' + examId,
    method: 'get'
  })
}

export function addExam(data) {
  return request({
    url: '/seating/exam',
    method: 'post',
    data
  })
}

export function updateExam(data) {
  return request({
    url: '/seating/exam',
    method: 'put',
    data
  })
}

export function delExam(examId) {
  return request({
    url: '/seating/exam/' + examId,
    method: 'delete'
  })
}

export function setCurrentExam(examId) {
  return request({
    url: '/seating/exam/' + examId + '/current',
    method: 'put'
  })
}
