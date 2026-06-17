package com.ruoyi.web.controller.seating;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.seating.domain.SeatClass;
import com.ruoyi.seating.domain.SeatClassroom;
import com.ruoyi.seating.domain.SeatPosition;
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.ISeatClassroomService;
import com.ruoyi.seating.service.ISeatPositionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 排座座位位置Controller
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@RestController
@RequestMapping("/seating/position")
public class SeatPositionController extends BaseController
{
    @Autowired
    private ISeatPositionService seatPositionService;

    @Autowired
    private ISeatClassroomService seatClassroomService;

    @Autowired
    private ISeatClassService seatClassService;

    /**
     * 查询排座座位位置列表
     */
    @PreAuthorize("@ss.hasPermi('seating:position:list')")
    @GetMapping("/list")
    public TableDataInfo list(SeatPosition seatPosition)
    {
        checkListAccess(seatPosition);
        startPage();
        List<SeatPosition> list = seatPositionService.selectSeatPositionList(seatPosition);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('seating:classroom:query')")
    @GetMapping("/classroom/{classroomId}/layout")
    public AjaxResult layout(@PathVariable Long classroomId)
    {
        checkClassroomAccess(classroomId);
        SeatPosition query = new SeatPosition();
        query.setClassroomId(classroomId);
        return success(seatPositionService.selectSeatPositionList(query));
    }

    /**
     * 导出排座座位位置列表
     */
    @PreAuthorize("@ss.hasPermi('seating:position:export')")
    @Log(title = "排座座位位置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SeatPosition seatPosition)
    {
        checkListAccess(seatPosition);
        List<SeatPosition> list = seatPositionService.selectSeatPositionList(seatPosition);
        ExcelUtil<SeatPosition> util = new ExcelUtil<SeatPosition>(SeatPosition.class);
        util.exportExcel(response, list, "排座座位位置数据");
    }

    /**
     * 获取排座座位位置详细信息
     */
    @PreAuthorize("@ss.hasPermi('seating:position:query')")
    @GetMapping(value = "/{seatId}")
    public AjaxResult getInfo(@PathVariable("seatId") Long seatId)
    {
        return success(checkSeatAccess(seatId));
    }

    /**
     * 新增排座座位位置
     */
    @PreAuthorize("@ss.hasPermi('seating:position:add')")
    @Log(title = "排座座位位置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SeatPosition seatPosition)
    {
        checkClassroomAccess(seatPosition.getClassroomId());
        seatPosition.setCreateBy(getUsername());
        if (StringUtils.isBlank(seatPosition.getStatus()))
        {
            seatPosition.setStatus("0");
        }
        return toAjax(seatPositionService.insertSeatPosition(seatPosition));
    }

    /**
     * 修改排座座位位置
     */
    @PreAuthorize("@ss.hasPermi('seating:position:edit')")
    @Log(title = "排座座位位置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SeatPosition seatPosition)
    {
        SeatPosition oldPosition = checkSeatAccess(seatPosition.getSeatId());
        Long classroomId = seatPosition.getClassroomId() == null ? oldPosition.getClassroomId() : seatPosition.getClassroomId();
        checkClassroomAccess(classroomId);
        seatPosition.setClassroomId(classroomId);
        seatPosition.setCreateBy(null);
        seatPosition.setCreateTime(null);
        seatPosition.setUpdateBy(getUsername());
        return toAjax(seatPositionService.updateSeatPosition(seatPosition));
    }

    @PreAuthorize("@ss.hasPermi('seating:classroom:edit')")
    @Log(title = "教室座位布局", businessType = BusinessType.UPDATE)
    @PutMapping("/classroom/{classroomId}/layout")
    public AjaxResult saveLayout(@PathVariable Long classroomId, @RequestBody List<SeatPosition> positionList)
    {
        SeatClassroom classroom = checkClassroomAccess(classroomId);
        return success(seatPositionService.saveLayout(classroom, positionList, getUsername()));
    }

    /**
     * 删除排座座位位置
     */
    @PreAuthorize("@ss.hasPermi('seating:position:remove')")
    @Log(title = "排座座位位置", businessType = BusinessType.DELETE)
	@DeleteMapping("/{seatIds}")
    public AjaxResult remove(@PathVariable Long[] seatIds)
    {
        for (Long seatId : seatIds)
        {
            checkSeatAccess(seatId);
        }
        return toAjax(seatPositionService.deleteSeatPositionBySeatIds(seatIds));
    }

    private void checkListAccess(SeatPosition seatPosition)
    {
        if (seatPosition.getClassroomId() != null)
        {
            checkClassroomAccess(seatPosition.getClassroomId());
        }
        else if (!SecurityUtils.isAdmin())
        {
            throw new ServiceException("请选择教室布局");
        }
    }

    private SeatPosition checkSeatAccess(Long seatId)
    {
        SeatPosition seatPosition = seatPositionService.selectSeatPositionBySeatId(seatId);
        if (seatPosition == null)
        {
            throw new ServiceException("座位不存在");
        }
        checkClassroomAccess(seatPosition.getClassroomId());
        return seatPosition;
    }

    private SeatClassroom checkClassroomAccess(Long classroomId)
    {
        if (classroomId == null)
        {
            throw new ServiceException("请选择教室布局");
        }
        SeatClassroom classroom = seatClassroomService.selectSeatClassroomByClassroomId(classroomId);
        if (classroom == null)
        {
            throw new ServiceException("教室布局不存在");
        }
        checkClassAccess(classroom.getClassId());
        return classroom;
    }

    private SeatClass checkClassAccess(Long classId)
    {
        SeatClass seatClass = seatClassService.selectSeatClassByClassId(classId);
        if (seatClass == null)
        {
            throw new ServiceException("班级不存在");
        }
        if (!SecurityUtils.isAdmin() && !getUserId().equals(seatClass.getTeacherId()))
        {
            throw new ServiceException("无权操作该班级");
        }
        return seatClass;
    }
}
