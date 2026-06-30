package com.ruoyi.seating.service;

import java.util.List;
import com.ruoyi.seating.domain.SeatClass;
import com.ruoyi.seating.domain.request.SeatClassCopyRequest;

/**
 * 排座班级Service接口
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
public interface ISeatClassService 
{
    /**
     * 查询排座班级
     * 
     * @param classId 排座班级主键
     * @return 排座班级
     */
    public SeatClass selectSeatClassByClassId(Long classId);

    /**
     * 查询排座班级列表
     * 
     * @param seatClass 排座班级
     * @return 排座班级集合
     */
    public List<SeatClass> selectSeatClassList(SeatClass seatClass);

    /**
     * 新增排座班级
     * 
     * @param seatClass 排座班级
     * @return 结果
     */
    public int insertSeatClass(SeatClass seatClass);

    /**
     * 修改排座班级
     * 
     * @param seatClass 排座班级
     * @return 结果
     */
    public int updateSeatClass(SeatClass seatClass);

    /**
     * 批量删除排座班级
     * 
     * @param classIds 需要删除的排座班级主键集合
     * @return 结果
     */
    public int deleteSeatClassByClassIds(Long[] classIds);

    /**
     * 删除排座班级信息
     * 
     * @param classId 排座班级主键
     * @return 结果
     */
    public int deleteSeatClassByClassId(Long classId);

    /**
     * 新学期复制班级。
     *
     * @param classId 源班级主键
     * @param copyRequest 复制参数
     * @param operName 操作人
     * @return 新班级
     */
    public SeatClass copyNewTerm(Long classId, SeatClassCopyRequest copyRequest, String operName);
}
