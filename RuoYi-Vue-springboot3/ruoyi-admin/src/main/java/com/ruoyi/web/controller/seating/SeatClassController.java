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
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 排座班级Controller
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@RestController
@RequestMapping("/seating/class")
public class SeatClassController extends BaseController
{
    @Autowired
    private ISeatClassService seatClassService;

    /**
     * 查询排座班级列表
     */
    @PreAuthorize("@ss.hasPermi('seating:class:list')")
    @GetMapping("/list")
    public TableDataInfo list(SeatClass seatClass)
    {
        applyDataScope(seatClass);
        startPage();
        List<SeatClass> list = seatClassService.selectSeatClassList(seatClass);
        return getDataTable(list);
    }

    /**
     * 导出排座班级列表
     */
    @PreAuthorize("@ss.hasPermi('seating:class:export')")
    @Log(title = "排座班级", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SeatClass seatClass)
    {
        applyDataScope(seatClass);
        List<SeatClass> list = seatClassService.selectSeatClassList(seatClass);
        ExcelUtil<SeatClass> util = new ExcelUtil<SeatClass>(SeatClass.class);
        util.exportExcel(response, list, "排座班级数据");
    }

    /**
     * 获取排座班级详细信息
     */
    @PreAuthorize("@ss.hasPermi('seating:class:query')")
    @GetMapping(value = "/{classId}")
    public AjaxResult getInfo(@PathVariable("classId") Long classId)
    {
        return success(checkClassAccess(classId));
    }

    /**
     * 新增排座班级
     */
    @PreAuthorize("@ss.hasPermi('seating:class:add')")
    @Log(title = "排座班级", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SeatClass seatClass)
    {
        seatClass.setTeacherId(getUserId());
        seatClass.setDeptId(getDeptId());
        seatClass.setCreateBy(getUsername());
        seatClass.setDelFlag("0");
        if (StringUtils.isBlank(seatClass.getStatus()))
        {
            seatClass.setStatus("0");
        }
        return toAjax(seatClassService.insertSeatClass(seatClass));
    }

    /**
     * 修改排座班级
     */
    @PreAuthorize("@ss.hasPermi('seating:class:edit')")
    @Log(title = "排座班级", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SeatClass seatClass)
    {
        checkClassAccess(seatClass.getClassId());
        seatClass.setTeacherId(null);
        seatClass.setDeptId(null);
        seatClass.setDelFlag(null);
        seatClass.setCreateBy(null);
        seatClass.setCreateTime(null);
        seatClass.setUpdateBy(getUsername());
        return toAjax(seatClassService.updateSeatClass(seatClass));
    }

    /**
     * 删除排座班级
     */
    @PreAuthorize("@ss.hasPermi('seating:class:remove')")
    @Log(title = "排座班级", businessType = BusinessType.DELETE)
	@DeleteMapping("/{classIds}")
    public AjaxResult remove(@PathVariable Long[] classIds)
    {
        for (Long classId : classIds)
        {
            checkClassAccess(classId);
        }
        return toAjax(seatClassService.deleteSeatClassByClassIds(classIds));
    }

    private void applyDataScope(SeatClass seatClass)
    {
        if (!SecurityUtils.isAdmin())
        {
            seatClass.setTeacherId(getUserId());
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
}
