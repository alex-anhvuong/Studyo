package com.example.studyo;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class StatisticViewModel extends ViewModel {
    private StatisticRepository statisticRepository;
    private LiveData<List<PomoRecord>> pomoRecords;

    public StatisticViewModel (Application application) {
        //super(application);
        statisticRepository = new StatisticRepository(application);
        pomoRecords = statisticRepository.getPomoRecords();
    }

    public StatisticViewModel() {}

    LiveData<List<PomoRecord>> getPomoRecords() { return pomoRecords; }
    public void insert (PomoRecord pomoRecord) { statisticRepository.insert(pomoRecord); }
}
