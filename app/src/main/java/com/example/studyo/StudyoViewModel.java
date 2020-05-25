package com.example.studyo;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.studyo.database.PomoRecord;
import com.example.studyo.database.StudyoRepository;

import java.util.List;

public class  StudyoViewModel extends AndroidViewModel {
    private String TAG = this.getClass().getSimpleName();
    private StudyoRepository studyoRepository;
    private LiveData<List<PomoRecord>> pomoRecords;

    public StudyoViewModel(Application application) {
        super(application);

        Log.i(TAG, "ViewModel created");
        studyoRepository = new StudyoRepository(application);
        pomoRecords = studyoRepository.getPomoRecords();
    }

    LiveData<List<PomoRecord>> getPomoRecords() { return pomoRecords; }
    public void insert (PomoRecord pomoRecord) { studyoRepository.insert(pomoRecord); }
}
