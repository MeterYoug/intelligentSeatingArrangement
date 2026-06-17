package com.ruoyi.seating.mapper;

import java.util.List;
import com.ruoyi.seating.domain.SeatPosition;

/**
 * 排座座位位置Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface SeatPositionMapper 
{
    /**
     * 查询排座座位位置
     * 
     * @param seatId 排座座位位置主键
     * @return 排座座位位置
     */
    public SeatPosition selectSeatPositionBySeatId(Long seatId);

    /**
     * 查询排座座位位置列表
     * 
     * @param seatPosition 排座座位位置
     * @return 排座座位位置集合
     */
    public List<SeatPosition> selectSeatPositionList(SeatPosition seatPosition);

    /**
     * 新增排座座位位置
     * 
     * @param seatPosition 排座座位位置
     * @return 结果
     */
    public int insertSeatPosition(SeatPosition seatPosition);

    /**
     * 修改排座座位位置
     * 
     * @param seatPosition 排座座位位置
     * @return 结果
     */
    public int updateSeatPosition(SeatPosition seatPosition);

    /**
     * 删除排座座位位置
     * 
     * @param seatId 排座座位位置主键
     * @return 结果
     */
    public int deleteSeatPositionBySeatId(Long seatId);

    /**
     * 批量删除排座座位位置
     * 
     * @param seatIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSeatPositionBySeatIds(Long[] seatIds);

    public int deleteSeatPositionByClassroomId(Long classroomId);
}
