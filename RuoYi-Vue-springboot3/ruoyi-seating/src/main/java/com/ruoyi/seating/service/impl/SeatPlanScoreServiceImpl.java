package com.ruoyi.seating.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.seating.mapper.SeatPlanScoreMapper;
import com.ruoyi.seating.domain.SeatPlanScore;
import com.ruoyi.seating.service.ISeatPlanScoreService;

/**
 * 排座方案评分明细Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@Service
public class SeatPlanScoreServiceImpl implements ISeatPlanScoreService 
{
    @Autowired
    private SeatPlanScoreMapper seatPlanScoreMapper;

    /**
     * 查询排座方案评分明细
     * 
     * @param scoreId 排座方案评分明细主键
     * @return 排座方案评分明细
     */
    @Override
    public SeatPlanScore selectSeatPlanScoreByScoreId(Long scoreId)
    {
        return seatPlanScoreMapper.selectSeatPlanScoreByScoreId(scoreId);
    }

    /**
     * 查询排座方案评分明细列表
     * 
     * @param seatPlanScore 排座方案评分明细
     * @return 排座方案评分明细
     */
    @Override
    public List<SeatPlanScore> selectSeatPlanScoreList(SeatPlanScore seatPlanScore)
    {
        return seatPlanScoreMapper.selectSeatPlanScoreList(seatPlanScore);
    }

    /**
     * 新增排座方案评分明细
     * 
     * @param seatPlanScore 排座方案评分明细
     * @return 结果
     */
    @Override
    public int insertSeatPlanScore(SeatPlanScore seatPlanScore)
    {
        seatPlanScore.setCreateTime(DateUtils.getNowDate());
        return seatPlanScoreMapper.insertSeatPlanScore(seatPlanScore);
    }

    /**
     * 修改排座方案评分明细
     * 
     * @param seatPlanScore 排座方案评分明细
     * @return 结果
     */
    @Override
    public int updateSeatPlanScore(SeatPlanScore seatPlanScore)
    {
        return seatPlanScoreMapper.updateSeatPlanScore(seatPlanScore);
    }

    /**
     * 批量删除排座方案评分明细
     * 
     * @param scoreIds 需要删除的排座方案评分明细主键
     * @return 结果
     */
    @Override
    public int deleteSeatPlanScoreByScoreIds(Long[] scoreIds)
    {
        return seatPlanScoreMapper.deleteSeatPlanScoreByScoreIds(scoreIds);
    }

    /**
     * 删除排座方案评分明细信息
     * 
     * @param scoreId 排座方案评分明细主键
     * @return 结果
     */
    @Override
    public int deleteSeatPlanScoreByScoreId(Long scoreId)
    {
        return seatPlanScoreMapper.deleteSeatPlanScoreByScoreId(scoreId);
    }
}
