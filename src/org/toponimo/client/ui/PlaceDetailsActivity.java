package org.toponimo.client.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.toponimo.client.R;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.ui.adapters.WordListAdapter;
import org.toponimo.client.utils.http.HttpUtils;
import org.toponimo.client.utils.location.DistanceCalculator;
import org.toponimo.client.utils.maps.CustomOverlay;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.gson.Gson;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;

import android.support.v4.view.ViewPager;

public class PlaceDetailsActivity extends SherlockFragmentActivity {

    protected static final String       TAG          = "PlaceDetailsActivity";
    private static int                  TAKE_PICTURE = 1;
    private int                         currentPlaceIndex;
    private String                      placeName;
    private String                      placeAddress;
    private final String                extraData    = "";
    private String                      currentPosLat;
    private String                      currentPosLng;
    private Double                      targetPosLat;
    private Double                      targetPosLng;
    private ListView                    wordListView;
    private ArrayAdapter<String>        wordListArrayAdapter;
    private ToponimoApplication         application;
    private ImageView                   mapImage;
    private RelativeLayout              headerViewMap;
    private LinearLayout                headerViewWordButton;
    private Button                      addWordButton;
    private MapView                     mapView;
    private View                        mapViewClickReceiver;
    private MapController               mapController;
    private GeoPoint                    targetLocation;
    private ArrayList<String>           wordList;

    private static PlaceDetailsActivity mainActivity;

    private LayoutInflater              inflater;

    private GoogleMap                   mMap;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            // extraData = data.getStringExtra("words");

