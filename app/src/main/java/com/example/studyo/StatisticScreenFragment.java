package com.example.studyo;


import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.studyo.database.PomoRecord;

import java.util.List;


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
    private StudyoViewModel sViewModel;
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

        optionsRecyclerView = view.findViewById(R.id.recyclerview_stat_options);
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        optionsAdapter = new OptionsAdapter(getResources().getStringArray(R.array.stat_option_titles));
        optionsRecyclerView.setAdapter(optionsAdapter);

//        ViewModelProvider.AndroidViewModelFactory avmFactory = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication());
//        sViewModel =  new ViewModelProvider(requireActivity(), avmFactory).get(StudyoViewModel.class);
//
//        sViewModel.getPomoRecords().observe(getViewLifecycleOwner(), new Observer<List<PomoRecord>>() {
//            @Override
//            public void onChanged(List<PomoRecord> pomoRecords) {
//                TextView showDataView = view.findViewById(R.id.text_display_database);
//                Log.i(TAG, String.valueOf(pomoRecords.size()));
//                String data = "";
//                if (pomoRecords.size() != 0) {
//                    for (PomoRecord pRecords: pomoRecords) {
//                        data += pRecords.getPmID() + "&" +pRecords.getPmIsSuccessful().toString() + "&" + pRecords.getPmSeconds() + "&" + pRecords.getPmDate() + "/";
//                    }
//                }
//                showDataView.setText(data);
//            }
//        });
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
                    switch (optionName.getText().toString()) {
                        case "Pomodoro History":
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, new PomoHistoryFragment())
                                    .commit();
                            break;
                        default:
                            break;
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
