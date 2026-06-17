package com.ruoyi.seating.domain;

import java.math.BigDecimal;
import com.ruoyi.common.annotation.Excel;

/**
 * 学生导入数据。
 */
public class SeatStudentImportData
{
    @Excel(name = "学号")
    private String studentNo;

    @Excel(name = "学生姓名")
    private String studentName;

    @Excel(name = "性别", readConverterExp = "0=男,1=女,2=未知")
    private String gender;

    @Excel(name = "身高厘米")
    private BigDecimal heightCm;

    @Excel(name = "视力等级", readConverterExp = "0=正常,1=轻度,2=中度,3=重度")
    private String visionLevel;

    @Excel(name = "成绩等级")
    private String scoreLevel;

    @Excel(name = "纪律等级", readConverterExp = "0=正常,1=关注,2=重点关注")
    private String disciplineLevel;

    @Excel(name = "特殊需求")
    private String specialNeed;

    @Excel(name = "排序号")
    private Long sortNo;

    @Excel(name = "备注")
    private String remark;

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public BigDecimal getHeightCm() { return heightCm; }
    public void setHeightCm(BigDecimal heightCm) { this.heightCm = heightCm; }
    public String getVisionLevel() { return visionLevel; }
    public void setVisionLevel(String visionLevel) { this.visionLevel = visionLevel; }
    public String getScoreLevel() { return scoreLevel; }
    public void setScoreLevel(String scoreLevel) { this.scoreLevel = scoreLevel; }
    public String getDisciplineLevel() { return disciplineLevel; }
    public void setDisciplineLevel(String disciplineLevel) { this.disciplineLevel = disciplineLevel; }
    public String getSpecialNeed() { return specialNeed; }
    public void setSpecialNeed(String specialNeed) { this.specialNeed = specialNeed; }
    public Long getSortNo() { return sortNo; }
    public void setSortNo(Long sortNo) { this.sortNo = sortNo; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
