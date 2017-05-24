package com.weixue.Function;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import com.weixue.Utils.Constants_Url;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class AsyncImageLoader {

	// 解决大量图片下载溢出，使用SoftRefence<Drawable>
	private HashMap<String, SoftReference<Drawable>> imageCache;
	private String img_tosdcare_fileName;

	public String getImg_tosdcare_fileName() {
		return img_tosdcare_fileName;
	}

	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageView imgView,final ImageCallback imageCallback) {
		// 如果在缓存中找到图片
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				//System.out.println("存在drawable");
				return drawable;
			}
		}
		//System.out.println("不存在drawable");
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((Drawable) message.obj, imageUrl,imgView);
			}
		};

		new Thread() {
			@Override
			public void run() {
				Bitmap bitmap = GetUrlToBitmap(imageUrl);// 下载图片

				if (bitmap != null) {
					Drawable drawable = new BitmapDrawable(bitmap);
					imageCache.put(imageUrl, new SoftReference<Drawable>(
							drawable));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);

					img_tosdcare_fileName = GetImgName(imageUrl);// 获取保存图片的名字，方便开启飞行模式时候能从SD卡里取出该照片
					savePic(bitmap, img_tosdcare_fileName);// 将图片缓存到SD卡

				}
			}

		}.start();
		return null;
	}

	// 根据URL生成Drawable图片
	public static Drawable loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Drawable d = Drawable.createFromStream(i, "src");
		return d;
	}

	public interface ImageCallback {
		public void imageLoaded(Drawable imageDrawable, String imageUrl,ImageView imgView);
	}

	// 根据URL下载图片
	private Bitmap GetUrlToBitmap(String picUrl) {

		URL url = null;
		URLConnection conn = null;
		InputStream in = null;
		Bitmap itemBitmap = null;

		try {

			url = new URL(picUrl);
			conn = url.openConnection();
			conn.setConnectTimeout(10 * 1000);
			conn.connect();
			in = conn.getInputStream();
			itemBitmap = BitmapFactory.decodeStream(in);

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (url != null) {

				url = null;

			}

			if (conn != null) {

				conn = null;

			}

			if (in != null) {

				try {

					in.close();

				} catch (IOException e) {

					e.printStackTrace();

				}

			}

		}

		return itemBitmap;

	}

	private final static String CACHE = Constants_Url.PIC_CACHE_PATH;

	/**
	 * 保存图片的方法 保存到sdcard
	 * 
	 * @throws IOException
	 */
	public void savePic(Bitmap b, String imgName) {

		String filePath = isExistsFilePath();

		//System.out.println("filePath-->" + filePath);

		FileOutputStream fos = null;

		String imageName = imgName;

		File file = new File(filePath, imageName);

		if (file.exists()) {

			Log.i(imageName, "is Exist!!!");

		} else {
			try {
				fos = new FileOutputStream(file);
				if (null != fos) {
					b.compress(Bitmap.CompressFormat.JPEG, 90, fos);
					fos.flush();
					fos.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取SD卡的路径 通常SD卡中sdCard就是这个目录
	 * 
	 * @return SDPath
	 */
	public static String getSDPath() {
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存�?
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取根目录
			//System.out.println(Environment.getExternalStorageDirectory());
		} else {
			Log.e("ERROR", "没有找到SD卡");
		}
		if(sdDir==null){
			return null;
		}
		return sdDir.toString();

	}

	/**
	 * 获取缓存文件夹路径，如果存在就遍历，否则则创建文件夹
	 * 
	 * @return filePath
	 */
	private String isExistsFilePath() {
		String filePath = CACHE;
		File file = new File(filePath);
		// 如果文件不存在就遍历
		if (!file.exists()) {
			file.mkdirs();
		}
		return filePath;
	}

	// 根据URL获取图片名称
	public String GetImgName(String fileName) {
		int a = fileName.lastIndexOf("/");
		String s = fileName.substring(a + 1, fileName.length());
		return s;
	}

	public boolean ishavaImg(String fileName) {
		String filePath = isExistsFilePath();
		File file = new File(filePath, fileName);
		if (file.exists()) {
			return true;
		} else {
			return false;
		}
	}

	// 根据文件名字返回图片
	public Drawable GetImgByfileName(String fileName) {
		String filePath = isExistsFilePath() + "/" + fileName;
		BitmapDrawable bd = null;
		File file = new File(filePath);
		if (file.exists()) {
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, opts);

			opts.inSampleSize = CompressImg.computeSampleSize(opts, -1, 100*100);
			opts.inJustDecodeBounds = false;
			try {
				Bitmap bitmap = BitmapFactory.decodeFile(filePath, opts);				
				bd = new BitmapDrawable(bitmap);
			    } catch (OutOfMemoryError err) {
			    	System.out.println("内存溢 出!");
			}
		
			
		} else {
			return null;
		}
		return bd;
	}

}
