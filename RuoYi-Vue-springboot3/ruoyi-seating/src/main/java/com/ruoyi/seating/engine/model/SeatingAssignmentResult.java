package com.ruoyi.seating.engine.model;

import com.ruoyi.seating.domain.SeatPosition;
import com.ruoyi.seating.domain.SeatStudent;

/**
 * 单个学生到座位的算法分配结果。
 */
public class SeatingAssignmentResult
{
    private SeatStudent student;

    private SeatPosition seat;

    public SeatingAssignmentResult(SeatStudent student, SeatPosition seat)
    {
        this.student = student;
        this.seat = seat;
    }

    public SeatStudent getStudent()
    {
        return student;
    }

    public SeatPosition getSeat()
    {
        return seat;
    }

    public void setSeat(SeatPosition seat)
    {
        this.seat = seat;
    }
}
