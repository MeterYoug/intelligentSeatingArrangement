package com.ruoyi.seating.service;

import java.util.List;
import com.ruoyi.seating.domain.SeatStudentRelation;

/**
 * 排座学生关系约束Service接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface ISeatStudentRelationService 
{
    /**
     * 查询排座学生关系约束
     * 
     * @param relationId 排座学生关系约束主键
     * @return 排座学生关系约束
     */
    public SeatStudentRelation selectSeatStudentRelationByRelationId(Long relationId);

    /**
     * 查询排座学生关系约束列表
     * 
     * @param seatStudentRelation 排座学生关系约束
     * @return 排座学生关系约束集合
     */
    public List<SeatStudentRelation> selectSeatStudentRelationList(SeatStudentRelation seatStudentRelation);

    /**
     * 新增排座学生关系约束
     * 
     * @param seatStudentRelation 排座学生关系约束
     * @return 结果
     */
    public int insertSeatStudentRelation(SeatStudentRelation seatStudentRelation);

    /**
     * 修改排座学生关系约束
     * 
     * @param seatStudentRelation 排座学生关系约束
     * @return 结果
     */
    public int updateSeatStudentRelation(SeatStudentRelation seatStudentRelation);

    /**
     * 批量删除排座学生关系约束
     * 
     * @param relationIds 需要删除的排座学生关系约束主键集合
     * @return 结果
     */
    public int deleteSeatStudentRelationByRelationIds(Long[] relationIds);

    /**
     * 删除排座学生关系约束信息
     * 
     * @param relationId 排座学生关系约束主键
     * @return 结果
     */
    public int deleteSeatStudentRelationByRelationId(Long relationId);
}
