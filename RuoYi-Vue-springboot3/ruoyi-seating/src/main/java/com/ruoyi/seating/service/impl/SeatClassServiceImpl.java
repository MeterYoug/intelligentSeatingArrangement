package com.ruoyi.seating.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.seating.domain.SeatClass;
import com.ruoyi.seating.domain.SeatClassroom;
import com.ruoyi.seating.domain.SeatPosition;
import com.ruoyi.seating.domain.SeatRule;
import com.ruoyi.seating.domain.SeatStudent;
import com.ruoyi.seating.domain.SeatStudentRelation;
import com.ruoyi.seating.domain.request.SeatClassCopyRequest;
import com.ruoyi.seating.mapper.SeatClassMapper;
import com.ruoyi.seating.mapper.SeatClassroomMapper;
import com.ruoyi.seating.mapper.SeatPositionMapper;
import com.ruoyi.seating.mapper.SeatRuleMapper;
import com.ruoyi.seating.mapper.SeatStudentMapper;
import com.ruoyi.seating.mapper.SeatStudentRelationMapper;
import com.ruoyi.seating.service.GradeSubjectHelper;
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.support.SeatClassCopySupport;

/**
 * 排座班级Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@Service
public class SeatClassServiceImpl implements ISeatClassService 
{
    @Autowired
    private SeatClassMapper seatClassMapper;

    @Autowired
    private SeatStudentMapper seatStudentMapper;

    @Autowired
    private SeatStudentRelationMapper seatStudentRelationMapper;

    @Autowired
    private SeatRuleMapper seatRuleMapper;

    @Autowired
    private SeatClassroomMapper seatClassroomMapper;

    @Autowired
    private SeatPositionMapper seatPositionMapper;

    /**
     * 查询排座班级
     * 
     * @param classId 排座班级主键
     * @return 排座班级
     */
    @Override
    public SeatClass selectSeatClassByClassId(Long classId)
    {
        return normalizeClass(seatClassMapper.selectSeatClassByClassId(classId));
    }

    /**
     * 查询排座班级列表
     * 
     * @param seatClass 排座班级
     * @return 排座班级
     */
    @Override
    public List<SeatClass> selectSeatClassList(SeatClass seatClass)
    {
        List<SeatClass> list = seatClassMapper.selectSeatClassList(seatClass);
        list.forEach(this::normalizeClass);
        return list;
    }

    /**
     * 新增排座班级
     * 
     * @param seatClass 排座班级
     * @return 结果
     */
    @Override
    public int insertSeatClass(SeatClass seatClass)
    {
        normalizeClass(seatClass);
        seatClass.setCreateTime(DateUtils.getNowDate());
        return seatClassMapper.insertSeatClass(seatClass);
    }

    /**
     * 修改排座班级
     * 
     * @param seatClass 排座班级
     * @return 结果
     */
    @Override
    public int updateSeatClass(SeatClass seatClass)
    {
        normalizeClass(seatClass);
        seatClass.setUpdateTime(DateUtils.getNowDate());
        return seatClassMapper.updateSeatClass(seatClass);
    }

    /**
     * 批量删除排座班级
     * 
     * @param classIds 需要删除的排座班级主键
     * @return 结果
     */
    @Override
    public int deleteSeatClassByClassIds(Long[] classIds)
    {
        return seatClassMapper.deleteSeatClassByClassIds(classIds);
    }

    /**
     * 删除排座班级信息
     * 
     * @param classId 排座班级主键
     * @return 结果
     */
    @Override
    public int deleteSeatClassByClassId(Long classId)
    {
        return seatClassMapper.deleteSeatClassByClassId(classId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeatClass copyNewTerm(Long classId, SeatClassCopyRequest copyRequest, String operName)
    {
        SeatClass sourceClass = selectSeatClassByClassId(classId);
        if (sourceClass == null)
        {
            throw new ServiceException("班级不存在");
        }
        if (copyRequest == null)
        {
            throw new ServiceException("复制参数不能为空");
        }

        boolean copyStudents = isCopyEnabled(copyRequest.getCopyStudents());
        boolean copyRelations = isCopyEnabled(copyRequest.getCopyRelations());
        boolean copyRules = isCopyEnabled(copyRequest.getCopyRules());
        boolean copyClassroomLayout = isCopyEnabled(copyRequest.getCopyClassroomLayout());
        if (copyRelations && !copyStudents)
        {
            throw new ServiceException("复制学生关系前必须先复制学生");
        }

        SeatClass copiedClass = buildCopiedClass(sourceClass, copyRequest, operName);
        insertSeatClass(copiedClass);

        if (copyClassroomLayout)
        {
            copyClassroomLayoutIfNeeded(sourceClass, copiedClass, operName);
        }

        Map<Long, Long> studentIdMap = new HashMap<>();
        if (copyStudents)
        {
            studentIdMap = copyStudentsIfNeeded(sourceClass, copiedClass, operName);
        }

        if (copyRelations)
        {
            copyRelationsIfNeeded(sourceClass, copiedClass, studentIdMap, operName);
        }

        if (copyRules)
        {
            copyRulesIfNeeded(sourceClass, copiedClass, operName);
        }

        return copiedClass;
    }

    private SeatClass normalizeClass(SeatClass seatClass)
    {
        if (seatClass == null)
        {
            return null;
        }
        seatClass.setSemester(normalizeSemester(seatClass.getSemester()));
        if (StringUtils.isBlank(seatClass.getSchoolStage()) && StringUtils.isNotBlank(seatClass.getGradeCode()))
        {
            seatClass.setSchoolStage(GradeSubjectHelper.stageOf(seatClass.getGradeCode()));
        }
        if (StringUtils.isBlank(seatClass.getGradeName()) && StringUtils.isNotBlank(seatClass.getGradeCode()))
        {
            seatClass.setGradeName(GradeSubjectHelper.gradeNameOf(seatClass.getGradeCode()));
        }
        List<String> subjects = GradeSubjectHelper.parseSubjectSnapshot(seatClass.getSubjectSnapshot());
        if (subjects.isEmpty())
        {
            subjects = GradeSubjectHelper.defaultSubjects(seatClass);
        }
        seatClass.setSubjectSnapshot(JSON.toJSONString(subjects));
        return seatClass;
    }

    private String normalizeSemester(String semester)
    {
        if (StringUtils.isBlank(semester))
        {
            return "1";
        }
        if ("上学期".equals(semester) || "1".equals(semester))
        {
            return "1";
        }
        if ("下学期".equals(semester) || "2".equals(semester))
        {
            return "2";
        }
        return semester;
    }

    private SeatClass buildCopiedClass(SeatClass sourceClass, SeatClassCopyRequest copyRequest, String operName)
    {
        SeatClass copiedClass = new SeatClass();
        copiedClass.setClassName(StringUtils.trim(copyRequest.getClassName()));
        copiedClass.setSchoolYear(StringUtils.isBlank(copyRequest.getSchoolYear())
                ? SeatClassCopySupport.nextSchoolYear(sourceClass.getSchoolYear(), sourceClass.getSemester())
                : StringUtils.trim(copyRequest.getSchoolYear()));
        copiedClass.setSemester(StringUtils.isBlank(copyRequest.getSemester())
                ? SeatClassCopySupport.nextSemester(sourceClass.getSemester())
                : StringUtils.trim(copyRequest.getSemester()));
        copiedClass.setGradeName(sourceClass.getGradeName());
        copiedClass.setSchoolStage(sourceClass.getSchoolStage());
        copiedClass.setGradeCode(sourceClass.getGradeCode());
        copiedClass.setSubjectSnapshot(sourceClass.getSubjectSnapshot());
        copiedClass.setTeacherId(sourceClass.getTeacherId());
        copiedClass.setDeptId(sourceClass.getDeptId());
        copiedClass.setStatus("0");
        copiedClass.setDelFlag("0");
        copiedClass.setCreateBy(operName);
        copiedClass.setRemark(sourceClass.getRemark());
        return copiedClass;
    }

    private void copyClassroomLayoutIfNeeded(SeatClass sourceClass, SeatClass copiedClass, String operName)
    {
        SeatClassroom query = new SeatClassroom();
        query.setClassId(sourceClass.getClassId());
        List<SeatClassroom> classroomList = seatClassroomMapper.selectSeatClassroomList(query);
        SeatClassroom sourceClassroom = SeatClassCopySupport.selectPrimaryClassroom(classroomList);
        if (sourceClassroom == null)
        {
            throw new ServiceException("源班级没有可复制的默认启用教室布局");
        }

        SeatClassroom copiedClassroom = new SeatClassroom();
        copiedClassroom.setClassId(copiedClass.getClassId());
        copiedClassroom.setClassroomName(sourceClassroom.getClassroomName());
        copiedClassroom.setRowCount(sourceClassroom.getRowCount());
        copiedClassroom.setColCount(sourceClassroom.getColCount());
        copiedClassroom.setPlatformPosition(sourceClassroom.getPlatformPosition());
        copiedClassroom.setAisleAfterCols(sourceClassroom.getAisleAfterCols());
        copiedClassroom.setIsDefault(sourceClassroom.getIsDefault());
        copiedClassroom.setStatus(sourceClassroom.getStatus());
        copiedClassroom.setDelFlag("0");
        copiedClassroom.setCreateBy(operName);
        copiedClassroom.setCreateTime(DateUtils.getNowDate());
        copiedClassroom.setRemark(sourceClassroom.getRemark());
        seatClassroomMapper.insertSeatClassroom(copiedClassroom);

        SeatPosition positionQuery = new SeatPosition();
        positionQuery.setClassroomId(sourceClassroom.getClassroomId());
        List<SeatPosition> positionList = seatPositionMapper.selectSeatPositionList(positionQuery);
        if (positionList.isEmpty())
        {
            throw new ServiceException("源班级默认教室没有可复制的座位布局");
        }
        for (SeatPosition position : positionList)
        {
            SeatPosition copiedPosition = new SeatPosition();
            copiedPosition.setClassroomId(copiedClassroom.getClassroomId());
            copiedPosition.setRowIndex(position.getRowIndex());
            copiedPosition.setColIndex(position.getColIndex());
            copiedPosition.setSeatCode(StringUtils.defaultIfBlank(position.getSeatCode(),
                    "R" + position.getRowIndex() + "C" + position.getColIndex()));
            copiedPosition.setSeatType(position.getSeatType());
            copiedPosition.setIsAvailable(position.getIsAvailable());
            copiedPosition.setStatus(position.getStatus());
            copiedPosition.setCreateBy(operName);
            copiedPosition.setCreateTime(DateUtils.getNowDate());
            copiedPosition.setRemark(position.getRemark());
            seatPositionMapper.insertSeatPosition(copiedPosition);
        }
    }

    private Map<Long, Long> copyStudentsIfNeeded(SeatClass sourceClass, SeatClass copiedClass, String operName)
    {
        SeatStudent query = new SeatStudent();
        query.setClassId(sourceClass.getClassId());
        List<SeatStudent> studentList = seatStudentMapper.selectSeatStudentList(query);
        Map<Long, Long> studentIdMap = new HashMap<>();
        for (SeatStudent student : studentList)
        {
            SeatStudent copiedStudent = new SeatStudent();
            copiedStudent.setClassId(copiedClass.getClassId());
            copiedStudent.setClassName(copiedClass.getClassName());
            copiedStudent.setStudentNo(student.getStudentNo());
            copiedStudent.setStudentName(student.getStudentName());
            copiedStudent.setGender(student.getGender());
            copiedStudent.setHeightCm(student.getHeightCm());
            copiedStudent.setVisionLevel(student.getVisionLevel());
            copiedStudent.setScoreLevel(student.getScoreLevel());
            copiedStudent.setDisciplineLevel(student.getDisciplineLevel());
            copiedStudent.setSpecialNeed(student.getSpecialNeed());
            copiedStudent.setSortNo(student.getSortNo());
            copiedStudent.setStatus(student.getStatus());
            copiedStudent.setDelFlag("0");
            copiedStudent.setCreateBy(operName);
            copiedStudent.setCreateTime(DateUtils.getNowDate());
            copiedStudent.setRemark(student.getRemark());
            seatStudentMapper.insertSeatStudent(copiedStudent);
            studentIdMap.put(student.getStudentId(), copiedStudent.getStudentId());
        }
        return studentIdMap;
    }

    private void copyRelationsIfNeeded(SeatClass sourceClass, SeatClass copiedClass, Map<Long, Long> studentIdMap, String operName)
    {
        SeatStudentRelation query = new SeatStudentRelation();
        query.setClassId(sourceClass.getClassId());
        List<SeatStudentRelation> relationList = seatStudentRelationMapper.selectSeatStudentRelationList(query);
        for (SeatStudentRelation relation : relationList)
        {
            Long copiedStudentId = studentIdMap.get(relation.getStudentId());
            Long copiedRelatedId = studentIdMap.get(relation.getRelatedId());
            if (copiedStudentId == null || copiedRelatedId == null)
            {
                throw new ServiceException("学生关系存在无法映射的学生，复制失败");
            }

            SeatStudentRelation copiedRelation = new SeatStudentRelation();
            copiedRelation.setClassId(copiedClass.getClassId());
            copiedRelation.setStudentId(copiedStudentId);
            copiedRelation.setRelatedId(copiedRelatedId);
            copiedRelation.setRelationType(relation.getRelationType());
            copiedRelation.setRelationWeight(relation.getRelationWeight());
            copiedRelation.setEnabled(relation.getEnabled());
            copiedRelation.setCreateBy(operName);
            copiedRelation.setCreateTime(DateUtils.getNowDate());
            copiedRelation.setRemark(relation.getRemark());
            seatStudentRelationMapper.insertSeatStudentRelation(copiedRelation);
        }
    }

    private void copyRulesIfNeeded(SeatClass sourceClass, SeatClass copiedClass, String operName)
    {
        SeatRule query = new SeatRule();
        query.setClassId(sourceClass.getClassId());
        List<SeatRule> ruleList = seatRuleMapper.selectSeatRuleList(query);
        for (SeatRule rule : ruleList)
        {
            SeatRule copiedRule = new SeatRule();
            copiedRule.setClassId(copiedClass.getClassId());
            copiedRule.setClassName(copiedClass.getClassName());
            copiedRule.setRuleName(rule.getRuleName());
            copiedRule.setRuleCategory(rule.getRuleCategory());
            copiedRule.setRuleCode(rule.getRuleCode());
            copiedRule.setRuleWeight(rule.getRuleWeight());
            copiedRule.setRuleConfig(rule.getRuleConfig());
            copiedRule.setEnabled(rule.getEnabled());
            copiedRule.setStatus(rule.getStatus());
            copiedRule.setDelFlag("0");
            copiedRule.setCreateBy(operName);
            copiedRule.setCreateTime(DateUtils.getNowDate());
            copiedRule.setRemark(rule.getRemark());
            seatRuleMapper.insertSeatRule(copiedRule);
        }
    }

    private boolean isCopyEnabled(Boolean value)
    {
        return value == null || value;
    }
}
