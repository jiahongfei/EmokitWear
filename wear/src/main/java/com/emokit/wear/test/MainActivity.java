package com.emokit.wear.test;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.emokit.wear.constant.PublicConstant;
import com.emokit.wear.init.EmokitApiManager;
import com.emokit.wear.init.WearApiConfiguration;
import com.emokit.wear.receive.WearWearableReceive;
import com.emokit.wear.test.emo.WearEmokitService;

public class MainActivity extends Activity {

    private TextView emotionTextView;
    private TextView countdownTextView;
    private TextView heartRateTextView;

    private CountDownTimer countDownTimer = new CountDownTimer(EmokitApiManager.getInstance().getWearConfiguration().mHeartRateDuration, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
            countdownTextView.setText((millisUntilFinished/1000) + "");
        }

        @Override
        public void onFinish() {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emotionTextView = (TextView)findViewById(R.id.emotionTextView);
        countdownTextView = (TextView)findViewById(R.id.countdownTextView);
        heartRateTextView = (TextView)findViewById(R.id.heartRateTextView);


        findViewById(R.id.emotionStartButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, WearEmokitService.class);
                        intent.putExtra(PublicConstant.SERVICE_ACTION_START_HEART_RATE, "");
                        startService(intent);
                    }
                });

        findViewById(R.id.emotionEndButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, WearEmokitService.class);
                        intent.putExtra(PublicConstant.SERVICE_ACTION_STOP_HEART_RATE, "");
                        startService(intent);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mWearWearableReceive, new IntentFilter(WearWearableReceive.ACTION_WEAR_EMO));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mWearWearableReceive);

    }

    private WearWearableReceive mWearWearableReceive = new WearWearableReceive() {
        @Override
        public void onStartHeartRateListener() {
            Toast.makeText(mContext, "StartHeartRate", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelHeartRateListener() {
            Toast.makeText(mContext, "CancelHeartRate", Toast.LENGTH_SHORT).show();
            countDownTimer.cancel();
            heartRateTextView.setText("");
            countdownTextView.setText("");
        }

        @Override
        public void onArrayHeartRateListener(float[] heartRates) {
//            Toast.makeText(mContext, "ArrayHeartRate", Toast.LENGTH_SHORT).show();
            countDownTimer.cancel();
            heartRateTextView.setText("");
            countdownTextView.setText("");
        }

        @Override
        public void onHeartRateValueListener(int index, float value) {
//            Toast.makeText(mContext, "HeartRateValue : " + value + "   " + index, Toast.LENGTH_SHORT).show();
            if(0 == index){
                Toast.makeText(mContext, "HeartRateValue : " + value + "   " + index, Toast.LENGTH_SHORT).show();
                countDownTimer.start();
            }
            heartRateTextView.setText(value+"");
        }

        @Override
        public void onEmotionListener(String emoJson) {
            Toast.makeText(mContext, "Emotion : " + emoJson, Toast.LENGTH_SHORT).show();
            emotionTextView.setText(emoJson);
        }

        @Override
        public void onErrorListener(String errCode) {
            Toast.makeText(mContext, "errCode : " + errCode, Toast.LENGTH_SHORT).show();
            countDownTimer.cancel();
        }
    };

}
