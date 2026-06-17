package com.ruoyi.seating.service;

import java.util.List;
import com.ruoyi.seating.domain.SeatRule;

/**
 * 排座规则Service接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface ISeatRuleService 
{
    /**
     * 查询排座规则
     * 
     * @param ruleId 排座规则主键
     * @return 排座规则
     */
    public SeatRule selectSeatRuleByRuleId(Long ruleId);

    /**
     * 查询排座规则列表
     * 
     * @param seatRule 排座规则
     * @return 排座规则集合
     */
    public List<SeatRule> selectSeatRuleList(SeatRule seatRule);

    /**
     * 新增排座规则
     * 
     * @param seatRule 排座规则
     * @return 结果
     */
    public int insertSeatRule(SeatRule seatRule);

    /**
     * 修改排座规则
     * 
     * @param seatRule 排座规则
     * @return 结果
     */
    public int updateSeatRule(SeatRule seatRule);

    /**
     * 批量删除排座规则
     * 
     * @param ruleIds 需要删除的排座规则主键集合
     * @return 结果
     */
    public int deleteSeatRuleByRuleIds(Long[] ruleIds);

    /**
     * 删除排座规则信息
     * 
     * @param ruleId 排座规则主键
     * @return 结果
     */
    public int deleteSeatRuleByRuleId(Long ruleId);
}
