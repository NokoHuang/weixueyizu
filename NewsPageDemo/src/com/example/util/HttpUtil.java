package com.example.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtil {
	public static HttpClient httpClient;


	/**
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String PostRequest(final String url) throws Exception{
		httpClient=new DefaultHttpClient();
		FutureTask<String>  task=new FutureTask<String>(
				new Callable<String>(){
					public String call() throws Exception{
						HttpPost post=new HttpPost(url);
						HttpResponse  httpResponse=httpClient.execute(post);

						if(httpResponse.getStatusLine().getStatusCode()==200){
							String result=EntityUtils.toString(httpResponse.getEntity(),"GBK");
							return result;
						}
						return null;
					}
				});
		new Thread(task).start();
		return task.get();
	}

	/**
	 * @param Url
	 * @param Params
	 * @return
	 * @throws Exception
	 */
	public static String PostRequestWithParams(final String Url,final Map<String,String> Params) throws Exception{
		FutureTask<String> task=new FutureTask<String>(
				new Callable<String>(){
					public String call() throws Exception{
						HttpPost post=new HttpPost(Url);

						List<NameValuePair> params=new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("name",Params.get("username")));
						params.add(new BasicNameValuePair("pwd",Params.get("password")));

						post.setEntity(new UrlEncodedFormEntity(params,"utf-8"));

						System.out.println(Url+" "+params.toString());
						HttpResponse httpResponse=httpClient.execute(post);

						if(httpResponse.getStatusLine().getStatusCode()==200){
							String result=EntityUtils.toString(httpResponse.getEntity());
							return result;
						}
						return null;
					}
				});
		new Thread(task).start();
		return task.get();
	}


	/**
	 * 携带参数发送Post请求
	 * @param url
	 * @param params
	 * @return
	 */
	public static String httpPost(String url, List<NameValuePair> params) {
		try {
			HttpPost httpRequest = new HttpPost(url);

			/* 添加请求参数到请求对象 */
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpParams httpParameters = new BasicHttpParams();
			// 设置请求超时
			int timeoutConnection = 5 * 1000;
			HttpConnectionParams.setConnectionTimeout(httpParameters,
					timeoutConnection);
			// 设置响应超时
			int timeoutSocket = 5 * 1000;
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
			// 发送请求并获取反馈
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			if (httpResponse.getStatusLine().getStatusCode() == 200) {// 成功响应
				return EntityUtils.toString(httpResponse.getEntity(),
						HTTP.UTF_8);
			}

		} catch (Exception e) {
			for (StackTraceElement s : e.getStackTrace())
				Log.d("Exception", s.toString());
			Log.d("Exception", e.getLocalizedMessage());

			return null;
		}
		return null;
	}

	/**
	 * 发送get请求返回字符串
	 *
	 * @return
	 * @throws Exception
	 * @throws Exception
	 */
	public static String sendRequestString(String urlStr) throws Exception {
		URL url = new URL(urlStr);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setReadTimeout(8000);
		connection.setRequestMethod("GET");

		int responseCode = connection.getResponseCode();
		// 成功响应
		if (responseCode == 200) {
			InputStream is = connection.getInputStream();
			// 得到返回的输入流
			byte[] buf = GettoByteArray(is);
			is.close();
			return new String(buf, "UTF-8");
		}
		return "";
	}

	/**
	 * 输入流转字节数组
	 *
	 * @param is
	 * @return
	 */
	public static byte[] GettoByteArray(InputStream is) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		BufferedInputStream bis = new BufferedInputStream(is);
		int len = 0;
		try {
			while ((len = bis.read(buf)) != -1) {
				bos.write(buf, 0, len);
			}
			bos.flush();
			byte[] b = bos.toByteArray();
			bos.close();
			bis.close();
			return b;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}





