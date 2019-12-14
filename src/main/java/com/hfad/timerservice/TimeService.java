package com.hfad.timerservice;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import java.util.Locale;


public class TimeService extends Service {


    private static int seconds = 0;
    private static int secondsContinuous = 0;
    countPassFail counter = new countPassFail();

    private String result;
    public static final int NOTIFICATION_ID = 5555;

    private  final IBinder binder = new TimeBinder();

    public class TimeBinder extends Binder {
        TimeService getTimeService() {
            return TimeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void onCreate() {
        super.onCreate();
        MultipleChoiceActivity.running = true;
        MultipleChoiceActivity.runningContinuous = true;
    }

    public void onDestroy() {
        super.onDestroy();
        MultipleChoiceActivity.running = false;
    }


    public String getTime() {
        int minutes = (seconds%3600)/60;
        int secs = seconds%60;
        String time = String.format(Locale.getDefault(),"%02d:%02d",minutes,secs);
        if(MultipleChoiceActivity.running) {
            seconds++;
            return time;
        }
        return time;
    }

    public String getTimeContinuous() {
        int minutes = (secondsContinuous%3600)/60;
        int secs = secondsContinuous%60;
        String time = String.format(Locale.getDefault(),"%02d:%02d",minutes,secs);
        if(MultipleChoiceActivity.runningContinuous) {
            secondsContinuous++;
            return time;
        }
        return time;
    }

    public void endTimer(int timeLimit) {

        if(timeLimit == seconds && timeLimit == secondsContinuous) {
            CharSequence text =  getString(R.string.toastTime);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this,text,duration);
            toast.show();
            result = "fail";
            counter.count(result);
            Intent failedQuiz = new Intent(this, ResultActivity.class);
            failedQuiz.putExtra("result", result);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setContentTitle(getString(R.string.endTime))
                    .setContentText(getString(R.string.endResults))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[] {0,1000})
                    .setAutoCancel(true);

            startActivity(failedQuiz);
            PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 0, failedQuiz, PendingIntent.FLAG_CANCEL_CURRENT);


            builder.setContentIntent(actionPendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }


        if(timeLimit == secondsContinuous && timeLimit != seconds) {
            CharSequence text =  getString(R.string.toastTime);
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this,text,duration);
            toast.show();
            result = "fail";
            counter.count(result);
            Intent failedQuiz = new Intent(this, ResultActivity.class);
            failedQuiz.putExtra("result", result);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                    .setContentTitle(getString(R.string.endTime))
                    .setContentText(getString(R.string.endResults))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[] {0,1000})
                    .setAutoCancel(true);


            PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 0, failedQuiz, PendingIntent.FLAG_CANCEL_CURRENT);


            builder.setContentIntent(actionPendingIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    public void resetTime() {
        seconds = 0;
        secondsContinuous = 0;
    }


}
