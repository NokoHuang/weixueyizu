package com.weixue.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.weixue.Model.DownloadFile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 业务bean
 * 
 */
public class FileService {
	private DBOpenHelper openHelper;

	public FileService(Context context) {
		openHelper = new DBOpenHelper(context);
	}

	/**
	 * 获取获取存在几个未完成任务
	 * 
	 * @param path
	 * @return
	 */
	public synchronized List<DownloadFile> getAllData() {
		List<DownloadFile> li = new ArrayList<DownloadFile>();
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from nofinishfile ",
				new String[] {});

		while (cursor.moveToNext()) {
			DownloadFile file = new DownloadFile();
			String downPath = cursor.getString(cursor
					.getColumnIndex("downpath"));
			String fileName = downPath
					.substring((downPath.lastIndexOf("/") + 1));
			file.setFileName(fileName);
			file.setDownLoadAddress(downPath);
			file.setDownloadSize(cursor.getInt(cursor
					.getColumnIndex("downloadlength")));
			file.setFileSize(cursor.getInt(cursor.getColumnIndex("filelength")));
			li.add(file);
		}
		cursor.close();
		db.close();
		return li;
	}

	public synchronized boolean isTaskNotFinish(String path) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from nofinishfile where downpath=?",
				new String[] { path });
		if (cursor.moveToNext()) {
			return true;
		}

		return false;
	}

	/**
	 * 保存未下载完成的文件下载地址
	 * 
	 * @param path
	 * @param fileLength
	 */
	public synchronized void saveNoFinishFile(String path, int fileLength) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from nofinishfile where downpath=?",
				new String[] { path });

		if (!cursor.moveToNext()) {
			db.beginTransaction();
			try {

				db.execSQL(
						"insert into nofinishfile(downpath,filelength) values(?,?)",
						new Object[] { path, fileLength });

				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}
		cursor.close();

		db.close();
	}

	/**
	 * 保存每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @param downloadLength
	 */
	public synchronized void updateNoFinishFileLength(String path,
			int downloadLength) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(
				"select * from nofinishfile where downpath=?",
				new String[] { path });

		if (cursor.moveToNext()) {
			db.beginTransaction();
			try {

				db.execSQL(
						"update nofinishfile set downloadlength=? where downpath=?",
						new Object[] { downloadLength, path });

				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}
		cursor.close();

		db.close();
	}

	/**
	 * 当文件下载完成后，删除对应的下载记录(nofinishfile表)
	 * 
	 * @param path
	 */
	public synchronized void deleteFinishFile(String path) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("delete from nofinishfile where downpath=?",
				new Object[] { path });
		db.close();
	}

	/**
	 * 获取每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @return
	 */
	public synchronized Map<Integer, Integer> getData(String path) {
		SQLiteDatabase db = openHelper.getReadableDatabase();
		Cursor cursor = db
				.rawQuery(
						"select threadid, downlength from filedownlog where downpath=?",
						new String[] { path });
		Map<Integer, Integer> data = new HashMap<Integer, Integer>();
		while (cursor.moveToNext()) {
			data.put(cursor.getInt(0), cursor.getInt(1));
		}
		cursor.close();
		db.close();
		return data;
	}

	/**
	 * 保存每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @param map
	 */
	public synchronized void save(String path, Map<Integer, Integer> map) {// int
																			// threadid,
																			// int
																			// position
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				db.execSQL(
						"insert into filedownlog(downpath, threadid, downlength) values(?,?,?)",
						new Object[] { path, entry.getKey(), entry.getValue() });
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		db.close();
	}

	/**
	 * 实时更新每条线程已经下载的文件长度
	 * 
	 * @param path
	 * @param map
	 */
	public synchronized void update(String path, int threadId, int pos) {

		SQLiteDatabase db = openHelper.getWritableDatabase();

		db.execSQL(
				"update filedownlog set downlength=? where downpath=? and threadid=?",
				new Object[] { pos, path, threadId });

		db.close();
	}

	/**
	 * 当文件下载完成后，删除对应的下载记录
	 * 
	 * @param path
	 */
	public synchronized void delete(String path) {
		SQLiteDatabase db = openHelper.getWritableDatabase();
		db.execSQL("delete from filedownlog where downpath=?",
				new Object[] { path });
		db.close();
	}

}
