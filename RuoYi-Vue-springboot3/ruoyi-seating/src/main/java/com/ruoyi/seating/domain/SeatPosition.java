package com.ruoyi.seating.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 排座座位位置对象 seat_position
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public class SeatPosition extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 座位ID */
    private Long seatId;

    /** 教室布局ID */
    @Excel(name = "教室布局ID")
    private Long classroomId;

    /** 行号，从1开始 */
    @Excel(name = "行号，从1开始")
    private Long rowIndex;

    /** 列号，从1开始 */
    @Excel(name = "列号，从1开始")
    private Long colIndex;

    /** 座位编号 */
    @Excel(name = "座位编号")
    private String seatCode;

    /** 座位类型（0普通 1空位 2过道） */
    @Excel(name = "座位类型", readConverterExp = "0=普通,1=空位,2=过道")
    private String seatType;

    /** 是否可用（0否 1是） */
    @Excel(name = "是否可用", readConverterExp = "0=否,1=是")
    private String isAvailable;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    public void setSeatId(Long seatId) 
    {
        this.seatId = seatId;
    }

    public Long getSeatId() 
    {
        return seatId;
    }

    public void setClassroomId(Long classroomId) 
    {
        this.classroomId = classroomId;
    }

    public Long getClassroomId() 
    {
        return classroomId;
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

    public void setSeatCode(String seatCode) 
    {
        this.seatCode = seatCode;
    }

    public String getSeatCode() 
    {
        return seatCode;
    }

    public void setSeatType(String seatType) 
    {
        this.seatType = seatType;
    }

    public String getSeatType() 
    {
        return seatType;
    }

    public void setIsAvailable(String isAvailable) 
    {
        this.isAvailable = isAvailable;
    }

    public String getIsAvailable() 
    {
        return isAvailable;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("seatId", getSeatId())
            .append("classroomId", getClassroomId())
            .append("rowIndex", getRowIndex())
            .append("colIndex", getColIndex())
            .append("seatCode", getSeatCode())
            .append("seatType", getSeatType())
            .append("isAvailable", getIsAvailable())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
