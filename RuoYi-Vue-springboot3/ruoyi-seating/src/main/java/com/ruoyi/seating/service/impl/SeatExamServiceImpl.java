package com.ruoyi.seating.service.impl;

import java.util.List;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.seating.domain.SeatClass;
import com.ruoyi.seating.domain.SeatExam;
import com.ruoyi.seating.domain.SeatSubject;
import com.ruoyi.seating.mapper.SeatExamMapper;
import com.ruoyi.seating.mapper.SeatSubjectMapper;
import com.ruoyi.seating.service.GradeSubjectHelper;
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.ISeatExamService;

@Service
public class SeatExamServiceImpl implements ISeatExamService
{
    @Autowired
    private SeatExamMapper seatExamMapper;

    @Autowired
    private ISeatClassService seatClassService;

    @Autowired
    private SeatSubjectMapper seatSubjectMapper;

    @Override
    public SeatExam selectSeatExamByExamId(Long examId)
    {
        return seatExamMapper.selectSeatExamByExamId(examId);
    }

    @Override
    public List<SeatExam> selectSeatExamList(SeatExam seatExam)
    {
        return seatExamMapper.selectSeatExamList(seatExam);
    }

    @Override
    public int insertSeatExam(SeatExam seatExam)
    {
        SeatClass seatClass = seatClassService.selectSeatClassByClassId(seatExam.getClassId());
        if (seatClass == null)
        {
            throw new ServiceException("班级不存在");
        }
        fillClassSnapshot(seatExam, seatClass);
        seatExam.setIsCurrent(StringUtils.defaultIfBlank(seatExam.getIsCurrent(), "0"));
        seatExam.setStatus(StringUtils.defaultIfBlank(seatExam.getStatus(), "0"));
        seatExam.setDelFlag("0");
        seatExam.setCreateTime(DateUtils.getNowDate());
        return seatExamMapper.insertSeatExam(seatExam);
    }

    @Override
    public int updateSeatExam(SeatExam seatExam)
    {
        SeatExam oldExam = seatExamMapper.selectSeatExamByExamId(seatExam.getExamId());
        if (oldExam == null)
        {
            throw new ServiceException("考试批次不存在");
        }
        if (seatExam.getClassId() != null && !seatExam.getClassId().equals(oldExam.getClassId()))
        {
            SeatClass seatClass = seatClassService.selectSeatClassByClassId(seatExam.getClassId());
            if (seatClass == null)
            {
                throw new ServiceException("班级不存在");
            }
            fillClassSnapshot(seatExam, seatClass);
        }
        seatExam.setUpdateTime(DateUtils.getNowDate());
        return seatExamMapper.updateSeatExam(seatExam);
    }

    @Override
    public int deleteSeatExamByExamIds(Long[] examIds)
    {
        return seatExamMapper.deleteSeatExamByExamIds(examIds);
    }

    @Override
    public int deleteSeatExamByExamId(Long examId)
    {
        return seatExamMapper.deleteSeatExamByExamId(examId);
    }

    @Override
    @Transactional
    public int setCurrentExam(Long examId, String operName)
    {
        SeatExam exam = seatExamMapper.selectSeatExamByExamId(examId);
        if (exam == null)
        {
            throw new ServiceException("考试批次不存在");
        }
        seatExamMapper.clearCurrentByClassId(exam.getClassId(), operName);
        SeatExam update = new SeatExam();
        update.setExamId(examId);
        update.setIsCurrent("1");
        update.setUpdateBy(operName);
        update.setUpdateTime(DateUtils.getNowDate());
        return seatExamMapper.updateSeatExam(update);
    }

    private void fillClassSnapshot(SeatExam seatExam, SeatClass seatClass)
    {
        String gradeCode = seatClass.getGradeCode();
        String schoolStage = StringUtils.defaultIfBlank(seatClass.getSchoolStage(), GradeSubjectHelper.stageOf(gradeCode));
        String gradeName = StringUtils.defaultIfBlank(seatClass.getGradeName(), GradeSubjectHelper.gradeNameOf(gradeCode));
        seatExam.setSchoolStageSnapshot(schoolStage);
        seatExam.setGradeCodeSnapshot(gradeCode);
        seatExam.setGradeNameSnapshot(gradeName);
        seatExam.setSubjectSnapshot(JSON.toJSONString(resolveSubjectNames(seatClass)));
    }

    private List<String> resolveSubjectNames(SeatClass seatClass)
    {
        List<String> classSubjects = GradeSubjectHelper.resolveClassSubjects(seatClass);
        if (!classSubjects.isEmpty())
        {
            return classSubjects;
        }
        SeatSubject query = new SeatSubject();
        query.setSchoolStage(StringUtils.defaultIfBlank(seatClass.getSchoolStage(), GradeSubjectHelper.stageOf(seatClass.getGradeCode())));
        query.setGradeCode(seatClass.getGradeCode());
        query.setStatus("0");
        List<SeatSubject> configured = seatSubjectMapper.selectSeatSubjectList(query);
        if (configured != null && !configured.isEmpty())
        {
            return GradeSubjectHelper.normalizeSubjects(configured.stream().map(SeatSubject::getSubjectName).toList());
        }
        return GradeSubjectHelper.defaultSubjects(seatClass);
    }
}
