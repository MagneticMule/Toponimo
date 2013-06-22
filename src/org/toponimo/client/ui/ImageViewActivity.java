package org.toponimo.client.ui;

import java.util.ArrayList;

import org.toponimo.client.utils.SimpleGestureFilter;
import org.toponimo.client.utils.SimpleGestureFilter.SimpleGestureListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;

import org.toponimo.client.R;

import com.google.analytics.tracking.android.EasyTracker;

public class ImageViewActivity extends Activity implements
	SimpleGestureListener, ViewFactory {

    private SimpleGestureFilter gestureFilter;

    private ArrayList<String> pathNames;

    private ImageSwitcher imageSwitcher;

    private Bitmap bitmap;

    private int currentIndex;

    private Drawable drawable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.image_viewer);

	imageSwitcher = (ImageSwitcher) findViewById(R.id.image_viewer_activity_image_switcher);
	imageSwitcher.setFactory(this);

	gestureFilter = new SimpleGestureFilter(this, this);
	Bundle bundle = this.getIntent().getBundleExtra("imageArrayList");
	pathNames = bundle.getStringArrayList("images");
	currentIndex = bundle.getInt("currentIndex");
	setBitmap();

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
	this.gestureFilter.onTouchEvent(ev);
	return super.dispatchTouchEvent(ev);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.magneticmule.toponimo.client.SimpleGestureFilter.SimpleGestureListener
     * #onSwipe(int)
     */
    public void onSwipe(int direction) {
	switch (direction) {
	case SimpleGestureFilter.SWIPE_LEFT:
	    if (currentIndex < pathNames.size() - 1) {
		imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
			R.anim.slide_in_right));
		imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(
			this, R.anim.slide_out_left));
		currentIndex++;

		setBitmap();
	    }
	    break;

	case SimpleGestureFilter.SWIPE_RIGHT:
	    if ((currentIndex != 0)) {
		currentIndex--;
		imageSwitcher.setInAnimation(AnimationUtils.loadAnimation(this,
			R.anim.slide_in_left));
		imageSwitcher.setOutAnimation(AnimationUtils.loadAnimation(
			this, R.anim.slide_out_right));

		setBitmap();
	    }
	    break;
	}
    }


    private void setBitmap() {
	String currentPath = pathNames.get(currentIndex).toString();
	currentPath = currentPath.substring(0, currentPath.length() - 10)
		+ ".jpg";
	Log.d("CurrentPath", currentPath.toString());

	// set this to a multiple of 2 reduce decoded image size
	Options bfOptions = new Options();
	bfOptions.inDensity = 1;

	if (bitmap != null) {
	    bitmap.recycle();
	    bitmap = null;
	}

	Bitmap bitmap = BitmapFactory.decodeFile(currentPath, bfOptions);

	drawable = new BitmapDrawable(bitmap);
	imageSwitcher.setImageDrawable(drawable);

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
	if (bitmap != null) {
	    bitmap.recycle();
	    bitmap = null;
	}
	super.onPause();
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ViewSwitcher.ViewFactory#makeView()
     */
    public View makeView() {
	ImageView imageView = new ImageView(this);
	imageView.setBackgroundColor(0xFF000000);
	imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
	imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
		LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

	return imageView;

    }

    /*
     * Enable full color support (non-Javadoc)
     * 
     * @see android.app.Activity#onAttachedToWindow()
     */
    @Override
    public void onAttachedToWindow() {
	super.onAttachedToWindow();
	Window window = getWindow();
	window.setFormat(PixelFormat.RGBA_8888);
	window.addFlags(WindowManager.LayoutParams.FLAG_DITHER);
    }

    public void onDoubleTap() {

    }
    
	@Override
	protected void onStart() {
		super.onStart();
		// start analytics tracking
		EasyTracker.getInstance().activityStart(this);
		
	}

	@Override
	protected void onStop() {
		super.onStop();
		// stop analytics tracking
		EasyTracker.getInstance().activityStop(this);

	}

}