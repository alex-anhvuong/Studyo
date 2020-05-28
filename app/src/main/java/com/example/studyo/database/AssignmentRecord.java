package com.example.studyo.database;

import java.util.Date;

public class AssignmentRecord {

    private int aID;
    private String aName;
    private String aUnit;
    private Date aDate;
    private Boolean isOnTime;

    public AssignmentRecord(int aID, String aName, String aUnit, Date aDate, Boolean isOnTime) {
        this.aID = aID;
        this.aName = aName;
        this.aUnit = aUnit;
        this.aDate = aDate;
        this.isOnTime = isOnTime;
    }

    public AssignmentRecord() {

    }

    public int getaID() {
        return aID;
    }

    public void setaID(int aID) {
        this.aID = aID;
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

    public Boolean getOnTime() {
        return isOnTime;
    }

    public void setOnTime(Boolean onTime) {
        isOnTime = onTime;
    }
}
