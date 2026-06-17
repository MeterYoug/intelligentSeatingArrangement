package com.ruoyi.seating.mapper;

import java.util.List;
import com.ruoyi.seating.domain.SeatPlanScore;

/**
 * 排座方案评分明细Mapper接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface SeatPlanScoreMapper 
{
    /**
     * 查询排座方案评分明细
     * 
     * @param scoreId 排座方案评分明细主键
     * @return 排座方案评分明细
     */
    public SeatPlanScore selectSeatPlanScoreByScoreId(Long scoreId);

    /**
     * 查询排座方案评分明细列表
     * 
     * @param seatPlanScore 排座方案评分明细
     * @return 排座方案评分明细集合
     */
    public List<SeatPlanScore> selectSeatPlanScoreList(SeatPlanScore seatPlanScore);

    /**
     * 新增排座方案评分明细
     * 
     * @param seatPlanScore 排座方案评分明细
     * @return 结果
     */
    public int insertSeatPlanScore(SeatPlanScore seatPlanScore);

    /**
     * 修改排座方案评分明细
     * 
     * @param seatPlanScore 排座方案评分明细
     * @return 结果
     */
    public int updateSeatPlanScore(SeatPlanScore seatPlanScore);

    /**
     * 删除排座方案评分明细
     * 
     * @param scoreId 排座方案评分明细主键
     * @return 结果
     */
    public int deleteSeatPlanScoreByScoreId(Long scoreId);

    /**
     * 批量删除排座方案评分明细
     * 
     * @param scoreIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSeatPlanScoreByScoreIds(Long[] scoreIds);
}
