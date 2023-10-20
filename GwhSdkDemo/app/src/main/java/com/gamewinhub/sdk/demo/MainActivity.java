package com.gamewinhub.sdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.gamewinhub.open.GwhApiFactory;
import com.gamewinhub.open.callback.GwhLoginResultListener;
import com.gamewinhub.open.callback.GwhSdkInitListener;
import com.gamewinhub.open.callback.GwhUserInfo;
import com.gamewinhub.open.callback.LogoutCallback;
import com.gamewinhub.open.entity.OrderInfo;
import com.gamewinhub.sdk.callback.PayCallBack;
import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private String extra_param;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImmersionBar.with(MainActivity.this)
                .fullScreen(true)
                .hideBar(BarHide.FLAG_HIDE_BAR)
                .init();
        setContentView(R.layout.activity_main);

        initSdk();

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GwhApiFactory.startLogin(MainActivity.this);
            }
        });

        findViewById(R.id.signOut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GwhApiFactory.loginOut();
            }
        });

        findViewById(R.id.full).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImmersionBar.with(MainActivity.this).hideBar(BarHide.FLAG_HIDE_BAR).init();
            }
        });

        findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置道具参数，调用sdk支付界面（以下参数为必填参数，如果没有可传"0"）
                OrderInfo order = new OrderInfo();
                order.productName = "钻石礼包";                //游戏道具名称
                order.productDesc = "钻石可用于购买游戏内道具";  //游戏道具描述
                order.productPriceValue = 1.01;            //游戏道具价格
                order.productCurrency = "USD";               //货币单位
                order.serverName = "江苏一区";                 //游戏区服名
                order.gameServerId = "12";                    //游戏区服ID
                order.roleName = "放学你别跑";                 //游戏角色名
                order.roleId = "10";                          //游戏角色ID
                order.roleLevel = "70";                       //游戏角色等级
                order.sdk_param = extra_param;  //平台方的预留标识（默认值是平台域名，sdk用户登录成功时获取，不需改动）
                order.extendInfo = String.valueOf(System.currentTimeMillis());    //游戏方的透传参数，服务端支付回调时原样返回，建议传订单号（当前demo用系统时间模拟订单号，正式接入时请传订单号）
                GwhApiFactory.startPay(MainActivity.this, order, new PayCallBack() {
                    @Override
                    public void onPaySuccess(String result) {
                        Log.w(TAG, "支付成功");
                        Toast.makeText(MainActivity.this, "Purchase successful", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPayFail(String result) {
                        Log.w(TAG, "支付失败");
                        Toast.makeText(MainActivity.this, "Sorry, your payment failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initSdk() {
        // 1.初始化SDK
        /*
         * Context con 当前Activity
         * boolean isDebug  日志开关（生产设置为false）
         * GwhSdkInitListener gwhSdkInitListener 初始化SDK完成回调
         * */
        GwhApiFactory.initActivity(this, true, new GwhSdkInitListener() {
            @Override
            public void onInitFinish(int result) {
                //初始化成功弹出登录对话框
                GwhApiFactory.startLogin(MainActivity.this);
            }
        });

        //2.设置登录回调
        GwhApiFactory.setLoginCallback(new GwhLoginResultListener() {
            @Override
            public void onFinish(GwhUserInfo result) {
                switch (result.getmErrCode()) {
                    case 1:
                        Log.e(TAG, "sdk登录回调：登录失败");
                        break;
                    case 0:
                        String uid = result.getAccountNo();     //用户id（用户唯一标识）
                        String token = result.getToken();       //用户token
                        extra_param = result.getExtraParam();  //sdk预留的标识，发起支付方法时再传给sdk
                        Log.w(TAG, "sdk登录成功," + "userid = " + uid + "，token = " + token);

                        //游戏在这时需要拿到以上userid和token到sdk服务端验证登录结果（详见《游戏登录支付通知接口文档》）
                        break;
                }
            }
        });

        // 3.账号注销监听初始化
        GwhApiFactory.setLogoutCallback(new LogoutCallback() {
            @Override
            public void logoutResult(String result) {
                if ("1".equals(result)) {
                    Log.i(TAG, "sdk注销回调：注销成功");
                    GwhApiFactory.startLogin(MainActivity.this);//调用登录弹窗
                } else {
                    Log.e(TAG, "sdk注销回调：注销失败");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GwhApiFactory.onActivityResult(requestCode, resultCode, data);
    }
}
