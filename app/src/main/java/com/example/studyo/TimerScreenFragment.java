package com.example.studyo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.studyo.database.PomoRecord;
import com.example.studyo.viewmodels.PomoViewModel;
import com.github.stefanodp91.android.circularseekbar.CircularSeekBar;
import com.github.stefanodp91.android.circularseekbar.OnCircularSeekBarChangeListener;

import java.util.Date;


/**
 *  TimerScreenFragment
 *
 *  Usage: This fragment shows the pomodoro timer screen
 *  Functionality:
 *      - Start timer countdown
 *      - Stop the timer
 */
public class TimerScreenFragment extends Fragment {

    private String TAG = this.getClass().getSimpleName();
    /**
     *  Properties of the fragment
     */
    private int ONE_MINUTE = 60;
    private Boolean isRunning = false;  //  The state of the timer (is running / paused)
    private int initSecs = 10 * ONE_MINUTE;   //    The time user will start count down from
    private Long endTime = Long.valueOf(0); //  Time stamp: endTime = initSecs + (the timestamp when user play the timer)
    private int seconds;    //  The time that is left
    private Handler timerHandler;   //  a Handler to run a task in the background
    private Runnable timerRunnable; //  the task to be run  (counting down)
    //  Views
    private TextView timerView; //  the displayed time
    private CircularSeekBar studyTimerBar;  //  the progress bar representation of the timer
    private Button triggerTimerButton;
    //  Architecture Components
    private PomoViewModel sViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //  Initiate the state of the timer
        if (savedInstanceState != null) {
            initSecs = savedInstanceState.getInt("currentTimerInitSeconds");
            endTime = savedInstanceState.getLong("endTime");
            isRunning = savedInstanceState.getBoolean("currentTimerRunning");
            Log.i(TAG, "Restore the state: " + initSecs + " " + isRunning);
        }

        timerView = getActivity().findViewById(R.id.text_study_timer);
        triggerTimerButton = getActivity().findViewById(R.id.button_trigger_timer);
        studyTimerBar = getActivity().findViewById(R.id.seekBar_study);

        //  Define UI Thread's Looper as the looper of timerHandler
        //  Define the runnable
        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = new TimerRunnable();
        //  Set onclick method for Start/Pause button
        triggerTimerButton.setOnClickListener(new TriggerTimerOnClickListener());
        //  Set event listener for the progress bar
        setSeekBarListener();
        //  Create a ViewModel to insert data into database
        ViewModelProvider.AndroidViewModelFactory avmFactory = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication());
        sViewModel = new ViewModelProvider(requireActivity(), avmFactory).get(PomoViewModel.class);

        //  Initiate timer view
        if (!isRunning) {
            timerView.setText(SecondsToTimeFormat(initSecs));
            setStudyTimerBarStatus(initSecs/ONE_MINUTE - 10);
        }
        else {
            //  endTime - current TimeStamp = the time we have left
            int timeLeft = (int) ((endTime - System.currentTimeMillis()) / 1000);
            if (timeLeft <=  0) {
                //  timer has finished
                SetSuccessAttempt();
            } else {
                //  Continue the timer from timeLeft
                seconds = timeLeft;
                timerView.setText(SecondsToTimeFormat(seconds));
                setStudyTimerBarStatus(100 /(float)initSecs * (float)seconds);
                timerHandler.post(timerRunnable);
                triggerTimerButton.setText("Stop");
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentTimerInitSeconds", initSecs);
        outState.putLong("endTime", endTime);
        outState.putBoolean("currentTimerRunning", isRunning);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //  Stop the callback when Fragment is destroyed
        //  The timer will be continue when returning with our pre-defined endTime
        timerHandler.removeCallbacks(timerRunnable);
    }

    /**
     *  TriggerTimerOnClickListener
     *
     *  Usage: Start/stop button OnClickListener
     */
    private class TriggerTimerOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //  If the timer is not running, start the timer
            if (!isRunning) {
                isRunning = true;
                ((Button)v).setText("Stop");
                seconds = initSecs;
                //  Predefine a timestamp in the future when the timer will finish
                endTime = System.currentTimeMillis() + (long)initSecs * 1000;

                //  Start the counting down in the background
                timerHandler.post(timerRunnable);
                //  Showing the progress starting from 100%
                setStudyTimerBarStatus(100);
            }
            else {
                //  Stop the timer if it's running, counted as one failed pomodoro attempt
                timerHandler.removeCallbacks(timerRunnable);
                isRunning = false;
                ((Button)v).setText("Start");
                timerView.setText(SecondsToTimeFormat(initSecs));
                //  Reset the progress to our initial time
                setStudyTimerBarStatus(initSecs/ONE_MINUTE - 10);
                //  Post this result to the database as a failed attempt
                //  pmDate = current Date
                //  pmTime = initSecs - seconds;
                //  pmIsSuccessul = false
                sViewModel.insert(new PomoRecord(false, initSecs - seconds, new Date()));
            }
        }
    }

    /**
     *  TimerRunnable
     *
     *  Usage: a Task object that decrease "seconds" every second and update the UI
     */
    class TimerRunnable implements Runnable {
        @Override
        public void run() {
            Log.i(TAG, "The time is " + seconds);
            //  Decrease "seconds" for each run()
            if (isRunning) {
                //  Decrease the time & the progress's indicator
                seconds--;
                float progress = studyTimerBar.getProgress();
                setStudyTimerBarStatus(progress - 100 /(float)initSecs);
                timerView.setText(SecondsToTimeFormat(seconds));
            }

            //  TO DO:
            //  If the timer reach 0, trigger a congratulation fragment
            if (seconds == 0) {
                SetSuccessAttempt();
                return;
            }

            //  Recurse the TimeRunnable object to keep the timer running
            timerHandler.postDelayed(this, 1000);
        }
    }

    private void SetSuccessAttempt() {
        isRunning = false;
        triggerTimerButton.setText("Start");
        timerHandler.removeCallbacks(timerRunnable);
        timerView.setText(SecondsToTimeFormat(initSecs));
        setStudyTimerBarStatus(initSecs/ONE_MINUTE - 10);
        //  Post this result to the database as a successful attempt
        //  pmDate = current Date
        //  pmTime = initSecs;
        //  pmIsSuccessul = true
        sViewModel.insert(new PomoRecord(true, initSecs, new Date()));
        return;
    }

    /**
     * SecondsToTimeFormat
     *
     * Usage: Convert a number of seconds to string format (mm(minutes) : ss(seconds))
     * @param convertedTime: an amount of seconds
     * @return the String representation of the current timer's time
     */
    private String SecondsToTimeFormat(int convertedTime) {
        String minutes = String.valueOf(convertedTime / 60);
        String secs = String.valueOf(convertedTime % 60);

        if (minutes.length() <= 1) minutes = "0" + minutes;
        if (secs.length() <= 1) secs = "0" + secs;

        String formattedTime = minutes + " : " + secs;

        return formattedTime;
    }

    /**
     * setStudyTimerBarStatus
     *
     * @param progress: the current progress
     */
    private void setStudyTimerBarStatus (float progress) {
        studyTimerBar.setProgress(progress);
        studyTimerBar.setEnabled(!isRunning);
    }

    /**
     *  setSeekBarListener
     *
     *  Usage: set a event listener for our progress bar
     */
    private void setSeekBarListener() {
        studyTimerBar.setOnRoundedSeekChangeListener(new OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChange(CircularSeekBar CircularSeekBar, float progress) {
                //  set the timer's initial time by dragging the bar
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
}
