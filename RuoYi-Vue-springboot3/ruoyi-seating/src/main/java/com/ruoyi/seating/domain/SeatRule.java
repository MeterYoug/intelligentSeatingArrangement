package com.ruoyi.seating.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 排座规则对象 seat_rule
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public class SeatRule extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 规则ID */
    private Long ruleId;

    /** 班级ID */
    @Excel(name = "班级ID")
    private Long classId;

    /** 班级名称 */
    private String className;

    /** 规则名称 */
    @Excel(name = "规则名称")
    private String ruleName;

    /** 规则类别（HARD硬规则 SOFT软规则） */
    @Excel(name = "规则类别", readConverterExp = "H=ARD硬规则,S=OFT软规则")
    private String ruleCategory;

    /** 规则编码 */
    @Excel(name = "规则编码")
    private String ruleCode;

    /** 规则权重 */
    @Excel(name = "规则权重")
    private Long ruleWeight;

    /** 规则配置JSON */
    @Excel(name = "规则配置JSON")
    private String ruleConfig;

    /** 是否启用（0否 1是） */
    @Excel(name = "是否启用", readConverterExp = "0=否,1=是")
    private String enabled;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    public void setRuleId(Long ruleId) 
    {
        this.ruleId = ruleId;
    }

    public Long getRuleId() 
    {
        return ruleId;
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

    public void setRuleName(String ruleName) 
    {
        this.ruleName = ruleName;
    }

    public String getRuleName() 
    {
        return ruleName;
    }

    public void setRuleCategory(String ruleCategory) 
    {
        this.ruleCategory = ruleCategory;
    }

    public String getRuleCategory() 
    {
        return ruleCategory;
    }

    public void setRuleCode(String ruleCode) 
    {
        this.ruleCode = ruleCode;
    }

    public String getRuleCode() 
    {
        return ruleCode;
    }

    public void setRuleWeight(Long ruleWeight) 
    {
        this.ruleWeight = ruleWeight;
    }

    public Long getRuleWeight() 
    {
        return ruleWeight;
    }

    public void setRuleConfig(String ruleConfig) 
    {
        this.ruleConfig = ruleConfig;
    }

    public String getRuleConfig() 
    {
        return ruleConfig;
    }

    public void setEnabled(String enabled) 
    {
        this.enabled = enabled;
    }

    public String getEnabled() 
    {
        return enabled;
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
            .append("ruleId", getRuleId())
            .append("classId", getClassId())
            .append("className", getClassName())
            .append("ruleName", getRuleName())
            .append("ruleCategory", getRuleCategory())
            .append("ruleCode", getRuleCode())
            .append("ruleWeight", getRuleWeight())
            .append("ruleConfig", getRuleConfig())
            .append("enabled", getEnabled())
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
