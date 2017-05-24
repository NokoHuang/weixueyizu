package com.weixue.Tool;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import com.weixue.Model.ImageModel;

public class ImageDealTool {

	/**
	 * 根据宽压缩图片
	 * 
	 * @param stream
	 * @param width
	 * @return
	 * @throws IOException
	 */
	public static ImageModel revitionImageSize(byte[] bytes, int width)
			throws IOException {
		// 4为资讯显示页面的大图
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		// 这个参数表示 新生成的图片为原始图片的几分之一。
		options.inSampleSize = options.outWidth / width;
		int mHeight = options.outHeight * width / options.outWidth;
		options.outHeight = mHeight;
		options.outWidth = width;
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
				options);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, mHeight,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		ImageModel model = new ImageModel(width, mHeight, bitmap);
		return model;
	}

	/**
	 * 根据url获取图片数组
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static byte[] getImage(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection httpURLconnection = (HttpURLConnection) url
				.openConnection();
		httpURLconnection.setRequestMethod("GET");
		httpURLconnection.setReadTimeout(10 * 1000);
		InputStream in = null;
		if (httpURLconnection.getResponseCode() == 200) {
			in = httpURLconnection.getInputStream();
			byte[] result = readStream(in);
			in.close();
			return result;

		}
		return null;
	}

	/**
	 * 将输入流转成字节数组
	 * 
	 * @param in
	 * @return
	 * @throws Exception
	 */
	private static byte[] readStream(InputStream in) throws Exception {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = in.read(buffer)) != -1) {
			outputStream.write(buffer, 0, len);
		}
		outputStream.close();
		return outputStream.toByteArray();
	}

}
