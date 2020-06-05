package com.example.studyo;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.studyo.viewmodels.AssignmentViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlannerScreenFragment extends Fragment {

    public static final int MAX_DATE_COUNT = 35;
    Button addAssignmentButton;
    RecyclerView calendarGridView, todayAsmListView;
    CalendarAdapter calendarAdapter;
    TextView monAndYearText;
    Calendar calendar;
    AssignmentViewModel asmViewModel;
    private ArrayList<Date> asmDates = new ArrayList<>();

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
        todayAsmListView = view.findViewById(R.id.recyclerview_today_asm);
        monAndYearText = view.findViewById(R.id.text_cal_mon_year);
        ImageButton prevButton = view.findViewById(R.id.button_prev_month);
        ImageButton nextButton = view.findViewById(R.id.button_next_month);
        prevButton.setOnClickListener(new SwitchMonthOnClickListener());
        nextButton.setOnClickListener(new SwitchMonthOnClickListener());

        addAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddAssignmentFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        calendarGridView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarAdapter = new CalendarAdapter(new ArrayList<>());
        calendarGridView.setAdapter(calendarAdapter);

        calendar = Calendar.getInstance();
        UpdateCalendar();

        todayAsmListView.setLayoutManager(new LinearLayoutManager(getContext()));

        asmViewModel = new AssignmentViewModel();
        asmViewModel.getAsmDates().observe(getViewLifecycleOwner(), new Observer<Map<String, Date>>() {
            @Override
            public void onChanged(Map<String, Date> stringDateMap) {
                if (stringDateMap != null) {
                    asmDates = new ArrayList<>(stringDateMap.values());
                    Log.i("DEBUG", asmDates.toString());
                    calendarAdapter.notifyItemRangeChanged(0, calendarAdapter.getItemCount());
                }
            }
        });

        asmViewModel.getTodayAsmList().observe(getViewLifecycleOwner(), new Observer<List<List<String>>>() {
            @Override
            public void onChanged(List<List<String>> lists) {
                Log.i("DEBUG", lists.toString());
                todayAsmListView.setAdapter(new TodayAsmAdapter(lists));
                asmViewModel.getTodayAsmList().removeObservers(getViewLifecycleOwner());
            }
        });
    }

    class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
        ArrayList<Date> dates;

        public CalendarAdapter(ArrayList<Date> dates) { this.dates = dates; }

        public void clearDates() {
            dates.clear();
        }

        public void setDates(ArrayList<Date> dates) {
            this.dates.addAll(dates);
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
            cellCalendar.setTime(dates.get(position));
            holder.dateView.setText(cellCalendar.get(Calendar.DATE) + "");
            holder.dateView.setTextColor(Color.BLACK);
            holder.cardView.setBackgroundColor(Color.WHITE);

            //  If the cell is not in the calendar's month, make is less visible
            if (cellCalendar.get(Calendar.MONTH) != calendar.get(Calendar.MONTH)) holder.dateView.setTextColor(Color.parseColor("#E0E0E0"));

            //  Highlight the cell of the current date
            if (cellCalendar.get(Calendar.DATE) == Calendar.getInstance().get(Calendar.DATE)
                && cellCalendar.get(Calendar.MONTH) == Calendar.getInstance().get(Calendar.MONTH)
                && cellCalendar.get(Calendar.YEAR) == Calendar.getInstance().get(Calendar.YEAR)) {
                holder.cardView.setBackgroundColor(Color.rgb(51, 153, 255));
                return;
            }

            int asmCount = 0;
            for (int i = 0; i < asmDates.size(); i++) {
                Calendar asmCalendar = Calendar.getInstance();
                asmCalendar.setTime(asmDates.get(i));
                if (cellCalendar.get(Calendar.DATE) == asmCalendar.get(Calendar.DATE)
                        && cellCalendar.get(Calendar.MONTH) == asmCalendar.get(Calendar.MONTH)
                        && cellCalendar.get(Calendar.YEAR) == asmCalendar.get(Calendar.YEAR)) {
                    asmCount++;
                }
            }

            SetViewBackgroundColor(asmCount, holder.cardView);
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

    class TodayAsmAdapter extends RecyclerView.Adapter<TodayAsmAdapter.TodayAsmViewHolder> {
        List<List<String>> todayAsmList;

        public TodayAsmAdapter (List<List<String>> todayAsms) {
            this.todayAsmList = todayAsms;
        }

        @NonNull
        @Override
        public TodayAsmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View todayAsmCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_today_asm, parent, false);
            TodayAsmViewHolder todayAsmViewHolder = new TodayAsmViewHolder(todayAsmCard);
            return todayAsmViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TodayAsmViewHolder holder, int position) {
            holder.asmTitle.setText(todayAsmList.get(position).get(0));
            holder.asmUnitName.setText(todayAsmList.get(position).get(1));
        }

        @Override
        public int getItemCount() {
            return todayAsmList.size();
        }

        private class TodayAsmViewHolder extends RecyclerView.ViewHolder {
            TextView asmTitle, asmUnitName;

            public TodayAsmViewHolder(@NonNull View itemView) {
                super(itemView);
                asmTitle = itemView.findViewById(R.id.text_asm_title2);
                asmUnitName = itemView.findViewById(R.id.text_unit_name);
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

        //  Update the Adapter
        int currentSize = calendarAdapter.getItemCount();
        calendarAdapter.clearDates();
        calendarAdapter.setDates(cells);
        calendarAdapter.notifyItemRangeRemoved(0, currentSize);
        calendarAdapter.notifyItemRangeInserted(0, cells.size());

        //  Set the text displaying current month
        monAndYearText.setText(calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, new Locale("en")) + " " + calendar.get(Calendar.YEAR));
    }

    private class SwitchMonthOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int navigation = ((ImageButton)v).getId();
            switch (navigation) {
                case R.id.button_next_month:
                    calendar.add(Calendar.MONTH, 1);
                    UpdateCalendar();
                    break;
                default:
                    calendar.add(Calendar.MONTH, -1);
                    UpdateCalendar();
                    break;
            }
        }
    }

    private void SetViewBackgroundColor(int asmCount, CardView view) {
        switch (asmCount) {
            case 1:
                view.setBackgroundColor(Color.rgb(174, 213, 129));
                break;
            case 2:
                view.setBackgroundColor(Color.rgb(76, 175, 80));
                break;
            case 3:
                view.setBackgroundColor(Color.rgb(27, 94, 32));
                break;
        }
    }
}
