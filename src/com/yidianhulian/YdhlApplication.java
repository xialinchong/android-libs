package com.yidianhulian;


import org.json.JSONObject;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class YdhlApplication extends Application {
	
    private static YdhlApplication mInstance = null;
  

	private JSONObject user;
	
	public int  speakerMode = 0;
	

	@Override
    public void onCreate() {
	    super.onCreate();
	    if( !  isNetworkAvailable()){
	    	Toast.makeText(this, "网络不可用", Toast.LENGTH_LONG).show();
	    }
		
	}

	@Override
	//退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
	public void onTerminate() {
	   
		super.onTerminate();
	}
	
	
	public static YdhlApplication getInstance() {
		return mInstance;
	}

	public Boolean isNetworkAvailable() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
//		NetworkInfo mobNetInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE ); //3G
		if (info == null){
			return false;
		}
		if( ! info.isConnected()) {
			return false;
		}
		if (info.isRoaming()) {
			// here is the roaming option you can change it if you want to
			// disable internet while roaming, just return false
		}
		return true;
	}
	
}