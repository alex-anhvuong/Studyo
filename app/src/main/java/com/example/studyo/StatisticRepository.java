package com.example.studyo;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class StatisticRepository {
    private PomoDao pomoDao;
    private LiveData<List<PomoRecord>> pomoRecords;

    StatisticRepository(Application application) {
        StatisticDatabase db = StatisticDatabase.getDatabase(application);
        pomoDao = db.pomoDao();
        pomoRecords = pomoDao.getAllPomoRecords();
    }

    LiveData<List<PomoRecord>> getPomoRecords() {
        return pomoRecords;
    }

    void insert(final PomoRecord pomoRecord) {
        StatisticDatabase.databaseWriteExecutor.execute(() -> {
            pomoDao.insert(pomoRecord);
        });
    }
}
