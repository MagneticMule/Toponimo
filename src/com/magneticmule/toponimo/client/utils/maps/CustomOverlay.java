package com.magneticmule.toponimo.client.utils.maps;

//import android.R.color;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class CustomOverlay extends Overlay {

    private static final int CIRCLERADIUS = 12;
    private static final int CIRCLE_STROKE_WIDTH = 1;

    private final boolean languageCount = true;

    private int drawable = 0;

    private GeoPoint geopoint = null;
    private Context context = null;

    Bitmap bitmapMarker = null;

    // shaders for gps icons
    private static final Shader redIconShader = new LinearGradient(8f, 80f,
	    30f, 20f, Color.RED, Color.WHITE, Shader.TileMode.MIRROR);

    final Shader blueIconShader = new LinearGradient(8f, 80f, 30f, 10f,
	    Color.BLUE, Color.WHITE, Shader.TileMode.MIRROR);

    final Shader greenIconShader = new LinearGradient(8f, 80f, 30f, 20f,
	    Color.GREEN, Color.WHITE, Shader.TileMode.MIRROR);

    public CustomOverlay(Context context, GeoPoint point, int drawable) {
	this.geopoint = point;
	this.context = context;
	this.drawable = drawable;
	this.bitmapMarker = BitmapFactory.decodeResource(
		context.getResources(), drawable);

    }

    @Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	super.draw(canvas, mapView, shadow);
	// Transfrom geoposition to Point on canvas
	Projection projection = mapView.getProjection();
	Point point = new Point();
	projection.toPixels(geopoint, point);

	// background
	Paint background = new Paint();
	background.setColor(Color.WHITE);
	RectF rect = new RectF();
	rect.set(point.x + 2 * CIRCLERADIUS, point.y - 4 * CIRCLERADIUS,
		point.x + 90, point.y + 12);

	// drawMarker(canvas, point);
	canvas.drawBitmap(bitmapMarker,
		point.x - (bitmapMarker.getWidth() / 2), point.y
			- (bitmapMarker.getHeight()), null);

	// text "My Location"
	// Paint text = new Paint();
	// text.setAntiAlias(true);
	// text.setColor(Color.BLUE);
	// text.setTextSize(12);
	// text.setTypeface(Typeface.MONOSPACE);

	// canvas.drawRoundRect(rect, 2, 2, background);

	// canvas.drawText("My Location", point.x + 3 * CIRCLERADIUS, point.y +
	// 3
	// * CIRCLERADIUS, text);

    }

    /**
     * @param canvas
     * @param point
     */
    private void drawMarker(Canvas canvas, Point point) {

	if (languageCount) {
	    // draw the gps radius inner circle
	    Paint gpsRadiusInner = new Paint();
	    gpsRadiusInner.setAntiAlias(true);
	    gpsRadiusInner.setARGB(50, 150, 150, 100);
	    canvas.drawCircle(point.x, point.y, CIRCLERADIUS * 5,
		    gpsRadiusInner);

	    // draw the gps radius outer circle
	    Paint gpsRadiusOuter = new Paint();
	    gpsRadiusOuter.setAntiAlias(true);
	    gpsRadiusOuter.setARGB(150, 150, 150, 150);
	    gpsRadiusOuter.setStyle(Paint.Style.STROKE);
	    canvas.drawCircle(point.x, point.y, CIRCLERADIUS * 5,
		    gpsRadiusOuter);

	    /*
	     * // paint the circle fill Paint circle = new Paint();
	     * circle.setAntiAlias(true); circle.setShader(redIconShader); //
	     * circle.setShadowLayer(CIRCLERADIUS, point.x, point.y, //
	     * Color.BLACK); canvas.drawCircle(point.x, point.y, CIRCLERADIUS,
	     * circle);
	     * 
	     * // paint the circle outline Paint circleOutline = new Paint();
	     * circleOutline.setAntiAlias(true);
	     * circleOutline.setStrokeWidth(CIRCLE_STROKE_WIDTH);
	     * circleOutline.setColor(Color.BLACK);
	     * circleOutline.setStyle(Paint.Style.STROKE);
	     * canvas.drawCircle(point.x, point.y, CIRCLERADIUS, circleOutline);
	     */
	}

    }

    public boolean onSnapToItem(int x, int y, Point snapPoint, MapView mapView) {
	// TODO Auto-generated method stub
	return false;
    }

}