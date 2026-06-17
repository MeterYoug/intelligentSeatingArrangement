package com.ruoyi.seating.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 排座学生关系约束对象 seat_student_relation
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public class SeatStudentRelation extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 关系ID */
    private Long relationId;

    /** 班级ID */
    @Excel(name = "班级ID")
    private Long classId;

    /** 学生ID */
    @Excel(name = "学生ID")
    private Long studentId;

    /** 关联学生ID */
    @Excel(name = "关联学生ID")
    private Long relatedId;

    /** 关系类型（NOT_DESKMATE/NOT_ADJACENT/PREFER_DESKMATE） */
    @Excel(name = "关系类型", readConverterExp = "N=OT_DESKMATE/NOT_ADJACENT/PREFER_DESKMATE")
    private String relationType;

    /** 关系权重 */
    @Excel(name = "关系权重")
    private Long relationWeight;

    /** 是否启用（0否 1是） */
    @Excel(name = "是否启用", readConverterExp = "0=否,1=是")
    private String enabled;

    public void setRelationId(Long relationId) 
    {
        this.relationId = relationId;
    }

    public Long getRelationId() 
    {
        return relationId;
    }

    public void setClassId(Long classId) 
    {
        this.classId = classId;
    }

    public Long getClassId() 
    {
        return classId;
    }

    public void setStudentId(Long studentId) 
    {
        this.studentId = studentId;
    }

    public Long getStudentId() 
    {
        return studentId;
    }

    public void setRelatedId(Long relatedId) 
    {
        this.relatedId = relatedId;
    }

    public Long getRelatedId() 
    {
        return relatedId;
    }

    public void setRelationType(String relationType) 
    {
        this.relationType = relationType;
    }

    public String getRelationType() 
    {
        return relationType;
    }

    public void setRelationWeight(Long relationWeight) 
    {
        this.relationWeight = relationWeight;
    }

    public Long getRelationWeight() 
    {
        return relationWeight;
    }

    public void setEnabled(String enabled) 
    {
        this.enabled = enabled;
    }

    public String getEnabled() 
    {
        return enabled;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("relationId", getRelationId())
            .append("classId", getClassId())
            .append("studentId", getStudentId())
            .append("relatedId", getRelatedId())
            .append("relationType", getRelationType())
            .append("relationWeight", getRelationWeight())
            .append("enabled", getEnabled())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
