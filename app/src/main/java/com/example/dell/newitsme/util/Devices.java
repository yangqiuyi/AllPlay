package com.example.dell.newitsme.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;



import org.json.JSONObject;

import java.util.List;

public class Devices
{
    public static final String TAG = "Devices";

    /**
     *  - call in the first activity
     * */
    public static void initDisplayMetrics(WindowManager windowMgr){

        if (Devices.dm != null){
            return;
        }
        DisplayMetrics dm = new DisplayMetrics();
        windowMgr.getDefaultDisplay().getMetrics(dm);
        int widthPixels = dm.widthPixels;
        int heightPixels = dm.heightPixels;
        Devices.density = (int)dm.density;
        Devices.screenWidth = (widthPixels);
        Devices.screenHeight = (heightPixels);
        Devices.dm = dm;
       // LogUtil.d(TAG, dm.toString());
    }

    /**
     * - call in the application class
     * */
    public static void initDevice()
    {

        ua = android.os.Build.MODEL;
        if(ua == null) ua = "";
        osversion = android.os.Build.VERSION.RELEASE;
        if(osversion == null) osversion = "";
      //  devi = Settings.Secure.getString(TurtleApplication.inst().getContentResolver(), Settings.Secure.ANDROID_ID);
        if(devi == null) devi = "";

        TelephonyManager telephonyManager = null;
      //  telephonyManager = (TelephonyManager)TurtleApplication.inst().getSystemService(Context.TELEPHONY_SERVICE);
        if(telephonyManager != null){
            try {
                imei = telephonyManager.getDeviceId();
                imsi = telephonyManager.getSubscriberId();
            }catch (Exception e){

            }
        }
        if(imsi == null)imsi = "";
        if(imei == null)imei = "";

       // ConnectivityManager manager = (ConnectivityManager) TurtleApplication.inst().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State gprs = null;
        NetworkInfo.State wifi = null;
       // if(manager != null){
      /*      try {
                NetworkInfo infoMobile = null;
                infoMobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (infoMobile != null) {
                    gprs = infoMobile.getState();
                }
                NetworkInfo infoWifi = null;
                infoWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (infoWifi != null) {
                    wifi = infoWifi.getState();
                }
            }catch (Exception e){

            }
        }*/
        if(gprs != null){
            if(gprs == NetworkInfo.State.CONNECTED || gprs == NetworkInfo.State.CONNECTING){
                conn = "gprs";
            }
        }
        if(wifi != null){
            if(wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING){
                conn = "wifi";
            }
        }

    }

    /**判断某个程序是否已被安装**/
    public static boolean hasInstalledApp(Context context,String packageName){
        boolean flag = false;
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_SIGNATURES);
            if (list != null) {
                for (PackageInfo info : list) {
                    if (info.packageName.equals(packageName)) {
                        flag = true;
                        break;
                    }
                }
            }
        }catch (Exception e){}

        return flag;
    }

    /**判断某个程序是否已被安装  校验app的签名**/
    public static boolean hasInstalledApp(Context context,String packageName,String signature){
        boolean flag = false;
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_SIGNATURES);
            if (list != null) {
                for (PackageInfo info : list) {
                    if (info.packageName.equals(packageName)) {
                        if(!TextUtils.isEmpty(signature)){
                            for (Signature sig : info.signatures) {
                                if (signature.equalsIgnoreCase(sig.toCharsString())) {
                                    flag = true;
                                    break;
                                }
                            }
                        }
                        if(flag){
                            break;
                        }
                    }
                }
            }
        }catch (Exception e){}

        return flag;
    }

    public static JSONObject toJsonObj()
    {
        JSONObject json = new JSONObject();
        try {
            json.put("ua", ua);
            json.put("osversion", osversion);
            json.put("devi",devi);
            json.put("conn", conn);
            json.put("screenWidth", screenWidth);
            json.put("screenHeight", screenHeight);
            json.put("platform","android");

        }catch (Exception e){}
        return json;
    }

    public static String toJsonString()
    {
        JSONObject json = toJsonObj();
        return json.toString();
    }

    public static DisplayMetrics dm;
    public static int density = 0;
    public static int screenWidth = 0;
    public static int screenHeight = 0;
    public static String ua = "";//手机型号
    public static String osversion = ""; //android系统版本号
    public static String devi = "";//设备号
    public static String imei = "";//国际移动设备识别码
    public static String imsi = "";//国际移动用户识别码
    public static String conn = "";//网络连接类型

}
