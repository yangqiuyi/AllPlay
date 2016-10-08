package com.streampublisher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.util.log.LogUtil;


public class WifiReceiver extends BroadcastReceiver {
	interface Listener {
		public abstract void onConnected(WifiReceiver wifi);
		public abstract void onClosed(WifiReceiver wifi);
	}
	private Listener listener_ = null;
	
	public WifiReceiver(Listener listener) {
		listener_ = listener;
	}

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            Log.w("WifiReceiver", "Have Wifi Connection");
        } else {
            LogUtil.d("WifiReceiver", "Don't have Wifi Connection");
        }
    }
}
