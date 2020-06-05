package com.example.studyo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.studyo.database.AssignmentRecord;
import com.example.studyo.viewmodels.AssignmentViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class AssignmentRecordFragment extends Fragment {
    RecyclerView asmRecordView;
    AssignmentRecordAdapter asmRecordAdapter;
    AssignmentViewModel asmViewModel;

    public AssignmentRecordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_assignment_record, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        asmRecordView = view.findViewById(R.id.recyclerview_future_asm);
        asmRecordView.setLayoutManager(new LinearLayoutManager(getContext()));
        asmRecordAdapter = new AssignmentRecordAdapter();

        //  Declare a AssignmentViewModel that observes for any changes in the assignment record Firebase's Realtime Database
        //  Update the RecylerView's Adapter accordingly to update the UIs
        asmViewModel = new AssignmentViewModel();
        asmViewModel.getAsmAllDetailsList().observe(getViewLifecycleOwner(), new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> stringObjectMap) {
                //  Convert data object into three arrays that store:
                //  Assignment's title, unit name and due date

                //  Get value of key "titles" and "units" in the database
                //  each of them contains a list of data, signitured by an automatically created key from Realtime Database
                //  For example:
                //  titles {
                //    "fasdkjfjsadkf-this-is-the-id" : "Some string",
                //    "id-2": "Another string",
                //    ...
                //  }
                //  the type of the value of "titles" is a HashMap<String, String>
                List<String> asmTitles = new ArrayList<>(((HashMap<String, String>) Objects.requireNonNull(stringObjectMap.get("titles"))).values());
                List<String> asmUnits = new ArrayList<>(((HashMap<String, String>) Objects.requireNonNull(stringObjectMap.get("units"))).values());
                List<Date> asmDates = new ArrayList<>();

                //  With Date type, Realtime Database convert the type to HashMap<String, Object>
                //  We use JSON to facilitate this conversion from Object to Date
                JSONObject asmDatesJson;
                try {
                    asmDatesJson = new JSONObject(stringObjectMap.get("dates").toString());
                    for(int i = 0; i < asmDatesJson.names().length(); i++){
                        JSONObject dateJSON = (JSONObject) asmDatesJson.get(asmDatesJson.names().getString(i));
                        Date d = new Date(dateJSON.getLong("time"));
                        asmDates.add(d);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < asmDates.size(); i++) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(asmDates.get(i));
                    //  If the assignment date is no less than today's date (still not due)
                    if (c.get(Calendar.YEAR) >= Calendar.getInstance().get(Calendar.YEAR)) {
                        if (c.get(Calendar.MONTH) >= Calendar.getInstance().get(Calendar.MONTH)) {
                            if (c.get(Calendar.DATE) >= Calendar.getInstance().get(Calendar.DATE)) {
                                asmRecordAdapter.addAsmRecords(new AssignmentRecord(asmTitles.get(i), asmUnits.get(i), asmDates.get(i)));
                            }
                        }
                    }
                }
                asmRecordView.setAdapter(asmRecordAdapter);
                asmViewModel.getAsmAllDetailsList().removeObservers(getViewLifecycleOwner());
            }
        });
    }

    private class AssignmentRecordAdapter extends RecyclerView.Adapter<AssignmentRecordAdapter.AsmRecordViewholder> {
        private List<AssignmentRecord> asmRecords = new ArrayList<>();

        public void addAsmRecords(AssignmentRecord asmRecord) {
            asmRecords.add(asmRecord);
            Collections.sort(asmRecords, new Comparator<AssignmentRecord>() {
                @Override
                public int compare(AssignmentRecord o1, AssignmentRecord o2) {
                    return o1.getaDate().compareTo(o2.getaDate());
                }
            });
        }

        @NonNull
        @Override
        public AssignmentRecordAdapter.AsmRecordViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_asm_item, parent, false);
            AsmRecordViewholder asmRecordViewholder = new AsmRecordViewholder(v);
            return asmRecordViewholder;
        }

        @Override
        public void onBindViewHolder(@NonNull AssignmentRecordAdapter.AsmRecordViewholder holder, int position) {
            holder.asmTitle.setText("Assignment: " + asmRecords.get(position).getaName());
            holder.asmUnit.setText("Unit: " + asmRecords.get(position).getaUnit());
            Calendar c = Calendar.getInstance();
            c.setTime(asmRecords.get(position).getaDate());
            holder.asmDate.setText("Due date: " + c.get(Calendar.DATE) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR));
        }

        @Override
        public int getItemCount() {
            return asmRecords.size();
        }

        private class AsmRecordViewholder extends RecyclerView.ViewHolder {
            TextView asmTitle, asmUnit, asmDate;
            public AsmRecordViewholder(@NonNull View itemView) {
                super(itemView);
                asmTitle = itemView.findViewById(R.id.text_asm_title2);
                asmUnit = itemView.findViewById(R.id.text_unit_name2);
                asmDate = itemView.findViewById(R.id.text_asm_date2);
            }
        }
    }
}
