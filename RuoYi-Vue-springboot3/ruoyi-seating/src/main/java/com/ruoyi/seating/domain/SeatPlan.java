package com.ruoyi.seating.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 排座方案对象 seat_plan
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public class SeatPlan extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 方案ID */
    private Long planId;

    /** 班级ID */
    @Excel(name = "班级ID")
    private Long classId;

    /** 班级名称 */
    private String className;

    /** 教室布局ID */
    @Excel(name = "教室布局ID")
    private Long classroomId;

    /** 教室布局名称 */
    private String classroomName;

    /** 方案名称 */
    @Excel(name = "方案名称")
    private String planName;

    /** 方案类型（AUTO自动 MANUAL手动） */
    @Excel(name = "方案类型", readConverterExp = "A=UTO自动,M=ANUAL手动")
    private String planType;

    /** 方案状态（DRAFT草稿 ACTIVE启用 ARCHIVED归档） */
    @Excel(name = "方案状态", readConverterExp = "D=RAFT草稿,A=CTIVE启用,A=RCHIVED归档")
    private String planStatus;

    /** 方案总评分 */
    @Excel(name = "方案总评分")
    private BigDecimal totalScore;

    /** 生成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date generatedAt;

    /** 启用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "启用时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date activeTime;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

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

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassroomId(Long classroomId) 
    {
        this.classroomId = classroomId;
    }

    public Long getClassroomId() 
    {
        return classroomId;
    }

    public void setClassroomName(String classroomName)
    {
        this.classroomName = classroomName;
    }

    public String getClassroomName()
    {
        return classroomName;
    }

    public void setPlanName(String planName) 
    {
        this.planName = planName;
    }

    public String getPlanName() 
    {
        return planName;
    }

    public void setPlanType(String planType) 
    {
        this.planType = planType;
    }

    public String getPlanType() 
    {
        return planType;
    }

    public void setPlanStatus(String planStatus) 
    {
        this.planStatus = planStatus;
    }

    public String getPlanStatus() 
    {
        return planStatus;
    }

    public void setTotalScore(BigDecimal totalScore) 
    {
        this.totalScore = totalScore;
    }

    public BigDecimal getTotalScore() 
    {
        return totalScore;
    }

    public void setGeneratedAt(Date generatedAt) 
    {
        this.generatedAt = generatedAt;
    }

    public Date getGeneratedAt() 
    {
        return generatedAt;
    }

    public void setActiveTime(Date activeTime) 
    {
        this.activeTime = activeTime;
    }

    public Date getActiveTime() 
    {
        return activeTime;
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
            .append("planId", getPlanId())
            .append("classId", getClassId())
            .append("className", getClassName())
            .append("classroomId", getClassroomId())
            .append("classroomName", getClassroomName())
            .append("planName", getPlanName())
            .append("planType", getPlanType())
            .append("planStatus", getPlanStatus())
            .append("totalScore", getTotalScore())
            .append("generatedAt", getGeneratedAt())
            .append("activeTime", getActiveTime())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
