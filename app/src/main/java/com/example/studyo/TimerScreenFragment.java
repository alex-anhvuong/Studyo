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

import com.github.stefanodp91.android.circularseekbar.CircularSeekBar;
import com.github.stefanodp91.android.circularseekbar.OnCircularSeekBarChangeListener;


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
    private int ONE_MINUTE = 1;
    private Boolean isRunning;  //  The state of the timer (is running / paused)
    private int initSecs;   //  initSecs is the time user will start count down from
    private int seconds;    //  seconds is  the timer that is left
    private Handler timerHandler;   //  a Handler run a task in the background
    private Runnable timerRunnable; //  the task to be run  (counting down)
    //  Views
    private TextView timerView; //  the displayed time
    private CircularSeekBar studyTimerBar;  //  the progress bar representation of the timer
    //  Architecture Components
    private StudyoViewModel sViewModel;

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
            Log.i(TAG, "Restore the state");
            initSecs = savedInstanceState.getInt("currentTimerInitSeconds");
            seconds = savedInstanceState.getInt("currentTimerSeconds");
            isRunning = savedInstanceState.getBoolean("currentTimerRunning");
        } else {
            initSecs = 10 * ONE_MINUTE;
            isRunning = false;
        }
        //  Define UI Thread's Looper as the looper of timerHandler
        //  Define the Runnable
        timerHandler = new Handler(Looper.getMainLooper());
        timerRunnable = new TimerRunnable();

        timerView = view.findViewById(R.id.text_study_timer);
        Button triggerTimer = view.findViewById(R.id.button_trigger_timer);
        studyTimerBar = view.findViewById(R.id.seekBar_study);

        //  Initiate timer view
        if (!isRunning) timerView.setText(SecondsToTimeFormat(initSecs));
        else {
            timerView.setText(SecondsToTimeFormat(seconds));
            triggerTimer.setText("Stop");
        }
        //  Set onclick method for Start/Pause button
        triggerTimer.setOnClickListener(new TriggerTimerOnClickListener());
        //  Set event listener for the progress bar
        setSeekBarListener();
        //  Create a ViewModel to insert data into database
        ViewModelProvider.AndroidViewModelFactory avmFactory = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication());
        sViewModel = new ViewModelProvider(requireActivity(), avmFactory).get(StudyoViewModel.class);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "Saving Fragment State");
        outState.putInt("currentTimerSeconds", seconds);
        outState.putBoolean("currentTimerRunning", isRunning);
        outState.putInt("currentTimerInitSeconds", initSecs);
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

                //  Start the counting down in the background
                timerHandler.post(timerRunnable);
                //  Stop the user from changing the status of the progress bar
                setStudyTimerBarStatus(false, 100);
            }
            else {  //  Stop the timer if it's running, counted as one failed pomodoro attempt
                //  Stop the counting down
                timerHandler.removeCallbacks(timerRunnable);

                isRunning = false;
                ((Button)v).setText("Start");
                timerView.setText(SecondsToTimeFormat(initSecs));

                //  Allow the user to change the progress bar
                setStudyTimerBarStatus(true, initSecs - 10 * ONE_MINUTE);
                //  Post this result to the database as a failed attempt
                //  pmDate = current Date
                //  pmTime = initSecs - seconds;
                //  pmIsSuccessul = false
                sViewModel.insert(new PomoRecord(false));
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
                seconds--;
                float progress = studyTimerBar.getProgress();
                studyTimerBar.setProgress(progress - 100 /(float)initSecs);
                timerView.setText(SecondsToTimeFormat(seconds));
            }

            //  TO DO:
            //  If the timer reach 0, trigger a congratulation fragment
            if (seconds == 0) {
                //  FIX:
                //  the process can run in the background, thus we can't change the view
                //  -> change the button status when re-create activity
                isRunning = false;
//                Button b = getActivity().findViewById(R.id.button_trigger_timer);
//                b.setText("Start");

                //  Stop the timer
                timerHandler.removeCallbacks(timerRunnable);
                timerView.setText(SecondsToTimeFormat(initSecs));
                setStudyTimerBarStatus(true, initSecs - 10 * ONE_MINUTE);
                //  Post this result to the database as a successful attempt
                //  pmDate = current Date
                //  pmTime = initSecs;
                //  pmIsSuccessul = true
                sViewModel.insert(new PomoRecord(true));
                return;
            }

            //  Recurse the TimeRunnable object to keep the timer running
            timerHandler.postDelayed(this, 1000);
        }
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
     * @param bool: activate/deactivate the progress bar
     * @param progress: the current progress
     */
    private void setStudyTimerBarStatus (Boolean bool, float progress) {
        studyTimerBar.setEnabled(bool);
        studyTimerBar.setIndicatorEnabled(bool);
        studyTimerBar.setProgress(progress);
    }
}
