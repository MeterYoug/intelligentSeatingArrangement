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
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.ISeatClassroomService;
import com.ruoyi.seating.service.ISeatPositionService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 排座教室布局Controller
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@RestController
@RequestMapping("/seating/classroom")
public class SeatClassroomController extends BaseController
{
    @Autowired
    private ISeatClassroomService seatClassroomService;

    @Autowired
    private ISeatClassService seatClassService;

    @Autowired
    private ISeatPositionService seatPositionService;

    /**
     * 查询排座教室布局列表
     */
    @PreAuthorize("@ss.hasPermi('seating:classroom:list')")
    @GetMapping("/list")
    public TableDataInfo list(SeatClassroom seatClassroom)
    {
        applyDataScope(seatClassroom);
        startPage();
        List<SeatClassroom> list = seatClassroomService.selectSeatClassroomList(seatClassroom);
        return getDataTable(list);
    }

    /**
     * 导出排座教室布局列表
     */
    @PreAuthorize("@ss.hasPermi('seating:classroom:export')")
    @Log(title = "排座教室布局", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SeatClassroom seatClassroom)
    {
        applyDataScope(seatClassroom);
        List<SeatClassroom> list = seatClassroomService.selectSeatClassroomList(seatClassroom);
        ExcelUtil<SeatClassroom> util = new ExcelUtil<SeatClassroom>(SeatClassroom.class);
        util.exportExcel(response, list, "排座教室布局数据");
    }

    /**
     * 获取排座教室布局详细信息
     */
    @PreAuthorize("@ss.hasPermi('seating:classroom:query')")
    @GetMapping(value = "/{classroomId}")
    public AjaxResult getInfo(@PathVariable("classroomId") Long classroomId)
    {
        return success(checkClassroomAccess(classroomId));
    }

    /**
     * 新增排座教室布局
     */
    @PreAuthorize("@ss.hasPermi('seating:classroom:add')")
    @Log(title = "排座教室布局", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SeatClassroom seatClassroom)
    {
        checkClassAccess(seatClassroom.getClassId());
        seatClassroom.setCreateBy(getUsername());
        seatClassroom.setDelFlag("0");
        if (StringUtils.isBlank(seatClassroom.getStatus()))
        {
            seatClassroom.setStatus("0");
        }
        seatClassroomService.insertSeatClassroom(seatClassroom);
        return success(seatClassroom);
    }

    /**
     * 修改排座教室布局
     */
    @PreAuthorize("@ss.hasPermi('seating:classroom:edit')")
    @Log(title = "排座教室布局", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SeatClassroom seatClassroom)
    {
        checkClassroomAccess(seatClassroom.getClassroomId());
        checkClassAccess(seatClassroom.getClassId());
        seatClassroom.setDelFlag(null);
        seatClassroom.setCreateBy(null);
        seatClassroom.setCreateTime(null);
        seatClassroom.setUpdateBy(getUsername());
        return toAjax(seatClassroomService.updateSeatClassroom(seatClassroom));
    }

    @PreAuthorize("@ss.hasPermi('seating:classroom:edit')")
    @Log(title = "教室座位初始化", businessType = BusinessType.UPDATE)
    @PostMapping("/{classroomId}/initialize")
    public AjaxResult initialize(@PathVariable Long classroomId)
    {
        SeatClassroom classroom = checkClassroomAccess(classroomId);
        return success(seatPositionService.initializePositions(classroom, getUsername()));
    }

    /**
     * 删除排座教室布局
     */
    @PreAuthorize("@ss.hasPermi('seating:classroom:remove')")
    @Log(title = "排座教室布局", businessType = BusinessType.DELETE)
	@DeleteMapping("/{classroomIds}")
    public AjaxResult remove(@PathVariable Long[] classroomIds)
    {
        for (Long classroomId : classroomIds)
        {
            checkClassroomAccess(classroomId);
        }
        return toAjax(seatClassroomService.deleteSeatClassroomByClassroomIds(classroomIds));
    }

    private void applyDataScope(SeatClassroom seatClassroom)
    {
        if (!SecurityUtils.isAdmin())
        {
            seatClassroom.getParams().put("teacherId", getUserId());
        }
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

    private SeatClassroom checkClassroomAccess(Long classroomId)
    {
        SeatClassroom classroom = seatClassroomService.selectSeatClassroomByClassroomId(classroomId);
        if (classroom == null)
        {
            throw new ServiceException("教室布局不存在");
        }
        checkClassAccess(classroom.getClassId());
        return classroom;
    }
}
