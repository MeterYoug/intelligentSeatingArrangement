package com.ruoyi.seating.mapper;

import java.util.List;
import com.ruoyi.seating.domain.SeatStudentRelation;

/**
 * 排座学生关系约束Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface SeatStudentRelationMapper 
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
     * 删除排座学生关系约束
     * 
     * @param relationId 排座学生关系约束主键
     * @return 结果
     */
    public int deleteSeatStudentRelationByRelationId(Long relationId);

    /**
     * 批量删除排座学生关系约束
     * 
     * @param relationIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSeatStudentRelationByRelationIds(Long[] relationIds);
}
