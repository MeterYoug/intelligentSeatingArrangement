package com.ruoyi.seating.engine.model;

import java.math.BigDecimal;

/**
 * 排座评分明细。
 */
public class SeatingScoreItem
{
    private String ruleCode;

    private String ruleName;

    private BigDecimal scoreValue;

    private BigDecimal penaltyValue;

    private String detailJson;

    public SeatingScoreItem(String ruleCode, String ruleName, BigDecimal scoreValue,
            BigDecimal penaltyValue, String detailJson)
    {
        this.ruleCode = ruleCode;
        this.ruleName = ruleName;
        this.scoreValue = scoreValue;
        this.penaltyValue = penaltyValue;
        this.detailJson = detailJson;
    }

    public String getRuleCode()
    {
        return ruleCode;
    }

    public String getRuleName()
    {
        return ruleName;
    }

    public BigDecimal getScoreValue()
    {
        return scoreValue;
    }

    public BigDecimal getPenaltyValue()
    {
        return penaltyValue;
    }

    public String getDetailJson()
    {
        return detailJson;
    }
}
