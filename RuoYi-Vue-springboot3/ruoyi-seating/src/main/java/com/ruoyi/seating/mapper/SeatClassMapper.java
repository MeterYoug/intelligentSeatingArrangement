package com.ruoyi.seating.mapper;

import java.util.List;
import com.ruoyi.seating.domain.SeatClass;

/**
 * 排座班级Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface SeatClassMapper 
{
    /**
     * 查询排座班级
     * 
     * @param classId 排座班级主键
     * @return 排座班级
     */
    public SeatClass selectSeatClassByClassId(Long classId);

    /**
     * 查询排座班级列表
     * 
     * @param seatClass 排座班级
     * @return 排座班级集合
     */
    public List<SeatClass> selectSeatClassList(SeatClass seatClass);

    /**
     * 新增排座班级
     * 
     * @param seatClass 排座班级
     * @return 结果
     */
    public int insertSeatClass(SeatClass seatClass);

    /**
     * 修改排座班级
     * 
     * @param seatClass 排座班级
     * @return 结果
     */
    public int updateSeatClass(SeatClass seatClass);

    /**
     * 删除排座班级
     * 
     * @param classId 排座班级主键
     * @return 结果
     */
    public int deleteSeatClassByClassId(Long classId);

    /**
     * 批量删除排座班级
     * 
     * @param classIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSeatClassByClassIds(Long[] classIds);
}
