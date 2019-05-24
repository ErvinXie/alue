package com.ervinxie.alue_client.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.ervinxie.alue_client.Activity.AlueMainActivity;

public class Contract {
    private static final String TAG = "Contract";

    private Contract(){}

    public static Context context=null;

    public static AlueMainActivity alueMainActivity;

    public static String getLocalVersion() {
        Context ctx = context;
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
            Log.d(TAG,"local version:"+localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

}
