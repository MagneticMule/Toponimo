package org.toponimo.client.database;

import java.util.ArrayList;
import java.util.HashMap;

import org.toponimo.client.ApiKeys;
import org.toponimo.client.Constants;
import org.toponimo.client.R;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.utils.http.ToponimoVolley;

import com.android.volley.RequestQueue;
import com.android.volley.Request.Method;
import com.android.volley.toolbox.StringRequest;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class ClientWordsProvider extends ContentProvider {

    private static final String            TAG         = org.toponimo.client.database.ClientWordsProvider.class
                                                               .getName();
    private static Uri                     CONTENT_URI = Constants.WORDS_URI;
    private Context                        mContext;
    private ToponimoApplication            mApplication;

    // Instantiate custom DBHelper;
    private DBHelper                       mDbHelper;
    private static UriMatcher              sUriMatcher;

    // words table projection map
    private static HashMap<String, String> mWordsProjectionMap;

    // definitions table projection map
    private static HashMap<String, String> mDefinitionsProjectionMap;

    // definitions table projection map
    private static HashMap<String, String> mImagesProjectionMap;

    // definitions table projection map
    private static HashMap<String, String> mUserExamplesProjectionMap;

    SQLiteDatabase                         mDb;

    /**
     * Get a reference to the global application context. Instantiate a new
     * DBHelper object which will open the database if it exists or else create
     * one. (non-Javadoc)
     * 
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate() {
        mContext = getContext();
        mDbHelper = new DBHelper(mContext, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        try {
            mDb = mDbHelper.getWritableDatabase();
        } catch (SQLiteException s) {
            Log.d(TAG, s.toString());
        }
        return true;
    }

    /**
     * Populate the uriMatchers. These will be used to map between column names
     * and queries.
     */
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // multiple words
        sUriMatcher.addURI(Constants.AUTHORITY, "words", Constants.WORDS);

        // for single word
        sUriMatcher.addURI(Constants.AUTHORITY, "words/#", Constants.WORD);

        // multliple definitions
        sUriMatcher.addURI(Constants.AUTHORITY, "definitions", Constants.DEFINITIONS);

        // single definition
        sUriMatcher.addURI(Constants.AUTHORITY, "definitions/#", Constants.DEFINITION);

        // multliple images
        sUriMatcher.addURI(Constants.AUTHORITY, "images", Constants.IMAGES);

        // single image
        sUriMatcher.addURI(Constants.AUTHORITY, "images/#", Constants.IMAGE);

        // single word matching lemma
        sUriMatcher.addURI(Constants.AUTHORITY, "lemma/#", Constants.LEMMA);

        // Projection map for the words table
        mWordsProjectionMap = new HashMap<String, String>();
        mWordsProjectionMap.put(Constants.KEY_ROW_ID, Constants.DATABASE_TABLE_MY_WORDS + "." + Constants.KEY_ROW_ID);
        mWordsProjectionMap.put(Constants.KEY_WORD_ID, Constants.DATABASE_TABLE_MY_WORDS + "." + Constants.KEY_WORD_ID);
        mWordsProjectionMap.put(Constants.KEY_WORD_LEMMA, Constants.DATABASE_TABLE_MY_WORDS + "."
                + Constants.KEY_WORD_LEMMA);
        mWordsProjectionMap.put(Constants.KEY_WORD_SYLLABLES, Constants.DATABASE_TABLE_MY_WORDS + "."
                + Constants.KEY_WORD_SYLLABLES);
        mWordsProjectionMap.put(Constants.KEY_WORD_POS, Constants.DATABASE_TABLE_MY_WORDS + "."
                + Constants.KEY_WORD_POS);
        mWordsProjectionMap.put(Constants.KEY_WORD_PRONOUNCIATION, Constants.DATABASE_TABLE_MY_WORDS + "."
                + Constants.KEY_WORD_PRONOUNCIATION);
        mWordsProjectionMap.put(Constants.KEY_WORD_FORMS, Constants.DATABASE_TABLE_MY_WORDS + "."
                + Constants.KEY_WORD_FORMS);
        mWordsProjectionMap.put(Constants.KEY_WORD_LOCATION, Constants.DATABASE_TABLE_MY_WORDS + "."
                + Constants.KEY_WORD_LOCATION);
        mWordsProjectionMap.put(Constants.KEY_WORD_LOCATION_LAT, Constants.DATABASE_TABLE_MY_WORDS + "."
                + Constants.KEY_WORD_LOCATION_LAT);
        mWordsProjectionMap.put(Constants.KEY_WORD_LOCATION_LNG, Constants.DATABASE_TABLE_MY_WORDS + "."
                + Constants.KEY_WORD_LOCATION_LNG);
        mWordsProjectionMap.put(Constants.KEY_WORD_LOCATION_ID, Constants.DATABASE_TABLE_MY_WORDS + "."
                + Constants.KEY_WORD_LOCATION_ID);
        mWordsProjectionMap.put(Constants.KEY_WORD_TIME, Constants.DATABASE_TABLE_MY_WORDS + "."
                + Constants.KEY_WORD_TIME);
        mWordsProjectionMap.put(Constants.KEY_WORD_ADDITION_TYPE, Constants.DATABASE_TABLE_MY_WORDS + "."
                + Constants.KEY_WORD_ADDITION_TYPE);

        mWordsProjectionMap.put(Constants.KEY_DEFINITION, Constants.DATABASE_TABLE_DEFINITIONS + "."
                + Constants.KEY_DEFINITION);

        mWordsProjectionMap.put(Constants.KEY_ROW_ID, Constants.DATABASE_TABLE_DEFINITIONS + Constants.KEY_ROW_ID);

        mWordsProjectionMap.put(Constants.KEY_IMAGE_FILEPATH, Constants.DATABASE_TABLE_MY_IMAGES + "."
                + Constants.KEY_IMAGE_FILEPATH);

        // projectionmap for the definitions table
        mDefinitionsProjectionMap = new HashMap<String, String>();
        mDefinitionsProjectionMap.put(Constants.DATABASE_TABLE_DEFINITIONS + "." + Constants.KEY_ROW_ID,
                Constants.KEY_ROW_ID);
        mDefinitionsProjectionMap.put(Constants.KEY_DEFINITION, Constants.DATABASE_TABLE_DEFINITIONS + "."
                + Constants.KEY_DEFINITION);
        mDefinitionsProjectionMap.put(Constants.KEY_WORD_ID, Constants.DATABASE_TABLE_DEFINITIONS + "."
                + Constants.KEY_WORD_ID);

        // projectionmap for the images table
        mImagesProjectionMap = new HashMap<String, String>();
        mImagesProjectionMap.put(Constants.KEY_ROW_ID, Constants.DATABASE_TABLE_MY_IMAGES + "." + Constants.KEY_ROW_ID);
        mImagesProjectionMap.put(Constants.KEY_IMAGE_FILEPATH, Constants.DATABASE_TABLE_MY_IMAGES + "."
                + Constants.KEY_IMAGE_FILEPATH);
        mImagesProjectionMap.put(Constants.KEY_IMAGE_OWNER_ID, Constants.DATABASE_TABLE_MY_IMAGES + "."
                + Constants.KEY_IMAGE_OWNER_ID);
        mImagesProjectionMap.put(Constants.KEY_WORD_ID, Constants.DATABASE_TABLE_MY_IMAGES + "."
                + Constants.KEY_WORD_ID);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri _uri = null;
        ContentValues contentValues;

        if (values != null) {
            contentValues = new ContentValues(values);
        } else {
            contentValues = new ContentValues();
        }

        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

        long rowID = 0;

        switch (sUriMatcher.match(uri)) {

            case Constants.WORDS:
                Log.d(TAG, uri.toString());
                if (!values.containsKey(Constants.KEY_WORD_LEMMA)) {
                    throw new SQLException("Failed to add to the Database due to empty Key Word" + uri);
                }

                // attempt to insert the row in the table
                rowID = mDb.insert(Constants.DATABASE_TABLE_MY_WORDS, null, contentValues);
                // if success then the returned row id will be >0
                if (rowID > 0) {
                    Uri wordURI = ContentUris.withAppendedId(Constants.WORDS_URI, rowID);
                    getContext().getContentResolver().notifyChange(wordURI, null);
                    return wordURI;
                }

            case Constants.DEFINITIONS:
                rowID = mDb.insert(Constants.DATABASE_TABLE_DEFINITIONS, null, contentValues);
                // Success, the id of the inserted row exists
                if (rowID > 0) {
                    Uri wordURI = ContentUris.withAppendedId(Constants.DEFINITIONS_URI, rowID);
                    getContext().getContentResolver().notifyChange(wordURI, null);
                    return wordURI;
                }

            case Constants.IMAGES:
                rowID = mDb.insert(Constants.DATABASE_TABLE_MY_IMAGES, null, contentValues);
                // Success, the id of the inserted row exists
                if (rowID > 0) {
                    Uri imageURI = ContentUris.withAppendedId(Constants.IMAGES_URI, rowID);
                    getContext().getContentResolver().notifyChange(imageURI, null);
                    // Sync to network code goes here
                    RequestQueue queue = ToponimoVolley.getRequestQueue();
                    // StringRequest request = new StringRequest(Method.POST,
                    // ApiKeys.UPLOAD_IMAGE_URL + lemma, successListener(),
                    // errorListener());
                    // request.setShouldCache(true);
                    // queue.add(request);
                    return imageURI;
                }

            default:
                throw new SQLException("Failed to insert row into " + uri);

        }

    }

    public void getWordDetails(String wordId) {

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

        switch (sUriMatcher.match(uri)) {
            case Constants.WORD:
                sqlBuilder.setTables(Constants.DATABASE_TABLE_MY_WORDS + " INNER JOIN "
                        + Constants.DATABASE_TABLE_DEFINITIONS + " ON " + Constants.DATABASE_TABLE_MY_WORDS + "."
                        + Constants.KEY_WORD_ID + " = " + Constants.DATABASE_TABLE_DEFINITIONS + "."
                        + Constants.KEY_WORD_ID);
                sqlBuilder.setProjectionMap(mWordsProjectionMap);
                sqlBuilder.setDistinct(true);

                sqlBuilder.appendWhere(Constants.KEY_ROW_ID + "=" + uri.getPathSegments().get(1));

                break;

            case Constants.WORDS:

                String rawQuery = "SELECT * FROM myWordsTable W "
                        + "LEFT JOIN myDefinitionsTable D ON (W.word_id = D.word_id) "
                        + "LEFT JOIN myPicturesTable I ON (W.word_id = I.word_id) "
                        + "GROUP BY word_id ORDER BY W.time DESC;";

                sqlBuilder.setTables(Constants.DATABASE_TABLE_MY_WORDS + " W LEFT JOIN "
                        + Constants.DATABASE_TABLE_DEFINITIONS + " D ON (W.word_id = D.word_id) "
                        + "LEFT OUTER JOIN myPicturesTable I ON (W.word_id = I.word_id) "
                        + "GROUP BY W.word_id ORDER BY W.time DESC;");
                break;

            // return mDb.rawQuery(rawQuery, selectionArgs);

            case Constants.SINGLE_DEFINITION:
                sqlBuilder.setTables(Constants.DATABASE_TABLE_MY_WORDS + " LEFT JOIN "
                        + Constants.DATABASE_TABLE_DEFINITIONS + " ON " + Constants.DATABASE_TABLE_MY_WORDS + "."
                        + Constants.KEY_WORD_ID + " = " + Constants.DATABASE_TABLE_DEFINITIONS + "."
                        + Constants.KEY_WORD_ID);

                break;

            case Constants.LEMMA:
                sqlBuilder.setTables(Constants.DATABASE_TABLE_MY_WORDS + " W LEFT JOIN "
                        + Constants.DATABASE_TABLE_DEFINITIONS + " D ON (W.word_id = D.word_id) "
                        + "LEFT OUTER JOIN myPicturesTable I ON (W.word_id = I.word_id) "
                        + "GROUP BY W.word_id ORDER BY W.time DESC;");
                break;

            // case Constants.DEFINITION:
            // sqlBuilder.setTables(
            // Constants.DATABASE_TABLE_DEFINITIONS

            default:
                throw new IllegalArgumentException(TAG + " : Unknown URI");
        }

        Cursor result = sqlBuilder.query(mDb, projection, selection, selectionArgs, null, null, null);
        // notify any listeners of changes
        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int count;

        switch (sUriMatcher.match(uri)) {
            case Constants.WORDS:
                count = mDb.update(Constants.DATABASE_TABLE_MY_WORDS, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknow URI");
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.content.ContentProvider#applyBatch(java.util.ArrayList)
     */
    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) {
        ContentProviderResult[] result = new ContentProviderResult[operations.size()];
        int i = 0;
        mDb.beginTransaction();
        try {
            for (ContentProviderOperation operation : operations) {
                result[i++] = operation.apply(this, result, i);
            }

            mDb.setTransactionSuccessful();
        } catch (OperationApplicationException e) {

            e.printStackTrace();
        } finally {
            mDb.endTransaction();
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.content.ContentProvider#bulkInsert(android.net.Uri,
     * android.content.ContentValues[])
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        // TODO Auto-generated method stub
        return super.bulkInsert(uri, values);
    }

}
