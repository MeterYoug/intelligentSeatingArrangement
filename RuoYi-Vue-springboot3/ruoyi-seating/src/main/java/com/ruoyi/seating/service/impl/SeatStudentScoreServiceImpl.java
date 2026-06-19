package com.ruoyi.seating.service.impl;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.seating.domain.SeatClass;
import com.ruoyi.seating.domain.SeatExam;
import com.ruoyi.seating.domain.SeatStudent;
import com.ruoyi.seating.domain.SeatStudentScore;
import com.ruoyi.seating.mapper.SeatStudentMapper;
import com.ruoyi.seating.mapper.SeatStudentScoreMapper;
import com.ruoyi.seating.service.GradeSubjectHelper;
import com.ruoyi.seating.service.ISeatClassService;
import com.ruoyi.seating.service.ISeatExamService;
import com.ruoyi.seating.service.ISeatStudentScoreService;

@Service
public class SeatStudentScoreServiceImpl implements ISeatStudentScoreService
{
    private static final List<String> FIXED_HEADERS = List.of("学号", "姓名");
    private static final List<String> TAIL_HEADERS = List.of("总分", "班级排名", "备注");

    @Autowired
    private SeatStudentScoreMapper seatStudentScoreMapper;

    @Autowired
    private SeatStudentMapper seatStudentMapper;

    @Autowired
    private ISeatExamService seatExamService;

    @Autowired
    private ISeatClassService seatClassService;

    @Override
    public SeatStudentScore selectSeatStudentScoreByScoreId(Long scoreId)
    {
        return seatStudentScoreMapper.selectSeatStudentScoreByScoreId(scoreId);
    }

    @Override
    public List<SeatStudentScore> selectSeatStudentScoreList(SeatStudentScore seatStudentScore)
    {
        return seatStudentScoreMapper.selectSeatStudentScoreList(seatStudentScore);
    }

    @Override
    public int insertSeatStudentScore(SeatStudentScore seatStudentScore)
    {
        seatStudentScore.setCreateTime(DateUtils.getNowDate());
        seatStudentScore.setDelFlag("0");
        return seatStudentScoreMapper.insertSeatStudentScore(seatStudentScore);
    }

    @Override
    public int updateSeatStudentScore(SeatStudentScore seatStudentScore)
    {
        seatStudentScore.setUpdateTime(DateUtils.getNowDate());
        int rows = seatStudentScoreMapper.updateSeatStudentScore(seatStudentScore);
        if (seatStudentScore.getExamId() != null)
        {
            refreshRanksAndLevels(seatStudentScore.getExamId(), seatStudentScore.getUpdateBy());
        }
        return rows;
    }

    @Override
    public int deleteSeatStudentScoreByScoreIds(Long[] scoreIds)
    {
        return seatStudentScoreMapper.deleteSeatStudentScoreByScoreIds(scoreIds);
    }

    @Override
    public int deleteSeatStudentScoreByScoreId(Long scoreId)
    {
        return seatStudentScoreMapper.deleteSeatStudentScoreByScoreId(scoreId);
    }

    @Override
    public void exportImportTemplate(HttpServletResponse response, Long classId)
    {
        SeatClass seatClass = seatClassService.selectSeatClassByClassId(classId);
        if (seatClass == null)
        {
            throw new ServiceException("班级不存在");
        }
        List<String> subjects = GradeSubjectHelper.resolveClassSubjects(seatClass);
        writeTemplate(response, seatClass, subjects);
    }

