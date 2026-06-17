import request from '@/utils/request'

// 查询排座学生关系约束列表
export function listRelation(query) {
  return request({
    url: '/seating/relation/list',
    method: 'get',
    params: query
  })
}

// 查询排座学生关系约束详细
export function getRelation(relationId) {
  return request({
    url: '/seating/relation/' + relationId,
    method: 'get'
  })
}

// 新增排座学生关系约束
export function addRelation(data) {
  return request({
    url: '/seating/relation',
    method: 'post',
    data: data
  })
}

// 修改排座学生关系约束
export function updateRelation(data) {
  return request({
    url: '/seating/relation',
    method: 'put',
    data: data
  })
}

// 删除排座学生关系约束
export function delRelation(relationId) {
  return request({
    url: '/seating/relation/' + relationId,
    method: 'delete'
  })
}
