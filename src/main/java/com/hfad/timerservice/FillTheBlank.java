package com.hfad.timerservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.app.Activity;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class FillTheBlank extends Activity {
    String result;
    int attempts = 0;
    private int timeLimit = 0;
    public static boolean running;
    public static boolean runningContinuous;
    private boolean wasRunning;
    private boolean endTime = true;
    private String viewTime ="";
    private String viewTimeContinuous = "";
    final Handler handler = new Handler();
    private TimeService time;
    private boolean bound = false;

    countPassFail counter = new countPassFail();


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            TimeService.TimeBinder timeBinder = (TimeService.TimeBinder) binder;
            time = timeBinder.getTimeService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_the_blank);
        runTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, TimeService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
        running = true;
        runningContinuous = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(bound) {
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
    public void onClickSubmit(View view) {

        EditText answer = (EditText) findViewById(R.id.textAnswer);
        String answerText = answer.getText().toString();
        Intent intent = new Intent (this, ResultActivity.class);
        String playerpiano = getString(R.string.playerPiano);

        if(answerText.toLowerCase().equals(playerpiano.toLowerCase())) {
            result = "pass";
            handler.removeCallbacksAndMessages(null);
            counter.count(result);
            intent.putExtra("result", result);
            startActivity(intent);
        }

        if(!answerText.toLowerCase().equals(playerpiano.toLowerCase())) {
            attempts++;
            CharSequence text =  getString(R.string.toastMessage);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this,text,duration);
            if(attempts < 3) {
                toast.show();
            }
        }

        if (attempts == 2) {
            result = "fail";
            counter.count(result);
            intent.putExtra("result", result);
            startActivity(intent);
            attempts = 0;
        }
    }

    private void runTimer() {
        final TextView timeView = (TextView) findViewById(R.id.time_start_stop);
        final TextView timeContinuous = (TextView) findViewById(R.id.time_continuous);

        handler.post(new Runnable() {
            public void run() {
                if(running && time != null) {
                    viewTime = time.getTime();
                    timeView.setText(viewTime);
                }

                if(runningContinuous && time != null) {
                    viewTimeContinuous = time.getTimeContinuous();
                    timeContinuous.setText(viewTimeContinuous);
                }

                if(!endTime) {
                    time.endTimer(timeLimit);
                }

                handler.postDelayed(this,1000);
            }
        });
    }

    public void onToggleButtonClicked(View view) {
        TextView timeStart = (TextView) findViewById(R.id.time_start_stop);
        TextView timeContinue = (TextView) findViewById(R.id.time_continuous);

        boolean on = ((ToggleButton) view).isChecked();
        if(on) {
            timeStart.setVisibility(View.GONE);
            timeContinue.setVisibility(View.GONE);
            time.resetTime();
            running = false;
            runningContinuous = false;
        } else {
            timeStart.setVisibility(View.VISIBLE);
            timeContinue.setVisibility(View.VISIBLE);
            running = true;
            runningContinuous = true;
        }
    }


    public void onRadioButtonClicked(View view) {
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        int id = radioGroup.getCheckedRadioButtonId();
        switch(id) {
            case R.id.tenseconds:
                timeLimit = 10;
                endTime = false;
                break;
            case R.id.twentyseconds:
                timeLimit = 20;
                endTime = false;
                break;
            case R.id.thirtyseconds:
                timeLimit = 30;
                endTime = false;
                break;
            case R.id.sixtyseconds:
                timeLimit = 60;
                endTime = false;
                break;
        }
    }


    public void onSwitchClick(View view) {
        Button button = (Button) findViewById(R.id.send);
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageSend);
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggle_button);
        ToggleButton toggleWatch = (ToggleButton) findViewById(R.id.toggle_watch);
        boolean on = ((Switch) view).isChecked();

        if(on) {
            button.setVisibility(View.GONE);
            imageButton.setVisibility(View.VISIBLE);
            toggleButton.setVisibility(View.GONE);
            toggleWatch.setVisibility(View.VISIBLE);
        }
        else {
            button.setVisibility(View.VISIBLE);
            imageButton.setVisibility(View.GONE);
            toggleButton.setVisibility(View.VISIBLE);
            toggleWatch.setVisibility(View.GONE);
        }
    }
}