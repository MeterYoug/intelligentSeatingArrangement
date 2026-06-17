package com.ruoyi.seating.domain;

import java.math.BigDecimal;
import java.util.List;

/**
 * 座位人工微调保存结果。
 */
public class SeatAssignmentAdjustResult
{
    private int updated;

    private BigDecimal totalScore;

    private BigDecimal scoreChange;

    private List<String> conflicts;

    public int getUpdated()
    {
        return updated;
    }

    public void setUpdated(int updated)
    {
        this.updated = updated;
    }

    public BigDecimal getTotalScore()
    {
        return totalScore;
    }

    public void setTotalScore(BigDecimal totalScore)
    {
        this.totalScore = totalScore;
    }

    public BigDecimal getScoreChange()
    {
        return scoreChange;
    }

    public void setScoreChange(BigDecimal scoreChange)
    {
        this.scoreChange = scoreChange;
    }

    public List<String> getConflicts()
    {
        return conflicts;
    }

    public void setConflicts(List<String> conflicts)
    {
        this.conflicts = conflicts;
    }
}
