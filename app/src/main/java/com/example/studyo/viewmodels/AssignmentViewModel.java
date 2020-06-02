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
    private LiveData<Map<String, Date>> asmDates;
    private LiveData<List<List<String>>> todayAsmList;

    private LiveData<Map<String, Object>> asmAllDetailsList;

    public AssignmentViewModel() {
        asmRepository = new AssignmentRepository();
    }

    public LiveData<Map<String, Date>> getAsmDates() {
        return asmRepository.getIdToDatesMap();
    }

    public LiveData<List<List<String>>> getTodayAsmList() {
        return asmRepository.getTodayAsmList();
    }

    public LiveData<Map<String, Object>> getAsmAllDetailsList() {
        return asmRepository.getAsmAllDetailsMap();
    }

    public void insertAssignmentRecord(AssignmentRecord asmRecord) {
        asmRepository.insert(asmRecord);
    }

}
