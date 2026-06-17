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
import com.ruoyi.seating.domain.SeatStudentRelation;
import com.ruoyi.seating.service.ISeatStudentRelationService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 排座学生关系约束Controller
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@RestController
@RequestMapping("/seating/relation")
public class SeatStudentRelationController extends BaseController
{
    @Autowired
    private ISeatStudentRelationService seatStudentRelationService;

    /**
     * 查询排座学生关系约束列表
     */
    @PreAuthorize("@ss.hasPermi('seating:relation:list')")
    @GetMapping("/list")
    public TableDataInfo list(SeatStudentRelation seatStudentRelation)
    {
        startPage();
        List<SeatStudentRelation> list = seatStudentRelationService.selectSeatStudentRelationList(seatStudentRelation);
        return getDataTable(list);
    }

    /**
     * 导出排座学生关系约束列表
     */
    @PreAuthorize("@ss.hasPermi('seating:relation:export')")
    @Log(title = "排座学生关系约束", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SeatStudentRelation seatStudentRelation)
    {
        List<SeatStudentRelation> list = seatStudentRelationService.selectSeatStudentRelationList(seatStudentRelation);
        ExcelUtil<SeatStudentRelation> util = new ExcelUtil<SeatStudentRelation>(SeatStudentRelation.class);
        util.exportExcel(response, list, "排座学生关系约束数据");
    }

    /**
     * 获取排座学生关系约束详细信息
     */
    @PreAuthorize("@ss.hasPermi('seating:relation:query')")
    @GetMapping(value = "/{relationId}")
    public AjaxResult getInfo(@PathVariable("relationId") Long relationId)
    {
        return success(seatStudentRelationService.selectSeatStudentRelationByRelationId(relationId));
    }

    /**
     * 新增排座学生关系约束
     */
    @PreAuthorize("@ss.hasPermi('seating:relation:add')")
    @Log(title = "排座学生关系约束", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SeatStudentRelation seatStudentRelation)
    {
        return toAjax(seatStudentRelationService.insertSeatStudentRelation(seatStudentRelation));
    }

    /**
     * 修改排座学生关系约束
     */
    @PreAuthorize("@ss.hasPermi('seating:relation:edit')")
    @Log(title = "排座学生关系约束", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SeatStudentRelation seatStudentRelation)
    {
        return toAjax(seatStudentRelationService.updateSeatStudentRelation(seatStudentRelation));
    }

    /**
     * 删除排座学生关系约束
     */
    @PreAuthorize("@ss.hasPermi('seating:relation:remove')")
    @Log(title = "排座学生关系约束", businessType = BusinessType.DELETE)
	@DeleteMapping("/{relationIds}")
    public AjaxResult remove(@PathVariable Long[] relationIds)
    {
        return toAjax(seatStudentRelationService.deleteSeatStudentRelationByRelationIds(relationIds));
    }
}
