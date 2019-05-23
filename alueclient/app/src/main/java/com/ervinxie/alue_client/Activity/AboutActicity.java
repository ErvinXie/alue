package com.ervinxie.alue_client.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alipay.sdk.app.PayTask;
import com.ervinxie.alue_client.R;
import com.ervinxie.alue_client.data.DataTest;
import com.ervinxie.alue_client.util.Contract;
import com.ervinxie.alue_client.util.FullscreenActivity;
import com.ervinxie.alue_client.util.pay.OrderInfoUtil2_0;
import com.ervinxie.alue_client.util.pay.PayResult;

import java.util.Map;

public class AboutActicity extends FullscreenActivity {

    ImageButton nav_bubble;
    Button offer_me_coffe, to_data_test;

    TextView bigtitle,version;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_view);
        requestPermission();
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        linearLayout = findViewById(R.id.contentPanel);
        to_data_test = findViewById(R.id.to_data_test);
        nav_bubble = findViewById(R.id.nav_bubble);
        offer_me_coffe = findViewById(R.id.offer_me_coffee);
        bigtitle = findViewById(R.id.bigtitle);
        version = findViewById(R.id.version);
        version.setText("version: "+Contract.getLocalVersion());


        mContentView = linearLayout;

        mContentView.setOnClickListener(v -> {
            toggle();

            if(AUTO_HIDE){delayedHide(3000);}
        });


        to_data_test.setOnClickListener(v -> {
            Intent intent = new Intent(this, DataTest.class);
            startActivity(intent);
        });

        nav_bubble.setOnClickListener(v -> finish());

        offer_me_coffe.setOnClickListener(v -> {
            payV2(v);
        });

    }


    /**
     * 用于支付宝支付业务的入参 app_id。
     */
    public static final String APPID = "2019052365373070";

    /**
     * 用于支付宝账户登录授权业务的入参 pid。
     */
    public static final String PID = "2088122426471256";

    /**
     * 用于支付宝账户登录授权业务的入参 target_id。
     */
    public static final String TARGET_ID = "";

    /**
     * pkcs8 格式的商户私钥。
     * <p>
     * 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个，如果两个都设置了，本 Demo 将优先
     * 使用 RSA2_PRIVATE。RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议商户使用
     * RSA2_PRIVATE。
     * <p>
     * 建议使用支付宝提供的公私钥生成工具生成和获取 RSA2_PRIVATE。
     * 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1
     */
    public static final String RSA2_PRIVATE =
    "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCngWFi9TsMQBqiym8KbdLfmaMQtgtg2aTh5UfNfQmsk4FTqxhmRHJolCnkEeHRix6EUSlw+4bw/xAUoNQCTtt9qWkN/DI6SvBcDNX36omuLhGX6RQjiMrMGV04qtOuDAIT06Me/6ON3rvifQIjJOwkPj2gHCToBYV5uzYgP5CLFpBE/GMIbMsPv34cvz7wPi+smYAQ5ABYG3a29WM3DsEixbNj+y8hosN52kwjRETMfGOhRppStTMpSA1S23spE2UEMKFeKzhFGSlRCKO3Ga0Kz1tDVvoWqHqepWaBU1lN5/JkKi5n6uNrCsHumSbcGi7NRdHzMZi5FhFIyWQyL4ljAgMBAAECggEBAJksBXrxVvqfz/uW861sug1AN837OjJpM45iFKPCuEr4Y6W36kryQlCBVEE7XO05s0E52pR3XXEZPIIwg05NAAdMGEXynZeSmXMDRdI7xYTimbH5MDJCT3gNpaJBC+q3DltFB4A8Mjp0OAknWQH8LHgD9mRn+BO3oNOtLxSUK8AS9jCPxBY/hjCg0Dz/8nTcBQLzo1ncawFGJFo9KKAAbL6WLAtvZbrBG32biXuFBU2bMmQ2liiK7pso1eXHsCIWSDw59l/T0TfSmMmKUmfQbIGtHCqlSlKAS3+TDCE10Qhf5/iNA26QxwBIeoJvCEtvZFmr+f1BqrUJ8nvAIS9c1TkCgYEA79KAFb57D2MsBvyLYAt5SIzoeKC9GV4i8spsxe3ofHZw9/gxPocUJg8IHJbP4wq+gLoaY9ZUew4mRNAAiBOZkRxtfMSiJJZNWCoTNUdl9k3r9X0fmwk1JwKUf9wxrf7JQn3Jct7Ne9T0xYv7CmgtWf0rPJcT//Do5gC/JNO8t7UCgYEAss4Jiyon4SuvdEgYbnRGje0U56ZrwAqAbz9Ig3ZmTW0GQO1FeAMsGFvPYhMHXkf9xAMvztuMDzXUDgSaH00rN9k/Aew9S92zUOodug3D1Xw6lp0pwNDotZCmT1kd0KbRjjCXLhRP8fobL3mzHuGmX9tvcVBlPgWdIx+rbawbu7cCgYEAyGHDIef5XN8JP4EuV37exO7vozLzLcoJO4JFpo/ljHFObPLU+qDVBgPTTEf6xYMJr/dP65F/Hx6wfRirCQgPbT8qgHCv5hAr6fml+QOCP23WNVVp3hmwbrrqJ3dtjytvMH53nuJpIQnLx2/xvz1Sf3lY8hRt4pGBmASRsYy1h20CgYBzVLKTMP3IH4VsW5RmqllX8jQptw6JMDznhMohAZ27EzeVaXYFkwY+L/n0KJH4Hjdw1x1fL/2HUhEVeaJvzjayL06Uzuw6oyWma1wBRh+q9BZWT8k+tYFkm4iqZbD1hKRmMrFQ54kpa0ldtgHzSVknO0MGs/SZrMVOgn7wnrLEwQKBgC2hsOMkN3cG41bNhCTv5fv/513PvRQB2xw+AWgjAhxTmO6iXcST6tdRHjuzzByjGg2BaoDXgJXFd6585EbusyL16/IffJEiJtizDBN/pxIu+Gw1a1TZ+2HAHS/cXj3EmluVbUquc6J1u74LtuewHfcbb6Xp3YMjQsLzsf0H+eTx";
    public static final String RSA_PRIVATE = "";

    private static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
