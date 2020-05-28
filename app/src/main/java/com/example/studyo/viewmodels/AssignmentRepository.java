package com.example.studyo.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.studyo.database.AssignmentRecord;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AssignmentRepository {

    private DatabaseReference onlineDB;

    public AssignmentRepository() {
        onlineDB = FirebaseDatabase.getInstance().getReference("assignment_records");
    }

    public DatabaseReference getOnlineDB() {
        return onlineDB;
    }
    public void insert (final AssignmentRecord asmRecord) {
        onlineDB.setValue(asmRecord)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("DEBUG", "Successfully added data");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Write failed
                        Log.i("DEBUG", "Fail to add data: " + e);
                    }
                });;
    }
}
