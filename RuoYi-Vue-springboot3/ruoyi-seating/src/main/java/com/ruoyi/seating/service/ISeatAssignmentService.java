package com.ruoyi.seating.service;

import java.util.List;
import com.ruoyi.seating.domain.SeatAssignment;
import com.ruoyi.seating.domain.SeatAssignmentAdjustResult;

/**
 * 排座分配Service接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface ISeatAssignmentService 
{
    /**
     * 查询排座分配
     * 
     * @param assignmentId 排座分配主键
     * @return 排座分配
     */
    public SeatAssignment selectSeatAssignmentByAssignmentId(Long assignmentId);

    /**
     * 查询排座分配列表
     * 
     * @param seatAssignment 排座分配
     * @return 排座分配集合
     */
    public List<SeatAssignment> selectSeatAssignmentList(SeatAssignment seatAssignment);

    /**
     * 新增排座分配
     * 
     * @param seatAssignment 排座分配
     * @return 结果
     */
    public int insertSeatAssignment(SeatAssignment seatAssignment);

    /**
     * 修改排座分配
     * 
     * @param seatAssignment 排座分配
     * @return 结果
     */
    public int updateSeatAssignment(SeatAssignment seatAssignment);

    /**
     * 批量删除排座分配
     * 
     * @param assignmentIds 需要删除的排座分配主键集合
     * @return 结果
     */
    public int deleteSeatAssignmentByAssignmentIds(Long[] assignmentIds);

    /**
     * 删除排座分配信息
     * 
     * @param assignmentId 排座分配主键
     * @return 结果
     */
    public int deleteSeatAssignmentByAssignmentId(Long assignmentId);

    /**
     * 保存方案座位分配调整
     *
     * @param planId 方案ID
     * @param assignmentList 分配列表
     * @param operName 操作人
     * @return 结果
     */
    public SeatAssignmentAdjustResult savePlanAssignments(Long planId, List<SeatAssignment> assignmentList, String operName);
}
