package com.ruoyi.seating.engine.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * 排座算法输出。
 */
public class SeatingResult
{
    private List<SeatingAssignmentResult> assignments;

    private List<SeatingScoreItem> scoreItems;

    private BigDecimal totalScore;

    private List<String> conflicts;

    public SeatingResult(List<SeatingAssignmentResult> assignments, List<SeatingScoreItem> scoreItems,
            BigDecimal totalScore)
    {
        this.assignments = assignments;
        this.scoreItems = scoreItems;
        this.totalScore = totalScore;
    }

    public SeatingResult(List<SeatingAssignmentResult> assignments, List<SeatingScoreItem> scoreItems,
            BigDecimal totalScore, List<String> conflicts)
    {
        this.assignments = assignments;
        this.scoreItems = scoreItems;
        this.totalScore = totalScore;
        this.conflicts = conflicts;
    }

    public List<SeatingAssignmentResult> getAssignments()
    {
        return assignments;
    }

    public List<SeatingScoreItem> getScoreItems()
    {
        return scoreItems;
    }

    public BigDecimal getTotalScore()
    {
        return totalScore;
    }

    public List<String> getConflicts()
    {
        return conflicts;
    }
}
