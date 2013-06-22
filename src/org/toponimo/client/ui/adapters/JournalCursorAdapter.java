package org.toponimo.client.ui.adapters;

import java.io.File;
import java.util.UUID;

import org.toponimo.client.Constants;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.ui.DrawActivity;
import org.toponimo.client.utils.MillisToDate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.Html;
import android.text.Spannable;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.TextView;

import org.toponimo.client.R;

public class JournalCursorAdapter extends SimpleCursorAdapter {

    public static final String   TAG = "JournalCursorAdapter";

    private Cursor               mCursor;
    private final Context        mContext;
    private final LayoutInflater mInflater;
    private ViewHolder           holder;
    private Spannable            mSpannable;
    private ToponimoApplication  mApplication;

    public JournalCursorAdapter(Context context, int layout, Cursor cursor, String[] from, int[] to, int flags) {
        super(context, layout, cursor, from, to, flags);
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mApplication = (ToponimoApplication) mContext.getApplicationContext();

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "GetView");
        mCursor = getCursor();
        mCursor.moveToPosition(position);
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.journal_entry, null);
            holder = new ViewHolder();
            // dictionary entry
            holder.wordView = (TextView) view.findViewById(R.id.label_word);
            holder.definitionView = (TextView) view.findViewById(R.id.label_definition);
            holder.glossView = (TextView) view.findViewById(R.id.label_gloss);
            holder.locationView = (TextView) view.findViewById(R.id.label_location);
            holder.lexTypeView = (TextView) view.findViewById(R.id.label_type);
            holder.pictureView = (ImageView) view.findViewById(R.id.image_view_picture);
            holder.timeView = (TextView) view.findViewById(R.id.label_time);

            // actions menu
            holder.addImage = (ImageView) view.findViewById(R.id.journal_entry_add_image);
            holder.addDrawing = (ImageView) view.findViewById(R.id.journal_entry_add_drawing);
            holder.addLocation = (ImageView) view.findViewById(R.id.journal_entry_add_location);
            holder.addNote = (ImageView) view.findViewById(R.id.journal_entry_add_note);
            holder.addSound = (ImageView) view.findViewById(R.id.journal_entry_add_sound);

            view.setTag(holder);
        } else {
            // get reference to the viewholder for low cost retrieval of views
            holder = (ViewHolder) view.getTag();
        }

        holder.wordView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_LEMMA)));

        String definition = mCursor.getString(mCursor.getColumnIndex(Constants.KEY_DEFINITION));
        if (definition != null) {
            holder.definitionView.setText(Html.fromHtml(definition));
        }

        holder.glossView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_PRONOUNCIATION)));
        holder.locationView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_LOCATION)));

        // holder.pictureView.setVisibility(View.VISIBLE);
        holder.lexTypeView.setText(mCursor.getString(mCursor.getColumnIndex(Constants.KEY_WORD_POS)));

        holder.timeView.setText(DateUtils.getRelativeTimeSpanString(
                mCursor.getLong(mCursor.getColumnIndex(Constants.KEY_WORD_TIME)), System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS));

        // set the image using the path located in the database. If there is no
        // image then we need to TODO: switch the layout.
        String imagePath = mCursor.getString(mCursor.getColumnIndex(Constants.KEY_IMAGE_FILEPATH));
        if (imagePath != null) {
            holder.pictureView.setImageURI(Uri.parse(imagePath));
        } else {
            holder.pictureView.setImageDrawable(null);
        }
        return view;
    }

    /* Static class to hold reference to views for quick recycling */
    private static class ViewHolder {

        // holders for dictionary entry
        public TextView  wordView;
        public ImageView pictureView;
        public TextView  typeView;
        public TextView  definitionView;
        public TextView  glossView;
        public TextView  lexTypeView;
        public TextView  locationView;
        public TextView  timeView;

        // holders for swipe icons
        public ImageView addImage;
        public ImageView addDrawing;
        public ImageView addLocation;
        public ImageView addNote;
        public ImageView addSound;

    }

}
