package com.example.studyo;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;
import androidx.room.Entity;

@Entity(tableName = "pomo_record_table")
public class PomoRecord {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "ID")
    private int pmID;

    @NonNull
    @ColumnInfo(name = "isSuccessful")
    private Boolean pmIsSuccessful;

    public PomoRecord(@NonNull Boolean pmIsSuccessful) {
        this.pmIsSuccessful = pmIsSuccessful;
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


}
