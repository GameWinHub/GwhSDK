## GameWinHub安卓sdk接入说明



| 修订日期   | **版本** |      | 更新内容                                                     |
| ---------- | -------- | ---- | ------------------------------------------------------------ |
| 2023-01-11 | V1.0.0   |      | 1.    Paypal支付<br/>2.    Payssion支付  <br/>3.    Facebook登录，谷歌登录，游客登录 |



### **一、文件介绍**

#### 1. SDK包体介绍

GameWinHub SDK文件为GwhSdkDemo/app/libs/gwhsdk.aar，集成时需要将此文件包到游戏工程内引入使用。具体参考对接文件 - GwhSdkDemo演示工程。

#### 2. SDK所需依赖(AndroidStudio环境)

SDK支持使用**AndroidStudio**环境接入，请加入以下代码到build.gradle：

•     项目build.gradle添加以下配置，添加依赖包下载地址及账号

```groovy
repositories {
  ....
  ....     
  // This private repository is  required to resolve the Cardinal SDK transitive dependency.     
  maven {
    url   "https://cardinalcommerceprod.jfrog.io/artifactory/android"
    credentials {         
      // Be sure to add these  non-sensitive credentials in order to retrieve dependencies from
      // the private repository.
      username  "paypal_sgerritz"
      password  "AKCp8jQ8tAahqpT5JjZ4FRP2mW7GMoFZ674kGqHmupTesKeAY2G8NcmPKLuTxTGkKjDLRzDUQ"
    }
  }
}
```

•     在build.gradle中添加如下代码，引入依赖

```groovy
//基础依赖
implementation 'androidx.recyclerview:recyclerview:1.2.1'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.appcompat:appcompat:1.5.1'
implementation "androidx.core:core-ktx:1.5.0"
implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.4.10"

//第三方框架依赖
implementation 'com.google.code.gson:gson:2.10.1'                   // gson解析
implementation 'com.google.android.gms:play-services-auth:20.7.0'   //谷歌登录
implementation 'com.facebook.android:facebook-login:16.2.0'         //FaceBook
implementation 'com.paypal.checkout:android-sdk:0.6.1'              //Paypal
implementation 'com.stripe:stripe-android:20.15.1'                  //Stripe
implementation 'io.github.lucksiege:pictureselector:v3.11.1'        //pictureselector
implementation 'io.github.lucksiege:compress:v3.11.1'
```

 

#### 3. 游戏参数文件说明

`gwhgameinfo.json`为游戏参数文件，里面保存着游戏参数信息。对接时将GameWinHub提供的`gwhgameinfo.json`复制到游戏工程的assets目录下，sdk会优先读取assets下的此参数文件。

| **游戏参数名称** | **含义**                                                     |
| ---------------- | ------------------------------------------------------------ |
| gameId           | 游戏ID                                                       |
| gameName         | 游戏名称                                                     |
| gameAppId        | 游戏Appid                                                    |
| accessKey        | 访问密钥(access_key，用于SDK初始化参数配置)                  |
| gameUrl          | 平台域名：<br/>测试环境：https://papi-stage.gamewinhub.com<br/>生产环境：https://papi.gamewinhub.com |
| googleClientId   | 谷歌clientid                                                 |
| stripePublishKey | Stripe支付key                                                |
| payssionApiKey   | Payssion支付key                                                |
| paypalClientId   | paypal的clientid<br/>沙盒环境有效，本地测试参数，打包后使用后台设置参数<br/>沙盒环境在Application接口设置 |

 

#### 4. facebook参数设置

在`strings.xml`文件中添加以下参数，替换自己的appid (由GameWinHub提供)

```xml
<string name="facebook_app_id"  translatable="false">1021990672049786</string>
<string name="fb_login_protocol_scheme"  translatable="false">fb1021990672049786</string>
<string name="facebook_client_token"  translatable="false">8b491ba0f702aba811d930f59e2a4685</string>  
```

 

### 二、初始化（必接）

详见demo演示工程`com.gamewinhub.sdk_test_demo.MainActivity`内代码示例

 

#### 1. Application接口

```JAVA
//Application的onCreate中调用(Paypal需求)，不可在其它方法中。   
//第一个参数是Application   
//第二个参数设置Paypal支付环境，true是沙盒 false是上线   
GwhApiFactory.getApi().initApplication(this,  false);  
```



#### 2. sdk初始化

游戏Activity页面的onCreate()中调用如下方法，执行sdk初始化

```kotlin
//设置登录回调
GwhApiFactory.getApi().setLoginCallback(loginCallback);   
// 1.初始化SDK   
GwhApiFactory.getApi().init(this,  true, new GwhSdkInitListener() {     
  @Override     
  public void onInitFinish(int  result) {
  }   
});
// 2.账号注销监听初始化
GwhApiFactory.getApi().initLogoutCallback(logoutCallback);
```



#### 3. 游戏Activity生命周期相关接入

```java
/**生命周期onActivityResult()
 *第三方登录相关设置
 */
@Override protected void onActivityResult(int  requestCode, int resultCode, Intent data) {
  super.onActivityResult(requestCode, resultCode, data);
  GwhApiFactory.getApi().onActivityResult(requestCode, resultCode, data);
}
```



