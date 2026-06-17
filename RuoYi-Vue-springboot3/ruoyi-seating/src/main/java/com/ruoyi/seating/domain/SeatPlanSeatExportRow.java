package com.ruoyi.seating.domain;

import com.ruoyi.common.annotation.Excel;

/**
 * 座位方案座位表导出行。
 */
public class SeatPlanSeatExportRow
{
    @Excel(name = "方案名称")
    private String planName;

    @Excel(name = "班级")
    private String className;

    @Excel(name = "教室布局")
    private String classroomName;

    @Excel(name = "行号")
    private Long rowIndex;

    @Excel(name = "列号")
    private Long colIndex;

    @Excel(name = "座位编号")
    private String seatCode;

    @Excel(name = "座位状态")
    private String seatStatus;

    @Excel(name = "学生姓名")
    private String studentName;

    @Excel(name = "是否锁定")
    private String lockedText;

    @Excel(name = "分配来源")
    private String assignSourceText;

    public String getPlanName()
    {
        return planName;
    }

    public void setPlanName(String planName)
    {
        this.planName = planName;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getClassroomName()
    {
        return classroomName;
    }

    public void setClassroomName(String classroomName)
    {
        this.classroomName = classroomName;
    }

    public Long getRowIndex()
    {
        return rowIndex;
    }

    public void setRowIndex(Long rowIndex)
    {
        this.rowIndex = rowIndex;
    }

    public Long getColIndex()
    {
        return colIndex;
    }

    public void setColIndex(Long colIndex)
    {
        this.colIndex = colIndex;
    }

    public String getSeatCode()
    {
        return seatCode;
    }

    public void setSeatCode(String seatCode)
    {
        this.seatCode = seatCode;
    }

    public String getSeatStatus()
    {
        return seatStatus;
    }

    public void setSeatStatus(String seatStatus)
    {
        this.seatStatus = seatStatus;
    }

    public String getStudentName()
    {
        return studentName;
    }

    public void setStudentName(String studentName)
    {
        this.studentName = studentName;
    }

    public String getLockedText()
    {
        return lockedText;
    }

    public void setLockedText(String lockedText)
    {
        this.lockedText = lockedText;
    }

    public String getAssignSourceText()
    {
        return assignSourceText;
    }

    public void setAssignSourceText(String assignSourceText)
    {
        this.assignSourceText = assignSourceText;
    }
}
