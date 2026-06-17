package com.ruoyi.seating.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.seating.mapper.SeatStudentRelationMapper;
import com.ruoyi.seating.domain.SeatStudentRelation;
import com.ruoyi.seating.service.ISeatStudentRelationService;

/**
 * 排座学生关系约束Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@Service
public class SeatStudentRelationServiceImpl implements ISeatStudentRelationService 
{
    @Autowired
    private SeatStudentRelationMapper seatStudentRelationMapper;

    /**
     * 查询排座学生关系约束
     * 
     * @param relationId 排座学生关系约束主键
     * @return 排座学生关系约束
     */
    @Override
    public SeatStudentRelation selectSeatStudentRelationByRelationId(Long relationId)
    {
        return seatStudentRelationMapper.selectSeatStudentRelationByRelationId(relationId);
    }

    /**
     * 查询排座学生关系约束列表
     * 
     * @param seatStudentRelation 排座学生关系约束
     * @return 排座学生关系约束
     */
    @Override
    public List<SeatStudentRelation> selectSeatStudentRelationList(SeatStudentRelation seatStudentRelation)
    {
        return seatStudentRelationMapper.selectSeatStudentRelationList(seatStudentRelation);
    }

    /**
     * 新增排座学生关系约束
     * 
     * @param seatStudentRelation 排座学生关系约束
     * @return 结果
     */
    @Override
    public int insertSeatStudentRelation(SeatStudentRelation seatStudentRelation)
    {
        seatStudentRelation.setCreateTime(DateUtils.getNowDate());
        return seatStudentRelationMapper.insertSeatStudentRelation(seatStudentRelation);
    }

    /**
     * 修改排座学生关系约束
     * 
     * @param seatStudentRelation 排座学生关系约束
     * @return 结果
     */
    @Override
    public int updateSeatStudentRelation(SeatStudentRelation seatStudentRelation)
    {
        seatStudentRelation.setUpdateTime(DateUtils.getNowDate());
        return seatStudentRelationMapper.updateSeatStudentRelation(seatStudentRelation);
    }

    /**
     * 批量删除排座学生关系约束
     * 
     * @param relationIds 需要删除的排座学生关系约束主键
     * @return 结果
     */
    @Override
    public int deleteSeatStudentRelationByRelationIds(Long[] relationIds)
    {
        return seatStudentRelationMapper.deleteSeatStudentRelationByRelationIds(relationIds);
    }

    /**
     * 删除排座学生关系约束信息
     * 
     * @param relationId 排座学生关系约束主键
     * @return 结果
     */
    @Override
    public int deleteSeatStudentRelationByRelationId(Long relationId)
    {
        return seatStudentRelationMapper.deleteSeatStudentRelationByRelationId(relationId);
    }
}
