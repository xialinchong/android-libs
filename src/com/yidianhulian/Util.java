package com.yidianhulian;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Util {

	@SuppressLint("UseValueOf")
	public static void downFile(final ProgressDialog proBar, final String url, final Handler myHandler, final Activity activity) {
		proBar.show();
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();

					if (length <= 0) {
						proBar.cancel();
					}

					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						fileOutputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory()+ "/weinuode.apk"));

						byte[] buf = new byte[1024];
						int ch = -1;
						float count = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							count += ch;
							proBar.setProgress((new Double(Math.ceil((count / length) * 100))).intValue());
						}

					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}

					myHandler.post(new Runnable() {
						public void run() {
							proBar.cancel();
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory()+ "/weinuode.apk")),"application/vnd.android.package-archive");
							activity.startActivity(intent);
						}
					});

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}.start();

	}
	public static String getTakeTypeZH(String takeType){
		String takeTypeZH = "打车";
		if( "bespeak".equals(takeType) ){
			takeTypeZH = "预约";
		}else if( "pool".equals(takeType) ){
			takeTypeZH = "拼车";
		}
		return takeTypeZH;
	}
	
	/**
	 * md5加密
	 * 
	 * @param string
	 * @return
	 */
	public static String md5(String string) {
		byte[] hash;

		try {
			hash = MessageDigest.getInstance("MD5").digest(
					string.getBytes("UTF-8"));
		} catch (Exception e) {
			return "";
		}

		StringBuilder hex = new StringBuilder(hash.length * 2);

		for (byte b : hash) {
			int i = (b & 0xFF);
			if (i < 0x10)
				hex.append('0');
			hex.append(Integer.toHexString(i));
		}

		return hex.toString();
	}

	/**
	 * 注ListView 会设置tag
	 * 
	 * @param context
	 * @param listView
	 * @param emptyId
	 * @param tip
	 */
	public static void showListEmptyView(Activity context, ListView listView,
			int emptyId, String tip) {
		ViewStub noContent = (ViewStub) listView.getTag();
		if (noContent == null) {
			noContent = (ViewStub) context.findViewById(emptyId);
			noContent.inflate();
			listView.setTag(noContent);
		}

		TextView noContentDesc = (TextView) context
				.findViewById(R.id.nocontent_desc);
		noContentDesc.setText(tip);

		listView.setEmptyView(noContent);
	}

	public static void setListViewHeightBasedOnChildren(ListView listView,
			int addtionalHeight) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			// pre-condition
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1))
				+ addtionalHeight;
		listView.setLayoutParams(params);
	}

	public static void playButtonVoice(Context context){
		final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

		
		     float streamVolumeMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);       
		     
		SoundPool 	soundPool= new SoundPool(10,AudioManager.STREAM_SYSTEM,5);
		soundPool.load(context,R.raw.button,1);
		soundPool.play(1,streamVolumeMax, streamVolumeMax, 0, 0, 1);
	}
	

	public static int strtoInt(String str) {
		if (str == null)
			return 0;
		if ("".equals(str.trim()))
			return 0;
		return Integer.valueOf(str);
	}

	public static HashMap<String, String> jsonToMap(JSONObject object) {
		HashMap<String, String> map = new HashMap<String, String>();
		if (object == null)
			return map;
		Iterator<?> keys = object.keys();
		while (keys.hasNext()) {
			String string = (String) keys.next();
			map.put(string, Util.getJSONStringValue(object, string));
		}

		return map;
	}

	public static Double getJSONDoubleValue(JSONObject json, String name) {
		if (json == null)
			return 0d;

		try {
			if (!json.has(name)) {
				return 0d;
			}
			return json.getDouble(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0d;
	}

	/**
	 * 取得json对象，对象不存在返回null
	 * 
	 * @param json
	 * @param name
	 * @return
	 */
	public static JSONObject getJSONObjectValue(JSONObject json, String name) {
		if (json == null)
			return null;

		try {
			if (!json.has(name)) {
				return null;
			}
			return json.getJSONObject(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 取得JSONArray对象，对象不存在返回空的JSONArray
	 * 
	 * @param json
	 * @param name
	 * @return
	 */
	public static JSONArray getJSONArrayValue(JSONObject json, String name) {
		if (json == null)
			return new JSONArray();

		try {
			if (!json.has(name)) {
				return new JSONArray();
			}
			return json.getJSONArray(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONArray();
	}

	/**
	 * 返回json中的int值，不存在返回0
	 * 
	 * @param json
	 * @param name
	 * @return
	 */
	public static Integer getJSONIntValue(JSONObject json, String name) {
		if (json == null)
			return 0;

		try {
			if (!json.has(name)) {
				return 0;
			}
			return json.getInt(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getJSONStringValue(JSONObject json, String name) {
		if (json == null)
			return "";

		try {
			if (!json.has(name)) {
				return "";
			}
			return json.getString(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static Bitmap convertViewToBitmap(View view) {
		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.buildDrawingCache();
		Bitmap bitmap = view.getDrawingCache();

		return bitmap;
	}

	/**
	 * 判断给定的司机是否是给定乘客的（当前订单）匹配司机
	 * 
	 * @param driverId
	 * @return
	 */
	public static boolean isPairDriver(JSONObject order, String driverId) {
		if (order == null)
			return false;
		if (driverId == null || "".equals(driverId))
			return false;
		
		return driverId.equals(Util.getJSONStringValue(order, "driver_id"));
	}
	
}
