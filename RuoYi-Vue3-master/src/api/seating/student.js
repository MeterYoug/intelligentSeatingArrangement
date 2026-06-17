import request from '@/utils/request'

// 查询排座学生列表
export function listStudent(query) {
  return request({
    url: '/seating/student/list',
    method: 'get',
    params: query
  })
}

// 查询排座学生详细
export function getStudent(studentId) {
  return request({
    url: '/seating/student/' + studentId,
    method: 'get'
  })
}

// 新增排座学生
export function addStudent(data) {
  return request({
    url: '/seating/student',
    method: 'post',
    data: data
  })
}

// 修改排座学生
export function updateStudent(data) {
  return request({
    url: '/seating/student',
    method: 'put',
    data: data
  })
}

// 删除排座学生
export function delStudent(studentId) {
  return request({
    url: '/seating/student/' + studentId,
    method: 'delete'
  })
}
