package com.example.studyo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.studyo.database.AssignmentRecord;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlannerScreenFragment extends Fragment {

    public static final int MAX_DATE_COUNT = 35;
    Button addAssignmentButton;
    RecyclerView calendarGridView;
    CalendarAdapter calendarAdapter;
    TextView monAndYearText;
    Calendar calendar;

    public PlannerScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_planner_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addAssignmentButton = view.findViewById(R.id.button_add_assignment);
        calendarGridView = view.findViewById(R.id.recyclerview_calendar_grid);
        monAndYearText = view.findViewById(R.id.text_cal_mon_year);
        Button prevButton = view.findViewById(R.id.button_prev_month);
        Button nextButton = view.findViewById(R.id.button_next_month);
        prevButton.setOnClickListener(new SwitchMonthOnClickListener());
        nextButton.setOnClickListener(new SwitchMonthOnClickListener());

        addAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddAssignmentFragment())
                        .commit();
            }
        });

        calendarGridView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarAdapter = new CalendarAdapter(new ArrayList<>());
        calendarGridView.setAdapter(calendarAdapter);

        calendar = Calendar.getInstance();
        UpdateCalendar();
    }

    class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
        ArrayList<Date> dates = new ArrayList<>();

        public CalendarAdapter(ArrayList<Date> dates) { this.dates = dates; }

        public void setDates(ArrayList<Date> dates) {
            this.dates = dates;
        }

        @NonNull
        @Override
        public CalendarAdapter.CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_cal_cell, parent, false);
            //  add on click listener here
            CalendarViewHolder viewHolder = new CalendarViewHolder(cellView);
            return viewHolder;
        }

        @SuppressLint("ResourceAsColor")
        @Override
        public void onBindViewHolder(@NonNull CalendarAdapter.CalendarViewHolder holder, int position) {
            Calendar cellCalendar = Calendar.getInstance();
            Date date = dates.get(position);
            cellCalendar.setTime(date);
            holder.dateView.setText(cellCalendar.get(Calendar.DATE) + "");
            holder.dateView.setTextColor(Color.BLACK);

            //  If the cell is not in the calendar's month, make is less visible
            if (cellCalendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) holder.dateView.setTextColor(Color.parseColor("#E0E0E0"));

            //  Highlight the cell of the current date
            if (cellCalendar.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE)
                && cellCalendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)
                && cellCalendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                holder.dateView.setTextColor(Color.WHITE);
                holder.cardView.setBackgroundColor(Color.rgb(51, 153, 255));
            }
        }

        @Override
        public int getItemCount() {
            return dates.size();
        }

        private class CalendarViewHolder extends RecyclerView.ViewHolder {
            CardView cardView;
            TextView dateView;

            public CalendarViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.card_date_cell);
                dateView = itemView.findViewById(R.id.text_cell_date);
            }
        }
    }

    private void UpdateCalendar() {
        //  Create array of Dates to be displayed
        ArrayList<Date> cells = new ArrayList<>();
        Calendar currentCalendar = (Calendar) calendar.clone();
        int maxCellsCount = MAX_DATE_COUNT;

        //  Determine the DAY of the 1st date of the month (Sun, Mon, Tue, ..., Sar)
        //
        currentCalendar.set(Calendar.DAY_OF_MONTH, 1);
        int monthBeginningCell = currentCalendar.get(Calendar.DAY_OF_WEEK) - 2;    //  Calendar.get() will return 2 for MONDAY

        //  Determine the date of the first cell (Monday)
        //  If the 1st day is Monday, we remains at the cell since we already -2 above
        if (monthBeginningCell == -1) {
            monthBeginningCell = 6; //  if 1st date is on Sunday
            maxCellsCount = 42;     //  42 cells for a month
        }
        currentCalendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell);

        //  Start filling all the cells with next dates

        while (cells.size() < maxCellsCount) {
            cells.add(currentCalendar.getTime());
            currentCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        Log.i("DEBUG", "Check calendar value " + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.MONTH));
        //  Update the Adapter
        calendarAdapter.setDates(cells);
        calendarAdapter.notifyDataSetChanged();

        //  Set the text displaying current month
        monAndYearText.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("en")) + " " + calendar.get(Calendar.YEAR));
    }

    private class SwitchMonthOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String navigation = ((Button)v).getText().toString();
            switch (navigation) {
                case "N":
                    calendar.add(Calendar.MONTH, 1);
                    Log.i("DEBUG", "Call to next calendar " + calendar.get(Calendar.DATE) + " " + calendar.get(Calendar.MONTH));
                    UpdateCalendar();
                    break;
                default:
                    calendar.add(Calendar.MONTH, -1);
                    UpdateCalendar();
                    break;
            }
        }
    }
}
