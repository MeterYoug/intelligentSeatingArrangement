import request from '@/utils/request'

// 查询排座分配列表
export function listAssignment(query) {
  return request({
    url: '/seating/assignment/list',
    method: 'get',
    params: query
  })
}

// 查询排座分配详细
export function getAssignment(assignmentId) {
  return request({
    url: '/seating/assignment/' + assignmentId,
    method: 'get'
  })
}

// 新增排座分配
export function addAssignment(data) {
  return request({
    url: '/seating/assignment',
    method: 'post',
    data: data
  })
}

// 修改排座分配
export function updateAssignment(data) {
  return request({
    url: '/seating/assignment',
    method: 'put',
    data: data
  })
}

// 保存方案座位分配调整
export function savePlanAssignments(planId, data) {
  return request({
    url: '/seating/assignment/plan/' + planId,
    method: 'put',
    data: data
  })
}

// 删除排座分配
export function delAssignment(assignmentId) {
  return request({
    url: '/seating/assignment/' + assignmentId,
    method: 'delete'
  })
}
