package com.ruoyi.seating.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Subject template for score imports.
 */
public class SeatSubject extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long subjectId;
    private String schoolStage;
    private String gradeCode;
    private String subjectCode;
    private String subjectName;
    private Long sortOrder;
    private String isRequired;
    private String status;

    public Long getSubjectId()
    {
        return subjectId;
    }

    public void setSubjectId(Long subjectId)
    {
        this.subjectId = subjectId;
    }

    public String getSchoolStage()
    {
        return schoolStage;
    }

    public void setSchoolStage(String schoolStage)
    {
        this.schoolStage = schoolStage;
    }

    public String getGradeCode()
    {
        return gradeCode;
    }

    public void setGradeCode(String gradeCode)
    {
        this.gradeCode = gradeCode;
    }

    public String getSubjectCode()
    {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode)
    {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName()
    {
        return subjectName;
    }

    public void setSubjectName(String subjectName)
    {
        this.subjectName = subjectName;
    }

    public Long getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Long sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    public String getIsRequired()
    {
        return isRequired;
    }

    public void setIsRequired(String isRequired)
    {
        this.isRequired = isRequired;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("subjectId", getSubjectId())
            .append("schoolStage", getSchoolStage())
            .append("gradeCode", getGradeCode())
            .append("subjectCode", getSubjectCode())
            .append("subjectName", getSubjectName())
            .append("sortOrder", getSortOrder())
            .append("isRequired", getIsRequired())
            .append("status", getStatus())
            .append("remark", getRemark())
            .toString();
    }
}
