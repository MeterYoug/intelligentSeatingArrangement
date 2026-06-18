package com.ruoyi.seating.service;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.seating.domain.SeatStudentScore;

public interface ISeatStudentScoreService
{
    public SeatStudentScore selectSeatStudentScoreByScoreId(Long scoreId);

    public List<SeatStudentScore> selectSeatStudentScoreList(SeatStudentScore seatStudentScore);

    public int insertSeatStudentScore(SeatStudentScore seatStudentScore);

    public int updateSeatStudentScore(SeatStudentScore seatStudentScore);

    public int deleteSeatStudentScoreByScoreIds(Long[] scoreIds);

    public int deleteSeatStudentScoreByScoreId(Long scoreId);

    public void exportImportTemplate(HttpServletResponse response, Long classId);

    public String importScores(Long examId, MultipartFile file, boolean updateSupport, String operName) throws Exception;

    public int syncStudentScoreLevel(Long examId, String operName);
}
