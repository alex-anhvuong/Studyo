package com.example.studyo;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.studyo.viewmodels.PomoViewModel;


/**
 *  StatisticScreenFragment
 *
 *  Usage: This fragment shows the statistic timer screen
 *  Functionality:
 *      - Show the stats of user's pomodoro status
 *      - 
 */
public class StatisticScreenFragment extends Fragment {
    private RecyclerView optionsRecyclerView;
    private RecyclerView.Adapter optionsAdapter;
    private PomoViewModel sViewModel;
    private String TAG = this.getClass().getSimpleName();

    public StatisticScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //  This fragment display a list showing two options
        //  One is navigating to Pomodoro history
        //  The other is navigating to assignment records
        optionsRecyclerView = view.findViewById(R.id.recyclerview_stat_options);
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        optionsAdapter = new OptionsAdapter(getResources().getStringArray(R.array.stat_option_titles));
        optionsRecyclerView.setAdapter(optionsAdapter);
    }

    class OptionsAdapter extends RecyclerView.Adapter<OptionsAdapter.OptionsViewHolder> {

        private String[] optionSet;
        public OptionsAdapter(String[] optionSet) {
            this.optionSet = optionSet;
        }

        @NonNull
        @Override
        public OptionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_stat_option, parent, false);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView optionName = v.findViewById(R.id.text_stat_option_name);
                    if (optionName.getText().toString().equals("Pomodoro History")) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new PomoHistoryFragment())
                                .addToBackStack(null)
                                .commit();
                    } else if (optionName.getText().toString().equals("Assignments")) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new AssignmentRecordFragment())
                                .addToBackStack(null)
                                .commit();
                    }
                }
            });
            OptionsViewHolder optionsViewHolder = new OptionsViewHolder(cardView);
            return optionsViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull OptionsViewHolder holder, int position) {
            holder.optionName.setText(optionSet[position]);
            switch (optionSet[position]) {
                case "Pomodoro History":
                    holder.optionIcon.setImageResource(R.drawable.ic_timer);
                    holder.optionName.setTextColor(getResources().getColor(R.color.green_800));
                    break;
                default:
                    holder.optionIcon.setImageResource(R.drawable.ic_planner);
                    holder.optionName.setTextColor(getResources().getColor(R.color.orange_800));
            }
        }

        @Override
        public int getItemCount() {
            return optionSet.length;
        }

        private class OptionsViewHolder extends RecyclerView.ViewHolder {
            TextView optionName;
            ImageView optionIcon;

            public OptionsViewHolder(@NonNull View itemView) {
                super(itemView);
                optionName = itemView.findViewById(R.id.text_stat_option_name);
                optionIcon = itemView.findViewById(R.id.image_stat_option_icon);
            }
        }
    }

}
