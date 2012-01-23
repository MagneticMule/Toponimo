package com.magneticmule.toponimo.client.ui.adapters;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.magneticmule.toponimo.client.R;

public class WordListAdapter extends ArrayAdapter<String> {

	private static final String TAG = "WordListAdapter";

	private static List<String> words;
	private Context context;

	public WordListAdapter(Context _context, int textViewResourceId,
			List<String> _words) {
		super(_context, textViewResourceId, _words);
		context = _context;
		words = _words;
	}

	@Override
	public int getCount() {
		return words != null ? words.size() : 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.word_row, null);
		}

		String word = words.get(position);
		Log.d("Position " + word + " " + TAG, Integer.toString(position));
		if (word != null) {
			TextView tv = (TextView) view.findViewById(R.id.word_row_word_view);
			if (tv != null) {
				tv.setText(word);
			}
		}
		return (view);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return super.getItemId(position);
	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return super.getItem(position);
	}

}
