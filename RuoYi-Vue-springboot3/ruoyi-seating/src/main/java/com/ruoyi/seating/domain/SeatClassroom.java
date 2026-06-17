package com.ruoyi.seating.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 排座教室布局对象 seat_classroom
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public class SeatClassroom extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 教室布局ID */
    private Long classroomId;

    /** 班级ID */
    @Excel(name = "班级ID")
    private Long classId;

    /** 班级名称 */
    private String className;

    /** 教室布局名称 */
    @Excel(name = "教室布局名称")
    private String classroomName;

    /** 座位行数 */
    @Excel(name = "座位行数")
    private Long rowCount;

    /** 座位列数 */
    @Excel(name = "座位列数")
    private Long colCount;

    /** 讲台位置（FRONT/BACK/LEFT/RIGHT） */
    @Excel(name = "讲台位置", readConverterExp = "F=RONT/BACK/LEFT/RIGHT")
    private String platformPosition;

    /** 过道所在列后，逗号分隔 */
    @Excel(name = "过道所在列后，逗号分隔")
    private String aisleAfterCols;

    /** 是否默认（0否 1是） */
    @Excel(name = "是否默认", readConverterExp = "0=否,1=是")
    private String isDefault;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setClassroomId(Long classroomId) 
    {
        this.classroomId = classroomId;
    }

    public Long getClassroomId() 
    {
        return classroomId;
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

    public void setClassroomName(String classroomName) 
    {
        this.classroomName = classroomName;
    }

    public String getClassroomName() 
    {
        return classroomName;
    }

    public void setRowCount(Long rowCount) 
    {
        this.rowCount = rowCount;
    }

    public Long getRowCount() 
    {
        return rowCount;
    }

    public void setColCount(Long colCount) 
    {
        this.colCount = colCount;
    }

    public Long getColCount() 
    {
        return colCount;
    }

    public void setPlatformPosition(String platformPosition) 
    {
        this.platformPosition = platformPosition;
    }

    public String getPlatformPosition() 
    {
        return platformPosition;
    }

    public void setAisleAfterCols(String aisleAfterCols) 
    {
        this.aisleAfterCols = aisleAfterCols;
    }

    public String getAisleAfterCols() 
    {
        return aisleAfterCols;
    }

    public void setIsDefault(String isDefault) 
    {
        this.isDefault = isDefault;
    }

    public String getIsDefault() 
    {
        return isDefault;
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
            .append("classroomId", getClassroomId())
            .append("classId", getClassId())
            .append("className", getClassName())
            .append("classroomName", getClassroomName())
            .append("rowCount", getRowCount())
            .append("colCount", getColCount())
            .append("platformPosition", getPlatformPosition())
            .append("aisleAfterCols", getAisleAfterCols())
            .append("isDefault", getIsDefault())
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