### 三、主要功能接入（必接）

#### 1. 设置用户登录结果监听、发起登录

登录成功时游戏服务器需要拿user_id和token访问sdk服务器，进行登录验证。详见《GameWinHub sdk服务端接口文档》   

```JAVA
//设置用户登录结果监听   
private GwhLoginResultListener loginCallback = new GwhLoginResultListener() {     
  @Override
  public void onFinish(GwhUserInfo  result) {
    switch (result.getmErrCode()) {
      case 1:
        Log.e(TAG, "sdk登录回调：登录失败");
        break;
      case 0:
        String uid =  result.getAccountNo();  //用户id（用户唯一标识）
        String token =  result.getToken();   //用户token
        extra_Param =  result.getExtra_param(); //sdk预留的参数信息，发起支付方法时再传给sdk
        Log.W(TAG, "sdk登录成功," + "userid = " + uid + ",token = " + token);
        
        //游戏在这时需要拿到以上userid和token到sdk服务端验证登录结果(详见《GameWinHub sdk服务端接口文档》）
        break;
    }
  }
};
//调用登录弹窗   
GwhApiFactory.getApi().startlogin(loginCallback);  
```

 

#### 2. 设置支付结果监听、设置游戏道具参数发起支付

支付结果应以sdk服务端通知为准，服务端通知规则详见《GameWinHub sdk服务端接口文档》

```JAVA
//设置sdk支付结果监听
private PayCallback payCallback = new PayCallback() {
  @Override
  public void callback(String  errorcode) {
    if  ("0".equals(errorcode)) {
      Log.i(TAG,"sdk支付回调：支付成功");
    } else {
      Log.e(TAG,"sdk支付回调：支付失败");
    }
  }
};      
//设置道具参数，调用sdk支付界面（以下参数为必填参数，如果没有可传"0"）
OrderInfo order = new OrderInfo();
order.productName = "钻石礼包"; //游戏道具名称
order.productDesc = "钻石可用于购买游戏内道具"; //游戏道具描述
order.productPrice = shopPrice; //游戏道具价格
order.productCurrency = "USD"; 
//order.productCurrency = "CNY"; //货币单位
//order.serverName = "江苏一区";         //游戏区服名: 可选
//order.gameServerId = "12";          //游戏区服ID: 可选
//order.roleName = "放学你别跑";         //游戏角色名: 可选
order.roleId = "10";             //游戏角色ID
//order.roleLevel = "70";            //游戏角色等级: 可选
order.sdk_param = extra_param; //平台方的预留标识（默认值是平台域名，sdk用户登录成功时获取，不需改动）
order.extendInfo = String.valueOf(System.currentTimeMillis());  //游戏方的透传参数，服务端支付回调时原样返回，建议传订单号（当前demo用系统时间模拟订单号，正式接入时请传订单号）
GwhApiFactory.getApi().pay(activity,  order, payCallback);  
```



#### 3. 设置注销登录结果回调、注销登录

```JAVA
//设置sdk注销登录结果监听
private LogoutCallback logoutCallback = new LogoutCallback() {
  @Override
  public void logoutResult(String  result) {
    if  ("1".equals(result)) {
      Log.i(TAG, "sdk注销回调：注销成功");
      GwhApiFactory.getApi().stopFloating(activity); //关闭悬浮球
      GwhApiFactory.getApi().startlogin(activity,  loginCallback); //调用登录弹窗
    } else {
      Log.e(TAG, "sdk注销回调：注销失败");
    }
  }
};
//sdk注销登录
GwhApiFactory.getApi().loginout(activity);  
```

 

#### 4**. **设置退出程序结果回调、退出游戏程序

```JAVA
//退出游戏程序结果监听
/**
 *退出程序回调接口
 **/
private IGPExitObsv mExitObsv = new IGPExitObsv() {
  @Override
  public void  onExitFinish(GPExitResult exitResult) {
    switch (exitResult.mResultCode)  {
      case GPExitResult.GPSDKExitResultCodeError:
        Log.e(TAG,  "退出回调:调用退出弹窗失败");
        break;
      case GPExitResult.GPSDKExitResultCodeExitGame:
        Log.i(TAG,  "退出回调:点击了退出程序确认弹窗中确定按钮");
        GwhApiFactory.getApi().stopFloating(activity); //关闭悬浮窗
        //退出程序
        finish();
        System.exit(0);
        break;
    }
  }
};
//调用sdk确认退出程序弹窗
GwhApiFactory.getApi().exitDialog(activity,  mExitObsv);  
```

 

### 四、注意事项（必读）

1、工程minSdkVersion（最低支持安卓版本）为：21

2、平台可能会对游戏进行分包处理，游戏出包时请使用V2签名

3、sdk所需权限和相关设置如下，详见演示Demo工程AndroidManifest.xml

```XML
    <!-- 添加必要的权限支持 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.CAMERA" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/icon"
        android:name="com.mchsdk.open.MCApplication"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/AppTheme"
        android:usesCleartextTraffic="true"
        tools:replace="android:allowBackup"> 
```

 
