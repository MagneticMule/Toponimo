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

    private Context context;

    private static ArrayList<String> imagePaths;

    int galleryBackground;

    public WordGalleryAdapter(Context _context, ArrayList<String> _list) {
	super(_context, 0);
	context = _context;
	imagePaths = _list;
	TypedArray attributes = context
		.obtainStyledAttributes(R.styleable.WordImageGallery);
	galleryBackground = attributes.getResourceId(
		R.styleable.WordImageGallery_android_galleryItemBackground, 0);

    }

    @Override
    public int getCount() {

	return imagePaths != null ? imagePaths.size() : 0;
    }

    public String getItem(String position) {

	return position;
    }

    @Override
    public long getItemId(int position) {

	return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	ImageView imageView = new ImageView(context);
	if (!imagePaths.isEmpty()) {
	    Bitmap bitmap = BitmapFactory.decodeFile(imagePaths.get(position));
	    imageView.setImageBitmap(bitmap);
	    imageView.setLayoutParams(new Gallery.LayoutParams(85, 85));
	    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
	    // imageView.setBackgroundColor(galleryBackground);
	    imageView.setBackgroundResource(R.drawable.drop_shadow_patch);
	    imageView.setPadding(6, 6, 6, 6);
	    imageView.setAnimation(AnimationUtils.loadAnimation(context,
		    android.R.anim.fade_in));

	}
	return imageView;
    }
}
