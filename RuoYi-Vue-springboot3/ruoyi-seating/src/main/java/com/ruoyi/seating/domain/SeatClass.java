package com.ruoyi.seating.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 排座班级对象 seat_class
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public class SeatClass extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 班级ID */
    private Long classId;

    /** 班级名称 */
    @Excel(name = "班级名称")
    private String className;

    /** 年级名称 */
    @Excel(name = "年级名称")
    private String gradeName;

    /** School stage: PRIMARY/JUNIOR/SENIOR */
    private String schoolStage;

    /** Stable grade code, such as PRIMARY_4 */
    private String gradeCode;

    /** 班级科目快照 JSON */
    private String subjectSnapshot;

    /** 学年 */
    @Excel(name = "学年")
    private String schoolYear;

    /** 学期（1上学期 2下学期） */
    @Excel(name = "学期", readConverterExp = "1=上学期,2=下学期")
    private String semester;

    /** 负责老师ID */
    @Excel(name = "负责老师ID")
    private Long teacherId;

    /** 所属部门ID */
    @Excel(name = "所属部门ID")
    private Long deptId;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setClassId(Long classId) 
    {
        this.classId = classId;
    }

    public Long getClassId() 
    {
        return classId;
    }

    public void setClassName(String className) 
    {
        this.className = className;
    }

    public String getClassName() 
    {
        return className;
    }

    public void setGradeName(String gradeName) 
    {
        this.gradeName = gradeName;
    }

    public String getGradeName() 
    {
        return gradeName;
    }

    public void setSchoolStage(String schoolStage)
    {
        this.schoolStage = schoolStage;
    }

    public String getSchoolStage()
    {
        return schoolStage;
    }

    public void setGradeCode(String gradeCode)
    {
        this.gradeCode = gradeCode;
    }

    public String getGradeCode()
    {
        return gradeCode;
    }

    public void setSubjectSnapshot(String subjectSnapshot)
    {
        this.subjectSnapshot = subjectSnapshot;
    }

    public String getSubjectSnapshot()
    {
        return subjectSnapshot;
    }

    public void setSchoolYear(String schoolYear) 
    {
        this.schoolYear = schoolYear;
    }

    public String getSchoolYear() 
    {
        return schoolYear;
    }

    public void setSemester(String semester) 
    {
        this.semester = semester;
    }

    public String getSemester() 
    {
        return semester;
    }

    public void setTeacherId(Long teacherId) 
    {
        this.teacherId = teacherId;
    }

    public Long getTeacherId() 
    {
        return teacherId;
    }

    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("classId", getClassId())
            .append("className", getClassName())
            .append("gradeName", getGradeName())
            .append("schoolStage", getSchoolStage())
            .append("gradeCode", getGradeCode())
            .append("subjectSnapshot", getSubjectSnapshot())
            .append("schoolYear", getSchoolYear())
            .append("semester", getSemester())
            .append("teacherId", getTeacherId())
            .append("deptId", getDeptId())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
