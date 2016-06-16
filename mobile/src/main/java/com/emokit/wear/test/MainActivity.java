package com.emokit.wear.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.emokit.wear.constant.PublicConstant;
import com.emokit.wear.emodata.entitys.EmoResult;
import com.emokit.wear.emodata.utils.EmotionsUtils;
import com.emokit.wear.init.EmokitApiManager;
import com.emokit.wear.init.MobileApiConfiguration;
import com.emokit.wear.receive.MobileWearableReceive;
import com.emokit.wear.test.emo.MobileEmokitService;

import java.util.List;

public class MainActivity extends Activity {

    private Context mContext;
    private TextView emotionTextView;
    private TextView emoTextView;
    private ImageView emoImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        emotionTextView = (TextView)findViewById(R.id.emotionTextView);
        emoTextView = (TextView)findViewById(R.id.emoTextView);
        emoImageView = (ImageView)findViewById(R.id.emoImageView);

        findViewById(R.id.emotionStartButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, MobileEmokitService.class);
                        intent.putExtra(PublicConstant.SERVICE_ACTION_START_HEART_RATE, "");
                        startService(intent);
                    }
                });

        findViewById(R.id.emotionEndButton)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext,MobileEmokitService.class);
                        intent.putExtra(PublicConstant.SERVICE_ACTION_STOP_HEART_RATE, "");
                        startService(intent);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mMobileWearableReceive, new IntentFilter(MobileWearableReceive.ACTION_MOBILE_EMO));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mMobileWearableReceive);
    }

    private void parseEmoResult(String emoJson){
        Log.e("emoJson", "emoJson : " + emoJson);
        emotionTextView.setText(emoJson);
        //解析返回结果
        EmoResult emoResult = null;
        try {
            emoResult = JSON.parseObject(emoJson, EmoResult.class);
        }catch (Exception e){
            e.printStackTrace();
            emoResult = null;
        }
        if(null == emoResult){
            return ;
        }
        if(EmokitApiManager.getInstance().getMobileConfiguration().mRcType
                .equals(MobileApiConfiguration.RC_TYPE_7)){
            List<String> emos = EmotionsUtils.getEmotions7ByEmoKey(mContext, emoResult.getRc_main());
            emoTextView.setText(emos.toString());
            int i = getResources().getIdentifier(emoResult.getRc_main().toLowerCase(), "drawable", getPackageName());
            emoImageView.setImageResource(i);
        }
    }

    private MobileWearableReceive mMobileWearableReceive = new MobileWearableReceive() {
        @Override
        public void onStartHeartRateListener() {
            Toast.makeText(mContext, "StartHeartRate", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelHeartRateListener() {
            Toast.makeText(mContext, "CancelHeartRate", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onArrayHeartRateListener(float[] heartRates) {
            Toast.makeText(mContext, "ArrayHeartRate", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onEmotionListener(String emoJson) {
            Toast.makeText(mContext, "Emotion : " + emoJson, Toast.LENGTH_SHORT).show();
            parseEmoResult(emoJson);
        }

        @Override
        public void onErrorListener(String errCode) {
            Toast.makeText(mContext, "errCode : " + errCode, Toast.LENGTH_SHORT).show();

        }
    };
}
