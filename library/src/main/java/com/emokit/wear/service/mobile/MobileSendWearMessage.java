package com.emokit.wear.service.mobile;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.emokit.wear.constant.PublicConstant;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.common.api.ResultCallback;
import com.mobvoi.android.wearable.MessageApi;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

/**
 * 手机端发送到腕表的Message
 * Created by jiahongfei on 16/1/26.
 */
class MobileSendWearMessage extends AsyncTask<MobileSendWearMessage.SendWearCommand, Void, Void> {

    public enum SendWearCommand{
        START_HEART_RATE_COMMAND,
        STOP_HEART_RATE_COMMAND,
    }

    private Context mContext;
    private MobvoiApiClient mMobvoiApiClient;

    public MobileSendWearMessage(Context context, MobvoiApiClient mobvoiApiClient){
        this.mMobvoiApiClient = mobvoiApiClient;
        this.mContext = context;
    }

    private Collection<String> getNodes(MobvoiApiClient mobvoiApiClient) {

        HashSet<String> results = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mobvoiApiClient).await();

        for (Node node : nodes.getNodes()) {
//	            Log.e(TAG, node.getId());
            results.add(node.getId());
        }

        return results;
    }

    @Override
    protected Void doInBackground(SendWearCommand... args) {

        SendWearCommand command = args[0];

        Collection<String> nodes = getNodes(mMobvoiApiClient);
        if (nodes.size() <= 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Please connect your watch", Toast.LENGTH_SHORT).show();
                }
            });
        }
        for (String node : nodes) {

            switch (command){
                case START_HEART_RATE_COMMAND:{
                    startWearHeartRate(node);
                    break;
                }
                case STOP_HEART_RATE_COMMAND:{
                    sendStopHeartRateTest(node);
                    break;
                }
                default:
                    break;
            }
        }
        return null;
    }

    /**
     * 启动腕表心率测试
     *
     * @param nodeId
     */
    private void startWearHeartRate(String nodeId) {
        Wearable.MessageApi.sendMessage(mMobvoiApiClient, nodeId, PublicConstant.HEART_RATE_TEST_START_PATH, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {

            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (sendMessageResult.getStatus().isSuccess()) {
//					HandlerToastUI.getHandlerToastUI("success");
                } else {
//					HandlerToastUI.getHandlerToastUI("请查看腕表是否安装应用");
                }
            }
        });
    }

    /**
     * 停止心跳测试
     */
    private void sendStopHeartRateTest(String nodeId) {
        Wearable.MessageApi.sendMessage(mMobvoiApiClient, nodeId, PublicConstant.HEART_RATE_TEST_STOP_PATH, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {

            }
        });
    }

}
