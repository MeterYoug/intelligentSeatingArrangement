import request from '@/utils/request'

// 查询排座方案评分明细列表
export function listScore(query) {
  return request({
    url: '/seating/score/list',
    method: 'get',
    params: query
  })
}

// 查询排座方案评分明细详细
export function getScore(scoreId) {
  return request({
    url: '/seating/score/' + scoreId,
    method: 'get'
  })
}

// 新增排座方案评分明细
export function addScore(data) {
  return request({
    url: '/seating/score',
    method: 'post',
    data: data
  })
}

// 修改排座方案评分明细
export function updateScore(data) {
  return request({
    url: '/seating/score',
    method: 'put',
    data: data
  })
}

// 删除排座方案评分明细
export function delScore(scoreId) {
  return request({
    url: '/seating/score/' + scoreId,
    method: 'delete'
  })
}