    @Override
    @Transactional
    public String importScores(Long examId, MultipartFile file, boolean updateSupport, String operName) throws Exception
    {
        SeatExam exam = seatExamService.selectSeatExamByExamId(examId);
        if (exam == null)
        {
            throw new ServiceException("考试批次不存在");
        }
        List<String> subjects = parseSubjects(exam);
        int successNum = 0;
        StringBuilder errors = new StringBuilder();
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream()))
        {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null || sheet.getPhysicalNumberOfRows() <= 1)
            {
                throw new ServiceException("导入成绩数据不能为空");
            }
            Map<String, Integer> headerIndex = readHeader(sheet.getRow(0));
            validateHeaders(headerIndex, subjects);
            for (int i = 1; i <= sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                if (isBlankRow(row))
                {
                    continue;
                }
                int rowNumber = i + 1;
                try
                {
                    SeatStudentScore score = buildScore(exam, subjects, headerIndex, row, operName);
                    SeatStudentScore existing = seatStudentScoreMapper.selectByExamIdAndStudentId(score);
                    if (existing == null)
                    {
                        insertSeatStudentScore(score);
                    }
                    else if (updateSupport)
                    {
                        score.setScoreId(existing.getScoreId());
                        score.setUpdateBy(operName);
                        updateSeatStudentScore(score);
                    }
                    else
                    {
                        throw new ServiceException("该学生成绩已存在");
                    }
                    successNum++;
                }
                catch (Exception e)
                {
                    errors.append("<br/>第 ").append(rowNumber).append(" 行：").append(e.getMessage());
                }
            }
        }
        if (!errors.isEmpty())
        {
            throw new ServiceException("导入失败，数据未写入：" + errors);
        }
        refreshRanksAndLevels(examId, operName);
        return "导入成功，共 " + successNum + " 条";
    }

    @Override
    @Transactional
    public int syncStudentScoreLevel(Long examId, String operName)
    {
        SeatStudentScore query = new SeatStudentScore();
        query.setExamId(examId);
        List<SeatStudentScore> scores = seatStudentScoreMapper.selectSeatStudentScoreList(query);
        int count = 0;
        for (SeatStudentScore score : scores)
        {
            if (score.getStudentId() != null && StringUtils.isNotBlank(score.getScoreLevel()))
            {
                count += seatStudentMapper.updateStudentScoreLevel(score.getStudentId(), score.getScoreLevel(), operName);
            }
        }
        return count;
    }

    private SeatStudentScore buildScore(SeatExam exam, List<String> subjects, Map<String, Integer> headerIndex, Row row,
            String operName)
    {
        String studentNo = cellString(row, headerIndex.get("学号"));
        if (StringUtils.isBlank(studentNo))
        {
            throw new ServiceException("学号不能为空");
        }
        SeatStudent lookup = new SeatStudent();
        lookup.setClassId(exam.getClassId());
        lookup.setStudentNo(studentNo);
        SeatStudent student = seatStudentMapper.selectSeatStudentByClassIdAndStudentNo(lookup);
        if (student == null)
        {
            throw new ServiceException("未找到学号对应的学生");
        }
        Map<String, BigDecimal> subjectMap = new LinkedHashMap<>();
        BigDecimal calculatedTotal = BigDecimal.ZERO;
        for (String subject : subjects)
        {
            BigDecimal value = cellDecimal(row, headerIndex.get(subject));
            if (value == null)
            {
                value = BigDecimal.ZERO;
            }
            subjectMap.put(subject, value);
            calculatedTotal = calculatedTotal.add(value);
        }
        BigDecimal totalScore = cellDecimal(row, headerIndex.get("总分"));
        if (totalScore == null)
        {
            totalScore = calculatedTotal;
        }
        SeatStudentScore score = new SeatStudentScore();
        score.setExamId(exam.getExamId());
        score.setClassId(exam.getClassId());
        score.setStudentId(student.getStudentId());
        score.setStudentNo(student.getStudentNo());
        score.setStudentNameSnapshot(student.getStudentName());
        score.setSubjectScores(JSON.toJSONString(subjectMap));
        score.setTotalScore(totalScore.setScale(2, RoundingMode.HALF_UP));
        score.setClassRank(cellLong(row, headerIndex.get("班级排名")));
        score.setRemark(cellString(row, headerIndex.get("备注")));
        score.setDelFlag("0");
        score.setCreateBy(operName);
        return score;
    }

    private void refreshRanksAndLevels(Long examId, String operName)
    {
        SeatStudentScore query = new SeatStudentScore();
        query.setExamId(examId);
        List<SeatStudentScore> scores = new ArrayList<>(seatStudentScoreMapper.selectSeatStudentScoreList(query));
        scores.sort(Comparator.comparing(SeatStudentScore::getTotalScore,
            Comparator.nullsLast(Comparator.reverseOrder())));
        int total = scores.size();
        for (int i = 0; i < scores.size(); i++)
        {
            SeatStudentScore score = scores.get(i);
            score.setClassRank((long) i + 1);
            score.setScoreLevel(levelOf(i + 1, total));
            score.setUpdateBy(operName);
            score.setUpdateTime(DateUtils.getNowDate());
            seatStudentScoreMapper.updateSeatStudentScore(score);
        }
    }

    private String levelOf(int rank, int total)
    {
        if (total <= 0)
        {
            return null;
        }
        if (rank <= Math.ceil(total * 0.25D))
        {
            return "A";
        }
        if (rank <= Math.ceil(total * 0.50D))
        {
            return "B";
        }
        if (rank <= Math.ceil(total * 0.75D))
        {
            return "C";
        }
        return "D";
    }

    private List<String> parseSubjects(SeatExam exam)
    {
        if (StringUtils.isNotBlank(exam.getSubjectSnapshot()))
        {
            return GradeSubjectHelper.normalizeSubjects(JSON.parseArray(exam.getSubjectSnapshot(), String.class));
        }
        SeatClass seatClass = seatClassService.selectSeatClassByClassId(exam.getClassId());
        return GradeSubjectHelper.resolveClassSubjects(seatClass);
    }

    private Map<String, Integer> readHeader(Row header)
    {
        if (header == null)
        {
            throw new ServiceException("模板表头不能为空");
        }
        Map<String, Integer> headerIndex = new LinkedHashMap<>();
        for (int i = 0; i < header.getLastCellNum(); i++)
        {
            String name = cellString(header, i);
            if (StringUtils.isNotBlank(name))
            {
                headerIndex.put(name.trim(), i);
            }
        }
        return headerIndex;
    }

    private void validateHeaders(Map<String, Integer> headerIndex, List<String> subjects)
    {
        List<String> required = new ArrayList<>();
        required.addAll(FIXED_HEADERS);
        required.addAll(subjects);
        required.addAll(TAIL_HEADERS);
        for (String header : required)
        {
            if (!headerIndex.containsKey(header))
            {
                throw new ServiceException("导入模板缺少列：" + header);
            }
        }
    }

    private void writeTemplate(HttpServletResponse response, SeatClass seatClass, List<String> subjects)
    {
        try (Workbook workbook = new XSSFWorkbook(); OutputStream os = response.getOutputStream())
        {
            Sheet sheet = workbook.createSheet("学生成绩");
            Row header = sheet.createRow(0);
            List<String> headers = new ArrayList<>();
            headers.addAll(FIXED_HEADERS);
            headers.addAll(subjects);
            headers.addAll(TAIL_HEADERS);
            for (int i = 0; i < headers.size(); i++)
            {
                header.createCell(i).setCellValue(headers.get(i));
                sheet.setColumnWidth(i, 16 * 256);
            }
            String fileName = URLEncoder.encode(seatClass.getClassName() + "_score_template.xlsx", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            workbook.write(os);
        }
        catch (Exception e)
        {
            throw new ServiceException("生成成绩导入模板失败：" + e.getMessage());
        }
    }

    private boolean isBlankRow(Row row)
    {
        return row == null || StringUtils.isBlank(cellString(row, 0));
    }

    private String cellString(Row row, Integer index)
    {
        if (row == null || index == null)
        {
            return null;
        }
        Cell cell = row.getCell(index);
        if (cell == null)
        {
            return null;
        }
        cell.setCellType(CellType.STRING);
        return StringUtils.trim(cell.getStringCellValue());
    }

    private BigDecimal cellDecimal(Row row, Integer index)
    {
        String value = cellString(row, index);
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        try
        {
            return new BigDecimal(value.trim());
        }
        catch (NumberFormatException e)
        {
            throw new ServiceException("分数必须为数字");
        }
    }

    private Long cellLong(Row row, Integer index)
    {
        BigDecimal value = cellDecimal(row, index);
        return value == null ? null : value.longValue();
    }
}
