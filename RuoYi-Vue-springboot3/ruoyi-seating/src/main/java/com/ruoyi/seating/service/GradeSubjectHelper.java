package com.ruoyi.seating.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.seating.domain.SeatClass;

/**
 * Grade and subject defaults used when no configurable subject rows exist.
 */
public class GradeSubjectHelper
{
    private static final List<String> PRIMARY_SUBJECTS = Arrays.asList("语文", "数学", "英语", "科学");
    private static final List<String> JUNIOR_COMMON = Arrays.asList("语文", "数学", "英语", "道德与法治", "历史", "地理", "生物");
    private static final List<String> SENIOR_SUBJECTS = Arrays.asList("语文", "数学", "英语", "物理", "化学", "生物", "政治", "历史", "地理");

    private static final Map<String, String> GRADE_NAME_MAP = Map.ofEntries(
        Map.entry("PRIMARY_1", "小学一年级"),
        Map.entry("PRIMARY_2", "小学二年级"),
        Map.entry("PRIMARY_3", "小学三年级"),
        Map.entry("PRIMARY_4", "小学四年级"),
        Map.entry("PRIMARY_5", "小学五年级"),
        Map.entry("PRIMARY_6", "小学六年级"),
        Map.entry("JUNIOR_1", "初中一年级"),
        Map.entry("JUNIOR_2", "初中二年级"),
        Map.entry("JUNIOR_3", "初中三年级"),
        Map.entry("SENIOR_1", "高中一年级"),
        Map.entry("SENIOR_2", "高中二年级"),
        Map.entry("SENIOR_3", "高中三年级")
    );

    private GradeSubjectHelper()
    {
    }

    public static String stageOf(String gradeCode)
    {
        if (StringUtils.isBlank(gradeCode))
        {
            return null;
        }
        if (gradeCode.startsWith("PRIMARY_"))
        {
            return "PRIMARY";
        }
        if (gradeCode.startsWith("JUNIOR_"))
        {
            return "JUNIOR";
        }
        if (gradeCode.startsWith("SENIOR_"))
        {
            return "SENIOR";
        }
        return null;
    }

    public static String gradeNameOf(String gradeCode)
    {
        return GRADE_NAME_MAP.get(gradeCode);
    }

    public static List<String> defaultSubjects(SeatClass seatClass)
    {
        String gradeCode = seatClass == null ? null : seatClass.getGradeCode();
        String schoolStage = StringUtils.defaultIfBlank(seatClass == null ? null : seatClass.getSchoolStage(), stageOf(gradeCode));
        if ("PRIMARY".equals(schoolStage))
        {
            return new ArrayList<>(PRIMARY_SUBJECTS);
        }
        if ("JUNIOR".equals(schoolStage))
        {
            List<String> subjects = new ArrayList<>(JUNIOR_COMMON);
            if (!"JUNIOR_1".equals(gradeCode))
            {
                subjects.add("物理");
            }
            if ("JUNIOR_3".equals(gradeCode))
            {
                subjects.add("化学");
            }
            return subjects;
        }
        if ("SENIOR".equals(schoolStage))
        {
            return new ArrayList<>(SENIOR_SUBJECTS);
        }
        return new ArrayList<>(PRIMARY_SUBJECTS);
    }
}
