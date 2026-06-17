package com.ruoyi.seating.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 排座分配对象 seat_assignment
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public class SeatAssignment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 分配ID */
    private Long assignmentId;

    /** 方案ID */
    @Excel(name = "方案ID")
    private Long planId;

    /** 班级ID */
    @Excel(name = "班级ID")
    private Long classId;

    /** 教室布局ID */
    @Excel(name = "教室布局ID")
    private Long classroomId;

    /** 座位ID */
    @Excel(name = "座位ID")
    private Long seatId;

    /** 学生ID */
    @Excel(name = "学生ID")
    private Long studentId;

    /** 学生姓名快照 */
    @Excel(name = "学生姓名快照")
    private String studentNameSnapshot;

    /** 行号快照 */
    @Excel(name = "行号快照")
    private Long rowIndex;

    /** 列号快照 */
    @Excel(name = "列号快照")
    private Long colIndex;

    /** 是否锁定（0否 1是） */
    @Excel(name = "是否锁定", readConverterExp = "0=否,1=是")
    private String isLocked;

    /** 分配来源（AUTO自动 MANUAL手动） */
    @Excel(name = "分配来源", readConverterExp = "A=UTO自动,M=ANUAL手动")
    private String assignSource;

    public void setAssignmentId(Long assignmentId) 
    {
        this.assignmentId = assignmentId;
    }

    public Long getAssignmentId() 
    {
        return assignmentId;
    }

    public void setPlanId(Long planId) 
    {
        this.planId = planId;
    }

    public Long getPlanId() 
    {
        return planId;
    }

    public void setClassId(Long classId) 
    {
        this.classId = classId;
    }

    public Long getClassId() 
    {
        return classId;
    }

    public void setClassroomId(Long classroomId) 
    {
        this.classroomId = classroomId;
    }

    public Long getClassroomId() 
    {
        return classroomId;
    }

    public void setSeatId(Long seatId) 
    {
        this.seatId = seatId;
    }

    public Long getSeatId() 
    {
        return seatId;
    }

    public void setStudentId(Long studentId) 
    {
        this.studentId = studentId;
    }

    public Long getStudentId() 
    {
        return studentId;
    }

    public void setStudentNameSnapshot(String studentNameSnapshot) 
    {
        this.studentNameSnapshot = studentNameSnapshot;
    }

    public String getStudentNameSnapshot() 
    {
        return studentNameSnapshot;
    }

    public void setRowIndex(Long rowIndex) 
    {
        this.rowIndex = rowIndex;
    }

    public Long getRowIndex() 
    {
        return rowIndex;
    }

    public void setColIndex(Long colIndex) 
    {
        this.colIndex = colIndex;
    }

    public Long getColIndex() 
    {
        return colIndex;
    }

    public void setIsLocked(String isLocked) 
    {
        this.isLocked = isLocked;
    }

    public String getIsLocked() 
    {
        return isLocked;
    }

    public void setAssignSource(String assignSource) 
    {
        this.assignSource = assignSource;
    }

    public String getAssignSource() 
    {
        return assignSource;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("assignmentId", getAssignmentId())
            .append("planId", getPlanId())
            .append("classId", getClassId())
            .append("classroomId", getClassroomId())
            .append("seatId", getSeatId())
            .append("studentId", getStudentId())
            .append("studentNameSnapshot", getStudentNameSnapshot())
            .append("rowIndex", getRowIndex())
            .append("colIndex", getColIndex())
            .append("isLocked", getIsLocked())
            .append("assignSource", getAssignSource())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
