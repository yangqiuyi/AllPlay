package com.example.dell.newitsme.util;


import android.net.Uri;
import android.util.Log;

import com.example.dell.newitsme.net.ApiUrls;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


public class ImageUrlParser {

    private static final String TAG = "ImageUrlParser";

    /**以下定义获取头像的大小**/
    public static int HEAD_IMAG_WIDTH_100 = 100;
    public static int HEAD_IMAG_WIDTH_150 = 150;
    public static int HEAD_IMAG_WIDTH_200 = 200;
    public static int HEAD_IMAG_WIDTH_250 = 250;
    public static int HEAD_IMAG_WIDTH_300 = 300;


    /*封面*/
    public static Uri coverImageUrl(String url)
    {

        String urlFull = imageUrlGen(url,Devices.screenWidth);
         return Uri.parse(urlFull);

    }

    //头像专用-大图
   /* public static Uri avatarImageUrl(String url) {
        return coverImageUrl(url);
    }

    *//**
     * 房间观众用户头像
     * @param url
     * @return
     */
    public static Uri avatarRoomHeadImageUrl(String url) {
        return avatarRoomHeadImageUrl(url,100);
    }

    /**
     * 用户头像
     * @param url
     * @param width
     * @return
     */
    public static Uri avatarRoomHeadImageUrl(String url, int width) {
        String urlFull = imageUrlGen(url, width);//number is too lagre, so artwork is return.
        //  LogUtil.i(TAG, "avatarImageUrl urlFull = " + urlFull);
        return Uri.parse(urlFull);
    }

    public static String imageGiftUrlGen(String url)
    {
       /* if (!ApiUrls.inst().isReady()){
           // LogUtil.e(TAG,"ApiUrls not ready");
            return url;
        }*/

        String scaleUrl = ApiUrls.inst().keyToUrl("IMAGE_GIFT");
        scaleUrl += url;
        //LogUtil.r(TAG,scaleUrl);
        return scaleUrl;
    }

    public static String imageUrlGen(String url, int size)
    {
        /*if (!ApiUrls.inst().isReady()){
           // LogUtil.e(TAG,"ApiUrls not ready");
            return url;
        }*/
        String scaleUrl = ApiUrls.inst().keyToUrl("IMAGE_SCALE");

        StringBuilder builder = new StringBuilder();
        builder.append(scaleUrl);
        builder.append("?");
        builder.append("url="+URLEncoder.encode(url));
        if (size > 0) {
            builder.append("&w=" + size);
            builder.append("&h=" + size);
        }
        String urlfull = builder.toString();

        Log.d(TAG,urlfull);
        return urlfull;
    }

    /**
     *
     * */
    public static String imageUrlGen_yike(String url, int size)
    {
        String urlfull = url;

        if (!url.startsWith("http")) {
            urlfull = "http://img.meelive.cn/" + url;
        }

        if (size <= 0) {
            //LogUtil.i(TAG, "imageUrlGen : url = " + urlfull);
            return urlfull;
        }

        try {
            String fmt = "http://image.scale.meelive.cn/imageproxy2/dimgm/scaleImage?url=%s&w=%d&h=%d&s=80&c=0&o=0";
            String urlEncode = URLEncoder.encode(urlfull, "utf8");
            urlfull = String.format(fmt, urlEncode, size, size);

        } catch (UnsupportedEncodingException e) {
        }

        return urlfull;
    }
}

