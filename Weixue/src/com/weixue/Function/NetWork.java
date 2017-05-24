package com.weixue.Function;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 
 * @author chenjunjie
 * 
 */
public class NetWork {
	private static final int TIME_OUT = 50 * 1000;

	/**
	 * 默认超时时间为5秒
	 * 
	 * @param requestUrl
	 *            请求的url
	 * @param encoding
	 *            编码方式
	 * @return 成功返回结果，否则返回null
	 * */
	public static String postData(String requestUrl, String encoding)
			throws Exception {
		HttpPost request_post = new HttpPost(requestUrl);
		HttpClient client = new DefaultHttpClient();
		// 设置参数
		HttpParams http_params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(http_params, TIME_OUT);
		HttpConnectionParams.setSoTimeout(http_params, TIME_OUT);

		HttpResponse response = client.execute(request_post);
		if (response.getStatusLine().getStatusCode() == 200) {
			return EntityUtils.toString(response.getEntity(), encoding);
		}
		return null;
	}

	/**
	 * @param requestUrl
	 *            请求的url
	 * @param encoding
	 *            编码方式
	 * @param time_out
	 *            请求超时时间
	 * @return 成功返回结果，否则返回null
	 * */
	public static String postData(String requestUrl, String encoding,
			int time_out) throws Exception {
		HttpPost request_post = new HttpPost(requestUrl);
		HttpClient client = new DefaultHttpClient();
		// 设置参数
		HttpParams http_params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(http_params, time_out);
		HttpConnectionParams.setSoTimeout(http_params, time_out);

		HttpResponse response = client.execute(request_post);
		if (response.getStatusLine().getStatusCode() == 200) {
			return EntityUtils.toString(response.getEntity(), encoding);
		}
		return null;
	}

	/**
	 * 请求方式为GET
	 * 
	 * @param requestUrl
	 *            请求的url
	 * @return 返回请求后获取的流
	 * */
	public static InputStream returnStream(String requestUrl) throws Exception {
		InputStream in = null;

		URL url = new URL(requestUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(TIME_OUT);
		con.setReadTimeout(TIME_OUT);
		con.setRequestMethod("GET");

		if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
			in = con.getInputStream();
		}

		return in;
	}

	/**
	 * 请求方式为GET
	 * 
	 * @param requestUrl
	 *            请求的url
	 * @return 字符串结果
	 * @throws Exception
	 */
	public static String getData(String requestUrl) throws Exception {
		InputStream in = null;
		String result = null;
		URL url = new URL(requestUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(TIME_OUT);
		con.setReadTimeout(TIME_OUT);
		con.setRequestMethod("GET");
		if (con.getResponseCode() == 200) {
			in = con.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuilder sb = new StringBuilder();
			String readContent = "";
			while ((readContent = br.readLine()) != null) {
				sb = sb.append(readContent);
			}
			return sb.toString();
		}
		return result;

	}

	/**
	 * 请求方式为GET
	 * 
	 * @param requestUrl
	 *            请求的url
	 * @return 返回请求后HttpURLConnection对象
	 * */
	public static HttpURLConnection returnHttpURLConnection(String requestUrl)
			throws Exception {

		URL url = new URL(requestUrl);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setConnectTimeout(TIME_OUT);
		con.setRequestMethod("GET");
		if (con.getResponseCode() == 200) {
			return con;
		}

		return null;
	}

	private static NetworkInfo info;
	private static ConnectivityManager connectivityManager;

	/**
	 * 判断当前是否有网络连接
	 * 
	 * @param context
	 * @return 有连接返回true
	 */
	public static boolean hasNetWorkConnect(Activity activity) {
		connectivityManager = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connectivityManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			return true;
		}
		return false;
	}

	public static String httpPost(String url, List<NameValuePair> params)
			throws Exception {
		HttpPost httpRequest = new HttpPost(url);

		/* 添加请求参数到请求对象 */
		httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		HttpParams httpParameters = new BasicHttpParams();
		// 设置请求超时
		int timeoutConnection = 5 * 1000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		// 设置响应超时
		int timeoutSocket = 10 * 1000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		// 发送请求并获取反馈
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse httpResponse = httpClient.execute(httpRequest);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {// 成功响应
			return EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
		}
		return "";
	}

}
