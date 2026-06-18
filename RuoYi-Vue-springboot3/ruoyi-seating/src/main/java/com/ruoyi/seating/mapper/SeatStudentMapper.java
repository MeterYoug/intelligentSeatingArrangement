package com.ruoyi.seating.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.seating.domain.SeatStudent;

/**
 * 排座学生Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface SeatStudentMapper 
{
    /**
     * 查询排座学生
     * 
     * @param studentId 排座学生主键
     * @return 排座学生
     */
    public SeatStudent selectSeatStudentByStudentId(Long studentId);

    /**
     * 查询排座学生列表
     * 
     * @param seatStudent 排座学生
     * @return 排座学生集合
     */
    public List<SeatStudent> selectSeatStudentList(SeatStudent seatStudent);

    public SeatStudent selectSeatStudentByClassIdAndStudentNo(SeatStudent seatStudent);

    /**
     * 新增排座学生
     * 
     * @param seatStudent 排座学生
     * @return 结果
     */
    public int insertSeatStudent(SeatStudent seatStudent);

    /**
     * 修改排座学生
     * 
     * @param seatStudent 排座学生
     * @return 结果
     */
    public int updateSeatStudent(SeatStudent seatStudent);

    public int updateStudentScoreLevel(@Param("studentId") Long studentId,
            @Param("scoreLevel") String scoreLevel,
            @Param("updateBy") String updateBy);

    /**
     * 删除排座学生
     * 
     * @param studentId 排座学生主键
     * @return 结果
     */
    public int deleteSeatStudentByStudentId(Long studentId);

    /**
     * 批量删除排座学生
     * 
     * @param studentIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSeatStudentByStudentIds(Long[] studentIds);
}
