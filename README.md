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

2.添加权限
1）手机端添加权限
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
2）腕表端添加权限
<uses-permission android:name="android.permission.BODY_SENSORS" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.DEVICE_POWER" />

