package com.hfad.timerservice;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MultipleChoiceActivity extends Activity {
    String result;
    int attempts = 0;
    private int timeLimit = 0;
    public static boolean running;
    public static boolean runningContinuous;
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
        setContentView(R.layout.activity_multiple_choice);
        runTimer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, TimeService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
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

        // Reference to the spinner
        Spinner selectbooks = (Spinner) findViewById(R.id.selectbooks);
        Spinner henryBooks = (Spinner) findViewById(R.id.secondSpinner);


        // Get the selection from spinner
        String selection = String.valueOf(selectbooks.getSelectedItem());
        String answer = getString(R.string.sl5);
        String [] books = getResources().getStringArray(R.array.books);

        String secondSelection = String.valueOf(henryBooks.getSelectedItem());
        String secondAnswer = getString(R.string.tropicCancer);
        String [] moreBooks = getResources().getStringArray(R.array.moreBooks);


        if (selection.equals(answer) && secondSelection.equals(secondAnswer)) {
            result = "pass";
            handler.removeCallbacksAndMessages(null);
            time.resetTime();
            running = true;
            runningContinuous = true;
            Intent nextQuestion = new Intent(this, FillTheBlank.class);
            startActivity(nextQuestion);
        }
        if(!selection.equals(answer) || !secondSelection.equals(secondAnswer)) {
            attempts++;
            CharSequence text =  getString(R.string.toastMessage);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this,text,duration);
            if(attempts < 3) {
                toast.show();
            }
        }

        if (attempts == (books.length) - 2) {
            result = "fail";
            counter.count(result);
            Intent failedQuiz = new Intent(this, ResultActivity.class);
            failedQuiz.putExtra("result", result);
            startActivity(failedQuiz);
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
