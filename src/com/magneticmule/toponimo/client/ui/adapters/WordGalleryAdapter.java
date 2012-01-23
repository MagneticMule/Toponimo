package com.magneticmule.toponimo.client.ui.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class WordGalleryAdapter extends ArrayAdapter<String> {

	public static final String TAG = "WordGalleryAdapter";

	private Context context;

	private static List<String> imagePaths;

	public WordGalleryAdapter(Context _context, List<String> _list) {
		super(_context, 0);
		context = _context;
		imagePaths = _list;
	}

	public int getCount() {

		return imagePaths != null ? imagePaths.size() : 0;
	}

	public String getItem(String position) {

		return position;
	}

	public long getItemId(int position) {

		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
		if (!imagePaths.isEmpty()) {
			Bitmap bitmap = BitmapFactory.decodeFile(imagePaths.get(position));
			imageView.setImageBitmap(bitmap);
		}
		return imageView;
	}
}
