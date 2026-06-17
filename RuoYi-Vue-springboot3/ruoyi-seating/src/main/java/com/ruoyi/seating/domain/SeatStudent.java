package com.ruoyi.seating.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 排座学生对象 seat_student
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public class SeatStudent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 学生ID */
    private Long studentId;

    /** 班级ID */
    @Excel(name = "班级ID")
    private Long classId;

    /** 班级名称 */
    private String className;

    /** 学号 */
    @Excel(name = "学号")
    private String studentNo;

    /** 学生姓名 */
    @Excel(name = "学生姓名")
    private String studentName;

    /** 性别（0男 1女 2未知） */
    @Excel(name = "性别", readConverterExp = "0=男,1=女,2=未知")
    private String gender;

    /** 身高厘米 */
    @Excel(name = "身高厘米")
    private BigDecimal heightCm;

    /** 视力等级（0正常 1轻度 2中度 3重度） */
    @Excel(name = "视力等级", readConverterExp = "0=正常,1=轻度,2=中度,3=重度")
    private String visionLevel;

    /** 成绩等级（A/B/C/D） */
    @Excel(name = "成绩等级", readConverterExp = "A=/B/C/D")
    private String scoreLevel;

    /** 纪律等级（0正常 1关注 2重点关注） */
    @Excel(name = "纪律等级", readConverterExp = "0=正常,1=关注,2=重点关注")
    private String disciplineLevel;

    /** 特殊需求 */
    @Excel(name = "特殊需求")
    private String specialNeed;

    /** 排序号 */
    @Excel(name = "排序号")
    private Long sortNo;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setStudentId(Long studentId) 
    {
        this.studentId = studentId;
    }

    public Long getStudentId() 
    {
        return studentId;
    }

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

    public void setStudentNo(String studentNo) 
    {
        this.studentNo = studentNo;
    }

    public String getStudentNo() 
    {
        return studentNo;
    }

    public void setStudentName(String studentName) 
    {
        this.studentName = studentName;
    }

    public String getStudentName() 
    {
        return studentName;
    }

    public void setGender(String gender) 
    {
        this.gender = gender;
    }

    public String getGender() 
    {
        return gender;
    }

    public void setHeightCm(BigDecimal heightCm) 
    {
        this.heightCm = heightCm;
    }

    public BigDecimal getHeightCm() 
    {
        return heightCm;
    }

    public void setVisionLevel(String visionLevel) 
    {
        this.visionLevel = visionLevel;
    }

    public String getVisionLevel() 
    {
        return visionLevel;
    }

    public void setScoreLevel(String scoreLevel) 
    {
        this.scoreLevel = scoreLevel;
    }

    public String getScoreLevel() 
    {
        return scoreLevel;
    }

    public void setDisciplineLevel(String disciplineLevel) 
    {
        this.disciplineLevel = disciplineLevel;
    }

    public String getDisciplineLevel() 
    {
        return disciplineLevel;
    }

    public void setSpecialNeed(String specialNeed) 
    {
        this.specialNeed = specialNeed;
    }

    public String getSpecialNeed() 
    {
        return specialNeed;
    }

    public void setSortNo(Long sortNo) 
    {
        this.sortNo = sortNo;
    }

    public Long getSortNo() 
    {
        return sortNo;
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
            .append("studentId", getStudentId())
            .append("classId", getClassId())
            .append("className", getClassName())
            .append("studentNo", getStudentNo())
            .append("studentName", getStudentName())
            .append("gender", getGender())
            .append("heightCm", getHeightCm())
            .append("visionLevel", getVisionLevel())
            .append("scoreLevel", getScoreLevel())
            .append("disciplineLevel", getDisciplineLevel())
            .append("specialNeed", getSpecialNeed())
            .append("sortNo", getSortNo())
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
