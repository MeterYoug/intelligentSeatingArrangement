package com.ruoyi.seating.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Exam batch for student scores.
 */
public class SeatExam extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long examId;

    @Excel(name = "班级ID")
    private Long classId;

    private String className;

    @Excel(name = "考试名称")
    private String examName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "考试日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date examDate;

    private String schoolStageSnapshot;
    private String gradeCodeSnapshot;
    private String gradeNameSnapshot;
    private String subjectSnapshot;
    private String isCurrent;
    private String status;
    private String delFlag;

    public Long getExamId()
    {
        return examId;
    }

    public void setExamId(Long examId)
    {
        this.examId = examId;
    }

    public Long getClassId()
    {
        return classId;
    }

    public void setClassId(Long classId)
    {
        this.classId = classId;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getExamName()
    {
        return examName;
    }

    public void setExamName(String examName)
    {
        this.examName = examName;
    }

    public Date getExamDate()
    {
        return examDate;
    }

    public void setExamDate(Date examDate)
    {
        this.examDate = examDate;
    }

    public String getSchoolStageSnapshot()
    {
        return schoolStageSnapshot;
    }

    public void setSchoolStageSnapshot(String schoolStageSnapshot)
    {
        this.schoolStageSnapshot = schoolStageSnapshot;
    }

    public String getGradeCodeSnapshot()
    {
        return gradeCodeSnapshot;
    }

    public void setGradeCodeSnapshot(String gradeCodeSnapshot)
    {
        this.gradeCodeSnapshot = gradeCodeSnapshot;
    }

    public String getGradeNameSnapshot()
    {
        return gradeNameSnapshot;
    }

    public void setGradeNameSnapshot(String gradeNameSnapshot)
    {
        this.gradeNameSnapshot = gradeNameSnapshot;
    }

    public String getSubjectSnapshot()
    {
        return subjectSnapshot;
    }

    public void setSubjectSnapshot(String subjectSnapshot)
    {
        this.subjectSnapshot = subjectSnapshot;
    }

    public String getIsCurrent()
    {
        return isCurrent;
    }

    public void setIsCurrent(String isCurrent)
    {
        this.isCurrent = isCurrent;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }

    @Override
    public String toString()
    {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("examId", getExamId())
            .append("classId", getClassId())
            .append("className", getClassName())
            .append("examName", getExamName())
            .append("examDate", getExamDate())
            .append("schoolStageSnapshot", getSchoolStageSnapshot())
            .append("gradeCodeSnapshot", getGradeCodeSnapshot())
            .append("gradeNameSnapshot", getGradeNameSnapshot())
            .append("subjectSnapshot", getSubjectSnapshot())
            .append("isCurrent", getIsCurrent())
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
