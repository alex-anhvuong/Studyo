package com.example.studyo.viewmodels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.studyo.database.PomoRecord;

import java.util.List;

public class PomoViewModel extends AndroidViewModel {
    private String TAG = this.getClass().getSimpleName();
    private PomoRepository pomoRepository;
    private LiveData<List<PomoRecord>> pomoRecords;

    public PomoViewModel(Application application) {
        super(application);

        Log.i(TAG, "ViewModel created");
        pomoRepository = new PomoRepository(application);
        pomoRecords = pomoRepository.getPomoRecords();
    }

    public LiveData<List<PomoRecord>> getPomoRecords() { return pomoRecords; }
    public void insert (PomoRecord pomoRecord) { pomoRepository.insert(pomoRecord); }
}
