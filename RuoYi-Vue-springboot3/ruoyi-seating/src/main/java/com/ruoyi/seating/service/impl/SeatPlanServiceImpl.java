package com.ruoyi.seating.service.impl;

import java.util.List;
import java.util.Objects;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.seating.domain.SeatAssignment;
import com.ruoyi.seating.domain.SeatPlanScore;
import com.ruoyi.seating.domain.SeatPosition;
import com.ruoyi.seating.domain.SeatRule;
import com.ruoyi.seating.domain.SeatStudent;
import com.ruoyi.seating.domain.SeatStudentRelation;
import com.ruoyi.seating.engine.SeatingEngine;
import com.ruoyi.seating.engine.model.SeatingAssignmentResult;
import com.ruoyi.seating.engine.model.SeatingContext;
import com.ruoyi.seating.engine.model.SeatingResult;
import com.ruoyi.seating.engine.model.SeatingScoreItem;
import com.ruoyi.seating.mapper.SeatAssignmentMapper;
import com.ruoyi.seating.mapper.SeatPlanMapper;
import com.ruoyi.seating.mapper.SeatPlanScoreMapper;
import com.ruoyi.seating.mapper.SeatPositionMapper;
import com.ruoyi.seating.mapper.SeatRuleMapper;
import com.ruoyi.seating.mapper.SeatStudentMapper;
import com.ruoyi.seating.mapper.SeatStudentRelationMapper;
import com.ruoyi.seating.domain.SeatPlan;
import com.ruoyi.seating.service.ISeatPlanService;

