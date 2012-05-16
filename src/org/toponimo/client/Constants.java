package org.toponimo.client;

import android.location.Criteria;
import android.net.Uri;
import org.toponimo.client.R;

public class Constants {

	// Parameters for passing a bundle to the ImageUploadService
	public static final String	UPLOAD_IMAGE_NAME				= "UIN";
	public static final String	UPLOAD_IMAGE_WORD_NAME			= "UIWN";
	public static final String	UPLOAD_IMAGE_WORD_NUMBER		= "UIWU";
	public static final String	UPLOAD_IMAGE_SYNSET_NUMBER		= "UISN";
	public static final String	UPLOAD_IMAGE_USER_ID			= "UIUID";
	public static final String	UPLOAD_IMAGE_PATH				= "UIP";
	public static final String	UPLOAD_IMAGE_PLACE_ID			= "UIPID";

	// REST verbs
	public static final int		GET								= 1;
	public static final int		PUT								= 2;
	public static final int		POST							= 3;
	public static final int		DELETE							= 4;

	// Parameters for passing data to and from the RESTService

	// PUT, POST, GET, DELETE
	public static final String	REST_ACTION						= "REST_ACTION";
	// Form data
	public static final String	REST_PARAMS						= "REST_PARAMS";
	// Returned from RESTService
	public static final String	REST_RESULT						= "REST_RESULT";

	public static final String	REST_RESULT_RECEIVER			= "REST_RESULT_RECIEVER";

	// Shared preferences file for login details storage
	public static final String	USER_DETAILS_PREFS				= "USP";

	// User details preferences
	public static final String	IS_LOGGED_IN					= "isLoggedIn";
	public static final String	FIRST_NAME						= "firstName";
	public static final String	LAST_NAME						= "lastName";
	public static final String	USER_ID							= "userId";

	// Path to the local client storage which will be used for storing images
	// after being taken via the camera.
	public static final String	LOCAL_IMAGE_STORE				= "/data/data/org.toponimo.client/";

	/**
	 * Database construction and manipulation
	 */
	public static final String	LOCAL_WORDS_URI					= "content://org.toponimo.provider.words/words";
	public static final Uri		WORDS_URI						= Uri.parse(LOCAL_WORDS_URI);

	// dealing with different URI requests: single_row / multiple_rows
	public static final int		SINGLE_ROW						= 1;
	public static final int		ALL_ROWS						= 2;

	public static final String	AUTHORITY						= "org.toponimo.provider.words";
	public static final String	DATABASE_NAME					= "toponimoData.db";
	// the users private word list
	public static final String	DATABASE_TABLE_MY_WORDS			= "myWordsTable";
	// local cache of places
	public static final String	DATABASE_TABLE_MY_PLACES		= "myPlaceTable";
	// local cache of pictures
	public static final String	DATABASE_TABLE_MY_PICTURES		= "myPicturesTable";

	public static final int		DATABASE_VERSION				= 25;

	// key used for where clauses
	public static final String	KEY_ROW_ID						= "_id";

	// Key and column pairs for the MY_WORDS table
	public static final String	KEY_WORD						= "word";

	public static final String	KEY_WORD_LOCATION				= "location";

	public static final String	KEY_WORD_DEFINITION				= "definition";

	public static final String	KEY_WORD_GLOSS					= "gloss";

	public static final String	KEY_WORD_TYPE					= "type";

	public static final String	KEY_WORD_LOCATION_LAT			= "lat";

	public static final String	KEY_WORD_LOCATION_LNG			= "lng";

	public static final String	KEY_WORD_LOCATION_ID			= "locationid";

	public static final String	KEY_WORD_TIME					= "time";

	public static final String	KEY_WORD_ADDITION_TYPE			= "addtype";

	public static final int		WORD_ADDITION_TYPE_COLLECT		= 0;
	public static final int		WORD_ADDITION_TYPE_ADD			= 1;

	// SQL query to construct words table
	public static final String	CREATE_TABLE_MY_WORDS			= "CREATE TABLE IF NOT EXISTS "
																		+ DATABASE_TABLE_MY_WORDS
																		+ "("
																		+ KEY_ROW_ID
																		+ " integer primary key autoincrement not null, "
																		+ KEY_WORD
																		+ " text not null, "
																		+ KEY_WORD_LOCATION
																		+ " text not null, "
																		+ KEY_WORD_DEFINITION
																		+ " text not null, "
																		+ KEY_WORD_GLOSS
																		+ " text, "
																		+ KEY_WORD_TYPE
																		+ " text, "
																		+ KEY_WORD_LOCATION_LAT
																		+ " real not null, "
																		+ KEY_WORD_LOCATION_LNG
																		+ " real not null, "
																		+ KEY_WORD_LOCATION_ID
																		+ " text not null, "
																		+ KEY_WORD_TIME
																		+ " integer not null, "
																		+ KEY_WORD_ADDITION_TYPE
																		+ " integer not null)";

