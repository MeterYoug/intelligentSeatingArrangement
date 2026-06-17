package com.ruoyi.web.controller.seating;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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
import com.ruoyi.seating.domain.SeatAssignment;
import com.ruoyi.seating.domain.SeatPlan;
import com.ruoyi.seating.domain.SeatPlanSeatExportRow;
import com.ruoyi.seating.domain.SeatPosition;
import com.ruoyi.seating.service.ISeatAssignmentService;
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.ISeatClassroomService;
import com.ruoyi.seating.service.ISeatPlanService;
import com.ruoyi.seating.service.ISeatPositionService;
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

    @Autowired
    private ISeatPositionService seatPositionService;

    @Autowired
    private ISeatAssignmentService seatAssignmentService;

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
     * 导出当前方案座位表
     */
    @PreAuthorize("@ss.hasPermi('seating:plan:export')")
    @Log(title = "座位表", businessType = BusinessType.EXPORT)
    @PostMapping("/{planId}/export-seat-table")
    public void exportSeatTable(HttpServletResponse response, @PathVariable("planId") Long planId)
    {
        SeatPlan plan = checkPlanAccess(planId);
        SeatPosition positionQuery = new SeatPosition();
        positionQuery.setClassroomId(plan.getClassroomId());
        List<SeatPosition> positions = seatPositionService.selectSeatPositionList(positionQuery);
        if (positions.isEmpty())
        {
            throw new ServiceException("教室没有座位布局");
        }

        SeatAssignment assignmentQuery = new SeatAssignment();
        assignmentQuery.setPlanId(planId);
        List<SeatAssignment> assignments = seatAssignmentService.selectSeatAssignmentList(assignmentQuery);
        Map<Long, SeatAssignment> assignmentMap = new HashMap<>();
        for (SeatAssignment assignment : assignments)
        {
            assignmentMap.put(assignment.getSeatId(), assignment);
        }

        SeatClassroom classroom = seatClassroomService.selectSeatClassroomByClassroomId(plan.getClassroomId());
        exportSeatGridExcel(response, plan, classroom, positions, assignmentMap);
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

    private SeatPlanSeatExportRow buildSeatExportRow(SeatPlan plan, SeatPosition position, SeatAssignment assignment)
    {
        SeatPlanSeatExportRow row = new SeatPlanSeatExportRow();
        row.setPlanName(plan.getPlanName());
        row.setClassName(plan.getClassName());
        row.setClassroomName(plan.getClassroomName());
        row.setRowIndex(position.getRowIndex());
        row.setColIndex(position.getColIndex());
        row.setSeatCode(position.getSeatCode());
        row.setSeatStatus(resolveSeatStatus(position, assignment));
        if (assignment != null)
        {
            row.setStudentName(assignment.getStudentNameSnapshot());
            row.setLockedText("1".equals(assignment.getIsLocked()) ? "是" : "否");
            row.setAssignSourceText("MANUAL".equals(assignment.getAssignSource()) ? "手动调整" : "自动生成");
        }
        return row;
    }

    private String resolveSeatStatus(SeatPosition position, SeatAssignment assignment)
    {
        if ("2".equals(position.getSeatType()))
        {
            return "过道";
        }
        if (!"0".equals(position.getSeatType()))
        {
            return "空位";
        }
        if (!"1".equals(position.getIsAvailable()) || "1".equals(position.getStatus()))
        {
            return "不可用";
        }
        return assignment == null ? "空座" : "已安排";
    }

    private void exportSeatGridExcel(HttpServletResponse response, SeatPlan plan, SeatClassroom classroom,
            List<SeatPosition> positions, Map<Long, SeatAssignment> assignmentMap)
    {
        int maxRow = positions.stream().mapToInt(item -> item.getRowIndex().intValue()).max().orElse(1);
        int maxCol = positions.stream().mapToInt(item -> item.getColIndex().intValue()).max().orElse(1);
        String platformPosition = classroom == null || classroom.getPlatformPosition() == null
                ? "FRONT" : classroom.getPlatformPosition();
        boolean leftPlatform = "LEFT".equals(platformPosition);
        boolean rightPlatform = "RIGHT".equals(platformPosition);
        int gridColOffset = leftPlatform ? 1 : 0;
        int totalCols = maxCol + (leftPlatform || rightPlatform ? 1 : 0);
        int gridStartRow = "FRONT".equals(platformPosition) ? 3 : 2;

        Map<String, SeatPosition> positionMap = new HashMap<>();
        for (SeatPosition position : positions)
        {
            positionMap.put(position.getRowIndex() + "-" + position.getColIndex(), position);
        }

        SXSSFWorkbook workbook = new SXSSFWorkbook(100);
        try
        {
            Sheet sheet = workbook.createSheet("座位表");
            Map<String, CellStyle> styles = createSeatGridStyles(workbook);
            writeSeatGridTitle(sheet, styles, plan, totalCols);
            if ("FRONT".equals(platformPosition))
            {
                writeMergedCell(sheet, 2, 0, 2, totalCols - 1, "讲台", styles.get("platform"));
            }
            if (leftPlatform)
            {
                writeMergedCell(sheet, gridStartRow, 0, gridStartRow + maxRow - 1, 0, "讲台", styles.get("platform"));
            }
            if (rightPlatform)
            {
                writeMergedCell(sheet, gridStartRow, totalCols - 1, gridStartRow + maxRow - 1, totalCols - 1, "讲台", styles.get("platform"));
            }

            for (int rowIndex = 1; rowIndex <= maxRow; rowIndex++)
            {
                Row row = sheet.createRow(gridStartRow + rowIndex - 1);
                row.setHeightInPoints(54);
                for (int colIndex = 1; colIndex <= maxCol; colIndex++)
                {
                    SeatPosition position = positionMap.get(rowIndex + "-" + colIndex);
                    Cell cell = row.createCell(gridColOffset + colIndex - 1);
                    if (position == null)
                    {
                        cell.setCellValue("");
                        cell.setCellStyle(styles.get("disabled"));
                        continue;
                    }
                    SeatAssignment assignment = assignmentMap.get(position.getSeatId());
                    SeatPlanSeatExportRow exportRow = buildSeatExportRow(plan, position, assignment);
                    cell.setCellValue(buildSeatCellText(exportRow));
                    cell.setCellStyle(resolveSeatCellStyle(styles, position, assignment));
                }
            }

            if ("BACK".equals(platformPosition))
            {
                writeMergedCell(sheet, gridStartRow + maxRow, 0, gridStartRow + maxRow, totalCols - 1, "讲台", styles.get("platform"));
            }

            for (int i = 0; i < totalCols; i++)
            {
                sheet.setColumnWidth(i, 18 * 256);
            }
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            String filename = URLEncoder.encode(plan.getPlanName() + "-座位表.xlsx", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment; filename*=utf-8''" + filename);
            response.setHeader("download-filename", filename);
            workbook.write(response.getOutputStream());
        }
        catch (Exception e)
        {
            throw new ServiceException("导出座位表失败：" + e.getMessage());
        }
        finally
        {
            workbook.dispose();
        }
    }

    private void writeSeatGridTitle(Sheet sheet, Map<String, CellStyle> styles, SeatPlan plan, int totalCols)
    {
        writeMergedCell(sheet, 0, 0, 0, totalCols - 1, plan.getPlanName(), styles.get("title"));
        String summary = "班级：" + defaultText(plan.getClassName()) + "    教室：" + defaultText(plan.getClassroomName())
                + "    总评分：" + (plan.getTotalScore() == null ? "-" : plan.getTotalScore());
        writeMergedCell(sheet, 1, 0, 1, totalCols - 1, summary, styles.get("meta"));
    }

    private void writeMergedCell(Sheet sheet, int firstRow, int firstCol, int lastRow, int lastCol, String value, CellStyle style)
    {
        Row row = sheet.getRow(firstRow);
        if (row == null)
        {
            row = sheet.createRow(firstRow);
        }
        Cell cell = row.createCell(firstCol);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        if (firstRow != lastRow || firstCol != lastCol)
        {
            sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
        }
    }

    private String buildSeatCellText(SeatPlanSeatExportRow row)
    {
        if ("已安排".equals(row.getSeatStatus()))
        {
            String lockedText = "是".equals(row.getLockedText()) ? "\n已锁定" : "";
            return defaultText(row.getSeatCode()) + "\n" + defaultText(row.getStudentName()) + lockedText;
        }
        if ("空座".equals(row.getSeatStatus()))
        {
            return defaultText(row.getSeatCode()) + "\n空座";
        }
        return row.getSeatStatus();
    }

    private CellStyle resolveSeatCellStyle(Map<String, CellStyle> styles, SeatPosition position, SeatAssignment assignment)
    {
        if ("2".equals(position.getSeatType()))
        {
            return styles.get("aisle");
        }
        if (!"0".equals(position.getSeatType()) || !"1".equals(position.getIsAvailable()) || "1".equals(position.getStatus()))
        {
            return styles.get("disabled");
        }
        return assignment == null ? styles.get("empty") : styles.get("assigned");
    }

    private Map<String, CellStyle> createSeatGridStyles(SXSSFWorkbook workbook)
    {
        Map<String, CellStyle> styles = new HashMap<>();
        styles.put("title", createStyle(workbook, IndexedColors.WHITE, IndexedColors.ROYAL_BLUE, true, 16));
        styles.put("meta", createStyle(workbook, IndexedColors.BLACK, IndexedColors.GREY_25_PERCENT, false, 11));
        styles.put("platform", createStyle(workbook, IndexedColors.WHITE, IndexedColors.BLUE, true, 12));
        styles.put("assigned", createStyle(workbook, IndexedColors.BLACK, IndexedColors.LIGHT_CORNFLOWER_BLUE, false, 11));
        styles.put("empty", createStyle(workbook, IndexedColors.GREY_50_PERCENT, IndexedColors.WHITE, false, 11));
        styles.put("aisle", createStyle(workbook, IndexedColors.GREY_50_PERCENT, IndexedColors.GREY_25_PERCENT, false, 11));
        styles.put("disabled", createStyle(workbook, IndexedColors.RED, IndexedColors.ROSE, false, 11));
        return styles;
    }

    private CellStyle createStyle(SXSSFWorkbook workbook, IndexedColors fontColor, IndexedColors backgroundColor,
            boolean bold, int fontSize)
    {
        Font font = workbook.createFont();
        font.setColor(fontColor.getIndex());
        font.setBold(bold);
        font.setFontHeightInPoints((short) fontSize);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setFillForegroundColor(backgroundColor.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private String defaultText(Object value)
    {
        return value == null ? "-" : value.toString();
    }
}
