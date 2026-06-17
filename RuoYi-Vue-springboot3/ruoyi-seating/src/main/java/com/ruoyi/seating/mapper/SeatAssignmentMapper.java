package com.ruoyi.seating.mapper;

import java.util.List;
import com.ruoyi.seating.domain.SeatAssignment;

/**
 * 排座分配Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface SeatAssignmentMapper 
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
     * 删除排座分配
     * 
     * @param assignmentId 排座分配主键
     * @return 结果
     */
    public int deleteSeatAssignmentByAssignmentId(Long assignmentId);

    /**
     * 批量删除排座分配
     * 
     * @param assignmentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSeatAssignmentByAssignmentIds(Long[] assignmentIds);
}