/**
 * 排座方案Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@Service
public class SeatPlanServiceImpl implements ISeatPlanService 
{
    @Autowired
    private SeatPlanMapper seatPlanMapper;

    @Autowired
    private SeatStudentMapper seatStudentMapper;

    @Autowired
    private SeatPositionMapper seatPositionMapper;

    @Autowired
    private SeatAssignmentMapper seatAssignmentMapper;

    @Autowired
    private SeatPlanScoreMapper seatPlanScoreMapper;

    @Autowired
    private SeatRuleMapper seatRuleMapper;

    @Autowired
    private SeatStudentRelationMapper seatStudentRelationMapper;

    @Autowired
    private SeatingEngine seatingEngine;

    /**
     * 查询排座方案
     * 
     * @param planId 排座方案主键
     * @return 排座方案
     */
    @Override
    public SeatPlan selectSeatPlanByPlanId(Long planId)
    {
        return seatPlanMapper.selectSeatPlanByPlanId(planId);
    }

    /**
     * 查询排座方案列表
     * 
     * @param seatPlan 排座方案
     * @return 排座方案
     */
    @Override
    public List<SeatPlan> selectSeatPlanList(SeatPlan seatPlan)
    {
        return seatPlanMapper.selectSeatPlanList(seatPlan);
    }

    /**
     * 新增排座方案
     * 
     * @param seatPlan 排座方案
     * @return 结果
     */
    @Override
    public int insertSeatPlan(SeatPlan seatPlan)
    {
        seatPlan.setCreateTime(DateUtils.getNowDate());
        return seatPlanMapper.insertSeatPlan(seatPlan);
    }

    /**
     * 修改排座方案
     * 
     * @param seatPlan 排座方案
     * @return 结果
     */
    @Override
    public int updateSeatPlan(SeatPlan seatPlan)
    {
        seatPlan.setUpdateTime(DateUtils.getNowDate());
        return seatPlanMapper.updateSeatPlan(seatPlan);
    }

    /**
     * 批量删除排座方案
     * 
     * @param planIds 需要删除的排座方案主键
     * @return 结果
     */
    @Override
    public int deleteSeatPlanByPlanIds(Long[] planIds)
    {
        return seatPlanMapper.deleteSeatPlanByPlanIds(planIds);
    }

    /**
     * 删除排座方案信息
     * 
     * @param planId 排座方案主键
     * @return 结果
     */
    @Override
    public int deleteSeatPlanByPlanId(Long planId)
    {
        return seatPlanMapper.deleteSeatPlanByPlanId(planId);
    }

    @Override
    @Transactional
    public SeatPlan generateSeatPlan(SeatPlan seatPlan, String operName)
    {
        SeatStudent studentQuery = new SeatStudent();
        studentQuery.setClassId(seatPlan.getClassId());
        studentQuery.setStatus("0");
        List<SeatStudent> students = seatStudentMapper.selectSeatStudentList(studentQuery);
        if (students.isEmpty())
        {
            throw new ServiceException("班级没有可排座学生");
        }

        SeatPosition seatQuery = new SeatPosition();
        seatQuery.setClassroomId(seatPlan.getClassroomId());
        seatQuery.setSeatType("0");
        seatQuery.setIsAvailable("1");
        seatQuery.setStatus("0");
        List<SeatPosition> seats = seatPositionMapper.selectSeatPositionList(seatQuery);
        if (seats.isEmpty())
        {
            throw new ServiceException("教室没有可用座位");
        }
        if (seats.size() < students.size())
        {
            throw new ServiceException("可用座位数不足，学生 " + students.size() + " 人，可用座位 " + seats.size() + " 个");
        }

        SeatRule ruleQuery = new SeatRule();
        ruleQuery.setClassId(seatPlan.getClassId());
        ruleQuery.setEnabled("1");
        ruleQuery.setStatus("0");
        List<SeatRule> rules = seatRuleMapper.selectSeatRuleList(ruleQuery);

        SeatStudentRelation relationQuery = new SeatStudentRelation();
        relationQuery.setClassId(seatPlan.getClassId());
        relationQuery.setEnabled("1");
        List<SeatStudentRelation> relations = seatStudentRelationMapper.selectSeatStudentRelationList(relationQuery);

        long randomSeed = resolveRandomSeed(seatPlan, students.size(), seats.size());
        int optimizeIterations = resolveOptimizeIterations(seatPlan, students.size());
        SeatingResult result = seatingEngine.generate(
                new SeatingContext(students, seats, rules, relations, randomSeed, optimizeIterations));
        if (result.getConflicts() != null && !result.getConflicts().isEmpty())
        {
            throw new ServiceException("硬规则冲突：" + String.join("；", result.getConflicts()));
        }
        SeatPlan generatedPlan = buildGeneratedPlan(seatPlan, result, operName);
        insertSeatPlan(generatedPlan);
        saveAssignments(generatedPlan, result, operName);
        saveScores(generatedPlan, result);
        return generatedPlan;
    }

    @Override
    @Transactional
    public int confirmSeatPlan(SeatPlan seatPlan, String operName)
    {
        if (seatPlan == null || seatPlan.getPlanId() == null)
        {
            throw new ServiceException("座位方案不存在");
        }

        SeatAssignment assignmentQuery = new SeatAssignment();
        assignmentQuery.setPlanId(seatPlan.getPlanId());
        List<SeatAssignment> assignments = seatAssignmentMapper.selectSeatAssignmentList(assignmentQuery);
        if (assignments.isEmpty())
        {
            throw new ServiceException("方案没有座位分配，不能确认");
        }

        int rows = 0;
        SeatPlan activeQuery = new SeatPlan();
        activeQuery.setClassId(seatPlan.getClassId());
        activeQuery.setPlanStatus("ACTIVE");
        List<SeatPlan> activePlans = seatPlanMapper.selectSeatPlanList(activeQuery);
        for (SeatPlan activePlan : activePlans)
        {
            if (!seatPlan.getPlanId().equals(activePlan.getPlanId()))
            {
                SeatPlan archivePlan = new SeatPlan();
                archivePlan.setPlanId(activePlan.getPlanId());
                archivePlan.setPlanStatus("ARCHIVED");
                archivePlan.setUpdateBy(operName);
                archivePlan.setUpdateTime(DateUtils.getNowDate());
                rows += seatPlanMapper.updateSeatPlan(archivePlan);
            }
        }

        SeatPlan confirmPlan = new SeatPlan();
        confirmPlan.setPlanId(seatPlan.getPlanId());
        confirmPlan.setPlanStatus("ACTIVE");
        confirmPlan.setActiveTime(DateUtils.getNowDate());
        confirmPlan.setUpdateBy(operName);
        confirmPlan.setUpdateTime(DateUtils.getNowDate());
        rows += seatPlanMapper.updateSeatPlan(confirmPlan);
        return rows;
    }

    private SeatPlan buildGeneratedPlan(SeatPlan seatPlan, SeatingResult result, String operName)
    {
        SeatPlan generatedPlan = new SeatPlan();
        generatedPlan.setClassId(seatPlan.getClassId());
        generatedPlan.setClassroomId(seatPlan.getClassroomId());
        generatedPlan.setPlanName(StringUtils.defaultIfBlank(seatPlan.getPlanName(),
                "智能排座-" + DateUtils.dateTimeNow()));
        generatedPlan.setPlanType("AUTO");
        generatedPlan.setPlanStatus(StringUtils.defaultIfBlank(seatPlan.getPlanStatus(), "DRAFT"));
        generatedPlan.setTotalScore(result.getTotalScore());
        generatedPlan.setGeneratedAt(DateUtils.getNowDate());
        generatedPlan.setDelFlag("0");
        generatedPlan.setCreateBy(operName);
        generatedPlan.setRemark(seatPlan.getRemark());
        return generatedPlan;
    }

    private long resolveRandomSeed(SeatPlan seatPlan, int studentCount, int seatCount)
    {
        Object seedParam = seatPlan.getParams() == null ? null : seatPlan.getParams().get("seed");
        if (seedParam != null)
        {
            try
            {
                return Long.parseLong(seedParam.toString());
            }
            catch (NumberFormatException e)
            {
                throw new ServiceException("随机种子必须是数字");
            }
        }
        return Objects.hash(seatPlan.getClassId(), seatPlan.getClassroomId(), studentCount, seatCount);
    }

    private int resolveOptimizeIterations(SeatPlan seatPlan, int studentCount)
    {
        Object iterationsParam = seatPlan.getParams() == null ? null : seatPlan.getParams().get("iterations");
        if (iterationsParam != null)
        {
            try
            {
                return Integer.parseInt(iterationsParam.toString());
            }
            catch (NumberFormatException e)
            {
                throw new ServiceException("优化次数必须是数字");
            }
        }
        return Math.max(800, studentCount * 80);
    }

    private void saveAssignments(SeatPlan plan, SeatingResult result, String operName)
    {
        for (SeatingAssignmentResult item : result.getAssignments())
        {
            SeatAssignment assignment = new SeatAssignment();
            assignment.setPlanId(plan.getPlanId());
            assignment.setClassId(plan.getClassId());
            assignment.setClassroomId(plan.getClassroomId());
            assignment.setSeatId(item.getSeat().getSeatId());
            assignment.setStudentId(item.getStudent().getStudentId());
            assignment.setStudentNameSnapshot(item.getStudent().getStudentName());
            assignment.setRowIndex(item.getSeat().getRowIndex());
            assignment.setColIndex(item.getSeat().getColIndex());
            assignment.setIsLocked("0");
            assignment.setAssignSource("AUTO");
            assignment.setCreateBy(operName);
            assignment.setCreateTime(DateUtils.getNowDate());
            seatAssignmentMapper.insertSeatAssignment(assignment);
        }
    }

    private void saveScores(SeatPlan plan, SeatingResult result)
    {
        for (SeatingScoreItem item : result.getScoreItems())
        {
            SeatPlanScore score = new SeatPlanScore();
            score.setPlanId(plan.getPlanId());
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
