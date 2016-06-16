# EmokitWear
Android可穿戴设备 手表根据手表获取的心率测试当前的情绪，目前支持AndroidWear，TicWear系统的手表

#####介绍
当前Android腕表都可以测试心率，这个库就是根据腕表获取的心率来计算当前的心情，主要用到了http://ticwear.com/和http://www.emokit.com/开发包，
详情大家可以进入官网查看

#####EmokitWear结构分析
1.最主要的也就是类库是 library
  里面主要封装了手表端采集心率，传递到手机端，手机端连接网络检测当前心情的过程和一些必要的监听<br>  
2.手机端腕表端公用的数据 emodata 
  里面主要是手机端和腕表端公用的数据和类方法
3.手机端程序demo  mobile
  手机端程序demo
4.玩表单程序demo  wear
  腕表端程序demo
  
#####EmokitWearSDK技术文档
1.导入SDK
创建一个AndoridWear工程，将<br>
emokitsdk4.3.jar（手机端引入）<br>
emokitwear.jar（手机端、腕表端都需要引入）<br>
mobvoi-api.jar（手机端、腕表端都需要引入）<br>
三个包引入到工程。<br>
http://www.emokit.com/ <br>
下载emokitsdk4.3.jar<br>
http://developer.ticwear.com/
下载mobvoi-api.jar

2.添加权限<br>
1）手机端添加权限<br>
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" /> 
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
```
2）腕表端添加权限<br>
```xml
<uses-permission android:name="android.permission.BODY_SENSORS" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.DEVICE_POWER" />
```
3.添加AID和KEY
初始化时候手机端需要配置从EmoKit 开发者中心(http://dev.emokit.com/)申请的AID和KEY。
AndroidManifest.xml中配置如下:
```xml
<meta‐data
android:name="EMOKIT_AID"
android:value="XXXXXX"/>
<meta‐data
android:name="EMOKIT_KEY"
android:value="XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"/>
```
4.初始化SDK
一定要在Application的onCreate中调用。
1）手机端初始化代码
```java
MobileApiConfiguration configuration = new MobileApiConfiguration.Builder()
        .setPlatflag("EmokitWearSDK")//platflag 应用名
        .setUserName("userName")//userName 用户名或设备 ID
        .setPassword("10000")//password 用户登录密码(默认10000)
        .setRcType(rcType)//情绪结果种类，分为24种，7种，5种（见附录5）
        .create();
