package com.ruoyi.seating.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.seating.mapper.SeatPositionMapper;
import com.ruoyi.seating.domain.SeatClassroom;
import com.ruoyi.seating.domain.SeatPosition;
import com.ruoyi.seating.service.ISeatPositionService;

/**
 * 排座座位位置Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@Service
public class SeatPositionServiceImpl implements ISeatPositionService 
{
    @Autowired
    private SeatPositionMapper seatPositionMapper;

    /**
     * 查询排座座位位置
     * 
     * @param seatId 排座座位位置主键
     * @return 排座座位位置
     */
    @Override
    public SeatPosition selectSeatPositionBySeatId(Long seatId)
    {
        return seatPositionMapper.selectSeatPositionBySeatId(seatId);
    }

    /**
     * 查询排座座位位置列表
     * 
     * @param seatPosition 排座座位位置
     * @return 排座座位位置
     */
    @Override
    public List<SeatPosition> selectSeatPositionList(SeatPosition seatPosition)
    {
        return seatPositionMapper.selectSeatPositionList(seatPosition);
    }

    /**
     * 新增排座座位位置
     * 
     * @param seatPosition 排座座位位置
     * @return 结果
     */
    @Override
    public int insertSeatPosition(SeatPosition seatPosition)
    {
        seatPosition.setCreateTime(DateUtils.getNowDate());
        return seatPositionMapper.insertSeatPosition(seatPosition);
    }

    /**
     * 修改排座座位位置
     * 
     * @param seatPosition 排座座位位置
     * @return 结果
     */
    @Override
    public int updateSeatPosition(SeatPosition seatPosition)
    {
        seatPosition.setUpdateTime(DateUtils.getNowDate());
        return seatPositionMapper.updateSeatPosition(seatPosition);
    }

    /**
     * 批量删除排座座位位置
     * 
     * @param seatIds 需要删除的排座座位位置主键
     * @return 结果
     */
    @Override
    public int deleteSeatPositionBySeatIds(Long[] seatIds)
    {
        return seatPositionMapper.deleteSeatPositionBySeatIds(seatIds);
    }

    /**
     * 删除排座座位位置信息
     * 
     * @param seatId 排座座位位置主键
     * @return 结果
     */
    @Override
    public int deleteSeatPositionBySeatId(Long seatId)
    {
        return seatPositionMapper.deleteSeatPositionBySeatId(seatId);
    }

    @Override
    @Transactional
    public int initializePositions(SeatClassroom classroom, String operName)
    {
        if (classroom.getRowCount() == null || classroom.getColCount() == null
                || classroom.getRowCount() < 1 || classroom.getColCount() < 1
                || classroom.getRowCount() > 30 || classroom.getColCount() > 30)
        {
            throw new ServiceException("座位行列数必须在 1 至 30 之间");
        }
        seatPositionMapper.deleteSeatPositionByClassroomId(classroom.getClassroomId());
        int count = 0;
        for (long row = 1; row <= classroom.getRowCount(); row++)
        {
            for (long col = 1; col <= classroom.getColCount(); col++)
            {
                SeatPosition position = new SeatPosition();
                position.setClassroomId(classroom.getClassroomId());
                position.setRowIndex(row);
                position.setColIndex(col);
                position.setSeatCode("R" + row + "C" + col);
                position.setSeatType("0");
                position.setIsAvailable("1");
                position.setStatus("0");
                position.setCreateBy(operName);
                count += insertSeatPosition(position);
            }
        }
        return count;
    }

    @Override
    @Transactional
    public int saveLayout(SeatClassroom classroom, List<SeatPosition> positionList, String operName)
    {
        validateClassroomGrid(classroom);
        long expectedCount = classroom.getRowCount() * classroom.getColCount();
        if (positionList == null || positionList.size() != expectedCount)
        {
            throw new ServiceException("座位布局数量必须等于行数乘以列数");
        }

        Set<String> coordinateSet = new HashSet<>();
        for (SeatPosition position : positionList)
        {
            validateLayoutPosition(classroom, position, coordinateSet);
        }

        seatPositionMapper.deleteSeatPositionByClassroomId(classroom.getClassroomId());
        int count = 0;
        for (SeatPosition position : positionList)
        {
            position.setSeatId(null);
            position.setClassroomId(classroom.getClassroomId());
            position.setSeatCode("R" + position.getRowIndex() + "C" + position.getColIndex());
            position.setIsAvailable("0".equals(position.getSeatType()) ? "1" : "0");
            position.setStatus(StringUtils.defaultIfBlank(position.getStatus(), "0"));
            position.setCreateBy(operName);
            count += insertSeatPosition(position);
        }
        return count;
    }

    private void validateClassroomGrid(SeatClassroom classroom)
    {
        if (classroom.getRowCount() == null || classroom.getColCount() == null
                || classroom.getRowCount() < 1 || classroom.getColCount() < 1
                || classroom.getRowCount() > 30 || classroom.getColCount() > 30)
        {
            throw new ServiceException("座位行列数必须在 1 至 30 之间");
        }
    }

    private void validateLayoutPosition(SeatClassroom classroom, SeatPosition position, Set<String> coordinateSet)
    {
        if (position == null || position.getRowIndex() == null || position.getColIndex() == null)
        {
            throw new ServiceException("座位行列坐标不能为空");
        }
        if (position.getRowIndex() < 1 || position.getRowIndex() > classroom.getRowCount()
                || position.getColIndex() < 1 || position.getColIndex() > classroom.getColCount())
        {
            throw new ServiceException("座位坐标超出教室行列范围");
        }
        if (StringUtils.isBlank(position.getSeatType()))
        {
            position.setSeatType("0");
        }
        if (!Set.of("0", "1", "2").contains(position.getSeatType()))
        {
            throw new ServiceException("座位类型无效");
        }
        String coordinate = position.getRowIndex() + "-" + position.getColIndex();
        if (!coordinateSet.add(coordinate))
        {
            throw new ServiceException("座位坐标重复：" + coordinate);
        }
    }
}
