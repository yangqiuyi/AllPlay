package com.example.util;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

    public static class Encrypt {


        public static String md5hash(String key) {
            return hashWithAlgorithm("MD5", key);
        }

        public static String md5hash(File file) {
            InputStream fis;
            byte[] buffer = new byte[1024];
            int numRead = 0;
            MessageDigest md5;
            try{
                fis = new FileInputStream(file);
                md5 = MessageDigest.getInstance("MD5");
                while((numRead=fis.read(buffer)) > 0) {
                    md5.update(buffer,0,numRead);
                }
                fis.close();
                return toHexString(md5.digest());
            } catch (Exception e) {
                return "";
            }
        }

        private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'A', 'B', 'C', 'D', 'E', 'F' };

        public static String toHexString(byte[] b) {
            StringBuilder sb = new StringBuilder(b.length * 2);
            for (int i = 0; i < b.length; i++) {
                sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
                sb.append(HEX_DIGITS[b[i] & 0x0f]);
            }
            return sb.toString();
        }

        private static String hashWithAlgorithm(String algorithm, String key) {
            return hashWithAlgorithm(algorithm, key.getBytes());
        }

        private static String hashWithAlgorithm(String algorithm, byte[] bytes) {
            MessageDigest hash;
            try {
                hash = MessageDigest.getInstance(algorithm);
            } catch (NoSuchAlgorithmException e) {
                return null;
            }
            return hashBytes(hash, bytes);
        }

        private static String hashBytes(MessageDigest hash, byte[] bytes) {
            hash.update(bytes);
            byte[] digest = hash.digest();
            StringBuilder builder = new StringBuilder();
            for (int b : digest) {
                builder.append(Integer.toHexString((b >> 4) & 0xf));
                builder.append(Integer.toHexString((b >> 0) & 0xf));
            }
            return builder.toString();
        }

        public static String SHA1(String decript) {
            try {
                MessageDigest digest = MessageDigest
                        .getInstance("SHA-1");
                digest.update(decript.getBytes());
                byte messageDigest[] = digest.digest();
                // Create Hex String
                StringBuffer hexString = new StringBuffer();
                // 字节数组转换为 十六进制 数
                for (int i = 0; i < messageDigest.length; i++) {
                    String shaHex = Integer.toHexString(messageDigest[i] & 0xFF);
                    if (shaHex.length() < 2) {
                        hexString.append(0);
                    }
                    hexString.append(shaHex);
                }
                return hexString.toString();

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        }

    }
}
