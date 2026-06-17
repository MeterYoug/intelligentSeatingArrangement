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
import com.ruoyi.seating.domain.SeatClass;
import com.ruoyi.seating.domain.SeatClassroom;
import com.ruoyi.seating.domain.SeatPlan;
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.ISeatClassroomService;
import com.ruoyi.seating.service.ISeatPlanService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 排座方案Controller
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@RestController
@RequestMapping("/seating/plan")
public class SeatPlanController extends BaseController
{
    @Autowired
    private ISeatPlanService seatPlanService;

    @Autowired
    private ISeatClassService seatClassService;

    @Autowired
    private ISeatClassroomService seatClassroomService;

    /**
     * 查询排座方案列表
     */
    @PreAuthorize("@ss.hasPermi('seating:plan:list')")
    @GetMapping("/list")
    public TableDataInfo list(SeatPlan seatPlan)
    {
        applyDataScope(seatPlan);
        startPage();
        List<SeatPlan> list = seatPlanService.selectSeatPlanList(seatPlan);
        return getDataTable(list);
    }

    /**
     * 导出排座方案列表
     */
    @PreAuthorize("@ss.hasPermi('seating:plan:export')")
    @Log(title = "排座方案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SeatPlan seatPlan)
    {
        applyDataScope(seatPlan);
        List<SeatPlan> list = seatPlanService.selectSeatPlanList(seatPlan);
        ExcelUtil<SeatPlan> util = new ExcelUtil<SeatPlan>(SeatPlan.class);
        util.exportExcel(response, list, "排座方案数据");
    }

    /**
     * 获取排座方案详细信息
     */
    @PreAuthorize("@ss.hasPermi('seating:plan:query')")
    @GetMapping(value = "/{planId}")
    public AjaxResult getInfo(@PathVariable("planId") Long planId)
    {
        return success(checkPlanAccess(planId));
    }

    /**
     * 新增排座方案
     */
    @PreAuthorize("@ss.hasPermi('seating:plan:add')")
    @Log(title = "排座方案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SeatPlan seatPlan)
    {
        checkPlanRelations(seatPlan);
        seatPlan.setCreateBy(getUsername());
        seatPlan.setDelFlag("0");
        return toAjax(seatPlanService.insertSeatPlan(seatPlan));
    }

    /**
     * 智能生成座位方案
     */
    @PreAuthorize("@ss.hasPermi('seating:plan:generate')")
    @Log(title = "排座方案", businessType = BusinessType.INSERT)
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody SeatPlan seatPlan)
    {
        checkPlanRelations(seatPlan);
        return success(seatPlanService.generateSeatPlan(seatPlan, getUsername()));
    }

    /**
     * 确认座位方案
     */
    @PreAuthorize("@ss.hasPermi('seating:plan:edit')")
    @Log(title = "排座方案", businessType = BusinessType.UPDATE)
    @PutMapping("/{planId}/confirm")
    public AjaxResult confirm(@PathVariable("planId") Long planId)
    {
        SeatPlan plan = checkPlanAccess(planId);
        return toAjax(seatPlanService.confirmSeatPlan(plan, getUsername()));
    }

    /**
     * 修改排座方案
     */
    @PreAuthorize("@ss.hasPermi('seating:plan:edit')")
    @Log(title = "排座方案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SeatPlan seatPlan)
    {
        checkPlanAccess(seatPlan.getPlanId());
        checkPlanRelations(seatPlan);
        seatPlan.setDelFlag(null);
        seatPlan.setCreateBy(null);
        seatPlan.setCreateTime(null);
        seatPlan.setUpdateBy(getUsername());
        return toAjax(seatPlanService.updateSeatPlan(seatPlan));
    }

    /**
     * 删除排座方案
     */
    @PreAuthorize("@ss.hasPermi('seating:plan:remove')")
    @Log(title = "排座方案", businessType = BusinessType.DELETE)
	@DeleteMapping("/{planIds}")
    public AjaxResult remove(@PathVariable Long[] planIds)
    {
        for (Long planId : planIds)
        {
            checkPlanAccess(planId);
        }
        return toAjax(seatPlanService.deleteSeatPlanByPlanIds(planIds));
    }

    private void applyDataScope(SeatPlan seatPlan)
    {
        if (!SecurityUtils.isAdmin())
        {
            seatPlan.getParams().put("teacherId", getUserId());
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

    private void checkPlanRelations(SeatPlan plan)
    {
        checkClassAccess(plan.getClassId());
        SeatClassroom classroom = seatClassroomService.selectSeatClassroomByClassroomId(plan.getClassroomId());
        if (classroom == null || !plan.getClassId().equals(classroom.getClassId()))
        {
            throw new ServiceException("教室布局不属于所选班级");
        }
    }

    private SeatPlan checkPlanAccess(Long planId)
    {
        SeatPlan plan = seatPlanService.selectSeatPlanByPlanId(planId);
        if (plan == null)
        {
            throw new ServiceException("座位方案不存在");
        }
        checkClassAccess(plan.getClassId());
        return plan;
    }
}
