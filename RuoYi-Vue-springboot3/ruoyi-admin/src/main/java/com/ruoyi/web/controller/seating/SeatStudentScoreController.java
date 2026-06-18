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
import org.springframework.web.multipart.MultipartFile;
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
import com.ruoyi.seating.domain.SeatStudentScore;
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.ISeatExamService;
import com.ruoyi.seating.service.ISeatStudentScoreService;

@RestController
@RequestMapping("/seating/student-score")
public class SeatStudentScoreController extends BaseController
{
    @Autowired
    private ISeatStudentScoreService seatStudentScoreService;

    @Autowired
    private ISeatExamService seatExamService;

    @Autowired
    private ISeatClassService seatClassService;

    @PreAuthorize("@ss.hasPermi('seating:studentScore:list')")
    @GetMapping("/list")
    public TableDataInfo list(SeatStudentScore seatStudentScore)
    {
        applyDataScope(seatStudentScore);
        startPage();
        List<SeatStudentScore> list = seatStudentScoreService.selectSeatStudentScoreList(seatStudentScore);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('seating:studentScore:export')")
    @Log(title = "学生成绩", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SeatStudentScore seatStudentScore)
    {
        applyDataScope(seatStudentScore);
        List<SeatStudentScore> list = seatStudentScoreService.selectSeatStudentScoreList(seatStudentScore);
        ExcelUtil<SeatStudentScore> util = new ExcelUtil<>(SeatStudentScore.class);
        util.exportExcel(response, list, "学生成绩数据");
    }

    @Log(title = "学生成绩", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('seating:studentScore:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, Long examId, boolean updateSupport) throws Exception
    {
        checkExamAccess(examId);
        return success(seatStudentScoreService.importScores(examId, file, updateSupport, getUsername()));
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response, Long classId)
    {
        checkClassAccess(classId);
        seatStudentScoreService.exportImportTemplate(response, classId);
    }

    @PreAuthorize("@ss.hasPermi('seating:studentScore:query')")
    @GetMapping(value = "/{scoreId}")
    public AjaxResult getInfo(@PathVariable("scoreId") Long scoreId)
    {
        return success(checkScoreAccess(scoreId));
    }

    @PreAuthorize("@ss.hasPermi('seating:studentScore:add')")
    @Log(title = "学生成绩", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SeatStudentScore seatStudentScore)
    {
        checkExamAccess(seatStudentScore.getExamId());
        seatStudentScore.setCreateBy(getUsername());
        return toAjax(seatStudentScoreService.insertSeatStudentScore(seatStudentScore));
    }

    @PreAuthorize("@ss.hasPermi('seating:studentScore:edit')")
    @Log(title = "学生成绩", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SeatStudentScore seatStudentScore)
    {
        checkScoreAccess(seatStudentScore.getScoreId());
        if (seatStudentScore.getExamId() != null)
        {
            checkExamAccess(seatStudentScore.getExamId());
        }
        seatStudentScore.setDelFlag(null);
        seatStudentScore.setCreateBy(null);
        seatStudentScore.setCreateTime(null);
        seatStudentScore.setUpdateBy(getUsername());
        return toAjax(seatStudentScoreService.updateSeatStudentScore(seatStudentScore));
    }

    @PreAuthorize("@ss.hasPermi('seating:studentScore:sync')")
    @Log(title = "学生成绩", businessType = BusinessType.UPDATE)
    @PostMapping("/exam/{examId}/sync-student-level")
    public AjaxResult syncStudentLevel(@PathVariable Long examId)
    {
        checkExamAccess(examId);
        return success(seatStudentScoreService.syncStudentScoreLevel(examId, getUsername()));
    }

    @PreAuthorize("@ss.hasPermi('seating:studentScore:remove')")
    @Log(title = "学生成绩", businessType = BusinessType.DELETE)
    @DeleteMapping("/{scoreIds}")
    public AjaxResult remove(@PathVariable Long[] scoreIds)
    {
        for (Long scoreId : scoreIds)
        {
            checkScoreAccess(scoreId);
        }
        return toAjax(seatStudentScoreService.deleteSeatStudentScoreByScoreIds(scoreIds));
    }

    private void applyDataScope(SeatStudentScore score)
    {
        if (!SecurityUtils.isAdmin())
        {
            score.getParams().put("teacherId", getUserId());
        }
    }

    private SeatStudentScore checkScoreAccess(Long scoreId)
    {
        SeatStudentScore score = seatStudentScoreService.selectSeatStudentScoreByScoreId(scoreId);
        if (score == null)
        {
            throw new ServiceException("学生成绩不存在");
        }
        checkClassAccess(score.getClassId());
        return score;
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
