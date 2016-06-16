package com.emokit.wear.service;

import android.os.AsyncTask;

import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.wearable.Node;
import com.mobvoi.android.wearable.NodeApi;
import com.mobvoi.android.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;

/**
 * 判断设备之间是否连接成功
 * Created by jiahongfei on 16/2/2.
 */
public class NodeApiAsyncTask extends AsyncTask<Void, Void, Boolean> {

    public interface INodeConnectListener {
        public void successListener();

        public void failsListener();
    }

    private MobvoiApiClient mobvoiApiClient;
    private INodeConnectListener iNodeConnectListener;

    public NodeApiAsyncTask(MobvoiApiClient mobvoiApiClient, INodeConnectListener iNodeConnectListener) {
        this.mobvoiApiClient = mobvoiApiClient;
        this.iNodeConnectListener = iNodeConnectListener;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        Boolean result = false;

        if (isConnectedNodes(this.mobvoiApiClient)) {
            result = true;
        }


        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        if (result) {
            //有节点，连接成功
            if (null != iNodeConnectListener) {
                iNodeConnectListener.successListener();
            }
        } else {
            if (null != iNodeConnectListener) {
                iNodeConnectListener.failsListener();
            }
        }
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

    private boolean isConnectedNodes(MobvoiApiClient mobvoiApiClient) {
        Collection<String> nodes = getNodes(mobvoiApiClient);
        if (nodes.size() <= 0) {
            return false;
        }
        return true;
    }

}
