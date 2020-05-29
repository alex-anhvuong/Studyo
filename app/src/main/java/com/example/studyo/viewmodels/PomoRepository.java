package com.example.studyo.viewmodels;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.studyo.database.PomoDao;
import com.example.studyo.database.PomoRecord;
import com.example.studyo.database.StudyoDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class PomoRepository {

    private PomoDao pomoDao;
    private LiveData<List<PomoRecord>> pomoRecords;

    public LiveData<List<PomoRecord>> getPomoRecords() { return pomoRecords; }

    public PomoRepository(Application application) {
        StudyoDatabase db = StudyoDatabase.getDatabase(application);
        pomoDao = db.pomoDao();
        pomoRecords = pomoDao.getAllPomoRecords();
    }


    public void insert(final PomoRecord pomoRecord) {
        StudyoDatabase.databaseWriteExecutor.execute(() -> {
            pomoDao.insert(pomoRecord);
        });
    }

}
