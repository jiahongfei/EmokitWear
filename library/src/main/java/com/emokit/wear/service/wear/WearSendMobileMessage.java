package com.emokit.wear.service.wear;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
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
 * 发送到手机端的Message
 * Created by jiahongfei on 16/1/26.
 */
class WearSendMobileMessage extends AsyncTask<WearSendMobileMessage.SendMobileCommand, Void, Void> {

    public enum SendMobileCommand {
        START_HEART_RATE_COMMAND,
        STOP_HEART_RATE_COMMAND,
    }

    private Context mContext;
    private MobvoiApiClient mMobvoiApiClient;

    public WearSendMobileMessage(Context context, MobvoiApiClient mobvoiApiClient) {
        mContext = context;
        this.mMobvoiApiClient = mobvoiApiClient;
    }

    private Collection<String> getNodes() {

        HashSet<String> results = new HashSet<>();
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mMobvoiApiClient).await();

        for (Node node : nodes.getNodes()) {
//	            Log.e(TAG, node.getId());
            results.add(node.getId());
        }

        return results;
    }

    @Override
    protected Void doInBackground(SendMobileCommand... args) {
        SendMobileCommand command = args[0];

        Collection<String> nodes = getNodes();
        if (nodes.size() <= 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "Please connect your mobile", Toast.LENGTH_SHORT).show();

                }
            });
        }
        for (String node : nodes) {

            switch (command) {
                case STOP_HEART_RATE_COMMAND: {
                    //停止测试心率
                    sendStopHeartRateTestMessage(node);
                    break;
                }
                case START_HEART_RATE_COMMAND: {
                    //开始测试心率
                    sendStartHeartRateTestMessage(node);
                    break;
                }
                default:
                    break;
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }

    private void sendStopHeartRateTestMessage(String nodeId) {
        Wearable.MessageApi.sendMessage(mMobvoiApiClient, nodeId, PublicConstant.HEART_RATE_TEST_STOP_PATH, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (sendMessageResult.getStatus().isSuccess()) {
                }
            }
        });
    }

    /**
     * @param nodeId
     */
    private void sendStartHeartRateTestMessage(String nodeId) {
        Wearable.MessageApi.sendMessage(mMobvoiApiClient, nodeId, PublicConstant.HEART_RATE_TEST_START_PATH, new byte[0]).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                if (sendMessageResult.getStatus().isSuccess()) {
                } else {
                }
            }
        });
    }


}
