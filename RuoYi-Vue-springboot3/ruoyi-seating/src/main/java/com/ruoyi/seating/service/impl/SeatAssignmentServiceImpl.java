package com.ruoyi.seating.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.seating.engine.SeatingEngine;
import com.ruoyi.seating.engine.model.SeatingAssignmentResult;
import com.ruoyi.seating.engine.model.SeatingResult;
import com.ruoyi.seating.engine.model.SeatingScoreItem;
import com.ruoyi.seating.mapper.SeatPlanScoreMapper;
import com.ruoyi.seating.mapper.SeatRuleMapper;
import com.ruoyi.seating.mapper.SeatStudentMapper;
import com.ruoyi.seating.mapper.SeatStudentRelationMapper;
import com.ruoyi.seating.mapper.SeatAssignmentMapper;
import com.ruoyi.seating.mapper.SeatPlanMapper;
import com.ruoyi.seating.mapper.SeatPositionMapper;
import com.ruoyi.seating.domain.SeatAssignmentAdjustResult;
import com.ruoyi.seating.domain.SeatAssignment;
import com.ruoyi.seating.domain.SeatPlan;
import com.ruoyi.seating.domain.SeatPlanScore;
import com.ruoyi.seating.domain.SeatPosition;
import com.ruoyi.seating.domain.SeatRule;
import com.ruoyi.seating.domain.SeatStudent;
import com.ruoyi.seating.domain.SeatStudentRelation;
import com.ruoyi.seating.service.ISeatAssignmentService;

