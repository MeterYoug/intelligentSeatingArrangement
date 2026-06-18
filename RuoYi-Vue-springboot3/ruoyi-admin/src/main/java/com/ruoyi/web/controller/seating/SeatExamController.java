package com.ruoyi.web.controller.seating;

import java.util.List;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.seating.domain.SeatClass;
import com.ruoyi.seating.domain.SeatExam;
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.ISeatExamService;

@RestController
@RequestMapping("/seating/exam")
public class SeatExamController extends BaseController
{
    @Autowired
    private ISeatExamService seatExamService;

    @Autowired
    private ISeatClassService seatClassService;

    @PreAuthorize("@ss.hasPermi('seating:exam:list')")
    @GetMapping("/list")
    public TableDataInfo list(SeatExam seatExam)
    {
        applyDataScope(seatExam);
        startPage();
        List<SeatExam> list = seatExamService.selectSeatExamList(seatExam);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('seating:exam:export')")
    @Log(title = "成绩考试批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SeatExam seatExam)
    {
        applyDataScope(seatExam);
        List<SeatExam> list = seatExamService.selectSeatExamList(seatExam);
        ExcelUtil<SeatExam> util = new ExcelUtil<>(SeatExam.class);
        util.exportExcel(response, list, "成绩考试批次数据");
    }

    @PreAuthorize("@ss.hasPermi('seating:exam:query')")
    @GetMapping(value = "/{examId}")
    public AjaxResult getInfo(@PathVariable("examId") Long examId)
    {
        return success(checkExamAccess(examId));
    }

    @PreAuthorize("@ss.hasPermi('seating:exam:add')")
    @Log(title = "成绩考试批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SeatExam seatExam)
    {
        checkClassAccess(seatExam.getClassId());
        seatExam.setCreateBy(getUsername());
        return toAjax(seatExamService.insertSeatExam(seatExam));
    }

    @PreAuthorize("@ss.hasPermi('seating:exam:edit')")
    @Log(title = "成绩考试批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SeatExam seatExam)
    {
        checkExamAccess(seatExam.getExamId());
        if (seatExam.getClassId() != null)
        {
            checkClassAccess(seatExam.getClassId());
        }
        seatExam.setDelFlag(null);
        seatExam.setCreateBy(null);
        seatExam.setCreateTime(null);
        seatExam.setUpdateBy(getUsername());
        return toAjax(seatExamService.updateSeatExam(seatExam));
    }

    @PreAuthorize("@ss.hasPermi('seating:exam:edit')")
    @Log(title = "成绩考试批次", businessType = BusinessType.UPDATE)
    @PutMapping("/{examId}/current")
    public AjaxResult current(@PathVariable Long examId)
    {
        checkExamAccess(examId);
        return toAjax(seatExamService.setCurrentExam(examId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('seating:exam:remove')")
    @Log(title = "成绩考试批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{examIds}")
    public AjaxResult remove(@PathVariable Long[] examIds)
    {
        for (Long examId : examIds)
        {
            checkExamAccess(examId);
        }
        return toAjax(seatExamService.deleteSeatExamByExamIds(examIds));
    }

    private void applyDataScope(SeatExam seatExam)
    {
        if (!SecurityUtils.isAdmin())
        {
            seatExam.getParams().put("teacherId", getUserId());
        }
    }

    private SeatExam checkExamAccess(Long examId)
    {
        SeatExam exam = seatExamService.selectSeatExamByExamId(examId);
        if (exam == null)
        {
            throw new ServiceException("考试批次不存在");
        }
        checkClassAccess(exam.getClassId());
        return exam;
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
