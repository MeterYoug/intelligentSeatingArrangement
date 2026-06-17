package com.ruoyi.seating.service;

import java.util.List;
import com.ruoyi.seating.domain.SeatStudent;
import com.ruoyi.seating.domain.SeatStudentImportData;

/**
 * 排座学生Service接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface ISeatStudentService 
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

    public String importStudents(Long classId, List<SeatStudentImportData> studentList,
            boolean updateSupport, String operName);

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

    /**
     * 批量删除排座学生
     * 
     * @param studentIds 需要删除的排座学生主键集合
     * @return 结果
     */
    public int deleteSeatStudentByStudentIds(Long[] studentIds);

    /**
     * 删除排座学生信息
     * 
     * @param studentId 排座学生主键
     * @return 结果
     */
    public int deleteSeatStudentByStudentId(Long studentId);
}
