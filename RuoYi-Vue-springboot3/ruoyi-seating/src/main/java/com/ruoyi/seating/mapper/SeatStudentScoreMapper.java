package com.ruoyi.seating.mapper;

import java.util.List;
import com.ruoyi.seating.domain.SeatStudentScore;

public interface SeatStudentScoreMapper
{
    public SeatStudentScore selectSeatStudentScoreByScoreId(Long scoreId);

    public SeatStudentScore selectByExamIdAndStudentId(SeatStudentScore seatStudentScore);

    public List<SeatStudentScore> selectSeatStudentScoreList(SeatStudentScore seatStudentScore);

    public int insertSeatStudentScore(SeatStudentScore seatStudentScore);

    public int updateSeatStudentScore(SeatStudentScore seatStudentScore);

    public int deleteSeatStudentScoreByScoreId(Long scoreId);

    public int deleteSeatStudentScoreByScoreIds(Long[] scoreIds);
}
