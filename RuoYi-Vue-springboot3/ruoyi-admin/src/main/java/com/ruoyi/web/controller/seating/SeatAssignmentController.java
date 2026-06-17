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
import com.ruoyi.seating.domain.SeatAssignment;
import com.ruoyi.seating.domain.SeatClass;
import com.ruoyi.seating.domain.SeatPlan;
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.ISeatAssignmentService;
import com.ruoyi.seating.service.ISeatPlanService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 排座分配Controller
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@RestController
@RequestMapping("/seating/assignment")
public class SeatAssignmentController extends BaseController
{
    @Autowired
    private ISeatAssignmentService seatAssignmentService;

    @Autowired
    private ISeatPlanService seatPlanService;

    @Autowired
    private ISeatClassService seatClassService;

    /**
     * 查询排座分配列表
     */
    @PreAuthorize("@ss.hasPermi('seating:assignment:list')")
    @GetMapping("/list")
    public TableDataInfo list(SeatAssignment seatAssignment)
    {
        startPage();
        List<SeatAssignment> list = seatAssignmentService.selectSeatAssignmentList(seatAssignment);
        return getDataTable(list);
    }

    /**
     * 导出排座分配列表
     */
    @PreAuthorize("@ss.hasPermi('seating:assignment:export')")
    @Log(title = "排座分配", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SeatAssignment seatAssignment)
    {
        List<SeatAssignment> list = seatAssignmentService.selectSeatAssignmentList(seatAssignment);
        ExcelUtil<SeatAssignment> util = new ExcelUtil<SeatAssignment>(SeatAssignment.class);
        util.exportExcel(response, list, "排座分配数据");
    }

    /**
     * 获取排座分配详细信息
     */
    @PreAuthorize("@ss.hasPermi('seating:assignment:query')")
    @GetMapping(value = "/{assignmentId}")
    public AjaxResult getInfo(@PathVariable("assignmentId") Long assignmentId)
    {
        return success(seatAssignmentService.selectSeatAssignmentByAssignmentId(assignmentId));
    }

    /**
     * 新增排座分配
     */
    @PreAuthorize("@ss.hasPermi('seating:assignment:add')")
    @Log(title = "排座分配", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SeatAssignment seatAssignment)
    {
        return toAjax(seatAssignmentService.insertSeatAssignment(seatAssignment));
    }

    /**
     * 修改排座分配
     */
    @PreAuthorize("@ss.hasPermi('seating:assignment:edit')")
    @Log(title = "排座分配", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SeatAssignment seatAssignment)
    {
        return toAjax(seatAssignmentService.updateSeatAssignment(seatAssignment));
    }

    @PreAuthorize("@ss.hasPermi('seating:plan:edit')")
    @Log(title = "排座分配", businessType = BusinessType.UPDATE)
    @PutMapping("/plan/{planId}")
    public AjaxResult savePlanAssignments(@PathVariable Long planId, @RequestBody List<SeatAssignment> assignmentList)
    {
        checkPlanAccess(planId);
        return success(seatAssignmentService.savePlanAssignments(planId, assignmentList, getUsername()));
    }

    /**
     * 删除排座分配
     */
    @PreAuthorize("@ss.hasPermi('seating:assignment:remove')")
    @Log(title = "排座分配", businessType = BusinessType.DELETE)
	@DeleteMapping("/{assignmentIds}")
    public AjaxResult remove(@PathVariable Long[] assignmentIds)
    {
        return toAjax(seatAssignmentService.deleteSeatAssignmentByAssignmentIds(assignmentIds));
    }

    private SeatPlan checkPlanAccess(Long planId)
    {
        SeatPlan plan = seatPlanService.selectSeatPlanByPlanId(planId);
        if (plan == null)
        {
            throw new ServiceException("座位方案不存在");
        }
        SeatClass seatClass = seatClassService.selectSeatClassByClassId(plan.getClassId());
        if (seatClass == null)
        {
            throw new ServiceException("班级不存在");
        }
        if (!SecurityUtils.isAdmin() && !getUserId().equals(seatClass.getTeacherId()))
        {
            throw new ServiceException("无权操作该方案");
        }
        return plan;
    }
}
