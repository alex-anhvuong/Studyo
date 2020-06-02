package com.example.studyo;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.studyo.database.AssignmentRecord;
import com.example.studyo.viewmodels.AssignmentViewModel;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddAssignmentFragment extends Fragment {
    TextView textSetDate;
    EditText textSetAsmTitle;
    EditText textSetUnitName;
    Calendar calendar;
    Button submit;

    private AssignmentViewModel asmViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_assignment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textSetAsmTitle = view.findViewById(R.id.edit_asm_title);
        textSetUnitName = view.findViewById(R.id.edit_unit_title);
        textSetDate = view.findViewById(R.id.text_set_date);
        submit = view.findViewById(R.id.button_submit_asm);
        asmViewModel = new AssignmentViewModel();

        calendar = Calendar.getInstance();
        textSetDate.setText(calendar.get(Calendar.DATE) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));

        textSetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dialog = new DatePickerDialog(getContext(), 0, new DueDateOnDateSetListener(), calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
                dialog.show();
            }
        });

        submit.setOnClickListener(new SubmitAsmDateOnClickListener());
    }

    private class SubmitAsmDateOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (textSetAsmTitle.getText().toString().equals("") || textSetUnitName.getText().toString().equals("")) {
                Toast.makeText(getContext(), "Please let me know the Assignment Title, or the Unit Name!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (calendar.compareTo(Calendar.getInstance()) < 0) {
                Toast.makeText(getContext(), "Please choose a date later than today!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            AssignmentRecord newRecord = new AssignmentRecord();
            newRecord.setaName(textSetAsmTitle.getText().toString());
            newRecord.setaUnit(textSetUnitName.getText().toString());
            newRecord.setaDate(calendar.getTime());
            asmViewModel.insertAssignmentRecord(newRecord);
        }
    }

    private class DueDateOnDateSetListener implements DatePickerDialog.OnDateSetListener {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            textSetDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            calendar.set(year, month, dayOfMonth);
        }
    }
}
