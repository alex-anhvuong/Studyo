package com.example.studyo;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {PomoRecord.class}, version = 1, exportSchema = false)
public abstract class StudyoDatabase extends RoomDatabase {
    public abstract PomoDao pomoDao();

    private static volatile StudyoDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static StudyoDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (StudyoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), StudyoDatabase.class, "statistic_database").build();
                }
            }
        }
        return INSTANCE;
    }
}