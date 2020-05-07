package com.example.studyo;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticScreenFragment extends Fragment {
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

        ViewModelProvider.AndroidViewModelFactory avmFactory = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication());
        sViewModel =  new ViewModelProvider(requireActivity(), avmFactory).get(StudyoViewModel.class);

        sViewModel.getPomoRecords().observe(getViewLifecycleOwner(), new Observer<List<PomoRecord>>() {
            @Override
            public void onChanged(List<PomoRecord> pomoRecords) {
                TextView showDataView = view.findViewById(R.id.text_display_database);
                Log.i(TAG, String.valueOf(pomoRecords.size()));
                String data = "";
                if (pomoRecords.size() != 0) {
                    data = showDataView.getText().toString() + " " + pomoRecords.get(pomoRecords.size() - 1).getPmIsSuccessful();
                }
                showDataView.setText(data);
            }
        });
    }

}
