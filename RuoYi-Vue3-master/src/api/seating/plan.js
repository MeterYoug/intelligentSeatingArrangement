import request from '@/utils/request'

// 查询排座方案列表
export function listPlan(query) {
  return request({
    url: '/seating/plan/list',
    method: 'get',
    params: query
  })
}

// 查询排座方案详细
export function getPlan(planId) {
  return request({
    url: '/seating/plan/' + planId,
    method: 'get'
  })
}

// 新增排座方案
export function addPlan(data) {
  return request({
    url: '/seating/plan',
    method: 'post',
    data: data
  })
}

// 智能生成排座方案
export function generatePlan(data) {
  return request({
    url: '/seating/plan/generate',
    method: 'post',
    data: data
  })
}

// 确认座位方案
export function confirmPlan(planId) {
  return request({
    url: '/seating/plan/' + planId + '/confirm',
    method: 'put'
  })
}

// 复制座位方案
export function copyPlan(planId) {
  return request({
    url: '/seating/plan/' + planId + '/copy',
    method: 'post'
  })
}

// 导出当前方案座位表
export function exportSeatTableUrl(planId) {
  return 'seating/plan/' + planId + '/export-seat-table'
}

// 修改排座方案
export function updatePlan(data) {
  return request({
    url: '/seating/plan',
    method: 'put',
    data: data
  })
}

// 删除排座方案
export function delPlan(planId) {
  return request({
    url: '/seating/plan/' + planId,
    method: 'delete'
  })
}
