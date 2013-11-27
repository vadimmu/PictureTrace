package ro.vadim.picturetrails.database;

import ro.vadim.picturetrails.utils.Picture;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper{
	
	private static final String LOG = "DatabaseHelper";
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "pictureTrace";
	private static final String TABLE_PICTURES = "pictures";
	
	
	// TABLE_PICTURES - column names
	private static final String KEY_ID = "picture_id";
	private static final String KEY_URL = "url";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_TIMESTAMP = "timestamp";
	
	private static final String CREATE_TABLE_PICTURES = "CREATE TABLE "
			+ TABLE_PICTURES + " ( " 
			+ KEY_ID + " INTEGER PRIMARY KEY, "
			+ KEY_URL + " TEXT UNIQUE, "
			+ KEY_DESCRIPTION + " TEXT, "
			+ KEY_LATITUDE + " TEXT, "
			+ KEY_LONGITUDE + " TEXT, " 
			+ KEY_TIMESTAMP + " TEXT " + " ) ";
	
	
	
	
	
	
	
	public DatabaseHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
		
	
	
	
	
	
	
	
	
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_PICTURES);		
	}
	

	@Override	
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+TABLE_PICTURES);		
		onCreate(db);
	}
	
	

	
	
	
	
	public long insertPicture(Picture picture){
		
		if(picture == null)
			return -1;
		
		SQLiteDatabase db = this.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(KEY_URL, picture.getUrl());
		values.put(KEY_DESCRIPTION, picture.getDescription());
		values.put(KEY_LATITUDE, picture.getLatitude());
		values.put(KEY_LONGITUDE, picture.getLongitude());
		values.put(KEY_TIMESTAMP, picture.getTimestamp().getTime());
		
		return db.insert(TABLE_PICTURES, null, values);
	}
	
	
	
	
	public static Picture getPictureFromCursor(Cursor cursor){
		
		Picture picture = new Picture(
				cursor.getString(cursor.getColumnIndex(KEY_URL)), 
				cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)), 
				Double.valueOf(cursor.getString(cursor.getColumnIndex(KEY_LATITUDE))), 
				Double.valueOf(cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE))));
		
		
		return picture;
		
	}
	
	
	public Picture getPictureByID(long pictureID){
		
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM "+TABLE_PICTURES + " WHERE "
				+ KEY_ID + " = " + String.valueOf(pictureID);
		
		Log.i(LOG, selectQuery);
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor == null)
			return null;
		
		cursor.moveToFirst();				
		return getPictureFromCursor(cursor);				
	}
	
	
	public Picture getLastPicture(){
		
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM "+TABLE_PICTURES + " ORDER BY "
				+ KEY_ID + " DESC ";
		
		Log.i(LOG, selectQuery);
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor == null)
			return null;
		
		cursor.moveToFirst();		
		return getPictureFromCursor(cursor);		
	}
	
	
	public Picture[] getPicturesByCoordinates(double minLatitude, double minLongitude, double maxLatitude, double maxLongitude){
		
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM "+TABLE_PICTURES + " WHERE " 
		+ "(" + KEY_LATITUDE + " BETWEEN " + 
				String.valueOf(minLatitude) + " AND " + 
				String.valueOf(maxLatitude) + ")"
		+ " AND "
		+ "(" + KEY_LONGITUDE + " BETWEEN " + 
				String.valueOf(minLongitude) + " AND " + 
				String.valueOf(maxLongitude) + ")";
		
		Log.i(LOG, selectQuery);
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor == null)
			return null;
		
		Picture[] pictures = new Picture[cursor.getCount()];
		int index = 0;
		
		while(!cursor.isAfterLast()){
			pictures[index] = getPictureFromCursor(cursor);						
			++index;
			cursor.move(1);	
		}
		
		return pictures;
	}
	
	public Picture[] getAllPictures(){
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM "+TABLE_PICTURES + " ORDER BY "
				+ KEY_ID + " DESC ";
		
		Log.i(LOG, selectQuery);
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		if(cursor == null)
			return null;
		
		Picture[] pictures = new Picture[cursor.getCount()];
		int index = 0;
		
		while(!cursor.isAfterLast()){
			pictures[index] = getPictureFromCursor(cursor);						
			++index;
			cursor.move(1);	
		}
		
		return pictures;
	}
	
	public Cursor getAllPictures_Cursor(){
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = "SELECT * FROM "+TABLE_PICTURES + " ORDER BY "
				+ KEY_ID + " DESC ";
		
		Log.i(LOG, selectQuery);
		return db.rawQuery(selectQuery, null);
	}
	
	

}
