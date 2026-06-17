import request from '@/utils/request'

// 查询排座教室布局列表
export function listClassroom(query) {
  return request({
    url: '/seating/classroom/list',
    method: 'get',
    params: query
  })
}

// 查询排座教室布局详细
export function getClassroom(classroomId) {
  return request({
    url: '/seating/classroom/' + classroomId,
    method: 'get'
  })
}

// 新增排座教室布局
export function addClassroom(data) {
  return request({
    url: '/seating/classroom',
    method: 'post',
    data: data
  })
}

// 修改排座教室布局
export function updateClassroom(data) {
  return request({
    url: '/seating/classroom',
    method: 'put',
    data: data
  })
}

// 删除排座教室布局
export function delClassroom(classroomId) {
  return request({
    url: '/seating/classroom/' + classroomId,
    method: 'delete'
  })
}

export function initializeClassroom(classroomId) {
  return request({
    url: '/seating/classroom/' + classroomId + '/initialize',
    method: 'post'
  })
}
