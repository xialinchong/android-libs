package com.yidianhulian;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;


public class CallApiTask extends AsyncTask<Void, Void, JSONObject> {
	private int what;
	private String title;
	private CallApiListener listener;
	private boolean hasLoadingDlg;
	
	/**
	 * 调用api，并打车加载对话框
	 * 
	 * @param what
	 * @param title
	 * @param listener
	 */
	public static  void doCallApi(int what, String title, CallApiListener listener){
		new CallApiTask(what, title, listener).execute();
	}
	
	/**
	 * 调用api，但没有加载对话框，后台默默处理
	 * @param what
	 * @param listener
	 */
	
	public static  void doCallApi(int what, CallApiListener listener){
		new CallApiTask(what, listener).execute();
	}
	
	public CallApiTask(int what, String title, CallApiListener listener){
		this.what 			= what;
		this.title 			= title;
		this.hasLoadingDlg = true;
		this.listener 	= listener;
	}
	
	public CallApiTask(int what, CallApiListener listener){
		this.what 			= what;
		this.hasLoadingDlg = false;
		this.listener 	= listener;
	}
	
	@Override
	protected void onPreExecute() {
		if ( !(listener instanceof Activity)) {
			return;
		}
		Activity activity = (Activity)listener;
		if(hasLoadingDlg && !activity.isFinishing()){
			listener.getProgressDialog(this.title).show();
		}
	}

	@Override
	protected JSONObject doInBackground(Void... params) {
		return listener.callApi(what);
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		if ( (listener instanceof Activity)) {
			if(((Activity)listener).isFinishing())return;
		}
		
		if(hasLoadingDlg){
			listener.getProgressDialog(this.title).dismiss();
		}
		
		JSONObject error = new JSONObject();
		
		try {
			error.put("success", false);
			error.put("errorcode", 0);
			error.put("msg", "网络不可用，请检查网络");
			error.put("data", null);
			if(result==null || !result.getBoolean("success")){
				listener.onCallApiFail(what, result==null ? error : result);
				return;
			}
			if(result!=null && result.getBoolean("success")){
				listener.onCallApiSuccess(what, result);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			listener.onCallApiFail(what, result==null ? error  : result);
		}
		this.cancel(true);
	}
	
	public interface CallApiListener{
		public abstract Dialog getProgressDialog(String title);

		/**
		 * 调用接口，返回json对象，该方法会在Task线程中调用，<b><font color="red">所以不要在该方法中操作UI上的东西</font></b>
		 * what 一个activity调用多个api时what用于区分是那个api
		 * @return
		 */
		public abstract JSONObject callApi(int what);

		/**
		 * 接口调用成功的回调
		 *  what 一个activity调用多个api时what用于区分是那个api
		 *  
		 * @param result JSONObject 服务返回的完整json结果
		 * 
		 */
		public abstract void onCallApiSuccess(int what, JSONObject result);
		/**
		 * 接口调用失败的回调
		 *  what 一个activity调用多个api时what用于区分是那个api
		 *  
		 * @param result JSONObject 服务返回的完整json结果
		 */
		public abstract void onCallApiFail(int what, JSONObject result);
	}
}