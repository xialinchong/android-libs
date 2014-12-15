package com.yidianhulian;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class MyNotification {

	private List<Integer> openedNotification = new ArrayList<Integer>();
	private static MyNotification mInstance = null;
	
	public static MyNotification getNotification() {
		if (mInstance==null) {
			mInstance = new MyNotification();
		}
		return mInstance;
	}
	
	private MyNotification(){
		
	}
	
	//对话框关闭
	public void removeNotification(int notificationId){
		openedNotification.remove(Integer.valueOf(notificationId));
	}
	
	/**
	 * 显示通知栏
	 * 
	 * @param activity 调用者activity
	 * @param openIntent 点击通知栏显示的activity的intent
	 * @param notificationId 通知栏id
	 * @param icon 通知栏icon
	 * @param notificationText 通知文字
	 * @param title 下拉通知栏时显示的标题
	 * @param content 下拉通知栏时显示的内容
	 */
	public void showNotification(Activity activity, Intent openIntent, int notificationId, int icon,String notificationText,String title,String content){
		//return;
		if(activity.isFinishing()){
			return;
		}
		
		if(openedNotification.contains(notificationId))return;//opened
		
    	//Notification管理器
		//后面的参数分别是显示在顶部通知栏的小图标，小图标旁的文字（短暂显示，自动消失）系统当前时间（不明白这个有什么用）
    	Notification notification=new Notification(icon,notificationText,System.currentTimeMillis());

    	
    	//这是设置通知是否同时播放声音或振动，声音为Notification.DEFAULT_SOUND
    	//振动为Notification.DEFAULT_VIBRATE;
    	//Light为Notification.DEFAULT_LIGHTS，在我的Milestone上好像没什么反应
    	//全部为Notification.DEFAULT_ALL
    	//如果是振动或者全部，必须在AndroidManifest.xml加入振动权限
    	notification.defaults=Notification.DEFAULT_ALL; 
    	notification.flags = Notification.FLAG_AUTO_CANCEL;

    	PendingIntent pt=PendingIntent.getActivity(activity, 0, openIntent, 0);
    	notification.setLatestEventInfo(activity,title,content,pt);
    	NotificationManager nm=(NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE);
    	nm.notify(notificationId, notification);
    	
    	openedNotification.add(notificationId);
    }
}
