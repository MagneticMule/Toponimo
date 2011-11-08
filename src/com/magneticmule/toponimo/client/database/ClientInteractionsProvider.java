package com.magneticmule.toponimo.client.database;

import java.util.HashMap;

import com.magneticmule.toponimo.client.Constants;
import com.magneticmule.toponimo.client.ToponimoApplication;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class ClientInteractionsProvider extends ContentProvider {

	private static final String							TAG					= "ClientInteractionsProvider";
	private static Uri											CONTENT_URI	= Constants.INTERACTIONS_URI;
	private Context													context;
	ToponimoApplication											application;
	// Instantiate custom DBHelper;
	private DBHelper												dbHelper;
	private static UriMatcher								uriMatcher;

	// words database projection map
	private static HashMap<String, String>	interactionsProjectionMap;

	/**
	 * Start by getting a reference to the global application context. Instantiate
	 * a new DBHelper object which will open the database if exists else create
	 * one.
	 */
	@Override
	public boolean onCreate() {
		context = getContext();
		dbHelper = new DBHelper(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
		try {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
		} catch (SQLiteException s) {
			Log.d(TAG, s.toString());
		}
		return true;
	}

	/**
	 * Populate the uriMatcher
	 */
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

		// Register pattern for multiple words
		uriMatcher.addURI(Constants.INTERACTIONS_AUTHORITY, "interactions", Constants.ALL_ROWS);

		// Register pattern for Single book
		uriMatcher.addURI(Constants.AUTHORITY, "interactions/*", Constants.SINGLE_ROW);

		interactionsProjectionMap = new HashMap<String, String>();
		interactionsProjectionMap.put(Constants.KEY_ROW_ID, Constants.KEY_ROW_ID);
		interactionsProjectionMap.put(Constants.KEY_INTERACTION, Constants.KEY_INTERACTION);
		interactionsProjectionMap.put(Constants.KEY_INTERACTION_TIME, Constants.KEY_INTERACTION_TIME);
		interactionsProjectionMap.put(Constants.KEY_INTERACTION_SCREEN, Constants.KEY_INTERACTION_SCREEN);
		interactionsProjectionMap.put(Constants.KEY_WORD_LOCATION_LAT, Constants.KEY_WORD_LOCATION_LAT);
		interactionsProjectionMap.put(Constants.KEY_WORD_LOCATION_LNG, Constants.KEY_WORD_LOCATION_LNG);
		
	}

	/**
	 * 
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		ContentValues contentValues;
		if (values != null) {
			contentValues = new ContentValues(values);
		} else {
			contentValues = new ContentValues();
		}

		if (values.containsKey(Constants.KEY_INTERACTION) == false) {
			throw new SQLException("Failed to add to the Database due to empty key interaction" + uri);
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		long rowID = db.insert(Constants.DATABASE_TABLE_INTERACTIONS, null, contentValues);

		if (rowID > 0) {
			Uri interactionsURI = ContentUris.withAppendedId(Constants.INTERACTIONS_URI, rowID);
			getContext().getContentResolver().notifyChange(interactionsURI, null);
			return interactionsURI;
		}
		throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

		switch (uriMatcher.match(uri)) {
		case Constants.SINGLE_ROW:
			sqlBuilder.setTables(Constants.DATABASE_TABLE_INTERACTIONS);
			sqlBuilder.setProjectionMap(interactionsProjectionMap);
			sqlBuilder.appendWhere(Constants.KEY_ROW_ID + "=" + uri.getPathSegments().get(1));
			break;

		case Constants.ALL_ROWS:
			sqlBuilder.setTables(Constants.DATABASE_TABLE_INTERACTIONS);
			sqlBuilder.setProjectionMap(interactionsProjectionMap);
			break;

		default:
			throw new IllegalArgumentException(TAG + " : Unknown URI");
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = sqlBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		int count;

		switch (uriMatcher.match(uri)) {
		case Constants.ALL_ROWS:
			count = db.update(Constants.DATABASE_TABLE_INTERACTIONS, values, selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknow URI");
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

}
