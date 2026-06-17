import request from '@/utils/request'

// 查询排座座位位置列表
export function listPosition(query) {
  return request({
    url: '/seating/position/list',
    method: 'get',
    params: query
  })
}

// 查询排座座位位置详细
export function getPosition(seatId) {
  return request({
    url: '/seating/position/' + seatId,
    method: 'get'
  })
}

// 新增排座座位位置
export function addPosition(data) {
  return request({
    url: '/seating/position',
    method: 'post',
    data: data
  })
}

// 修改排座座位位置
export function updatePosition(data) {
  return request({
    url: '/seating/position',
    method: 'put',
    data: data
  })
}

// 删除排座座位位置
export function delPosition(seatId) {
  return request({
    url: '/seating/position/' + seatId,
    method: 'delete'
  })
}

// 查询教室座位布局
export function getClassroomLayout(classroomId) {
  return request({
    url: '/seating/position/classroom/' + classroomId + '/layout',
    method: 'get'
  })
}

// 保存教室座位布局
export function saveClassroomLayout(classroomId, data) {
  return request({
    url: '/seating/position/classroom/' + classroomId + '/layout',
    method: 'put',
    data: data
  })
}