//                        ToastShort("支付成功" + payResult);
                        ToastShort("非常感谢");
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
//                        ToastShort("支付失败" + payResult);
                        ToastShort("支付遇到问题");
                    }

                    break;
                }
                default:
                    break;
            }
        };
    };

    /**
     * 获取权限使用的 RequestCode
     */
    private static final int PERMISSIONS_REQUEST_CODE = 1002;

    /**
     * 检查支付宝 SDK 所需的权限，并在必要的时候动态获取。
     * 在 targetSDK = 23 以上，READ_PHONE_STATE 和 WRITE_EXTERNAL_STORAGE 权限需要应用在运行时获取。
     * 如果接入支付宝 SDK 的应用 targetSdk 在 23 以下，可以省略这个步骤。
     */
    private void requestPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(Contract.context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(Contract.context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(Contract.alueMainActivity,
                    new String[]{
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, PERMISSIONS_REQUEST_CODE);
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_CODE: {
//
//                // 用户取消了权限弹窗
//                if (grantResults.length == 0) {
//                    showToast(this, getString(R.string.permission_rejected));
//                    return;
//                }
//
//                // 用户拒绝了某些权限
//                for (int x : grantResults) {
//                    if (x == PackageManager.PERMISSION_DENIED) {
//                        showToast(this, getString(R.string.permission_rejected));
//                        return;
//                    }
//                }
//
//                // 所需的权限均正常获取
//                showToast(this, getString(R.string.permission_granted));
//            }
//        }
//    }
    /**
     * 支付宝支付业务示例
     */
    public void payV2(View v) {
        if (TextUtils.isEmpty(APPID) || (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))) {
            ToastShort("缺少密钥");
            return;
        }

        /*
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo 的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;


        final Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(Contract.alueMainActivity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

}
