package com.example.lab3_stopwatch;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //Properties
    private Handler timeHandler;
    private ArrayAdapter<String> itemsAdapter;
    private TextView txtTimer;
    private Button btnStartPause, btnLapReset;

    //Vars to keep track of time
    private long millisecondTime, startTime, pausedTime, updateTime = 0;

    //Vars to display time
    private int seconds, minutes, milliseconds;

    //Vars to handle Stopwatch state
    private boolean stopWatchStarted, stopWatchPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Not used somewhere else, thus no global
        ListView lvLaps;

        // To schedule Runnable to be executed after particular actions
        timeHandler = new Handler();

        // sets layout for each item of the list view
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        txtTimer = findViewById(R.id.txtTimer);
        btnStartPause = findViewById(R.id.btnStartPause);
        btnLapReset = findViewById(R.id.btnLapReset);
        lvLaps = findViewById(R.id.lvLaps);

        // binds data from adapter to the ListView
        lvLaps.setAdapter(itemsAdapter);

        // to handle stopwatch actions
        btnStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stopWatchStarted || stopWatchPaused){
                    stopWatchStarted = true;
                    stopWatchPaused = false;

                    startTime = SystemClock.uptimeMillis();

                    timeHandler.postDelayed(timerRunnable, 0);

                    btnStartPause.setText(R.string.lblPause);
                    btnLapReset.setText(R.string.btnLap);
                } else {
                    pausedTime += millisecondTime;
                    stopWatchPaused = true;

                    // remove pending posts of timerRunnable in message queue
                    timeHandler.removeCallbacks(timerRunnable);

                    // switch label strings
                    btnStartPause.setText(R.string.lblStart);
                    btnLapReset.setText(R.string.lblReset);
                }
            }
        });

        // to handle lap and resetting
        btnLapReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // to check if action is for new lap or reset
                if (stopWatchStarted && !stopWatchPaused){
                    String lapTime = minutes + ":"
                            + String.format("%02d", seconds) + ":"
                            + String.format("%03d", milliseconds);

                    itemsAdapter.add(lapTime);
                } else if(stopWatchPaused){
                    stopWatchStarted = false;
                    stopWatchPaused = false;

                    // remove pendind posts
                    timeHandler.removeCallbacks(timerRunnable);

                    // reset all
                    milliseconds = 0;
                    startTime = 0;
                    pausedTime = 0;
                    updateTime = 0;
                    seconds = 0;
                    minutes = 0;
                    milliseconds = 0;

                    // switch label strings
                    txtTimer.setText(R.string.lblTimer);
                    btnLapReset.setText(R.string.btnLap);

                    // wipe resources
                    itemsAdapter.clear();
                } else {
                    Toast.makeText(getApplicationContext(), "Timer hasn't started yet!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // executed by handler associated with a thread
    public Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            millisecondTime = SystemClock.uptimeMillis() - startTime;

            // values used to track of stopwatch time
            updateTime = pausedTime + millisecondTime;
            milliseconds = (int) (updateTime % 1000);
            seconds = (int) (updateTime / 1000);

            // convert value to display
            minutes = seconds / 60;
            seconds = minutes % 60;
            String updatedTime = minutes + ":"
                    + String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliseconds);

            txtTimer.setText(updatedTime);

            timeHandler.postDelayed(this, 0);
        }
    };
}
