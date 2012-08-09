package org.toponimo.client.ui.adapters;

import org.toponimo.client.Constants;
import org.toponimo.client.utils.MillisToDate;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.toponimo.client.R;

public class WordBankCursorAdapter extends SimpleCursorAdapter {

	public static final String		TAG	= "WordBankAdapter";

	private final Cursor			mCursor;
	private final Context			mContext;
	private final LayoutInflater	mInflater;

	public WordBankCursorAdapter(Context context, Cursor cursor, String[] from, int[] to) {
		super(context, 0, cursor, from, to);

		this.mContext = context;
		this.mCursor = cursor;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {
		return mCursor != null ? mCursor.getCount() : 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		/*
		 * if (view == null) { if (mCursor.isFirst() && mCursor.isLast()) { view
		 * = mInflater.inflate(R.layout.simplerow_definition_single, null);
		 * Log.i(TAG, "Cusrsor points to single entry");
		 * 
		 * } else if (mCursor.isFirst()) { view =
		 * mInflater.inflate(R.layout.simplerow_definition_bottom, null);
		 * Log.i(TAG, "Cusrsor points to first entry"); } else if
		 * (mCursor.isLast()) { view =
		 * mInflater.inflate(R.layout.simplerow_definition_top, null);
		 * Log.i(TAG, "Cusrsor points to last entry"); } else { view =
		 * mInflater.inflate(R.layout.simplerow_definition_middle, null);
		 * Log.i(TAG, "Cusrsor points to standard entry"); } }
		 */

		if (view == null) {
			view = mInflater.inflate(R.layout.simplerow_definition_single, null);
		}

		mCursor.moveToPosition(position);

		TextView wordView = (TextView) view.findViewById(R.id.label_word);
		TextView definitionView = (TextView) view.findViewById(R.id.label_definition);
		TextView glossView = (TextView) view.findViewById(R.id.label_gloss);
		TextView locationView = (TextView) view.findViewById(R.id.label_location);
		TextView lexTypeView = (TextView) view.findViewById(R.id.label_type);
		

		wordView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD)));
		definitionView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_DEFINITION)));
		glossView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_GLOSS)));
		locationView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_LOCATION)));
		lexTypeView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_TYPE)));

		String theTime = MillisToDate.millisecondsToDate(mCursor.getLong(mCursor
				.getColumnIndex(Constants.KEY_WORD_TIME)));

		Log.v("Collected at ", theTime);

		return view;
	}
	


	private static class ViewHolder {
		TextView	wordView;
		ImageView	imageView;
		TextView	typeView;
		TextView	definitionView;
		TextView	glossView;
		TextView	locationView;

	}

}
