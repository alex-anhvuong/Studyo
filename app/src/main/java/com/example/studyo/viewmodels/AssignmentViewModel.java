package com.example.studyo.viewmodels;

import androidx.lifecycle.ViewModel;

import com.example.studyo.database.AssignmentRecord;

public class AssignmentViewModel extends ViewModel {
    AssignmentRepository asmRepository;

    public AssignmentViewModel() {
        asmRepository = new AssignmentRepository();
    }

    public void insertAssignmentRecord(AssignmentRecord asmRecord) {
        asmRepository.insert(asmRecord);
    }
}
