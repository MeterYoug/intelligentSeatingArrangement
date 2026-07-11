import request from '@/utils/request'

// 查询排座班级列表
export function listClass(query) {
  return request({
    url: '/seating/class/list',
    method: 'get',
    params: query
  })
}

// 查询排座班级详细
export function getClass(classId) {
  return request({
    url: '/seating/class/' + classId,
    method: 'get'
  })
}

// 新学期复制班级
export function copyClassNewTerm(classId, data) {
  return request({
    url: `/seating/class/${classId}/copy-new-term`,
    method: 'post',
    data
  })
}

// 新增排座班级
export function addClass(data) {
  return request({
    url: '/seating/class',
    method: 'post',
    data: data
  })
}

// 修改排座班级
export function updateClass(data) {
  return request({
    url: '/seating/class',
    method: 'put',
    data: data
  })
}

// 删除排座班级
export function delClass(classId) {
  return request({
    url: '/seating/class/' + classId,
    method: 'delete'
  })
}
