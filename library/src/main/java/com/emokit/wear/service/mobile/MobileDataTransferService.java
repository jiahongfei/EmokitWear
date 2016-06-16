package com.emokit.wear.service.mobile;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.emokit.wear.constant.PublicConstant;
import com.emokit.sdk.netaccess.NetTransfer;
import com.emokit.sdk.util.SDKAppInit;
import com.emokit.wear.init.EmokitApiManager;
import com.emokit.wear.receive.MobileWearableReceive;
import com.emokit.wear.service.BaseWearableListenerService;
import com.emokit.wear.service.NodeApiAsyncTask;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.DataMapItem;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.PutDataMapRequest;
import com.mobvoi.android.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 手机端数据传输Service
 *
 * @author jiahongfei
 */
public abstract class MobileDataTransferService extends BaseWearableListenerService {

    private static final int HANDLER_MSG_START = 0;
    private static final int HANDLER_MSG_STOP = 1;
    private static final int HANDLER_SEND_EMO_RESULT = 3;
    private static final int HANDLER_SEND_ERROR = 4;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (null != intent) {
            if (TextUtils.isEmpty(EmokitApiManager.getInstance().getMobileConfiguration().mPlatflag)) {
                Toast.makeText(mContext, "Call EmokitApiManager initialization setting platflag", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(EmokitApiManager.getInstance().getMobileConfiguration().mUserName)) {
                Toast.makeText(mContext, "Call EmokitApiManager initialization setting username", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(EmokitApiManager.getInstance().getMobileConfiguration().mPassword)) {
                Toast.makeText(mContext, "Call EmokitApiManager initialization setting password", Toast.LENGTH_SHORT).show();
            } else {
                if (intent.hasExtra(PublicConstant.SERVICE_ACTION_START_HEART_RATE)) {
                    mHandler.sendEmptyMessageDelayed(HANDLER_MSG_START, 500);
                } else if (intent.hasExtra(PublicConstant.SERVICE_ACTION_STOP_HEART_RATE)) {
                    mHandler.sendEmptyMessageDelayed(HANDLER_MSG_STOP, 500);

                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onPeerConnected(Node arg0) {
    }

    @Override
    public void onPeerDisconnected(Node arg0) {
    }

    // 接收指令类实时性数据
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String pathString = messageEvent.getPath();

        if (PublicConstant.HEART_RATE_TEST_STOP_PATH.equals(pathString)) {

            Bundle bundle = new Bundle();
            bundle.putString(MobileWearableReceive.KEY_MOBILE_CANCEL_HEART_RATE, "");
            broadcastCallback(MobileWearableReceive.ACTION_MOBILE_EMO, bundle);

        } else if (PublicConstant.HEART_RATE_TEST_START_PATH.equals(pathString)) {

            Bundle bundle = new Bundle();
            bundle.putString(MobileWearableReceive.KEY_MOBILE_START_HEART_RATE, "");
            broadcastCallback(MobileWearableReceive.ACTION_MOBILE_EMO, bundle);

        }
    }

    @Override
    public void onDataChanged(DataEventBuffer eventBuffer) {
        final List<DataEvent> events = FreezableUtils.freezeIterable(eventBuffer);
        eventBuffer.close();
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event
                        .getDataItem());
                if (PublicConstant.HEART_RATE_DATA_PATH.equals(path)) {

                    getArrayHeartRate(dataMapItem);

                } else if (PublicConstant.ERROR_PATH.equals(path)) {

                    getMobileError(dataMapItem);

                }
            }
        }
    }

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MSG_START: {
                    startEmotionService();
                    break;
                }
                case HANDLER_MSG_STOP: {
                    stopEmotionService();
                    break;
                }
                case HANDLER_SEND_EMO_RESULT:{
                    String emoResult = (String) msg.obj;
                    sendHeartRateEmoResult(emoResult);
                    break;
                }
                case HANDLER_SEND_ERROR:{
                    String error = (String) msg.obj;
                    sendWearErrorMessage(error);
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 根据心率返回Emokit结果
     *
     * @author jiahongfei
     */
    private class HeartRateEmoResult extends AsyncTask<Float, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Float... heartRateArray) {

            double[] sendbpm = new double[heartRateArray.length];
            for (int i = 0; i < sendbpm.length; i++) {
                sendbpm[i] = heartRateArray[i];
            }
            SDKAppInit.registerforuid(
                    EmokitApiManager.getInstance().getMobileConfiguration().mPlatflag,
                    EmokitApiManager.getInstance().getMobileConfiguration().mUserName,
                    EmokitApiManager.getInstance().getMobileConfiguration().mPassword);
            NetTransfer net = new NetTransfer();
            String result = net.getRate(sendbpm, EmokitApiManager.getInstance().getMobileConfiguration().mRcType);

            try {
                //为了容错
                JSONObject jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                e.printStackTrace();
                result = null;
            }

            return result;
        }

        @Override
        protected void onPostExecute(String emoResult) {
            super.onPostExecute(emoResult);

            if (null == emoResult) {
//                sendWearErrorMessage(PublicConstant.ERR_EMOTION_RESULT);
                mHandler.sendMessage(mHandler.obtainMessage(HANDLER_SEND_ERROR, PublicConstant.ERR_EMOTION_RESULT));

                return;
            }

            //发送到手表端
//            sendHeartRateEmoResult(emoResult);
            mHandler.sendMessage(mHandler.obtainMessage(HANDLER_SEND_EMO_RESULT, emoResult));

        }
    }