/**
 * 排座分配Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@Service
public class SeatAssignmentServiceImpl implements ISeatAssignmentService 
{
    @Autowired
    private SeatAssignmentMapper seatAssignmentMapper;

    @Autowired
    private SeatPlanMapper seatPlanMapper;

    @Autowired
    private SeatPositionMapper seatPositionMapper;

    @Autowired
    private SeatStudentMapper seatStudentMapper;

    @Autowired
    private SeatRuleMapper seatRuleMapper;

    @Autowired
    private SeatStudentRelationMapper seatStudentRelationMapper;

    @Autowired
    private SeatPlanScoreMapper seatPlanScoreMapper;

    @Autowired
    private SeatingEngine seatingEngine;

    /**
     * 查询排座分配
     * 
     * @param assignmentId 排座分配主键
     * @return 排座分配
     */
    @Override
    public SeatAssignment selectSeatAssignmentByAssignmentId(Long assignmentId)
    {
        return seatAssignmentMapper.selectSeatAssignmentByAssignmentId(assignmentId);
    }

    /**
     * 查询排座分配列表
     * 
     * @param seatAssignment 排座分配
     * @return 排座分配
     */
    @Override
    public List<SeatAssignment> selectSeatAssignmentList(SeatAssignment seatAssignment)
    {
        return seatAssignmentMapper.selectSeatAssignmentList(seatAssignment);
    }

    /**
     * 新增排座分配
     * 
     * @param seatAssignment 排座分配
     * @return 结果
     */
    @Override
    public int insertSeatAssignment(SeatAssignment seatAssignment)
    {
        seatAssignment.setCreateTime(DateUtils.getNowDate());
        return seatAssignmentMapper.insertSeatAssignment(seatAssignment);
    }

    /**
     * 修改排座分配
     * 
     * @param seatAssignment 排座分配
     * @return 结果
     */
    @Override
    public int updateSeatAssignment(SeatAssignment seatAssignment)
    {
        seatAssignment.setUpdateTime(DateUtils.getNowDate());
        return seatAssignmentMapper.updateSeatAssignment(seatAssignment);
    }

    /**
     * 批量删除排座分配
     * 
     * @param assignmentIds 需要删除的排座分配主键
     * @return 结果
     */
    @Override
    public int deleteSeatAssignmentByAssignmentIds(Long[] assignmentIds)
    {
        return seatAssignmentMapper.deleteSeatAssignmentByAssignmentIds(assignmentIds);
    }

    /**
     * 删除排座分配信息
     * 
     * @param assignmentId 排座分配主键
     * @return 结果
     */
    @Override
    public int deleteSeatAssignmentByAssignmentId(Long assignmentId)
    {
        return seatAssignmentMapper.deleteSeatAssignmentByAssignmentId(assignmentId);
    }

    @Override
    @Transactional
    public SeatAssignmentAdjustResult savePlanAssignments(Long planId, List<SeatAssignment> assignmentList, String operName)
    {
        SeatPlan plan = seatPlanMapper.selectSeatPlanByPlanId(planId);
        if (plan == null)
        {
            throw new ServiceException("座位方案不存在");
        }
        Set<Long> seatIds = new HashSet<>();
        Set<Long> assignmentIds = new HashSet<>();
        Set<Long> studentIds = new HashSet<>();
        List<SeatAssignment> updateItems = new ArrayList<>();
        List<SeatAssignment> insertItems = new ArrayList<>();
        SeatAssignment planAssignmentQuery = new SeatAssignment();
        planAssignmentQuery.setPlanId(planId);
        List<SeatAssignment> currentAssignments = seatAssignmentMapper.selectSeatAssignmentList(planAssignmentQuery);
        Map<Long, SeatAssignment> currentAssignmentMap = new HashMap<>();
        for (SeatAssignment currentAssignment : currentAssignments)
        {
            currentAssignmentMap.put(currentAssignment.getAssignmentId(), currentAssignment);
        }

        if (assignmentList == null)
        {
            assignmentList = new ArrayList<>();
        }
        for (SeatAssignment submitItem : assignmentList)
        {
            if (submitItem.getSeatId() == null)
            {
                throw new ServiceException("座位不能为空");
            }
            SeatPosition seat = seatPositionMapper.selectSeatPositionBySeatId(submitItem.getSeatId());
            if (seat == null || !plan.getClassroomId().equals(seat.getClassroomId()))
            {
                throw new ServiceException("座位不属于当前教室布局");
            }
            if (!"0".equals(seat.getSeatType()) || !"1".equals(seat.getIsAvailable()))
            {
                throw new ServiceException("学生只能安排到可用座位");
            }
            if (!seatIds.add(seat.getSeatId()))
            {
                throw new ServiceException("同一个座位不能安排多个学生");
            }

            SeatAssignment updateItem = new SeatAssignment();
            if (submitItem.getAssignmentId() != null)
            {
                if (!assignmentIds.add(submitItem.getAssignmentId()))
                {
                    throw new ServiceException("存在重复的分配记录");
                }
                SeatAssignment oldAssignment = currentAssignmentMap.get(submitItem.getAssignmentId());
                if (oldAssignment == null)
                {
                    throw new ServiceException("座位分配不属于当前方案");
                }
                if (!studentIds.add(oldAssignment.getStudentId()))
                {
                    throw new ServiceException("同一个学生不能重复安排");
                }
                updateItem.setAssignmentId(oldAssignment.getAssignmentId());
                updateItem.setStudentId(oldAssignment.getStudentId());
                updateItem.setStudentNameSnapshot(oldAssignment.getStudentNameSnapshot());
            }
            else
            {
                if (submitItem.getStudentId() == null)
                {
                    throw new ServiceException("学生不能为空");
                }
                SeatStudent student = seatStudentMapper.selectSeatStudentByStudentId(submitItem.getStudentId());
                if (student == null || !plan.getClassId().equals(student.getClassId()))
                {
                    throw new ServiceException("学生不属于当前班级");
                }
                if (!"0".equals(student.getStatus()))
                {
                    throw new ServiceException("停用学生不能安排座位");
                }
                if (!studentIds.add(student.getStudentId()))
                {
                    throw new ServiceException("同一个学生不能重复安排");
                }
                updateItem.setStudentId(student.getStudentId());
                updateItem.setStudentNameSnapshot(student.getStudentName());
            }

            updateItem.setPlanId(planId);
            updateItem.setClassId(plan.getClassId());
            updateItem.setClassroomId(plan.getClassroomId());
            updateItem.setSeatId(seat.getSeatId());
            updateItem.setRowIndex(seat.getRowIndex());
            updateItem.setColIndex(seat.getColIndex());
            updateItem.setIsLocked("1".equals(submitItem.getIsLocked()) ? "1" : "0");
            updateItem.setAssignSource("MANUAL");
            updateItem.setUpdateBy(operName);
            if (updateItem.getAssignmentId() == null)
            {
                updateItem.setCreateBy(operName);
                updateItem.setCreateTime(DateUtils.getNowDate());
                insertItems.add(updateItem);
            }
            else
            {
                updateItems.add(updateItem);
            }
        }

        List<Long> deleteIds = new ArrayList<>();
        for (SeatAssignment currentAssignment : currentAssignments)
        {
            if (!assignmentIds.contains(currentAssignment.getAssignmentId()))
            {
                deleteIds.add(currentAssignment.getAssignmentId());
            }
        }
        int updated = 0;
        if (!deleteIds.isEmpty())
        {
            updated += seatAssignmentMapper.deleteSeatAssignmentByAssignmentIds(deleteIds.toArray(new Long[0]));
        }
        for (SeatAssignment updateItem : updateItems)
        {
            SeatAssignment releaseItem = new SeatAssignment();
            releaseItem.setAssignmentId(updateItem.getAssignmentId());
            releaseItem.setSeatId(-updateItem.getAssignmentId());
            releaseItem.setUpdateBy(operName);
            seatAssignmentMapper.updateSeatAssignment(releaseItem);
        }

        for (SeatAssignment updateItem : updateItems)
        {
            updated += seatAssignmentMapper.updateSeatAssignment(updateItem);
        }
        for (SeatAssignment insertItem : insertItems)
        {
            updated += seatAssignmentMapper.insertSeatAssignment(insertItem);
        }
        SeatAssignmentAdjustResult result = refreshPlanScore(plan, operName);
        result.setUpdated(updated);
        return result;
    }

    private SeatAssignmentAdjustResult refreshPlanScore(SeatPlan plan, String operName)
    {
        SeatAssignment assignmentQuery = new SeatAssignment();
        assignmentQuery.setPlanId(plan.getPlanId());
        List<SeatAssignment> assignments = seatAssignmentMapper.selectSeatAssignmentList(assignmentQuery);

        SeatStudent studentQuery = new SeatStudent();
        studentQuery.setClassId(plan.getClassId());
        Map<Long, SeatStudent> studentMap = new HashMap<>();
        for (SeatStudent student : seatStudentMapper.selectSeatStudentList(studentQuery))
        {
            studentMap.put(student.getStudentId(), student);
        }

        SeatPosition seatQuery = new SeatPosition();
        seatQuery.setClassroomId(plan.getClassroomId());
        Map<Long, SeatPosition> seatMap = new HashMap<>();
        List<SeatPosition> seats = seatPositionMapper.selectSeatPositionList(seatQuery);
        for (SeatPosition seat : seats)
        {
            seatMap.put(seat.getSeatId(), seat);
        }

        List<SeatingAssignmentResult> evaluateAssignments = new ArrayList<>();
        for (SeatAssignment assignment : assignments)
        {
            SeatStudent student = studentMap.get(assignment.getStudentId());
            SeatPosition seat = seatMap.get(assignment.getSeatId());
            if (student == null || seat == null)
            {
                continue;
            }
            evaluateAssignments.add(new SeatingAssignmentResult(student, seat));
        }

        SeatRule ruleQuery = new SeatRule();
        ruleQuery.setClassId(plan.getClassId());
        ruleQuery.setEnabled("1");
        ruleQuery.setStatus("0");
        List<SeatRule> rules = seatRuleMapper.selectSeatRuleList(ruleQuery);

        SeatStudentRelation relationQuery = new SeatStudentRelation();
        relationQuery.setClassId(plan.getClassId());
        relationQuery.setEnabled("1");
        List<SeatStudentRelation> relations = seatStudentRelationMapper.selectSeatStudentRelationList(relationQuery);

        BigDecimal oldScore = plan.getTotalScore() == null ? BigDecimal.ZERO : plan.getTotalScore();
        SeatingResult seatingResult = seatingEngine.evaluate(evaluateAssignments, seats, rules, relations, plan.getPlanId());
        replacePlanScores(plan.getPlanId(), seatingResult);

        SeatPlan updatePlan = new SeatPlan();
        updatePlan.setPlanId(plan.getPlanId());
        updatePlan.setTotalScore(seatingResult.getTotalScore());
        updatePlan.setUpdateBy(operName);
        seatPlanMapper.updateSeatPlan(updatePlan);

        SeatAssignmentAdjustResult result = new SeatAssignmentAdjustResult();
        result.setTotalScore(seatingResult.getTotalScore());
        result.setScoreChange(seatingResult.getTotalScore().subtract(oldScore));
        result.setConflicts(seatingResult.getConflicts());
        return result;
    }

    private void replacePlanScores(Long planId, SeatingResult seatingResult)
    {
        SeatPlanScore scoreQuery = new SeatPlanScore();
        scoreQuery.setPlanId(planId);
        List<SeatPlanScore> oldScores = seatPlanScoreMapper.selectSeatPlanScoreList(scoreQuery);
        if (!oldScores.isEmpty())
        {
            Long[] scoreIds = oldScores.stream().map(SeatPlanScore::getScoreId).toArray(Long[]::new);
            seatPlanScoreMapper.deleteSeatPlanScoreByScoreIds(scoreIds);
        }

        for (SeatingScoreItem item : seatingResult.getScoreItems())
        {
            SeatPlanScore score = new SeatPlanScore();
            score.setPlanId(planId);
            score.setRuleCode(item.getRuleCode());
            score.setRuleName(item.getRuleName());
            score.setScoreValue(item.getScoreValue());
            score.setPenaltyValue(item.getPenaltyValue());
            score.setDetailJson(item.getDetailJson());
            score.setCreateTime(DateUtils.getNowDate());
            seatPlanScoreMapper.insertSeatPlanScore(score);
        }
    }
}
