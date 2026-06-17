package com.ruoyi.seating.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 排座方案评分明细对象 seat_plan_score
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public class SeatPlanScore extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 评分ID */
    private Long scoreId;

    /** 方案ID */
    @Excel(name = "方案ID")
    private Long planId;

    /** 规则编码 */
    @Excel(name = "规则编码")
    private String ruleCode;

    /** 规则名称 */
    @Excel(name = "规则名称")
    private String ruleName;

    /** 评分值 */
    @Excel(name = "评分值")
    private BigDecimal scoreValue;

    /** 扣分值 */
    @Excel(name = "扣分值")
    private BigDecimal penaltyValue;

    /** 评分明细JSON */
    @Excel(name = "评分明细JSON")
    private String detailJson;

    public void setScoreId(Long scoreId) 
    {
        this.scoreId = scoreId;
    }

    public Long getScoreId() 
    {
        return scoreId;
    }

    public void setPlanId(Long planId) 
    {
        this.planId = planId;
    }

    public Long getPlanId() 
    {
        return planId;
    }

    public void setRuleCode(String ruleCode) 
    {
        this.ruleCode = ruleCode;
    }

    public String getRuleCode() 
    {
        return ruleCode;
    }

    public void setRuleName(String ruleName) 
    {
        this.ruleName = ruleName;
    }

    public String getRuleName() 
    {
        return ruleName;
    }

    public void setScoreValue(BigDecimal scoreValue) 
    {
        this.scoreValue = scoreValue;
    }

    public BigDecimal getScoreValue() 
    {
        return scoreValue;
    }

    public void setPenaltyValue(BigDecimal penaltyValue) 
    {
        this.penaltyValue = penaltyValue;
    }

    public BigDecimal getPenaltyValue() 
    {
        return penaltyValue;
    }

    public void setDetailJson(String detailJson) 
    {
        this.detailJson = detailJson;
    }

    public String getDetailJson() 
    {
        return detailJson;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("scoreId", getScoreId())
            .append("planId", getPlanId())
            .append("ruleCode", getRuleCode())
            .append("ruleName", getRuleName())
            .append("scoreValue", getScoreValue())
            .append("penaltyValue", getPenaltyValue())
            .append("detailJson", getDetailJson())
            .append("createTime", getCreateTime())
            .toString();
    }
}
