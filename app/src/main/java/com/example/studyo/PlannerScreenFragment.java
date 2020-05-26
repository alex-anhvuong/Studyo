package com.example.studyo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlannerScreenFragment extends Fragment {

    Button addAssignmentButton;
    RecyclerView calendarGridView;

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

        addAssignmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new AddAssignmentFragment())
                        .commit();
            }
        });

        calendarGridView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        ArrayList dates = new ArrayList<Integer>();
        for (int i = 0; i < 31; i++) dates.add(i+1);
        calendarGridView.setAdapter(new CalendarAdapter(dates));
    }

    class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {
        ArrayList dates = new ArrayList<Integer>();

        public CalendarAdapter(ArrayList<Integer> dates) { this.dates = dates; }

        @NonNull
        @Override
        public CalendarAdapter.CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cellView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_cal_cell, parent, false);
            //  add on click listener here
            CalendarViewHolder viewHolder = new CalendarViewHolder(cellView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull CalendarAdapter.CalendarViewHolder holder, int position) {
            holder.dateView.setText(dates.get(position).toString());
        }

        @Override
        public int getItemCount() {
            return dates.size();
        }

        private class CalendarViewHolder extends RecyclerView.ViewHolder {
            TextView dateView;

            public CalendarViewHolder(@NonNull View itemView) {
                super(itemView);
                dateView = itemView.findViewById(R.id.text_cell_date);
            }
        }
    }
}
