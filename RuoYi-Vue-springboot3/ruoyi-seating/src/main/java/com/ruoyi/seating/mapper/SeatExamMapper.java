package com.ruoyi.seating.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.seating.domain.SeatExam;

public interface SeatExamMapper
{
    public SeatExam selectSeatExamByExamId(Long examId);

    public List<SeatExam> selectSeatExamList(SeatExam seatExam);

    public int insertSeatExam(SeatExam seatExam);

    public int updateSeatExam(SeatExam seatExam);

    public int deleteSeatExamByExamId(Long examId);

    public int deleteSeatExamByExamIds(Long[] examIds);

    public int clearCurrentByClassId(@Param("classId") Long classId, @Param("updateBy") String updateBy);
}
