package com.ruoyi.seating.mapper;

import java.util.List;
import com.ruoyi.seating.domain.SeatClassroom;

/**
 * 排座教室布局Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface SeatClassroomMapper 
{
    /**
     * 查询排座教室布局
     * 
     * @param classroomId 排座教室布局主键
     * @return 排座教室布局
     */
    public SeatClassroom selectSeatClassroomByClassroomId(Long classroomId);

    /**
     * 查询排座教室布局列表
     * 
     * @param seatClassroom 排座教室布局
     * @return 排座教室布局集合
     */
    public List<SeatClassroom> selectSeatClassroomList(SeatClassroom seatClassroom);

    /**
     * 新增排座教室布局
     * 
     * @param seatClassroom 排座教室布局
     * @return 结果
     */
    public int insertSeatClassroom(SeatClassroom seatClassroom);

    /**
     * 修改排座教室布局
     * 
     * @param seatClassroom 排座教室布局
     * @return 结果
     */
    public int updateSeatClassroom(SeatClassroom seatClassroom);

    /**
     * 删除排座教室布局
     * 
     * @param classroomId 排座教室布局主键
     * @return 结果
     */
    public int deleteSeatClassroomByClassroomId(Long classroomId);

    /**
     * 批量删除排座教室布局
     * 
     * @param classroomIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSeatClassroomByClassroomIds(Long[] classroomIds);
}
