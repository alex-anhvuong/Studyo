package com.example.studyo.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.studyo.database.PomoRecord;

import java.util.List;

public class PomoViewModel extends AndroidViewModel {
    private PomoRepository pomoRepository;
    private LiveData<List<PomoRecord>> pomoRecords;

    public LiveData<List<PomoRecord>> getPomoRecords() { return pomoRecords; }

    public PomoViewModel(Application application) {
        super(application);

        pomoRepository = new PomoRepository(application);
        pomoRecords = pomoRepository.getPomoRecords();
    }
    public void insert (PomoRecord pomoRecord) { pomoRepository.insert(pomoRecord); }
}
