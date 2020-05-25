package com.example.studyo.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.Entity;
import androidx.room.TypeConverter;

import java.util.Date;

@Entity(tableName = "pomo_record_table")
public class PomoRecord {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "ID")
    private int pmID;

    @NonNull
    @ColumnInfo(name = "IsSuccessful")
    private Boolean pmIsSuccessful;

    @NonNull
    @ColumnInfo(name = "Time")
    private int pmSeconds;

    @NonNull
    @ColumnInfo(name = "Date")
    private Date pmDate;

    public PomoRecord(@NonNull Boolean pmIsSuccessful, @NonNull int pmSeconds, @NonNull Date pmDate) {
        this.pmIsSuccessful = pmIsSuccessful;
        this.pmSeconds = pmSeconds;
        this.pmDate = pmDate;
    }

    public int getPmID() {
        return pmID;
    }

    public void setPmID(int pmID) {
        this.pmID = pmID;
    }

    public Boolean getPmIsSuccessful() {
        return pmIsSuccessful;
    }

    public void setPmIsSuccessful(Boolean pmIsSuccessful) {
        this.pmIsSuccessful = pmIsSuccessful;
    }

    public int getPmSeconds() { return pmSeconds; }

    public void setPmSeconds(int pmSeconds) { this.pmSeconds = pmSeconds; }

    @NonNull
    public Date getPmDate() { return pmDate; }

    public void setPmDate(@NonNull Date pmDate) { this.pmDate = pmDate; }
}

class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}


