package com.emokit.wear.service.wear;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.emokit.wear.constant.PublicConstant;
import com.emokit.wear.init.EmokitApiManager;
import com.emokit.wear.receive.WearWearableReceive;
import com.emokit.wear.service.BaseWearableListenerService;
import com.emokit.wear.service.NodeApiAsyncTask;
import com.mobvoi.android.common.ConnectionResult;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.common.data.FreezableUtils;
import com.mobvoi.android.wearable.DataApi;
import com.mobvoi.android.wearable.DataEvent;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.DataMap;
import com.mobvoi.android.wearable.DataMapItem;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.PutDataMapRequest;
import com.mobvoi.android.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 腕表数据层接收数据Service
 *
 * @author jiahongfei
 */
public abstract class WearDataTransferService extends BaseWearableListenerService {

    /**
     * 等待接收收集数据的超时时间，超过这个时间可以认为超时
     */
    public static final long RECEIVE_MOBILE_DATA_TIMEOUT = 1000 * 60;
    private static final int HANDLER_MSG_START = 0;
    private static final int HANDLER_MSG_STOP = 1;
    private static final int HANDLER_SEND_HEART_RATE_DATA = 3;
    private static final int HANDLER_SEND_ERROR = 4;

    private Handler mHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_MSG_START: {
                    startHeartRateService();
                    break;
                }
                case HANDLER_MSG_STOP: {
                    stopHeartRateService();
                    break;
                }
                case 2: {
                    //长时间没有收到手机的返回数据提示错误
                    break;
                }
                case HANDLER_SEND_HEART_RATE_DATA: {
                    Float[] floats = (Float[]) msg.obj;
                    sendHeartRateDataMobile(floats);
                    break;
                }
                case HANDLER_SEND_ERROR: {
                    String error = (String) msg.obj;
                    sendWearErrorMessage(error);
                    break;
                }
                default:
                    break;
            }
            return false;
        }
    }
    );

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {

            if (!EmokitApiManager.getInstance().isWearApiConfig()) {
                Toast.makeText(mContext, "Call EmokitApiManager initialization", Toast.LENGTH_SHORT).show();
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
    public void onPeerConnected(Node node) {
    }

    @Override
    public void onPeerDisconnected(Node node) {
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        final List<DataEvent> events = FreezableUtils
                .freezeIterable(dataEvents);
        dataEvents.close();
        if (!mSuperMobvoiApiClient.isConnected()) {
            ConnectionResult connectionResult = mSuperMobvoiApiClient
                    .blockingConnect(30, TimeUnit.SECONDS);
            if (!connectionResult.isSuccess()) {
                return;
            }
        }
        // 接收数据
        for (DataEvent event : events) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                if (PublicConstant.HEART_RATE_EMO_RESULT_PATH.equals(path)) {

                    getEmotionResult(dataMapItem);

                } else if (PublicConstant.ERROR_PATH.equals(path)) {

                    getMobileError(dataMapItem);

                } else {
                }

            } else if (event.getType() == DataEvent.TYPE_DELETED) {
            } else {
            }
        }
    }

    @Override
    public void onMessageReceived(final MessageEvent messageEvent) {

        String pathString = messageEvent.getPath();

        if (PublicConstant.HEART_RATE_TEST_START_PATH.equals(pathString)) {

            startHeartRateService();

        } else if (PublicConstant.HEART_RATE_TEST_STOP_PATH.equals(pathString)) {
            stopHeartRateService();
        } else {
        }

    }

    private IHeartRateDataListener mInternalHearRateDataListener = new IHeartRateDataListener() {


        @Override
        public void onStartHeartRateDataListener() {
        }

        @Override
        public void onHeartRateValueListener(int index, Float value) {

            Bundle bundle = new Bundle();
            bundle.putInt(WearWearableReceive.KEY_WEAR_INDEX_HEART_RATE, index);
            bundle.putFloat(WearWearableReceive.KEY_WEAR_VALUE_HEART_RATE, value);
            broadcastCallback(WearWearableReceive.ACTION_WEAR_EMO, bundle);

        }

        @Override
        public void onArrayHeartRateListener(Float[] heartRateFloats) {
            // 发送心跳数据
//            sendHeartRateDataMobile(heartRateFloats);
            mHandler.sendMessage(mHandler.obtainMessage(HANDLER_SEND_HEART_RATE_DATA, heartRateFloats));
        }

        @Override
        public void onHeartRateFailsListener() {
//            sendWearErrorMessage(PublicConstant.ERR_HEART_RATE);
            mHandler.sendMessage(mHandler.obtainMessage(HANDLER_SEND_ERROR, PublicConstant.ERR_HEART_RATE));

        }
    };

    /**
     * ;
     * 获取手机端错误信息
     *
     * @param dataMapItem
     */
    private void getMobileError(DataMapItem dataMapItem) {
        String errCode = dataMapItem.getDataMap().getString(PublicConstant.ERROR_KEY);
        Bundle bundle = new Bundle();
        bundle.putString(WearWearableReceive.KEY_WEAR_ERROR_CODE, errCode);
        broadcastCallback(WearWearableReceive.ACTION_WEAR_EMO, bundle);
    }

    /**
     * 开启测试心率Service
     */
    public void startHeartRateService() {

        new NodeApiAsyncTask(mSuperMobvoiApiClient, new NodeApiAsyncTask.INodeConnectListener() {
            @Override
            public void successListener() {
                new HeartRateDataService
                        .Builder((Application) mContext.getApplicationContext())
                        .setHeartRateDataListener(mInternalHearRateDataListener)
                        .setHeartRateDuration(EmokitApiManager.getInstance().getWearConfiguration().mHeartRateDuration)
                        .setHeartRateTimeOut(EmokitApiManager.getInstance().getWearConfiguration().mHeartRateTimeOut)
                        .create();
                HeartRateDataService.getInstance(mContext).startHearRate();

                new WearSendMobileMessage(mContext, mSuperMobvoiApiClient)
                        .execute(WearSendMobileMessage.SendMobileCommand.START_HEART_RATE_COMMAND);

                Bundle bundle = new Bundle();
                bundle.putString(WearWearableReceive.KEY_WEAR_START_HEART_RATE, "");
                broadcastCallback(WearWearableReceive.ACTION_WEAR_EMO, bundle);
            }

            @Override
            public void failsListener() {
                Bundle bundle = new Bundle();
                bundle.putString(WearWearableReceive.KEY_WEAR_ERROR_CODE, PublicConstant.ERR_CONNECT_START_HEART_RATE);
                broadcastCallback(WearWearableReceive.ACTION_WEAR_EMO, bundle);
            }
        }).execute();


    }

    /**
     * 停止测试心率
     */
    private void stopHeartRateService() {
        //腕表端没连接到手机也可以取消获取心率
        HeartRateDataService.getInstance(mContext).stopHearRate();

        new WearSendMobileMessage(mContext, mSuperMobvoiApiClient)
                .execute(WearSendMobileMessage.SendMobileCommand.STOP_HEART_RATE_COMMAND);

        Bundle bundle = new Bundle();
        bundle.putString(WearWearableReceive.KEY_WEAR_CANCEL_HEART_RATE, "");
        broadcastCallback(WearWearableReceive.ACTION_WEAR_EMO, bundle);
    }

    /**
     * 获取情绪结果
     *
     * @param dataMapItem
     */
    private void getEmotionResult(DataMapItem dataMapItem) {
        String emoResult = dataMapItem.getDataMap().getString(PublicConstant.HEART_RATE_EMO_RESULT_KEY);
        Bundle bundle = new Bundle();
        bundle.putString(WearWearableReceive.KEY_WEAR_EMOTION, emoResult);
        broadcastCallback(WearWearableReceive.ACTION_WEAR_EMO, bundle);
        mHandler.removeMessages(2);
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
                PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PublicConstant.ERROR_PATH);
                putDataMapRequest.getDataMap().putString(PublicConstant.ERROR_KEY, error);
                Wearable.DataApi.putDataItem(mSuperMobvoiApiClient, putDataMapRequest.asPutDataRequest());

                Bundle bundle = new Bundle();
                bundle.putString(WearWearableReceive.KEY_WEAR_ERROR_CODE, error);
                broadcastCallback(WearWearableReceive.ACTION_WEAR_EMO, bundle);
            }

            @Override
            public void failsListener() {
                Toast.makeText(mContext, "Please connect your mobile", Toast.LENGTH_SHORT).show();

            }
        }).execute();


    }

    /**
     * 发送心率数据到手机
     *
     * @param values
     */
    private void sendHeartRateDataMobile(final Float[] values) {

        new NodeApiAsyncTask(mSuperMobvoiApiClient, new NodeApiAsyncTask.INodeConnectListener() {
            @Override
            public void successListener() {

                final float[] heartRateArray = new float[values.length];
                for (int i = 0; i < heartRateArray.length; i++) {
                    heartRateArray[i] = values[i];
                }
                PutDataMapRequest putDataMapRequest = PutDataMapRequest
                        .create(PublicConstant.HEART_RATE_DATA_PATH);
                DataMap dataMap = putDataMapRequest.getDataMap();
                dataMap.putFloatArray(PublicConstant.HEART_RATE_ARRAY_KEY,
                        heartRateArray);

                Wearable.DataApi.putDataItem(mSuperMobvoiApiClient,
                        putDataMapRequest.asPutDataRequest())
                        .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                            @Override
                            public void onResult(DataApi.DataItemResult dataItemResult) {
                                if (dataItemResult.getStatus().isSuccess()) {
                                    Bundle bundle = new Bundle();
                                    bundle.putFloatArray(WearWearableReceive.KEY_WEAR_ARRAY_HEART_RATE, heartRateArray);
                                    broadcastCallback(WearWearableReceive.ACTION_WEAR_EMO, bundle);
                                    //发送成功开启超时时间
                                    mHandler.sendEmptyMessageDelayed(2, RECEIVE_MOBILE_DATA_TIMEOUT);
                                }
                            }
                        });
            }

            @Override
            public void failsListener() {
                Bundle bundle = new Bundle();
                bundle.putString(WearWearableReceive.KEY_WEAR_ERROR_CODE, PublicConstant.ERR_CONNECT_SEND_HEART_RATE_ARRAY);
                broadcastCallback(WearWearableReceive.ACTION_WEAR_EMO, bundle);
            }
        }).execute();

    }

}
