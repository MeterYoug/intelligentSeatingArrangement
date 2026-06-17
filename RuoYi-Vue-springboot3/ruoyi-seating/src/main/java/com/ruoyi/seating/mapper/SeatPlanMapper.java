package com.ruoyi.seating.mapper;

import java.util.List;
import com.ruoyi.seating.domain.SeatPlan;

/**
 * 排座方案Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface SeatPlanMapper 
{
    /**
     * 查询排座方案
     * 
     * @param planId 排座方案主键
     * @return 排座方案
     */
    public SeatPlan selectSeatPlanByPlanId(Long planId);

    /**
     * 查询排座方案列表
     * 
     * @param seatPlan 排座方案
     * @return 排座方案集合
     */
    public List<SeatPlan> selectSeatPlanList(SeatPlan seatPlan);

    /**
     * 新增排座方案
     * 
     * @param seatPlan 排座方案
     * @return 结果
     */
    public int insertSeatPlan(SeatPlan seatPlan);

    /**
     * 修改排座方案
     * 
     * @param seatPlan 排座方案
     * @return 结果
     */
    public int updateSeatPlan(SeatPlan seatPlan);

    /**
     * 删除排座方案
     * 
     * @param planId 排座方案主键
     * @return 结果
     */
    public int deleteSeatPlanByPlanId(Long planId);

    /**
     * 批量删除排座方案
     * 
     * @param planIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSeatPlanByPlanIds(Long[] planIds);
}