EmokitApiManager.getInstance().mobileApiConfig(mContext,configuration);
```
2）腕表端初始化代码
```java
WearApiConfiguration configuration = new WearApiConfiguration.Builder()
// 设置获取心率的超时时间,如果超过这个时间还没有接收到一个有用的心率值会自动停止获取心率
//获取心率期间最小时间10秒，输入参数小于10秒无效
.setHeartRateDuration(30*1000)
// 设置获取心率的时间，在这个规定时间内获取心率在成功获取第一个心率值开始倒计时
//获取心率超时时间最小20秒，输入参数小于20秒无效
.setHeartRateTimeOut(45 * 1000)
.create();
EmokitApiManager.getInstance().wearApiConfig(getApplication(), configuration);
```
5.设置数据传输Service
1）手机端
手机端为了和腕表进行数据传递需要实现MobileDataTransferService并且在AndroidManifest.xml中注册
```java
public class MobileEmokitService extends MobileDataTransferService{

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
```
2）腕表端
腕表端为了和手机进行数据传递需要实现WearDataTransferService并且在AndroidManifest.xml中注册
```java
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
```
3）AndroidManifest.xml中注册Service
手机端
```xml
<service
    android:name=".emo.MobileEmokitService"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="com.mobvoi.android.wearable.BIND_LISTENER" />
    </intent-filter>
</service>
```
腕表端
```xml
<service
    android:name=".emo.WearEmokitService"
    android:enabled="true"
    android:exported="true">
    <intent-filter>
        <action android:name="com.mobvoi.android.wearable.BIND_LISTENER" />
    </intent-filter>
</service>
```
6.注册广播监听
手机端和腕表端注册以下广播，主要是监听和回调方法，例如：开始测试回调，停止测试回调，返回情绪回调等。
1）手机端
Action为
动态注册：
```java
MobileWearableReceive.ACTION_MOBILE_EMO
```
静态注册：
```xml
<action android:name="com.emokit.wear.action.MOBILE_EMO_LISTENER"/>
```
```java
private MobileWearableReceive mMobileWearableReceive = new MobileWearableReceive() {
    @Override
public void onStartHeartRateListener() {
//开始测试心率
        Toast.makeText(mContext, "StartHeartRate", Toast.LENGTH_SHORT).show();
    }

    @Override
public void onCancelHeartRateListener() {
//取消测试心率
        Toast.makeText(mContext, "CancelHeartRate", Toast.LENGTH_SHORT).show();

    }

    @Override
public void onArrayHeartRateListener(float[] heartRates) {
//获取心率完成，收到心率数组
        Toast.makeText(mContext, "ArrayHeartRate", Toast.LENGTH_SHORT).show();
    }

    @Override
public void onEmotionListener(String emoJson) {
//获取情绪结果
        Toast.makeText(mContext, "Emotion : " + emoJson, Toast.LENGTH_SHORT).show();
    }

    @Override
public void onErrorListener(String errCode) {
   //出错监听
        Toast.makeText(mContext, "errCode : " + errCode, Toast.LENGTH_SHORT).show();
    }
};
```
2）腕表端
Action为
动态注册：
```java
WearWearableReceive.ACTION_WEAR_EMO
```
静态注册：
```xml
<action android:name="com.emokit.wear.action.WEAR_EMO_LISTENER"/>
```
```java
private WearWearableReceive mWearWearableReceive = new WearWearableReceive() {
        @Override
        public void onStartHeartRateListener() {
		//开始测试心率
            Toast.makeText(mContext, "StartHeartRate", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelHeartRateListener() {
		//取消测试心率
            Toast.makeText(mContext, "CancelHeartRate", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onArrayHeartRateListener(float[] heartRates) {
		//获取心率完成，收到心率数组
            Toast.makeText(mContext, "ArrayHeartRate", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onHeartRateValueListener(int index, float value) {
		//每次获取的心率值
		//获取第index个心率
		//获取的心率值
            if(0 == index){
                Toast.makeText(mContext, "HeartRateValue : " + value + "   " + index, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onEmotionListener(String emoJson) {
		//获取情绪结果
            Toast.makeText(mContext, "Emotion : " + emoJson, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onErrorListener(String errCode) {
		//出错监听
            Toast.makeText(mContext, "errCode : " + errCode, Toast.LENGTH_SHORT).show();
        }
    };
```
3）情绪结果
成功结果:
```json
{"resultcode":"200","rc_main":"T","rc_main_value":"8.75","result_id":"14907293","exciting":"CH","exciting_trend":"UP","servertime":"20160319101255"}
```
错误结果:
```json
{"resultcode":"10000","reason":"传入的参数不正确","servertime":"20151030145703"}
```
字段说明
resultcode 结果码（详细见附录（1））<br>
rc_main 主要情绪(详细见附录（2）)<br>
rc_main_value 主要情绪分值<br>
result_id 返回情绪结果唯一id<br>
exciting 当前情绪状态（详细见附录（3））<br>
exciting_trend 情绪走势（详细见附录（4））<br>
servertime 服务器时间<br>
reason 错误描述<br>
4）广播监听错误代码
手机端、腕表端errCode如下
```java
public void onErrorListener(String errCode) {
		//出错监听
}

/**获取情绪结果错误*/
PublicConstant.ERR_EMOTION_RESULT = "10000";
/**获取心率错误，没有获取到心率*/
PublicConstant.ERR_HEART_RATE = "10001";
/**
 *点击开始测试心率,手机腕表没有建立链接， 
 * 1.手机端，没有链接到腕表
 * 2.腕表端，没有链接到手机
 */
PublicConstant. ERR_CONNECT_START_HEART_RATE
 = "10002";
/**
 *停止测试心率,手机腕表没有建立链接 
 * 1.手机端，没有链接到腕表(只有手机端，腕表端没链接到手机也可以取消)
 */
PublicConstant. ERR_CONNECT_STOP_HEART_RATE= "10003";
/**
 *手机端发送情绪结果,手机腕表没有建立链接 
 * 1.没有链接到腕表
 */
PublicConstant.ERR_CONNECT _SEND_EMO_RESULT = "10004";
/**
 * 手表端发送心率数组,手机腕表没有建立链接
 * 1.没有链接到手机
 */
PublicConstant.ERR_CONNECT _SEND_HEART_RATE_ARRAY = "10005";
```
7.开始获取情绪和停止获取情绪
1）手机端
开始获取情绪
```java
Intent intent = new Intent(mContext, MobileEmokitService.class);
intent.putExtra(PublicConstant.SERVICE_ACTION_START_HEART_RATE, "");
startService(intent);
```
停止获取情绪
```java
Intent intent = new Intent(mContext,MobileEmokitService.class);
intent.putExtra(PublicConstant.SERVICE_ACTION_STOP_HEART_RATE, "");
startService(intent);
```
2）手表端
开始获取情绪
```java
Intent intent = new Intent(MainActivity.this, WearEmokitService.class);
intent.putExtra(PublicConstant.SERVICE_ACTION_START_HEART_RATE, "");
startService(intent);
```
停止获取情绪
```java
Intent intent = new Intent(MainActivity.this, WearEmokitService.class);
intent.putExtra(PublicConstant.SERVICE_ACTION_STOP_HEART_RATE, "");
startService(intent);
```

8.附录<br>
（1）	结果码表<br>
成功<br>
resultcode = 200<br>
错误<br>
 
（2）	主要情绪<br>
详情见<br>
《EmoKit二十四种情绪描述》<br>
《EmoKit七种情绪描述》<br>
《EmoKit五种情绪描述》<br>
（3）	当前情绪状态<br>
LA:过于低迷<br>
LV:较为低迷<br>
CH:较为兴奋<br>
HO:过于兴奋<br>
（4）情绪走势<br>
DN:走低 <br>
ST:维持 <br>
UP:走高<br>
（5）情绪种类<br>
返回情绪结果种类，分为24种，7种，5种<br>
MobileApiConfiguration.RC_TYPE_24<br>
MobileApiConfiguration.RC_TYPE_7<br>
MobileApiConfiguration.RC_TYPE_5<br>