    /**
     * 开始获取情绪
     */
    private void startEmotionService() {
        new NodeApiAsyncTask(mSuperMobvoiApiClient, new NodeApiAsyncTask.INodeConnectListener() {
            @Override
            public void successListener() {
                new MobileSendWearMessage(mContext, mSuperMobvoiApiClient)
                        .execute(MobileSendWearMessage.SendWearCommand.START_HEART_RATE_COMMAND);
            }

            @Override
            public void failsListener() {
                Bundle bundle = new Bundle();
                bundle.putString(MobileWearableReceive.KEY_MOBILE_ERROR_CODE, PublicConstant.ERR_CONNECT_START_HEART_RATE);
                broadcastCallback(MobileWearableReceive.ACTION_MOBILE_EMO, bundle);
            }
        }).execute();


    }

    /**
     * 停止获取情绪
     */
    private void stopEmotionService() {
        new NodeApiAsyncTask(mSuperMobvoiApiClient, new NodeApiAsyncTask.INodeConnectListener() {
            @Override
            public void successListener() {
                new MobileSendWearMessage(mContext, mSuperMobvoiApiClient)
                        .execute(MobileSendWearMessage.SendWearCommand.STOP_HEART_RATE_COMMAND);
            }

            @Override
            public void failsListener() {
                Bundle bundle = new Bundle();
                bundle.putString(MobileWearableReceive.KEY_MOBILE_ERROR_CODE, PublicConstant.ERR_CONNECT_STOP_HEART_RATE);
                broadcastCallback(MobileWearableReceive.ACTION_MOBILE_EMO, bundle);
            }
        });

    }

    /**
     * 从腕表端获取心率数组
     *
     * @param dataMapItem
     */
    private void getArrayHeartRate(DataMapItem dataMapItem) {
        final float[] heartRateArray = dataMapItem.getDataMap()
                .getFloatArray(PublicConstant.HEART_RATE_ARRAY_KEY);

        Float[] heartRateFloats = new Float[heartRateArray.length];
        for (int i = 0; i < heartRateArray.length; i++) {
            heartRateFloats[i] = heartRateArray[i];
        }
        Bundle bundle = new Bundle();
        bundle.putFloatArray(MobileWearableReceive.KEY_MOBILE_ARRAY_HEART_RATE, heartRateArray);
        broadcastCallback(MobileWearableReceive.ACTION_MOBILE_EMO, bundle);

        new HeartRateEmoResult().execute(heartRateFloats);
    }

    /**
     * 获取手机端错误信息
     *
     * @param dataMapItem
     */
    private void getMobileError(DataMapItem dataMapItem) {
        String errCode = dataMapItem.getDataMap().getString(PublicConstant.ERROR_KEY);
        Bundle bundle = new Bundle();
        bundle.putString(MobileWearableReceive.KEY_MOBILE_ERROR_CODE, errCode);
        broadcastCallback(MobileWearableReceive.ACTION_MOBILE_EMO, bundle);
    }

    /**
     * 发送错误信息
     *
     * @param error
     */
    private void sendWearErrorMessage(final String error) {

        new NodeApiAsyncTask(mSuperMobvoiApiClient, new NodeApiAsyncTask.INodeConnectListener() {
            @Override
            public void successListener() {
                //链接腕表成功
                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PublicConstant.ERROR_PATH);
                putDataMapRequest.getDataMap().putString(PublicConstant.ERROR_KEY, error);
                Wearable.DataApi.putDataItem(mSuperMobvoiApiClient, putDataMapRequest.asPutDataRequest());

                Bundle bundle = new Bundle();
                bundle.putString(MobileWearableReceive.KEY_MOBILE_ERROR_CODE, error);
                broadcastCallback(MobileWearableReceive.ACTION_MOBILE_EMO, bundle);
            }

            @Override
            public void failsListener() {
                //没有链接腕表
                Toast.makeText(mContext, "Please connect your watch", Toast.LENGTH_SHORT).show();
            }
        }).execute();


    }

    /**
     * 发送情绪结果
     *
     * @param emoResult
     */
    private void sendHeartRateEmoResult(final String emoResult) {

        new NodeApiAsyncTask(mSuperMobvoiApiClient, new NodeApiAsyncTask.INodeConnectListener() {
            @Override
            public void successListener() {
                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PublicConstant.HEART_RATE_EMO_RESULT_PATH);
                putDataMapRequest.getDataMap().putString(PublicConstant.HEART_RATE_EMO_RESULT_KEY, emoResult);
                Wearable.DataApi.putDataItem(mSuperMobvoiApiClient, putDataMapRequest.asPutDataRequest());

                Bundle bundle = new Bundle();
                bundle.putString(MobileWearableReceive.KEY_MOBILE_EMOTION, emoResult);
                broadcastCallback(MobileWearableReceive.ACTION_MOBILE_EMO, bundle);
            }

            @Override
            public void failsListener() {
                //没有链接腕表
                Bundle bundle = new Bundle();
                bundle.putString(MobileWearableReceive.KEY_MOBILE_ERROR_CODE, PublicConstant.ERR_CONNECT_SEND_EMO_RESULT);
                broadcastCallback(MobileWearableReceive.ACTION_MOBILE_EMO, bundle);
            }
        }).execute();


    }

}
