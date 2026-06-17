import request from '@/utils/request'

// 查询排座规则列表
export function listRule(query) {
  return request({
    url: '/seating/rule/list',
    method: 'get',
    params: query
  })
}

// 查询排座规则详细
export function getRule(ruleId) {
  return request({
    url: '/seating/rule/' + ruleId,
    method: 'get'
  })
}

// 新增排座规则
export function addRule(data) {
  return request({
    url: '/seating/rule',
    method: 'post',
    data: data
  })
}

// 修改排座规则
export function updateRule(data) {
  return request({
    url: '/seating/rule',
    method: 'put',
    data: data
  })
}

// 删除排座规则
export function delRule(ruleId) {
  return request({
    url: '/seating/rule/' + ruleId,
    method: 'delete'
  })
}
