package com.hfad.timerservice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ResultActivity extends Activity {

    countPassFail countResults = new countPassFail();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Intent intent = getIntent();
        String resultText = intent.getStringExtra("result");
        TextView resultView = (TextView) findViewById(R.id.result);
        //resultView.setText(resultText);
        String results =  countResults.getResults();

        if(resultText.equals("fail")) {
            String message = (getString(R.string.failQuizz) + results);
            resultView.setText(message);
        }

        if(resultText.equals("pass")) {
            String message = (getString(R.string.passQuizz) + results);
            resultView.setText(message);
        }
    }

    public void onClickShare(View view) {

        String results =  countResults.getResults();
        String message = (getString(R.string.shareResults) + results);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);
        String chooserTitle = getString(R.string.chooser);
        Intent chosenIntent = Intent.createChooser(shareIntent,chooserTitle);
        startActivity(chosenIntent);

    }
}