package com.example.studyo.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyo.database.AssignmentRecord;

import java.util.Date;
import java.util.Map;

public class AssignmentViewModel extends ViewModel {
    private AssignmentRepository asmRepository;
    private LiveData<Map<String, Date>> asmDates;

    public LiveData<Map<String, Date>> getAsmDates() {
        return asmDates;
    }

    public AssignmentViewModel() {
        asmRepository = new AssignmentRepository();
    }

    public void insertAssignmentRecord(AssignmentRecord asmRecord) {
        asmRepository.insert(asmRecord);
    }

    public void getAssignmentDates(String path) {
       asmRepository.getDates(path);
       asmDates = asmRepository.getIdToDatesMap();
    }
}