	/**
	 * Definitions for local SQL database to store visited places
	 */

	// Key and column pairs for the PLACES table
	public static final String	KEY_PLACE_NAME					= "name";
	public static final int		KEY_PLACE_NAME_COLUMN			= 1;
	public static final String	KEY_PLACE_LAT					= "latitude";
	public static final int		KEY_PLACE_LAT_COLUMN			= 2;
	public static final String	KEY_PLACE_LNG					= "longitude";
	public static final int		KEY_PLACE_LNG_COLUMN			= 3;
	public static final String	KEY_PLACE_VICINITY				= "vicinity";
	public static final int		KEY_PLACE_VICINITY_COLUMN		= 4;
	public static final String	KEY_PLACE_PLACEID				= "placeid";
	public static final int		KEY_PLACE_PLACEID_COLUMN		= 5;
	public static final String	KEY_PLACE_TIME					= "time";
	public static final int		KEY_PLACE_TIME_COLUMN			= 6;

	// SQL query to construct places table
	public static final String	CREATE_TABLE_MY_PLACES			= "CREATE TABLE IF NOT EXISTS "
																		+ DATABASE_TABLE_MY_PLACES
																		+ "("
																		+ KEY_ROW_ID
																		+ " integer primary key autoincrement not null, "
																		+ KEY_PLACE_NAME
																		+ " text not null, "
																		+ KEY_PLACE_LAT
																		+ " real not null, "
																		+ KEY_PLACE_LNG
																		+ " real not null, "
																		+ KEY_PLACE_VICINITY
																		+ " text not null, "
																		+ KEY_PLACE_PLACEID
																		+ " text not null, "
																		+ KEY_PLACE_TIME
																		+ " timestamp default CURRENT_TIMESTAMP)";

	/**
	 * Definitions for local SQL database to store images
	 */

	// Key and column pairs for the PLACES table
	public static final String	KEY_IMAGE_NAME					= "name";
	public static final int		KEY_IMAGE_NAME_COLUMN			= 1;
	public static final String	KEY_IMAGE_FILEPATH				= "filepath";
	public static final int		KEY_IMAGE_FILEPATH_COLUMN		= 2;
	public static final String	KEY_IMAGE_PLACEID				= "placeid";
	public static final int		KEY_IMAGE_PLACEID_COLUMN		= 3;
	public static final String	KEY_IMAGE_WORD					= "word";
	public static final int		KEY_IMAGE_WORD_COLUMN			= 4;
	public static final String	KEY_IMAGE_WORD_NUMBER			= "wordno";
	public static final int		KEY_IMAGE_WORD_NUMBER_COLUMN	= 5;
	public static final String	KEY_IMAGE_SYNSET_NUMBER			= "synsetno";
	public static final int		KEY_IMAGE_SYNSET_NUMBER_COLUMN	= 6;
	public static final String	KEY_IMAGE_TIME					= "time";
	public static final int		KEY_IMAGE_TIME_COLUMN			= 7;

	// SQL query to construct pictures table
	public static final String	CREATE_TABLE_MY_PICTURES		= "CREATE TABLE IF NOT EXISTS "
																		+ DATABASE_TABLE_MY_PICTURES
																		+ "("
																		+ KEY_ROW_ID
																		+ " integer primary key autoincrement not null, "
																		+ KEY_IMAGE_NAME
																		+ " text not null, "
																		+ KEY_IMAGE_FILEPATH
																		+ " text not null, "
																		+ KEY_IMAGE_PLACEID
																		+ " text not null, "
																		+ KEY_IMAGE_WORD
																		+ " text not null, "
																		+ KEY_IMAGE_WORD_NUMBER
																		+ " real not null, "
																		+ KEY_IMAGE_SYNSET_NUMBER
																		+ " real not null, "
																		+ KEY_IMAGE_TIME
																		+ " timestamp default CURRENT_TIMESTAMP)";

	/**
	 * location requesting and tracking
	 */

	// the minimum distance between location updates; around 20 meters
	public static final float	MIN_DISTANCE					= 20;

	// The minimum time between location updates; 0.15 minutes
	public static final long	MIN_TIME						= 15000;

	// The minimum accuracy returned by the locationlistener. 100 meters
	public static final int		MIN_ACCURACY					= 100;

	/**
	 * Define a criteria which favours location accuracy over speed. Generally,
	 * this will be used sparingly as the battery consumption can be
	 * prohibitive.
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
		criteria.setCostAllowed(true);

		return criteria;

	}

	/**
	 * Define a criteria which favours speed over accuracy. This is useful when
	 * the user first starts the application and we need a location fix as soon
	 * as possible.
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
