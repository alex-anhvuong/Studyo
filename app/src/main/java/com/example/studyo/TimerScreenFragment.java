package com.example.studyo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.stefanodp91.android.circularseekbar.CircularSeekBar;
import com.github.stefanodp91.android.circularseekbar.OnCircularSeekBarChangeListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerScreenFragment extends Fragment {

    private Boolean isRunning;
    private int initSecs, seconds;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private TextView timerView;
    private CircularSeekBar studyTimerBar;

    public TimerScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initSecs = 10 * 60;
        isRunning = false;
        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = new TimerRunnable();
        timerView = view.findViewById(R.id.text_study_timer);

        timerView.setText(SecondsToTimeFormat(initSecs));

        Button triggerTimer = view.findViewById(R.id.button_trigger_timer);
        triggerTimer.setOnClickListener(new TriggerTimerOnClickListener());

        studyTimerBar = view.findViewById(R.id.seekBar_study);
        studyTimerBar.setOnRoundedSeekChangeListener(new OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChange(CircularSeekBar CircularSeekBar, float progress) {
                initSecs = 10 * 60 + (int)Math.floor(progress/5) * 5 * 60;
                timerView.setText(SecondsToTimeFormat(initSecs));
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar CircularSeekBar) {

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar CircularSeekBar) {
            }
        });
    }

    private class TriggerTimerOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (!isRunning) {
                //  Start the timer
                isRunning = true;
                seconds = initSecs;
                timerHandler.post(timerRunnable);
                ((Button)v).setText("Stop");
            }
            else {
                isRunning = false;
                timerHandler.removeCallbacks(timerRunnable);
                timerView.setText(SecondsToTimeFormat(initSecs));
                ((Button)v).setText("Start");
            }
        }
    }

    //  a Task object that do: decrease the timer and update the UI
    class TimerRunnable implements Runnable {
        @Override
        public void run() {
            //  Decrease the seconds variable for each run()
            if (isRunning) seconds--;

            timerView.setText(SecondsToTimeFormat(seconds));

            //  Recurse the TimeRunnable object
            timerHandler.postDelayed(this, 1000);
        }
    }

    //  Convert seconds -> Date object -> String
    private String SecondsToTimeFormat(int convertedTime) {
        int minute = convertedTime / 60;
        String secs = String.valueOf(convertedTime % 60);
        if (secs.length() <= 1) secs = "0" + secs;

        String formattedTime = minute + " : " + secs;

        return formattedTime;
    }
}
