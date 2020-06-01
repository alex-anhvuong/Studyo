package com.example.studyo.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyo.database.AssignmentRecord;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class AssignmentViewModel extends ViewModel {
    private AssignmentRepository asmRepository;
    private MutableLiveData<Map<String, Date>> asmDates;
    private MutableLiveData<List<List<String>>> todayAsmList;

    public AssignmentViewModel() {
        asmRepository = new AssignmentRepository();
    }

    public LiveData<Map<String, Date>> getAsmDates() {
        return asmRepository.getIdToDatesMap();
    }

    public LiveData<List<List<String>>> getTodayAsmList() {
        return asmRepository.getTodayAsmList();
    }

    public void insertAssignmentRecord(AssignmentRecord asmRecord) {
        asmRepository.insert(asmRecord);
    }
}
