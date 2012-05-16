package org.toponimo.client.utils.maps;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class Locations extends ItemizedOverlay<OverlayItem> {

    private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();

    public Locations(Drawable marker) {
	super(boundCenterBottom(marker));

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.google.android.maps.ItemizedOverlay#draw(android.graphics.Canvas,
     * com.google.android.maps.MapView, boolean)
     */
    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	// TODO Auto-generated method stub
	super.draw(canvas, mapView, true);
    }

    public Locations(Drawable marker, Context context) {
	super(marker);
    }

    public void addOverlay(OverlayItem overlay) {
	mOverlays.add(overlay);
	clearList();

    }

    @Override
    protected OverlayItem createItem(int i) {
	return mOverlays.get(i);
    }

    @Override
    public int size() {
	return mOverlays.size();
    }

    @Override
    protected boolean onTap(int index) {
	return super.onTap(index);
    }

    public void clearList() {
	mOverlays.clear();
	setLastFocusedIndex(-1);

    }

    public void populateNow() {
	this.populate();
    }

}
