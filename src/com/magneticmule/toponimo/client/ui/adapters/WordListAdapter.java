package com.magneticmule.toponimo.client.ui.adapters;

import java.util.ArrayList;
import java.util.List;

import com.magneticmule.toponimo.client.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class WordListAdapter extends ArrayAdapter<String> {
	
	private static final String TAG = "WordListAdapter";

	private static List<String>	words;
	private Context							context;

	public WordListAdapter(Context _context, int textViewResourceId, List<String> list) {
		super(_context, textViewResourceId, list);
		this.context = _context;
		words = list;
		Log.i(TAG + " Constructor",words.get(0).toString());
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.i(TAG + " Getview",words.get(0).toString());
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.word_row, null);
		}

		String word = words.get(position).toString();
		Log.i(TAG,word);
		Log.i(TAG,words.get(0));
		if (word != null) {
			TextView tv = (TextView) view.findViewById(R.id.word_row_word_view);
			if (tv != null) {
				tv.setText(word.toString());
			}
		}
		return (view);
	}

}
