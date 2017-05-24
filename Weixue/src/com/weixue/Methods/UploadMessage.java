package com.weixue.Methods;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.util.Log;

/**
 * 上传信息
 * 
 * @author chenjunjie
 * 
 * 
 */
public class UploadMessage {
	private static final int TIME_OUT=10*1000;
	/**
	 * 使用POST上传信息(使用第三方包)
	 * 
	 * @param requestUrl
	 *            请求的URL地址
	 * @param li_name
	 *            上传参数名
	 * @param li_content
	 *            上传参数名对应的内容
	 * @return 请求结果
	 * @throws Exception
	 */
	public static String uploadAnyMessage(String requestUrl, List<String> li_name,
			List<String> li_content) throws Exception{
		String sResponse = null;

		HttpClient httpClient = new DefaultHttpClient();

		HttpPost postRequest = new HttpPost(requestUrl);
		//设置参数
		HttpParams http_params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(http_params, TIME_OUT);
		HttpConnectionParams.setSoTimeout(http_params, TIME_OUT);
		postRequest.setParams(http_params);

		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);



		for (int i = 0; i < li_name.size(); i++) {
			ContentBody cb = new StringBody(li_content.get(i));
			reqEntity.addPart(li_name.get(i), cb);// 上传信息
		}


		postRequest.setEntity(reqEntity);

		HttpResponse response = httpClient.execute(postRequest);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "utf-8"));
		if (response.getStatusLine().getStatusCode() == 200) {
			System.out.println("成功发送");
			StringBuilder s = new StringBuilder();
			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			return s.toString();
		} else {
			System.out.println("http状态-->"
					+ response.getStatusLine().getStatusCode());
		}


		return sResponse;
	}

	/**
	 * 使用POST上传信息(使用第三方包)
	 * 
	 * @param requestUrl
	 *            请求的URL地址
	 * @param str_name
	 *            上传参数名
	 * @param str_content
	 *            上传参数名对应的内容
	 * @return 请求结果
	 * @throws Exception
	 */
	public static String uploadAnyMessage(String requestUrl, String[] str_name,
			String[] str_content) throws Exception{
		String sResponse = null;

		HttpClient httpClient = new DefaultHttpClient();

		HttpPost postRequest = new HttpPost(requestUrl);

		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);


		for (int i = 0; i < str_name.length; i++) {
			ContentBody cb = new StringBody(str_content[i]);
			reqEntity.addPart(str_name[i], cb);// 上传信息
		}



		postRequest.setEntity(reqEntity);

		HttpResponse response = httpClient.execute(postRequest);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "utf-8"));
		if (response.getStatusLine().getStatusCode() == 200) {
			System.out.println("成功发送");
			StringBuilder s = new StringBuilder();
			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			return s.toString();
		} else {
			System.out.println("http状态-->"
					+ response.getStatusLine().getStatusCode());
		}


		return sResponse;
	}



	/**
	 * post请求， parMap参数集合
	 */
	public static String post(String httpUrl, Map<String, String> parMap) throws Exception{
		// TODO Auto-generated method stub

		InputStream input = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (parMap != null && !parMap.isEmpty()) {
			Iterator<String> it = parMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				params.add(new BasicNameValuePair(key, parMap.get(key)));
			}
		}


		HttpPost request = new HttpPost(httpUrl);
		HttpParams param=request.getParams();
		HttpConnectionParams.setConnectionTimeout(param, TIME_OUT);
		HttpConnectionParams.setSoTimeout(param, TIME_OUT);
		request.setParams(param);
		HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);

		request.setEntity(entity);
		HttpResponse response = new DefaultHttpClient().execute(request);

		// 若状态值为200，则ok
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			input = response.getEntity().getContent();

			BufferedReader br=new BufferedReader(new InputStreamReader(input));
			StringBuilder sb=new StringBuilder();
			String readContent="";
			while((readContent=br.readLine())!=null){
				sb=sb.append(readContent);
			}
			return sb.toString();
		} else {
			Log.i("error", "服务器异常");
		}

		return null;
	}


	/**
	 * 使用POST上传图片
	 * 
	 * @param requestUrl
	 *            请求的URL地址
	 * @param file 上传的文件
	 * @return 请求结果
	 * @throws Exception
	 */
	public static String uploadPic(String requestUrl,File file,int uid) throws Exception{
		String sResponse = null;

		HttpClient httpClient = new DefaultHttpClient();

		HttpPost postRequest = new HttpPost(requestUrl);
		//设置参数
		HttpParams http_params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(http_params, TIME_OUT);
		HttpConnectionParams.setSoTimeout(http_params, TIME_OUT);
		postRequest.setParams(http_params);

		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);

		ContentBody sb = new StringBody(String.valueOf(uid));
		ContentBody cb = new FileBody(file);
		
		reqEntity.addPart("id", sb);// 上传信息
		reqEntity.addPart("upimg", cb);// 上传信息



		postRequest.setEntity(reqEntity);

		HttpResponse response = httpClient.execute(postRequest);
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				response.getEntity().getContent(), "utf-8"));
		if (response.getStatusLine().getStatusCode() == 200) {
			System.out.println("成功发送");
			StringBuilder s = new StringBuilder();
			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			return s.toString();
		} else {
			System.out.println("http状态-->"
					+ response.getStatusLine().getStatusCode());
		}


		return sResponse;
	}


}
