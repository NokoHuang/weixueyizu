package com.weixue.Tool;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.ImageView;

/**
 * 图片加载缓存
 */
public class ImageLoader {

	private static final int CACHE_CSPSCITY = 100;
	// 超过缓存就会回到访问网络
	// 一级缓存
	private static final ConcurrentHashMap<String, SoftReference<Bitmap>> softCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			CACHE_CSPSCITY / 2);

	// 二级缓存
	private static final LinkedHashMap<String, Bitmap> hardCache = new LinkedHashMap<String, Bitmap>(
			CACHE_CSPSCITY / 2, 0.75f, true) {
		protected boolean removeEldestEntry(
				java.util.Map.Entry<String, Bitmap> eldest) {
			if (size() > CACHE_CSPSCITY) {
				softCache.put(eldest.getKey(),
						new SoftReference<Bitmap>(eldest.getValue()));
				return true;
			}
			return false;

		};
	};

	public ImageLoader(Context context) {

	}

	public void download(String url, ImageView imageView) {

		Bitmap bitmap = getBitmapFromCache(url);

		// 缓存存在
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			canclePotentrialDownload(url, imageView);
			return;
		}

		// 缓存不存在
		if (canclePotentrialDownload(url, imageView)) {
			ImageDownloadTask task = new ImageDownloadTask(imageView);

			ImageloaderDrawable drawable = new ImageloaderDrawable(task);
			imageView.setImageDrawable(drawable);

			task.execute(url);
		}
	}

	/**
	 * 判断url地址是否相同
	 * 
	 * @param url
	 * @param imageView
	 * @return
	 */
	private boolean canclePotentrialDownload(String url, ImageView imageView) {
		ImageDownloadTask task = getImageDownloadTask(imageView);

		if (null != task) {
			String downurl = task.url;
			if (null == downurl || !downurl.equals(url)) {
				task.cancel(true);
			} else {
				return false;
			}
		}
		return true;
	}

	// 根据接口加载图片
	private Bitmap downloadImage(String utl) {
		final HttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(utl);

		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {

				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					return BitmapFactory.decodeStream(inputStream);
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			getRequest.abort();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			getRequest.abort();
		} catch (Exception e) {
			getRequest.abort();
		} finally {
			if (client instanceof AndroidHttpClient) {
				((AndroidHttpClient) client).close();
			}
		}
		return null;
	}

	private Bitmap getBitmapFromCache(String url) {
		synchronized (hardCache) {
			Bitmap bitmap = hardCache.get(url);
			if (null != bitmap) {
				// 放到第一位，其他往后靠
				hardCache.remove(url);
				hardCache.put(url, bitmap);
				return bitmap;
			}
		}
		SoftReference<Bitmap> softReference = softCache.get(url);
		if (null != softReference) {
			Bitmap bitmap = softReference.get();
			if (null != bitmap)
				return bitmap;
		} else {
			softCache.remove(url);
		}
		return null;
	}

	private void addBitmapToCache(String url, Bitmap bitmap) {

		if (null != bitmap) {
			// 加锁操作
			synchronized (hardCache) {
				hardCache.put(url, bitmap);
			}
		}
	}

	class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
		private String url;
		private WeakReference<ImageView> imgVReference;

		public ImageDownloadTask(ImageView imageView) {
			imgVReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			url = params[0];
			return downloadImage(url);
		}

		@Override
		protected void onPostExecute(Bitmap result) {

			if (isCancelled()) {
				result = null;
			}

			// 把Bitmp放到缓存中
			addBitmapToCache(url, result);

			if (null != result) {
				ImageView imageView = imgVReference.get();
				if (null != imageView) {
					ImageDownloadTask task = getImageDownloadTask(imageView);
					if (this == task) {
						imageView.setImageBitmap(result);
					}
				}
			}
		}
	}

	private ImageDownloadTask getImageDownloadTask(ImageView imageView) {

		if (null != imageView) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof ImageloaderDrawable)// 防止转换异常
				return ((ImageloaderDrawable) drawable).getImageDownloadTask();
		}
		return null;
	}

	class ImageloaderDrawable extends ColorDrawable {

		private final WeakReference<ImageDownloadTask> taskReference;

		public ImageloaderDrawable(ImageDownloadTask task) {
			super(Color.BLACK);
			taskReference = new WeakReference<ImageLoader.ImageDownloadTask>(
					task);
		}

		public ImageDownloadTask getImageDownloadTask() {
			return taskReference.get();
		}
	}
}
