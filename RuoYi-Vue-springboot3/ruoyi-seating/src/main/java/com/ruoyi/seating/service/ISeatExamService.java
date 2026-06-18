package com.ruoyi.seating.service;

import java.util.List;
import com.ruoyi.seating.domain.SeatExam;

public interface ISeatExamService
{
    public SeatExam selectSeatExamByExamId(Long examId);

    public List<SeatExam> selectSeatExamList(SeatExam seatExam);

    public int insertSeatExam(SeatExam seatExam);

    public int updateSeatExam(SeatExam seatExam);

    public int deleteSeatExamByExamIds(Long[] examIds);

    public int deleteSeatExamByExamId(Long examId);

    public int setCurrentExam(Long examId, String operName);
}
