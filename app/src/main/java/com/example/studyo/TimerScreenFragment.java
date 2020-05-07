package com.example.studyo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.stefanodp91.android.circularseekbar.CircularSeekBar;
import com.github.stefanodp91.android.circularseekbar.OnCircularSeekBarChangeListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class TimerScreenFragment extends Fragment {

    private int ONE_MINUTE = 1;
    private Boolean isRunning;
    private int initSecs, seconds;
    private Handler timerHandler;
    private Runnable timerRunnable;
    private TextView timerView;
    private CircularSeekBar studyTimerBar;

    private StudyoViewModel sViewModel;

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

        initSecs = 10 * ONE_MINUTE;
        isRunning = false;
        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = new TimerRunnable();
        timerView = view.findViewById(R.id.text_study_timer);

        timerView.setText(SecondsToTimeFormat(initSecs));

        Button triggerTimer = view.findViewById(R.id.button_trigger_timer);
        triggerTimer.setOnClickListener(new TriggerTimerOnClickListener());

        studyTimerBar = view.findViewById(R.id.seekBar_study);
        setSeekBarListener();

        // !!!!
        ViewModelProvider.AndroidViewModelFactory avmFactory = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication());
        sViewModel = new ViewModelProvider(requireActivity(), avmFactory).get(StudyoViewModel.class);
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

                //  Stop the user from changing the status of the progress bar
                setStudyTimerBarStatus(false, 100);
            }
            else {
                isRunning = false;
                timerHandler.removeCallbacks(timerRunnable);
                timerView.setText(SecondsToTimeFormat(initSecs));
                ((Button)v).setText("Start");

                //  Allow the user to change the progress bar
                setStudyTimerBarStatus(true, initSecs - 10 * ONE_MINUTE);
                sViewModel.insert(new PomoRecord(false));
            }
        }
    }

    //  a Task object that do: decrease the timer and update the UI
    class TimerRunnable implements Runnable {
        @Override
        public void run() {
            //  Decrease the seconds variable for each run()
            if (isRunning) {
                seconds--;
                float progress = studyTimerBar.getProgress();
                studyTimerBar.setProgress(progress - 100 /(float)initSecs);
            }

            timerView.setText(SecondsToTimeFormat(seconds));

            //  If the timer reach 0, reset the timer/run a new fragment
            if (seconds == 0) {
                isRunning = false;
                timerHandler.removeCallbacks(timerRunnable);
                timerView.setText(SecondsToTimeFormat(initSecs));
//                Button b = getActivity().findViewById(R.id.button_trigger_timer);
//                b.setText("Start");
                setStudyTimerBarStatus(true, initSecs - 10 * ONE_MINUTE);
                sViewModel.insert(new PomoRecord(true));
                return;
            }

            //  Recurse the TimeRunnable object
            timerHandler.postDelayed(this, 1000);
        }
    }

    private void setSeekBarListener() {
        studyTimerBar.setOnRoundedSeekChangeListener(new OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChange(CircularSeekBar CircularSeekBar, float progress) {
                initSecs = 10 * ONE_MINUTE + (int)Math.floor(progress/5) * 5 * ONE_MINUTE;
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

    //  Convert seconds -> Date object -> String
    private String SecondsToTimeFormat(int convertedTime) {
        String minutes = String.valueOf(convertedTime / ONE_MINUTE);
        String secs = String.valueOf(convertedTime % ONE_MINUTE);

        if (minutes.length() <= 1) minutes = "0" + minutes;
        if (secs.length() <= 1) secs = "0" + secs;

        String formattedTime = minutes + " : " + secs;

        return formattedTime;
    }

    private void setStudyTimerBarStatus (Boolean bool, float progress) {
        studyTimerBar.setEnabled(bool);
        studyTimerBar.setIndicatorEnabled(bool);
        studyTimerBar.setProgress(progress);
    }
}
