package com.yidianhulian;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import android.util.Log;

public class Api {
	public static final String BOUNDARY = "-----------AndroidFormBoundar7d4a6d158c9";

	/**
	 * 
	 * @param api
	 * @param queryStr
	 * @param files 如果queryStr中有某项是上传文件，则放该项的名字
	 * @return
	 */
	public static JSONObject post(String api, Map<String, String> queryStr) {
		return Api.post(api, queryStr, null);
	}
	
	public static JSONObject post(String api, Map<String, String> queryStr,
				List<String> files) {
		JSONObject json = null;
		boolean hasFile = false;
		byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();// 定义最后数据分隔线
		if (files != null) {
			hasFile = true;
		}
		StringBuffer contentBuffer = new StringBuffer();
		try {
			URL postUrl = new URL(api);

			queryStr.put("", "");//如果拼成a=b&c=d会导致a=b不能传到服务器上，所以这里在前面多加一个，原因未知
			
			// 打开连接
			HttpURLConnection connection = (HttpURLConnection) postUrl
					.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);
			if (hasFile) {
				connection.setRequestProperty("Content-type",
						"multipart/form-data; boundary=" + BOUNDARY);
				connection.setRequestProperty("Charset", "UTF-8");
			} else {
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
			}

			connection.connect();
			DataOutputStream out = new DataOutputStream(connection.getOutputStream());

			StringBuffer buffer = new StringBuffer();
			Iterator<Entry<String, String>> iterator = queryStr.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				if (hasFile && files.contains(entry.getKey())) {// 文件单独处理
					continue;
				}
				String value = entry.getValue();
				

				if (hasFile) {
					buffer.append("--");
					buffer.append(BOUNDARY);
					buffer.append("\r\n");
					buffer.append("Content-Disposition: form-data; name=\"");
					buffer.append(entry.getKey());

					buffer.append("\"\r\n\r\n");
					buffer.append(value);
					buffer.append("\r\n");
				} else {
					value = value != null ? URLEncoder.encode(value, "utf-8") : "";
					buffer.append(entry.getKey()).append("=").append(value).append("&");
				}
			}
			Log.d("post", api);
			Log.d("post-data", buffer.toString());
			if(hasFile){
				out.writeUTF(buffer.toString());
			}else{
				out.writeBytes(buffer.toString());
			}

			// 处理上传文件
			if (hasFile) {
				for (int i = 0; i < files.size(); i++) {
					String fname = queryStr.get(files.get(i));
					File file = new File(fname);
					StringBuilder sb = new StringBuilder();
					sb.append("--");
					sb.append(BOUNDARY);
					sb.append("\r\n");
					sb.append("Content-Disposition: form-data; name=\"");
					sb.append(files.get(i));
					sb.append("\"; filename=\"");
					sb.append(file.getName());
					sb.append("\"\r\n");
					sb.append("Content-Type: application/octet-stream\r\n\r\n");

					out.write(sb.toString().getBytes());
					DataInputStream in = new DataInputStream(new FileInputStream(file));
					int bytes = 0;
					byte[] bufferOut = new byte[1024];
					while ((bytes = in.read(bufferOut)) != -1) {
						out.write(bufferOut, 0, bytes);
					}
					out.write("\r\n".getBytes());
					in.close();
					
					//文件上传后删除之
					//file.delete();
				}
				out.write(end_data);
			}

			out.flush();
			out.close(); // flush and close

			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						connection.getInputStream(), "utf-8"));
				String inputLine = null;
				
				while ((inputLine = reader.readLine()) != null) {
					contentBuffer.append(inputLine);
				}
			}catch(Exception e){
				e.printStackTrace();
			}finally {
				connection.disconnect();
			}
			json = new JSONObject(contentBuffer.toString());
		} catch (Exception e) {
			Log.d("get-api-exception", contentBuffer.toString());
			e.printStackTrace();
		}
		return json;
	}

	public static JSONObject get(String api, Map<String, String> queryStr) {
		java.net.URL url;
		JSONObject json = null;
		StringBuffer contentBuffer = new StringBuffer();
		try {
			StringBuffer buffer = new StringBuffer();
			Iterator<Entry<String, String>> iterator = queryStr.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, String> entry = iterator.next();
				String value = entry.getValue();
				if (value != null) {
					value = URLEncoder.encode(value, "utf-8");
				} else {
					value = "";
				}
				buffer.append(entry.getKey()).append("=").append(value)
						.append("&");
			}

			url = new java.net.URL(api + "?" + buffer);
			Log.d("get", url.toString());
			java.net.URLConnection conn = url.openConnection();
			conn.connect();

			java.io.InputStream is = conn.getInputStream();
			java.io.BufferedReader reader = new java.io.BufferedReader(
					new java.io.InputStreamReader(is, "UTF-8"));
			String inputLine = null;
			while ((inputLine = reader.readLine()) != null) {
				contentBuffer.append(inputLine);
			}
			is.close();

			json = new JSONObject(contentBuffer.toString());

		} catch (Exception e) {
			Log.d("get-api-exception", contentBuffer.toString());
			e.printStackTrace();
		}
		return json;
	}

}
