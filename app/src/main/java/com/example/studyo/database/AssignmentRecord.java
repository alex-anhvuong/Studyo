package com.example.studyo.database;

import java.util.Comparator;
import java.util.Date;

public class AssignmentRecord {

    private String aName;
    private String aUnit;
    private Date aDate;

    public AssignmentRecord(String aName, String aUnit, Date aDate) {
        this.aName = aName;
        this.aUnit = aUnit;
        this.aDate = aDate;
    }

    public AssignmentRecord() {

    }

    public String getaName() {
        return aName;
    }

    public void setaName(String aName) {
        this.aName = aName;
    }

    public String getaUnit() {
        return aUnit;
    }

    public void setaUnit(String aUnit) {
        this.aUnit = aUnit;
    }

    public Date getaDate() {
        return aDate;
    }

    public void setaDate(Date aDate) {
        this.aDate = aDate;
    }
}
