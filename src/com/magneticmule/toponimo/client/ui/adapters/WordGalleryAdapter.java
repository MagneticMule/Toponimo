package com.magneticmule.toponimo.client.ui.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

import com.magneticmule.toponimo.client.R;

public class WordGalleryAdapter extends ArrayAdapter<String> {

    public static final String TAG = "WordGalleryAdapter";

    private final Context mContext;

    private static ArrayList<String> mImagePathsList;

    int mGalleryBackground;

    /**
     * Constructor: assigns passed values to member variables
     * 
     * @param _context
     * @param _list
     */
    public WordGalleryAdapter(Context _context, ArrayList<String> _list) {
	super(_context, 0);
	mContext = _context;
	mImagePathsList = _list;
	TypedArray attributes = mContext
		.obtainStyledAttributes(R.styleable.WordImageGallery);
	mGalleryBackground = attributes.getResourceId(
		R.styleable.WordImageGallery_android_galleryItemBackground, 0);

    }

    /*
     * Returns the size of the list containing the paths to the images
     * (non-Javadoc)
     * 
     * @see android.widget.ArrayAdapter#getCount()
     */
    @Override
    public int getCount() {
	return mImagePathsList != null ? mImagePathsList.size() : 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.widget.ArrayAdapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	ImageView imageView = new ImageView(mContext);
	if (!mImagePathsList.isEmpty()) {
	    Bitmap bitmap = BitmapFactory.decodeFile(mImagePathsList
		    .get(position));
	    imageView.setImageBitmap(bitmap);
	    imageView.setLayoutParams(new Gallery.LayoutParams(85, 85));
	    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
	    imageView.setBackgroundResource(R.drawable.drop_shadow_patch);
	    imageView.setPadding(6, 6, 6, 6);
	    imageView.setAnimation(AnimationUtils.loadAnimation(mContext,
		    android.R.anim.fade_in));

	}
	return (imageView);
    }
}
