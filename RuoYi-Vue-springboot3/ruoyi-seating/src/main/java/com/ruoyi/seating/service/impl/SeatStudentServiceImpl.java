package com.ruoyi.seating.service.impl;

import java.util.List;
import java.util.Set;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.seating.mapper.SeatStudentMapper;
import com.ruoyi.seating.domain.SeatStudent;
import com.ruoyi.seating.domain.SeatStudentImportData;
import com.ruoyi.seating.service.ISeatStudentService;

/**
 * 排座学生Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@Service
public class SeatStudentServiceImpl implements ISeatStudentService 
{
    @Autowired
    private SeatStudentMapper seatStudentMapper;

    /**
     * 查询排座学生
     * 
     * @param studentId 排座学生主键
     * @return 排座学生
     */
    @Override
    public SeatStudent selectSeatStudentByStudentId(Long studentId)
    {
        return seatStudentMapper.selectSeatStudentByStudentId(studentId);
    }

    /**
     * 查询排座学生列表
     * 
     * @param seatStudent 排座学生
     * @return 排座学生
     */
    @Override
    public List<SeatStudent> selectSeatStudentList(SeatStudent seatStudent)
    {
        return seatStudentMapper.selectSeatStudentList(seatStudent);
    }

    @Override
    @Transactional
    public String importStudents(Long classId, List<SeatStudentImportData> studentList,
            boolean updateSupport, String operName)
    {
        if (studentList == null || studentList.isEmpty())
        {
            throw new ServiceException("导入学生数据不能为空");
        }
        int successNum = 0;
        StringBuilder errors = new StringBuilder();
        for (int i = 0; i < studentList.size(); i++)
        {
            SeatStudentImportData row = studentList.get(i);
            int rowNumber = i + 2;
            try
            {
                validateImportRow(row);
                SeatStudent student = buildStudent(classId, row, operName);
                SeatStudent existing = StringUtils.isBlank(student.getStudentNo()) ? null
                        : seatStudentMapper.selectSeatStudentByClassIdAndStudentNo(student);
                if (existing == null)
                {
                    insertSeatStudent(student);
                }
                else if (updateSupport)
                {
                    student.setStudentId(existing.getStudentId());
                    student.setUpdateBy(operName);
                    updateSeatStudent(student);
                }
                else
                {
                    throw new ServiceException("学号已存在");
                }
                successNum++;
            }
            catch (Exception e)
            {
                errors.append("<br/>第 ").append(rowNumber).append(" 行：").append(e.getMessage());
            }
        }
        if (!errors.isEmpty())
        {
            throw new ServiceException("导入失败，数据未写入：" + errors);
        }
        return "导入成功，共 " + successNum + " 条";
    }

    private void validateImportRow(SeatStudentImportData row)
    {
        if (row == null || StringUtils.isBlank(row.getStudentName()))
        {
            throw new ServiceException("学生姓名不能为空");
        }
        if (StringUtils.isNotBlank(row.getGender()) && !Set.of("0", "1", "2").contains(row.getGender()))
        {
            throw new ServiceException("性别必须为男、女或未知");
        }
        if (row.getHeightCm() != null
                && (row.getHeightCm().doubleValue() < 50 || row.getHeightCm().doubleValue() > 250))
        {
            throw new ServiceException("身高必须在 50 至 250 厘米之间");
        }
        if (StringUtils.isNotBlank(row.getVisionLevel())
                && !Set.of("0", "1", "2", "3").contains(row.getVisionLevel()))
        {
            throw new ServiceException("视力等级无效");
        }
        if (StringUtils.isNotBlank(row.getScoreLevel())
                && !Set.of("A", "B", "C", "D").contains(row.getScoreLevel().toUpperCase()))
        {
            throw new ServiceException("成绩等级必须为 A、B、C 或 D");
        }
        if (StringUtils.isNotBlank(row.getDisciplineLevel())
                && !Set.of("0", "1", "2").contains(row.getDisciplineLevel()))
        {
            throw new ServiceException("纪律等级无效");
        }
    }

    private SeatStudent buildStudent(Long classId, SeatStudentImportData row, String operName)
    {
        SeatStudent student = new SeatStudent();
        student.setClassId(classId);
        student.setStudentNo(StringUtils.trim(row.getStudentNo()));
        student.setStudentName(StringUtils.trim(row.getStudentName()));
        student.setGender(StringUtils.defaultIfBlank(row.getGender(), "2"));
        student.setHeightCm(row.getHeightCm());
        student.setVisionLevel(StringUtils.defaultIfBlank(row.getVisionLevel(), "0"));
        student.setScoreLevel(StringUtils.isBlank(row.getScoreLevel()) ? null : row.getScoreLevel().toUpperCase());
        student.setDisciplineLevel(StringUtils.defaultIfBlank(row.getDisciplineLevel(), "0"));
        student.setSpecialNeed(row.getSpecialNeed());
        student.setSortNo(row.getSortNo() == null ? 0L : row.getSortNo());
        student.setStatus("0");
        student.setDelFlag("0");
        student.setCreateBy(operName);
        student.setRemark(row.getRemark());
        return student;
    }

    /**
     * 新增排座学生
     * 
     * @param seatStudent 排座学生
     * @return 结果
     */
    @Override
    public int insertSeatStudent(SeatStudent seatStudent)
    {
        seatStudent.setCreateTime(DateUtils.getNowDate());
        return seatStudentMapper.insertSeatStudent(seatStudent);
    }

    /**
     * 修改排座学生
     * 
     * @param seatStudent 排座学生
     * @return 结果
     */
    @Override
    public int updateSeatStudent(SeatStudent seatStudent)
    {
        seatStudent.setUpdateTime(DateUtils.getNowDate());
        return seatStudentMapper.updateSeatStudent(seatStudent);
    }

    /**
     * 批量删除排座学生
     * 
     * @param studentIds 需要删除的排座学生主键
     * @return 结果
     */
    @Override
    public int deleteSeatStudentByStudentIds(Long[] studentIds)
    {
        return seatStudentMapper.deleteSeatStudentByStudentIds(studentIds);
    }

    /**
     * 删除排座学生信息
     * 
     * @param studentId 排座学生主键
     * @return 结果
     */
    @Override
    public int deleteSeatStudentByStudentId(Long studentId)
    {
        return seatStudentMapper.deleteSeatStudentByStudentId(studentId);
    }
}
