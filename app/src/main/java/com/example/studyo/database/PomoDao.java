package com.example.studyo.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PomoDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(PomoRecord pmRecord);

    @Query("DELETE FROM pomo_record_table")
    void deleteAll();

    @Query("SELECT * FROM pomo_record_table ORDER BY ID DESC")
    LiveData<List<PomoRecord>> getAllPomoRecords();
}
