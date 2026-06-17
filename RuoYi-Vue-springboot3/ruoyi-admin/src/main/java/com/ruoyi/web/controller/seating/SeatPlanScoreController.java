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
import com.ruoyi.seating.domain.SeatPlanScore;
import com.ruoyi.seating.service.ISeatPlanScoreService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 排座方案评分明细Controller
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@RestController
@RequestMapping("/seating/score")
public class SeatPlanScoreController extends BaseController
{
    @Autowired
    private ISeatPlanScoreService seatPlanScoreService;

    /**
     * 查询排座方案评分明细列表
     */
    @PreAuthorize("@ss.hasPermi('seating:score:list')")
    @GetMapping("/list")
    public TableDataInfo list(SeatPlanScore seatPlanScore)
    {
        startPage();
        List<SeatPlanScore> list = seatPlanScoreService.selectSeatPlanScoreList(seatPlanScore);
        return getDataTable(list);
    }

    /**
     * 导出排座方案评分明细列表
     */
    @PreAuthorize("@ss.hasPermi('seating:score:export')")
    @Log(title = "排座方案评分明细", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SeatPlanScore seatPlanScore)
    {
        List<SeatPlanScore> list = seatPlanScoreService.selectSeatPlanScoreList(seatPlanScore);
        ExcelUtil<SeatPlanScore> util = new ExcelUtil<SeatPlanScore>(SeatPlanScore.class);
        util.exportExcel(response, list, "排座方案评分明细数据");
    }

    /**
     * 获取排座方案评分明细详细信息
     */
    @PreAuthorize("@ss.hasPermi('seating:score:query')")
    @GetMapping(value = "/{scoreId}")
    public AjaxResult getInfo(@PathVariable("scoreId") Long scoreId)
    {
        return success(seatPlanScoreService.selectSeatPlanScoreByScoreId(scoreId));
    }

    /**
     * 新增排座方案评分明细
     */
    @PreAuthorize("@ss.hasPermi('seating:score:add')")
    @Log(title = "排座方案评分明细", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SeatPlanScore seatPlanScore)
    {
        return toAjax(seatPlanScoreService.insertSeatPlanScore(seatPlanScore));
    }

    /**
     * 修改排座方案评分明细
     */
    @PreAuthorize("@ss.hasPermi('seating:score:edit')")
    @Log(title = "排座方案评分明细", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SeatPlanScore seatPlanScore)
    {
        return toAjax(seatPlanScoreService.updateSeatPlanScore(seatPlanScore));
    }

    /**
     * 删除排座方案评分明细
     */
    @PreAuthorize("@ss.hasPermi('seating:score:remove')")
    @Log(title = "排座方案评分明细", businessType = BusinessType.DELETE)
	@DeleteMapping("/{scoreIds}")
    public AjaxResult remove(@PathVariable Long[] scoreIds)
    {
        return toAjax(seatPlanScoreService.deleteSeatPlanScoreByScoreIds(scoreIds));
    }
}
