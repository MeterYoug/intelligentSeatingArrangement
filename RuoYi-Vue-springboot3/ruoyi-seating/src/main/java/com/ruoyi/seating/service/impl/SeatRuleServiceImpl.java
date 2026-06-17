package com.ruoyi.seating.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.seating.mapper.SeatRuleMapper;
import com.ruoyi.seating.domain.SeatRule;
import com.ruoyi.seating.service.ISeatRuleService;

/**
 * 排座规则Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@Service
public class SeatRuleServiceImpl implements ISeatRuleService 
{
    @Autowired
    private SeatRuleMapper seatRuleMapper;

    /**
     * 查询排座规则
     * 
     * @param ruleId 排座规则主键
     * @return 排座规则
     */
    @Override
    public SeatRule selectSeatRuleByRuleId(Long ruleId)
    {
        return seatRuleMapper.selectSeatRuleByRuleId(ruleId);
    }

    /**
     * 查询排座规则列表
     * 
     * @param seatRule 排座规则
     * @return 排座规则
     */
    @Override
    public List<SeatRule> selectSeatRuleList(SeatRule seatRule)
    {
        return seatRuleMapper.selectSeatRuleList(seatRule);
    }

    /**
     * 新增排座规则
     * 
     * @param seatRule 排座规则
     * @return 结果
     */
    @Override
    public int insertSeatRule(SeatRule seatRule)
    {
        seatRule.setCreateTime(DateUtils.getNowDate());
        return seatRuleMapper.insertSeatRule(seatRule);
    }

    /**
     * 修改排座规则
     * 
     * @param seatRule 排座规则
     * @return 结果
     */
    @Override
    public int updateSeatRule(SeatRule seatRule)
    {
        seatRule.setUpdateTime(DateUtils.getNowDate());
        return seatRuleMapper.updateSeatRule(seatRule);
    }

    /**
     * 批量删除排座规则
     * 
     * @param ruleIds 需要删除的排座规则主键
     * @return 结果
     */
    @Override
    public int deleteSeatRuleByRuleIds(Long[] ruleIds)
    {
        return seatRuleMapper.deleteSeatRuleByRuleIds(ruleIds);
    }

    /**
     * 删除排座规则信息
     * 
     * @param ruleId 排座规则主键
     * @return 结果
     */
    @Override
    public int deleteSeatRuleByRuleId(Long ruleId)
    {
        return seatRuleMapper.deleteSeatRuleByRuleId(ruleId);
    }
}
