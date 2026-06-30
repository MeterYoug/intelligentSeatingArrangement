package com.ruoyi.seating.domain.request;

public class SeatClassCopyRequest
{
    private String className;
    private String schoolYear;
    private String semester;
    private Boolean copyStudents = Boolean.TRUE;
    private Boolean copyRelations = Boolean.TRUE;
    private Boolean copyRules = Boolean.TRUE;
    private Boolean copyClassroomLayout = Boolean.TRUE;

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getSchoolYear()
    {
        return schoolYear;
    }

    public void setSchoolYear(String schoolYear)
    {
        this.schoolYear = schoolYear;
    }

    public String getSemester()
    {
        return semester;
    }

    public void setSemester(String semester)
    {
        this.semester = semester;
    }

    public Boolean getCopyStudents()
    {
        return copyStudents;
    }

    public void setCopyStudents(Boolean copyStudents)
    {
        this.copyStudents = copyStudents;
    }

    public Boolean getCopyRelations()
    {
        return copyRelations;
    }

    public void setCopyRelations(Boolean copyRelations)
    {
        this.copyRelations = copyRelations;
    }

    public Boolean getCopyRules()
    {
        return copyRules;
    }

    public void setCopyRules(Boolean copyRules)
    {
        this.copyRules = copyRules;
    }

    public Boolean getCopyClassroomLayout()
    {
        return copyClassroomLayout;
    }

    public void setCopyClassroomLayout(Boolean copyClassroomLayout)
    {
        this.copyClassroomLayout = copyClassroomLayout;
    }
}
