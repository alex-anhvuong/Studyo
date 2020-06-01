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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssignmentRepository {

    private DatabaseReference onlineDB;
    private MutableLiveData<Map<String, Date>> idToDatesMap = new MutableLiveData<>();
    private MutableLiveData<List<List<String>>> todayAsmList = new MutableLiveData<>();
    private Map<String, Date> idDatesMapOneTime = new HashMap<>();
    private Map<String, List<String>> idAsmDetailsMap = new HashMap<>();

    public LiveData<Map<String, Date>> getIdToDatesMap() {
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

    public LiveData<List<List<String>>> getTodayAsmList() {

        //  Get the IDs of assignments on today
        onlineDB.child("dates").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<Map<String, Date>> t = new GenericTypeIndicator<Map<String, Date>>() {};
                idDatesMapOneTime = dataSnapshot.getValue(t);
                Calendar calendar = Calendar.getInstance();
                for (Map.Entry<String, Date> entry : idDatesMapOneTime.entrySet()) {
                    String key = entry.getKey();
                    Calendar valueCal = Calendar.getInstance();
                    valueCal.setTime(entry.getValue());
                    if (valueCal.get(Calendar.DATE) == calendar.get(Calendar.DATE)
                            && valueCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                            && valueCal.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {

                        //  Get the assignment's title with the id arrays
                        onlineDB.child("titles").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                List<String> details = new ArrayList<String>();
                                details.add(dataSnapshot.getValue(String.class));
                                idAsmDetailsMap.put(key, details);
//                                Log.i("DEBUG", details.toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        //  Get the assignment's unit name with the id arrays
                        onlineDB.child("units").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                List<String> details = idAsmDetailsMap.get(key);
                                details.add(dataSnapshot.getValue(String.class));
                                idAsmDetailsMap.put(key, details);
//                                Log.i("DEBUG", details.toString());
                                todayAsmList.postValue(new ArrayList<>(idAsmDetailsMap.values()));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //  Get the assignment's title


        return todayAsmList;
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
