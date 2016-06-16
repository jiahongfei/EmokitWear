package com.emokit.wear.test.emo;

import com.emokit.wear.service.wear.WearDataTransferService;
import com.mobvoi.android.common.api.MobvoiApiClient;
import com.mobvoi.android.wearable.DataEventBuffer;
import com.mobvoi.android.wearable.MessageEvent;
import com.mobvoi.android.wearable.Node;

/**
 * Created by jiahongfei on 16/3/16.
 */
public class WearEmokitService extends WearDataTransferService {
    private MobvoiApiClient mMobvoiApiClient;

    @Override
    protected void superMobvoiApiClient(MobvoiApiClient superMobvoiApiClient) {
        //这个方法将父类的对象传递到子类中，子类可以根据这个对象进行数据的传递或者接收
        mMobvoiApiClient = superMobvoiApiClient;
    }

    @Override
    public void onPeerConnected(Node arg0) {
        super.onPeerConnected(arg0);
    }

    @Override
    public void onPeerDisconnected(Node arg0) {
        super.onPeerDisconnected(arg0);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //必须调用
        super.onMessageReceived(messageEvent);
    }

    @Override
    public void onDataChanged(DataEventBuffer eventBuffer) {
        //必须调用
        super.onDataChanged(eventBuffer);
    }

}

