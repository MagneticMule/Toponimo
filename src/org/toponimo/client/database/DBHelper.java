package org.toponimo.client.database;

import org.toponimo.client.Constants;
import org.toponimo.client.R;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG = "DbHelper";

    private Context mContext;

    public DBHelper(Context _context, String _name, CursorFactory _factory,
	    int _version) {
	super(_context, _name, _factory, _version);

    }

    @Override
    public void onCreate(SQLiteDatabase _db) {
	try {
	    _db.execSQL(Constants.CREATE_TABLE_MY_WORDS);
	    _db.execSQL(Constants.CREATE_TABLE_MY_PLACES);
	    _db.execSQL(Constants.CREATE_TABLE_MY_PICTURES);
	} catch (SQLiteException e) {
	    Log.d(TAG, e.getMessage());
	}

    }

    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion) {
	// Drop old database and create new one, losing all stored data.
	// TODO: A better method would be to migrate the data from old the old
	// db (use temp tables).
	_db.execSQL("DROP TABLE IF EXISTS " + Constants.DATABASE_TABLE_MY_WORDS);
	_db.execSQL("DROP TABLE IF EXISTS "
		+ Constants.DATABASE_TABLE_MY_PLACES);
	_db.execSQL("DROP TABLE IF EXISTS "
		+ Constants.DATABASE_TABLE_MY_PICTURES);

	// create new db
	onCreate(_db);

    }

}
