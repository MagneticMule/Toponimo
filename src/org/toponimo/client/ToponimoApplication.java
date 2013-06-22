package org.toponimo.client;

import java.util.List;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.toponimo.client.R;
import org.toponimo.client.models.Place;
import org.toponimo.client.models.Results;
import org.toponimo.client.utils.http.ToponimoVolley;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.util.LruCache;
import android.util.Log;

public class ToponimoApplication extends Application {

    public static final String             TAG               = "ToponimoApplication";

    private static SharedPreferences       userDetailsPrefs;

    private static User                    user;

    /**
     * Store a reference to the application object
     */
    private static ToponimoApplication     app               = null;
    private static Context                 context;
    private DefaultHttpClient              httpClient;

    private final LruCache<String, Bitmap> mImageCache       = new LruCache<String, Bitmap>(20);

    /**
     * placeResults holds a global reference to the PlaceStructure data. This
     * allows each class to access the placeResults without resorting to passing
     * values via bundled extras. // note passing data via bundles would be a
     * more 'correct' way to approach the problem.
     */
    private static List<Place>             placeResults      = null;

    private static String                  currentPlaceId    = null;

    private static int                     currentPlaceIndex = 0;

    // using this is ugly but having no luck with accessing the returned URI
    // from the camera intent
    private Uri                            newImageLocation;

    public ToponimoApplication() {
        super();

    }

    @Override
    public void onCreate() {
        // checkInstance();
        super.onCreate();
        ToponimoApplication.context = getApplicationContext();
        app = this;

        // init volley lib
        ToponimoVolley.init(this);

        // reference to the user shared preferences file
        userDetailsPrefs = getSharedPreferences(Constants.USER_DETAILS_PREFS, 0);

        // single Httpclient object which will be used throughout the
        // application
        httpClient = new DefaultHttpClient();
        ClientConnectionManager manager = httpClient.getConnectionManager();
        HttpParams params = httpClient.getParams();
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
                params, manager.getSchemeRegistry()), params);
        Log.d(TAG, "Tooponimo app started");

    }

    public User getUser() {
        if (this.user == null) {
            this.user = new User();
            user.setId(userDetailsPrefs.getString(Constants.USER_ID, null));
            user.setFirstName(userDetailsPrefs.getString(Constants.FIRST_NAME,
                    null));
            user.setLastName(userDetailsPrefs.getString(Constants.LAST_NAME,
                    null));
        }
        return this.user;
    }

    public void setUser(User _user) {
        this.user = _user;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (httpClient != null) {
            httpClient.getConnectionManager().shutdown();
        }
        Log.i(TAG, "Tooponimo application ended.");
    }

    public DefaultHttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(DefaultHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public void setPlaceResults(List<Place> placeResults) {
        checkInstance();
        this.placeResults = placeResults;
    }

    public Results getPlaceResults(int position) {
        checkInstance();
        return placeResults.get(position).getResults().get(position);

    }

    public int getCurrentPlaceIndex() {
        checkInstance();
        return currentPlaceIndex;
    }

    public String getCurrentPlaceId() {
        checkInstance();
        return currentPlaceId;
    }

    public void setCurrentPlaceId(String currentPlaceId) {
        checkInstance();
        this.currentPlaceId = currentPlaceId;
    }

    public void setCurrentPlaceIndex(int currentPlaceIndex) {
        checkInstance();
        this.currentPlaceIndex = currentPlaceIndex;
    }

    public static Context getGlobalContext() {
        checkInstance();
        return ToponimoApplication.context;
    }

    public static ToponimoApplication getApp() {
        checkInstance();
        return app;

    }

    public SharedPreferences getUserDetailsPrefs() {
        return userDetailsPrefs;
    }

    public static void checkInstance() {
        if (app == null) {
            throw new IllegalStateException(
                    "Application has not been initialized : " + TAG);
        }
    }

    public boolean isLoggedIn() {
        checkInstance();
        SharedPreferences sp = getSharedPreferences(
                Constants.USER_DETAILS_PREFS, 0);
        if ((sp.contains(Constants.USER_ID) && (sp
                .contains(Constants.FIRST_NAME) && (sp
                .contains(Constants.LAST_NAME))))) {
            return true;
        } else {
            return false;
        }

    }

    public User loadDetails() {

        User _user = new User();
        // set global user details
        SharedPreferences sp = getSharedPreferences(
                Constants.USER_DETAILS_PREFS, 0);
        _user.setId(sp.getString(Constants.USER_ID, null));
        _user.setFirstName(sp.getString(Constants.FIRST_NAME, null));
        _user.setLastName(sp.getString(Constants.LAST_NAME, null));

        Log.v("Firstname", user.getFirstName());
        Log.v("Lastname", user.getLastName());
        return _user;
    }

    public void saveDetails(User _user) {

        // Write user details to shared preferences file

        SharedPreferences sp = getSharedPreferences(
                Constants.USER_DETAILS_PREFS, 0);
        SharedPreferences.Editor editor;
        editor = sp.edit();
        editor.putBoolean(Constants.IS_LOGGED_IN, true);
        editor.putString(Constants.FIRST_NAME, _user.getFirstName());
        editor.putString(Constants.LAST_NAME, _user.getLastName());
        editor.putString(Constants.USER_ID, _user.getId());
        editor.commit();
    }

    public void deleteDetails() {
        SharedPreferences sp = getSharedPreferences(
                Constants.USER_DETAILS_PREFS, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.commit();
    }

    /**
     * @return the newImageLocation
     */
    public Uri getNewImageLocation() {
        return this.newImageLocation;
    }

    /**
     * @param newImageLocation
     *            the newImageLocation to set
     */
    public void setNewImageLocation(Uri newImageLocation) {
        this.newImageLocation = newImageLocation;
    }

}
