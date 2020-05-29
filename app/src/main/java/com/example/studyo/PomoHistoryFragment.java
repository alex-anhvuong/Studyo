package com.example.studyo;

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
import com.example.studyo.viewmodels.PomoViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PomoHistoryFragment extends Fragment {
    String TAG = this.getClass().getSimpleName();
    private RecyclerView itemsRecyclerView;
    private PmHistoryAdapter itemsAdapter;
    private PomoViewModel sViewModel;

    public PomoHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pomo_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemsRecyclerView = view.findViewById(R.id.recyclerview_pomo_items);
        itemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        itemsAdapter = new PmHistoryAdapter(new ArrayList<PomoRecord>());
        itemsRecyclerView.setAdapter(itemsAdapter);

        ViewModelProvider.AndroidViewModelFactory avmFactory = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication());
        sViewModel =  new ViewModelProvider(requireActivity(), avmFactory).get(PomoViewModel.class);
        sViewModel.getPomoRecords().observe(getViewLifecycleOwner(), new Observer<List<PomoRecord>>() {
            @Override
            public void onChanged(List<PomoRecord> pomoRecords) {
                itemsAdapter.setPomoHistory(pomoRecords);
                itemsAdapter.notifyDataSetChanged();
            }
        });
    }

    public class PmHistoryAdapter extends RecyclerView.Adapter<PomoViewHolder> {
        
        private List<PomoRecord> pomoHistory;

        public void setPomoHistory(List<PomoRecord> pomoHistory) {
            this.pomoHistory = pomoHistory;
        }

        PmHistoryAdapter(List<PomoRecord> pomoHistory) {
            this.pomoHistory = pomoHistory;
        }

        @NonNull
        @Override
        public PomoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View cardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pomo_item, parent, false);
            PomoViewHolder pomoViewHolder = new PomoViewHolder(cardView);
            return pomoViewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull PomoViewHolder holder, int position) {
            PomoRecord pomoItem = pomoHistory.get(position);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            holder.pmDate.setText(formatter.format(pomoItem.getPmDate()));
            if (pomoItem.getPmIsSuccessful()) {
//                holder.pmDescription.setText("Successfully studied for a duration of " +  pomoItem.getPmSeconds()/60 + " minutes.");
                holder.pmDescription.setText("Successfully studied for a duration of " +  pomoItem.getPmSeconds() + " seconds.");
                holder.pmIsSuccessful.setImageResource(R.drawable.ic_tick);
            }
            else {
//                holder.pmDescription.setText("You missed a pomodoro. You were focus for " + pomoItem.getPmSeconds()/60 + " minutes though, well done!");
                holder.pmDescription.setText("You missed a pomodoro. You were focus for " + pomoItem.getPmSeconds() + " seconds though, well done!");
                holder.pmIsSuccessful.setImageResource(R.drawable.ic_fail);
            }
        }

        @Override
        public int getItemCount() {
            return pomoHistory.size();
        }
    }

    private class PomoViewHolder extends RecyclerView.ViewHolder {
        TextView pmDescription;
        TextView pmDate;
        ImageView pmIsSuccessful;

        public PomoViewHolder(@NonNull View itemView) {
            super(itemView);
            pmDescription = itemView.findViewById(R.id.text_pomo_description);
            pmDate = itemView.findViewById(R.id.text_pomo_date);
            pmIsSuccessful = itemView.findViewById(R.id.image_pomo_isSuccessful);
        }
    }
}