            // application.getPlaceResults(application.getCurrentPlaceIndex())
            // .getWords().add(extraData + " *");
            wordListArrayAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_details);

        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.place_details_map_fragment))
                    .getMap();
            mMap.getUiSettings().setZoomControlsEnabled(false);
        }

        /*
         * get a reference to the activities ActionBar and add custom add words
         * button
         */
        com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(actionBar.DISPLAY_SHOW_HOME | actionBar.DISPLAY_SHOW_CUSTOM
                | actionBar.DISPLAY_SHOW_TITLE);
        actionBar.setTitle("Toponimo");
        actionBar.setSubtitle("About this Place");
        actionBar.setDisplayHomeAsUpEnabled(true);

        mainActivity = this;

        Intent sender = getIntent();
        application = (ToponimoApplication) getApplication();
        wordList = new ArrayList<String>();
        // wordList.addAll(application.getPlaceResults(application.getCurrentPlaceIndex()).getWords());

        placeName = application.getPlaceResults(application.getCurrentPlaceIndex()).getName();
        placeAddress = application.getPlaceResults(application.getCurrentPlaceIndex()).getVicinity();
        currentPosLat = sender.getExtras().getString("currentPosLat");
        currentPosLng = sender.getExtras().getString("currentPosLng");

        targetPosLat = application.getPlaceResults(application.getCurrentPlaceIndex()).getLocation().getLat();
        targetPosLng = application.getPlaceResults(application.getCurrentPlaceIndex()).getLocation().getLng();

        wordListView = (ListView) findViewById(R.id.place_details_word_listview);

        wordListArrayAdapter = new WordListAdapter(this, R.layout.word_row, application.getPlaceResults(
                application.getCurrentPlaceIndex()).getWords());

        inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        targetLocation = new GeoPoint((int) (targetPosLat * 1e6), ((int) (targetPosLng * 1e6)));

        if (mMap != null) {
            LatLng location = new LatLng(targetPosLat, targetPosLng);
            mMap.addMarker(new MarkerOptions().position(location).title(placeName)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_red)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
            // map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
        }

        // initMap();

        // inflate the second listview header and attach the add word button
        headerViewWordButton = (LinearLayout) inflater.inflate(R.layout.add_word_button, null);
        addWordButton = (Button) headerViewWordButton.findViewById(R.id.place_details_add_word_button);

        try {

            wordListView.addHeaderView(headerViewWordButton);
            wordListView.setAdapter(wordListArrayAdapter);
            if (!(wordListView.getCount() < 1)) {
                TextView wordInfoTextView = (TextView) findViewById(R.id.places_info_text);
                wordInfoTextView.setText(Integer.toString(application
                        .getPlaceResults(application.getCurrentPlaceIndex()).getWords().size())
                        + " words at this location");
            }
        } catch (Exception e) {

        }

        wordListView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // TextView wordView = (TextView)
                // v.findViewById(R.id.word_row_word_view);
                Intent intent = new Intent(PlaceDetailsActivity.this, WordDetailsActivity.class);
                Log.d("position", Integer.toString(position));
                Log.d("ID", Long.toString(id));
                String word = application.getPlaceResults(application.getCurrentPlaceIndex()).getWords().get((int) id);

                if (word != null || word.length() != 0) {
                    intent.putExtra("word", word);
                    startActivity(intent);
                }
            }
        });

        // Set header textviews with appropriate information
        TextView placeTitleView = (TextView) findViewById(R.id.place_details_place_name);
        placeTitleView.setText(placeName);
        TextView placeAddressView = (TextView) findViewById(R.id.place_details_place_address);
        placeAddressView.setText(placeAddress);
        TextView placeDistanceView = (TextView) findViewById(R.id.place_details_place_distance);

        double lat = Double.parseDouble(currentPosLat);
        double lng = Double.parseDouble(currentPosLng);
        double distance = DistanceCalculator.haverSine(lat, lng, targetPosLat, targetPosLng);

        String distanceIndicator = " Kilometers from here";
        Double d = (double) Math.round(distance * 1000);
        if (distance < 0.5) {
            distanceIndicator = " Meters from here";
        } else {
            distanceIndicator = " Kilometers from here";
            d /= 1000;
        }
        int roundedDistance = d.intValue();

        placeDistanceView.setText(Integer.toString(roundedDistance) + distanceIndicator);

        addWordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(PlaceDetailsActivity.this, AddWordActivity.class);
                intent.putExtra("position", currentPlaceIndex);
                intent.putExtra("name", placeName);
                final int result = 1;
                startActivityForResult(intent, result);
            }
        });
    }

    /**
     * mapViewClickReceiver.setOnClickListener(new View.OnClickListener() {
     * public void onClick(View v) { Intent intent = new
     * Intent(PlaceDetailsActivity.this, MapsViewActivity.class);
     * intent.putExtra("targetPosLat", targetPosLat);
     * intent.putExtra("targetPosLng", targetPosLng); startActivity(intent);
     * Log.i(TAG, "MAP PRESSED");
     * 
     * } }); }
     */

    private void initMap() {
        // inflate the first listview header view then find and attach the
        // mapview
        headerViewMap = (RelativeLayout) inflater.inflate(R.layout.map_image, null);
        mapView = (MapView) headerViewMap.findViewById(R.id.place_list_activity_mapview);

        mapViewClickReceiver = headerViewMap.findViewById(R.id.place_list_activity_click_view);

        mapController = mapView.getController();

        List<Overlay> placeOverlays = mapView.getOverlays();
        placeOverlays.clear();
        mapView.invalidate();

        targetLocation = new GeoPoint((int) (targetPosLat * 1e6), ((int) (targetPosLng * 1e6)));

        Drawable drawable = this.getResources().getDrawable(R.drawable.marker_red);

        List<Overlay> overlays = mapView.getOverlays();

        // remove old overlays
        if (overlays.size() > 0) {
            for (Iterator<Overlay> it = overlays.iterator(); it.hasNext();) {
                it.next();
                it.remove();
            }
        }

        overlays.add(new CustomOverlay(this, targetLocation, R.drawable.marker_red));
        mapView.postInvalidate();

    }

    @Override
    protected void onResume() {
        wordListArrayAdapter.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.place_details_menu, menu);
        return true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
        window.addFlags(WindowManager.LayoutParams.FLAG_DITHER);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start analytics tracking
        EasyTracker.getInstance().activityStart(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop analytics tracking
        EasyTracker.getInstance().activityStop(this);
    }

}