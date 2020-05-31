package com.example.studyo.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.studyo.database.AssignmentRecord;

import java.util.Date;
import java.util.Map;

public class AssignmentViewModel extends ViewModel {
    private AssignmentRepository asmRepository;
    private MutableLiveData<Map<String, Date>> asmDates;

    public AssignmentViewModel() {
        asmRepository = new AssignmentRepository();
    }

    public MutableLiveData<Map<String, Date>> getAsmDates() {
        return asmRepository.getIdToDatesMap();
    }


    public void insertAssignmentRecord(AssignmentRecord asmRecord) {
        asmRepository.insert(asmRecord);
    }
}
