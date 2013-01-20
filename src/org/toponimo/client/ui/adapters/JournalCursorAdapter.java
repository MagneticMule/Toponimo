package org.toponimo.client.ui.adapters;

import org.toponimo.client.Constants;
import org.toponimo.client.utils.MillisToDate;

import android.content.Context;
import android.database.Cursor;
import android.text.Spannable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.toponimo.client.R;

public class JournalCursorAdapter extends SimpleCursorAdapter {

	public static final String		TAG	= "JournalCursorAdapter";

	private Cursor					mCursor;
	private final Context			mContext;
	private final LayoutInflater	mInflater;
	private ViewHolder				holder;
	private Spannable				mSpannable;

	public JournalCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
		super(context, layout, cursor, from, to, flags);
		this.mContext = context;
		this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}



	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "GetView");
		mCursor=getCursor();
		mCursor.moveToPosition(position);		
		View view = convertView;
		if (view == null) {
			view = mInflater.inflate(R.layout.simplerow_definition_single, null);
			holder = new ViewHolder();
			holder.wordView = (TextView) view.findViewById(R.id.label_word);
			holder.definitionView = (TextView) view.findViewById(R.id.label_definition);
			holder.glossView = (TextView) view.findViewById(R.id.label_gloss);
			holder.locationView = (TextView) view.findViewById(R.id.label_location);
			holder.lexTypeView = (TextView) view.findViewById(R.id.label_type);
			holder.pictureView = (ImageView) view.findViewById(R.id.image_view_picture);
			holder.timeView = (TextView) view.findViewById(R.id.label_time);
			view.setTag(holder);
		} else {
			// get reference to the viewholder for low cost retrieval of views
			holder = (ViewHolder) view.getTag();
		}


		holder.wordView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD)));
		Log.d(Integer.toString(mCursor.getPosition()), mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD)));
		holder.definitionView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_DEFINITION)));
		holder.glossView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_GLOSS)));
		holder.locationView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_LOCATION)));

		// holder.pictureView.setVisibility(View.VISIBLE);
		holder.lexTypeView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_TYPE)));

		holder.timeView.setText(DateUtils.getRelativeTimeSpanString(
				mCursor.getLong(mCursor.getColumnIndex(Constants.KEY_WORD_TIME)), System.currentTimeMillis(),
				DateUtils.SECOND_IN_MILLIS));

		return view;
	}

	/* Static class to hold reference to views for quick recycling */
	private static class ViewHolder {
		public TextView		wordView;
		public ImageView	pictureView;
		public TextView		typeView;
		public TextView		definitionView;
		public TextView		glossView;
		public TextView		lexTypeView;
		public TextView		locationView;
		public TextView		timeView;

	}

}
