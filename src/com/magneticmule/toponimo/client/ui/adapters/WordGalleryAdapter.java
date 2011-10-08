package com.magneticmule.toponimo.client.ui.adapters;

import com.magneticmule.toponimo.client.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class WordGalleryAdapter extends BaseAdapter {
	
	private Context context;
	
	public WordGalleryAdapter(Context c){
		context = c;
	}

	public int getCount() {
		
		return 3;
	}

	public Object getItem(int position) {
		
		return position;
	}

	public long getItemId(int position) {
		
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(context);
		
		imageView.setImageResource(R.drawable.noimage180);
		
		return imageView;
	}

}
