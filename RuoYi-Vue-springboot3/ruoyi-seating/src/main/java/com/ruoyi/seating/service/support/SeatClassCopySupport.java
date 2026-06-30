package com.ruoyi.seating.service.support;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.ruoyi.seating.domain.SeatClassroom;

/**
 * 新学期复制辅助方法。
 */
public final class SeatClassCopySupport
{
    private SeatClassCopySupport()
    {
    }

    public static String nextSchoolYear(String schoolYear, String semester)
    {
        if (StringUtils.isBlank(schoolYear))
        {
            return "";
        }
        if (isSecondSemester(semester))
        {
            String[] parts = schoolYear.split("-");
            if (parts.length == 2)
            {
                try
                {
                    int startYear = Integer.parseInt(parts[0].trim());
                    return (startYear + 1) + "-" + (startYear + 2);
                }
                catch (NumberFormatException ignored)
                {
                    return schoolYear;
                }
            }
        }
        return schoolYear;
    }

    public static String nextSemester(String semester)
    {
        return isFirstSemester(semester) ? "2" : "1";
    }

    public static SeatClassroom selectPrimaryClassroom(List<SeatClassroom> classroomList)
    {
        if (classroomList == null || classroomList.isEmpty())
        {
            return null;
        }
        for (SeatClassroom classroom : classroomList)
        {
            if (classroom != null && "1".equals(classroom.getIsDefault()) && "0".equals(classroom.getStatus()))
            {
                return classroom;
            }
        }
        return null;
    }

    private static boolean isFirstSemester(String semester)
    {
        return "1".equals(semester) || "上学期".equals(semester);
    }

    private static boolean isSecondSemester(String semester)
    {
        return "2".equals(semester) || "下学期".equals(semester);
    }
}
