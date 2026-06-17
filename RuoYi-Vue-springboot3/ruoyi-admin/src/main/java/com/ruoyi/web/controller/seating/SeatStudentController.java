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
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.seating.domain.SeatClass;
import com.ruoyi.seating.domain.SeatStudent;
import com.ruoyi.seating.domain.SeatStudentImportData;
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.ISeatStudentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 排座学生Controller
 * 
 * @author ruoyi
 * @date 2026-06-15
 */
@RestController
@RequestMapping("/seating/student")
public class SeatStudentController extends BaseController
{
    @Autowired
    private ISeatStudentService seatStudentService;

    @Autowired
    private ISeatClassService seatClassService;

    /**
     * 查询排座学生列表
     */
    @PreAuthorize("@ss.hasPermi('seating:student:list')")
    @GetMapping("/list")
    public TableDataInfo list(SeatStudent seatStudent)
    {
        applyDataScope(seatStudent);
        startPage();
        List<SeatStudent> list = seatStudentService.selectSeatStudentList(seatStudent);
        return getDataTable(list);
    }

    /**
     * 导出排座学生列表
     */
    @PreAuthorize("@ss.hasPermi('seating:student:export')")
    @Log(title = "排座学生", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SeatStudent seatStudent)
    {
        applyDataScope(seatStudent);
        List<SeatStudent> list = seatStudentService.selectSeatStudentList(seatStudent);
        ExcelUtil<SeatStudent> util = new ExcelUtil<SeatStudent>(SeatStudent.class);
        util.exportExcel(response, list, "排座学生数据");
    }

    @Log(title = "排座学生", businessType = BusinessType.IMPORT)
    @PreAuthorize("@ss.hasPermi('seating:student:import')")
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, Long classId, boolean updateSupport) throws Exception
    {
        checkClassAccess(classId);
        ExcelUtil<SeatStudentImportData> util = new ExcelUtil<>(SeatStudentImportData.class);
        List<SeatStudentImportData> studentList = util.importExcel(file.getInputStream());
        return success(seatStudentService.importStudents(classId, studentList, updateSupport, getUsername()));
    }

    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response)
    {
        ExcelUtil<SeatStudentImportData> util = new ExcelUtil<>(SeatStudentImportData.class);
        util.importTemplateExcel(response, "学生数据");
    }

    /**
     * 获取排座学生详细信息
     */
    @PreAuthorize("@ss.hasPermi('seating:student:query')")
    @GetMapping(value = "/{studentId}")
    public AjaxResult getInfo(@PathVariable("studentId") Long studentId)
    {
        return success(checkStudentAccess(studentId));
    }

    /**
     * 新增排座学生
     */
    @PreAuthorize("@ss.hasPermi('seating:student:add')")
    @Log(title = "排座学生", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SeatStudent seatStudent)
    {
        checkClassAccess(seatStudent.getClassId());
        seatStudent.setCreateBy(getUsername());
        seatStudent.setDelFlag("0");
        if (StringUtils.isBlank(seatStudent.getStatus()))
        {
            seatStudent.setStatus("0");
        }
        return toAjax(seatStudentService.insertSeatStudent(seatStudent));
    }

    /**
     * 修改排座学生
     */
    @PreAuthorize("@ss.hasPermi('seating:student:edit')")
    @Log(title = "排座学生", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SeatStudent seatStudent)
    {
        checkStudentAccess(seatStudent.getStudentId());
        checkClassAccess(seatStudent.getClassId());
        seatStudent.setDelFlag(null);
        seatStudent.setCreateBy(null);
        seatStudent.setCreateTime(null);
        seatStudent.setUpdateBy(getUsername());
        return toAjax(seatStudentService.updateSeatStudent(seatStudent));
    }

    /**
     * 删除排座学生
     */
    @PreAuthorize("@ss.hasPermi('seating:student:remove')")
    @Log(title = "排座学生", businessType = BusinessType.DELETE)
	@DeleteMapping("/{studentIds}")
    public AjaxResult remove(@PathVariable Long[] studentIds)
    {
        for (Long studentId : studentIds)
        {
            checkStudentAccess(studentId);
        }
        return toAjax(seatStudentService.deleteSeatStudentByStudentIds(studentIds));
    }

    private void applyDataScope(SeatStudent seatStudent)
    {
        if (!SecurityUtils.isAdmin())
        {
            seatStudent.getParams().put("teacherId", getUserId());
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

    private SeatStudent checkStudentAccess(Long studentId)
    {
        SeatStudent seatStudent = seatStudentService.selectSeatStudentByStudentId(studentId);
        if (seatStudent == null)
        {
            throw new ServiceException("学生不存在");
        }
        checkClassAccess(seatStudent.getClassId());
        return seatStudent;
    }
}
