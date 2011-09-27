package com.magneticmule.toponimo.client;

import android.location.Criteria;
import android.net.Uri;

public class Constants {


	// Path to the local client storage which will be used for storing images
	// after being taken via the camera.
	public static final String	LOCAL_IMAGE_STORE							= "/data/data/com.magneticmule.toponimo.client/";

	/**
	 * location requesting and tracking
	 */

	// the minimum distance between location updates; around 20 meters
	public static final float		MIN_DISTANCE									= 20;

	// The minimum time between location updates; 0.15 minutes
	public static final long		MIN_TIME											= 15000;

	// The minimum accuracy returned by the locationlistener. 100 meters
	public static final int			MIN_ACCURACY									= 100;

	/**
	 * Database construction and manipulation
	 */
	public static final String	DATABASE_NAME									= "toponimoData.db";
	// the users private word list
	public static final String	DATABASE_TABLE_MY_WORDS				= "myWordsTable";
	// local cache of places
	public static final String	DATABASE_TABLE_PLACES					= "placeTable";

	public static final int			DATABASE_VERSION							= 10;

	// key used for where clauses
	public static final String	KEY_ROW_ID										= "_id";

	// Key and column pairs for the MY_WORDS table
	public static final String	KEY_WORD											= "word";
	public static final int			KEY_WORD_COLUMN								= 1;
	public static final String	KEY_WORD_LOCATION							= "location";
	public static final int			KEY_WORD_LOCATION_COLUMN			= 2;
	public static final String	KEY_WORD_DEFINITION						= "definition";
	public static final int			KEY_WORD_DEFINITION_COLUMN		= 3;
	public static final String	KEY_WORD_LOCATION_LAT					= "lat";
	public static final int			KEY_WORD_LOCATION_LAT_COLUMN	= 3;
	public static final String	KEY_WORD_LOCATION_LNG					= "lng";
	public static final int			KEY_WORD_LOCATION_LNG_COLUMN	= 4;
	public static final String	KEY_WORD_LOCATION_ID					= "locationid";
	public static final int			KEY_WORD_LOCATION_ID_COLUMN		= 5;

	// SQL database to construct database
	public static final String	DATABASE_CREATE								= "CREATE TABLE IF NOT EXISTS " + DATABASE_TABLE_MY_WORDS + "(" + KEY_ROW_ID
																																+ " integer primary key autoincrement, " + KEY_WORD + " text not null, "
																																+ KEY_WORD_LOCATION + " text not null, " + KEY_WORD_DEFINITION
																																+ " text not null, " + KEY_WORD_LOCATION_LAT + " real not null, "
																																+ KEY_WORD_LOCATION_LNG + " real not null, " + KEY_WORD_LOCATION_ID
																																+ " text not null)";

	// Define constants fro
	public static final String	LOCAL_WORDS_URI								= "content://com.magneticmule.provider.words/words";
	public static final Uri			WORDS_URI											= Uri.parse(LOCAL_WORDS_URI);

	// dealing with different URI requests: single_row / multiple_rows
	public static final int			SINGLE_ROW										= 1;
	public static final int			ALL_ROWS											= 2;

	public static final String	AUTHORITY											= "com.magneticmule.provider.words";

	/**
	 * Define a criteria which favours location accuracy over speed. Generally,
	 * this will be used sparingly as the battery consumption can be prohibitive.
	 * 
	 * @return Criteria
	 */
	public static Criteria accurateCriteria() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);

		return criteria;

	}

	/**
	 * Define a criteria which favours speed over accuracy. This is useful when
	 * the user first starts the application and we need a location fix as soon as
	 * possible.
	 * 
	 * @return Criteria
	 */
	public static Criteria fastCriteria() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setSpeedRequired(false);
		criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
		criteria.setCostAllowed(true);

		return criteria;

	}

}
