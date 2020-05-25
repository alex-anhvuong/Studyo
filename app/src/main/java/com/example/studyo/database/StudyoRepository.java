package com.example.studyo.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class StudyoRepository {
    private PomoDao pomoDao;
    private LiveData<List<PomoRecord>> pomoRecords;

    public StudyoRepository(Application application) {
        StudyoDatabase db = StudyoDatabase.getDatabase(application);
        pomoDao = db.pomoDao();
        pomoRecords = pomoDao.getAllPomoRecords();
    }

    public LiveData<List<PomoRecord>> getPomoRecords() {
        return pomoRecords;
    }

    public void insert(final PomoRecord pomoRecord) {
        StudyoDatabase.databaseWriteExecutor.execute(() -> {
            pomoDao.insert(pomoRecord);
        });
    }
}
