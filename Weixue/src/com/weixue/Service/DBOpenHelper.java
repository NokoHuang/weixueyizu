package com.weixue.Service;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper {
	private static final String DBNAME = "weixue.db";
	private static final int VERSION = 1;
	SQLiteDatabase db;

	public DBOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS filedownlog (id integer primary key autoincrement, downpath varchar(100), threadid INTEGER, downlength INTEGER)");
		db.execSQL("CREATE TABLE IF NOT EXISTS nofinishfile (downpath varchar(255),filelength INTEGER,downloadlength INTEGER)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS filedownlog");
		db.execSQL("DROP TABLE IF EXISTS nofinishfile");
		onCreate(db);
	}

	/**
	 * 给参数查询
	 * 
	 * @param table
	 *            表名
	 * @param columns
	 *            你想查询的内容
	 * @param selection
	 *            where？
	 * @param selectionArgs
	 *            where参数
	 * @param groupBy
	 *            组
	 * @param having
	 *            判断
	 * @param orderBy
	 *            排序
	 * @return
	 */
	public Cursor Query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy) {
		db = getReadableDatabase();
		Cursor c = db.query(table, columns, selection, selectionArgs, groupBy,
				having, orderBy);
		return c;
	}
}
