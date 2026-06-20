package com.ruoyi.seating.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * Student score record.
 */
public class SeatStudentScore extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long scoreId;
    private Long examId;
    private Long classId;
    private String className;
    private Long studentId;

    @Excel(name = "学号")
    private String studentNo;

    @Excel(name = "学生姓名")
    private String studentNameSnapshot;

    private String subjectScores;

    @Excel(name = "总分")
    private BigDecimal totalScore;

    @Excel(name = "班级排名")
    private Long classRank;

    @Excel(name = "成绩等级")
    private String scoreLevel;

    private String studentNoOrder;

    private String delFlag;

    public Long getScoreId()
    {
        return scoreId;
    }

    public void setScoreId(Long scoreId)
    {
        this.scoreId = scoreId;
    }

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

    public Long getStudentId()
    {
        return studentId;
    }

    public void setStudentId(Long studentId)
    {
        this.studentId = studentId;
    }

    public String getStudentNo()
    {
        return studentNo;
    }

    public void setStudentNo(String studentNo)
    {
        this.studentNo = studentNo;
    }

    public String getStudentNameSnapshot()
    {
        return studentNameSnapshot;
    }

    public void setStudentNameSnapshot(String studentNameSnapshot)
    {
        this.studentNameSnapshot = studentNameSnapshot;
    }

    public String getSubjectScores()
    {
        return subjectScores;
    }

    public void setSubjectScores(String subjectScores)
    {
        this.subjectScores = subjectScores;
    }

    public BigDecimal getTotalScore()
    {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore)
    {
        this.totalScore = totalScore;
    }

    public Long getClassRank()
    {
        return classRank;
    }

    public void setClassRank(Long classRank)
    {
        this.classRank = classRank;
    }

    public String getScoreLevel()
    {
        return scoreLevel;
    }

    public void setScoreLevel(String scoreLevel)
    {
        this.scoreLevel = scoreLevel;
    }

    public String getStudentNoOrder()
    {
        return studentNoOrder;
    }

    public void setStudentNoOrder(String studentNoOrder)
    {
        this.studentNoOrder = studentNoOrder;
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
            .append("scoreId", getScoreId())
            .append("examId", getExamId())
            .append("classId", getClassId())
            .append("className", getClassName())
            .append("studentId", getStudentId())
            .append("studentNo", getStudentNo())
            .append("studentNameSnapshot", getStudentNameSnapshot())
            .append("subjectScores", getSubjectScores())
            .append("totalScore", getTotalScore())
            .append("classRank", getClassRank())
            .append("scoreLevel", getScoreLevel())
            .append("studentNoOrder", getStudentNoOrder())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
