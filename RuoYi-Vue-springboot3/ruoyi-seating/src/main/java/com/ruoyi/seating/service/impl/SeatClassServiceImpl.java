package com.ruoyi.seating.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.seating.mapper.SeatClassMapper;
import com.ruoyi.seating.domain.SeatClass;
import com.ruoyi.seating.service.ISeatClassService;

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

    /**
     * 查询排座班级
     * 
     * @param classId 排座班级主键
     * @return 排座班级
     */
    @Override
    public SeatClass selectSeatClassByClassId(Long classId)
    {
        return seatClassMapper.selectSeatClassByClassId(classId);
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
        return seatClassMapper.selectSeatClassList(seatClass);
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
}
