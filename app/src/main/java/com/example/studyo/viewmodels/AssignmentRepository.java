package com.example.studyo.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.studyo.database.AssignmentRecord;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignmentRepository {

    private DatabaseReference onlineDB;
    private MutableLiveData<Map<String, Date>> idToDatesMap = new MutableLiveData<>();

    public MutableLiveData<Map<String, Date>> getIdToDatesMap() {
        if (idToDatesMap.getValue() == null) {
            onlineDB.child("/dates/").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<Map<String, Date>> dataType = new GenericTypeIndicator<Map<String, Date>>() {};
                    idToDatesMap.postValue(dataSnapshot.getValue(dataType));
//                    Log.i("DEBUG", dataSnapshot.getValue(dataType).values().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        return idToDatesMap;
    }


    public AssignmentRepository() {
        onlineDB = FirebaseDatabase.getInstance().getReference("assignment_records");
    }

    public void insert (final AssignmentRecord asmRecord) {

        String key = onlineDB.child("titles").push().getKey();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/titles/" + key, asmRecord.getaName());
        childUpdates.put("/units/" + key, asmRecord.getaUnit());
        childUpdates.put("/dates/" + key, asmRecord.getaDate());
        onlineDB.updateChildren(childUpdates)
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
