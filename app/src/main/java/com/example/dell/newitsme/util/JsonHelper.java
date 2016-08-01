package com.example.dell.newitsme.util;

import android.util.Log;
import org.json.JSONObject;

public class JsonHelper {
	public static String TAG = "JsonHelper";

	public static JSONObject jsonFromString(String jsonString) {
		try {
			JSONObject json = new JSONObject(jsonString);
			return json;
		}catch (Exception e){
			Log.e(TAG,"toJson Exception:" + e.toString());
		}
		return null;
	}

}
