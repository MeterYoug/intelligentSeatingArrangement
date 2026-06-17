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
import com.ruoyi.seating.domain.SeatRule;
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.ISeatRuleService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 排座规则Controller
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@RestController
@RequestMapping("/seating/rule")
public class SeatRuleController extends BaseController
{
    @Autowired
    private ISeatRuleService seatRuleService;

    @Autowired
    private ISeatClassService seatClassService;

    /**
     * 查询排座规则列表
     */
    @PreAuthorize("@ss.hasPermi('seating:rule:list')")
    @GetMapping("/list")
    public TableDataInfo list(SeatRule seatRule)
    {
        applyDataScope(seatRule);
        startPage();
        List<SeatRule> list = seatRuleService.selectSeatRuleList(seatRule);
        return getDataTable(list);
    }

    /**
     * 导出排座规则列表
     */
    @PreAuthorize("@ss.hasPermi('seating:rule:export')")
    @Log(title = "排座规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SeatRule seatRule)
    {
        applyDataScope(seatRule);
        List<SeatRule> list = seatRuleService.selectSeatRuleList(seatRule);
        ExcelUtil<SeatRule> util = new ExcelUtil<SeatRule>(SeatRule.class);
        util.exportExcel(response, list, "排座规则数据");
    }

    /**
     * 获取排座规则详细信息
     */
    @PreAuthorize("@ss.hasPermi('seating:rule:query')")
    @GetMapping(value = "/{ruleId}")
    public AjaxResult getInfo(@PathVariable("ruleId") Long ruleId)
    {
        return success(checkRuleAccess(ruleId));
    }

    /**
     * 新增排座规则
     */
    @PreAuthorize("@ss.hasPermi('seating:rule:add')")
    @Log(title = "排座规则", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SeatRule seatRule)
    {
        checkClassAccess(seatRule.getClassId());
        seatRule.setCreateBy(getUsername());
        seatRule.setDelFlag("0");
        if (StringUtils.isBlank(seatRule.getStatus()))
        {
            seatRule.setStatus("0");
        }
        return toAjax(seatRuleService.insertSeatRule(seatRule));
    }

    /**
     * 修改排座规则
     */
    @PreAuthorize("@ss.hasPermi('seating:rule:edit')")
    @Log(title = "排座规则", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SeatRule seatRule)
    {
        checkRuleAccess(seatRule.getRuleId());
        checkClassAccess(seatRule.getClassId());
        seatRule.setDelFlag(null);
        seatRule.setCreateBy(null);
        seatRule.setCreateTime(null);
        seatRule.setUpdateBy(getUsername());
        return toAjax(seatRuleService.updateSeatRule(seatRule));
    }

    /**
     * 删除排座规则
     */
    @PreAuthorize("@ss.hasPermi('seating:rule:remove')")
    @Log(title = "排座规则", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ruleIds}")
    public AjaxResult remove(@PathVariable Long[] ruleIds)
    {
        for (Long ruleId : ruleIds)
        {
            checkRuleAccess(ruleId);
        }
        return toAjax(seatRuleService.deleteSeatRuleByRuleIds(ruleIds));
    }

    private void applyDataScope(SeatRule seatRule)
    {
        if (!SecurityUtils.isAdmin())
        {
            seatRule.getParams().put("teacherId", getUserId());
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

    private SeatRule checkRuleAccess(Long ruleId)
    {
        SeatRule rule = seatRuleService.selectSeatRuleByRuleId(ruleId);
        if (rule == null)
        {
            throw new ServiceException("排座规则不存在");
        }
        checkClassAccess(rule.getClassId());
        return rule;
    }
}
