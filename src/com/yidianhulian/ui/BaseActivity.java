package com.yidianhulian.ui;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.yidianhulian.CallApiTask;
import com.yidianhulian.R;
import com.yidianhulian.YdhlApplication;
/**
 * 整个系统的基类
 * 1. 连接到DacheService，并接收消息、配对、gps的通知
 * 2. 实现CallApiTask.CallApiListener 负责处理数据加载时的进度对话框
 * 
 * @author leeboo
 *
 */
public abstract class BaseActivity extends Activity implements CallApiTask.CallApiListener {

	//数据加载时显示的加载框
	protected ProgressDialog progressDialog;
	protected YdhlApplication application;
	private View progressDialogView;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		application 			= (YdhlApplication)getApplication();


	}


	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	/**
	 * 调用api， 一个activity调用多个api时what用于区分是那个api, 并且显示加载对话框
	 * 
	 * @param what
	 */
	public void doCallApi(int what, String title){
		new CallApiTask(what, title, this).execute();
	}
	
	/**
	 * 调用api， 一个activity调用多个api时what用于区分是那个api, 不显示加载对话框
	 * @param what
	 */
	public void doCallApi(int what){
		new CallApiTask(what, this).execute();
	}
	
	@Override
	public Dialog getProgressDialog(String title) {
		if(progressDialog == null){
			progressDialog = ProgressDialog.show(this,"",  "请稍等...", false, false);  
		
			progressDialog.setMessage("请稍等");
			progressDialog.setIndeterminate(false);
			progressDialog.setCancelable(false);
		    LayoutInflater inflater = getLayoutInflater();

		    progressDialogView = inflater.inflate(R.layout.progress_layout, null);
			progressDialog.setContentView(progressDialogView);
			
			
		}
		progressDialog.findViewById(R.id.progress_bar).startAnimation(AnimationUtils.loadAnimation(this,R.anim.loading));
		TextView msg = (TextView)progressDialogView.findViewById(R.id.progress_message);
		msg.setText(title);
		return progressDialog;
	}


	@Override
	public JSONObject callApi(int what) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onCallApiSuccess(int what, JSONObject result) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onCallApiFail(int what, JSONObject result) {
		// TODO Auto-generated method stub
		
	}
	
}