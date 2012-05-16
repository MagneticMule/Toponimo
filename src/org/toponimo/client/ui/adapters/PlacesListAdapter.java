package org.toponimo.client.ui.adapters;

import java.util.List;

import org.toponimo.client.structures.placestructure.Place;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.toponimo.client.R;

public class PlacesListAdapter extends ArrayAdapter<Place> {

    private static List<Place> objects;
    private final Context context;

    public PlacesListAdapter(Context _context, int textViewResourceId,
	    List<Place> _objects) {
	super(_context, textViewResourceId, _objects);
	this.context = _context;
	objects = _objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
	View view = convertView;
	if (view == null) {
	    LayoutInflater inflater = (LayoutInflater) context
		    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    view = inflater.inflate(R.layout.simplerow, null);
	}

	Place place = objects.get(position);
	if (place != null) {

	    String placeName = place.getResults().get(position).getName();
	    if (placeName != null) {
		TextView tvName = (TextView) view.findViewById(R.id.label);
		tvName.setText(place.getResults().get(position).getName());
	    }

	    TextView addressTextView = (TextView) view
		    .findViewById(R.id.label_address);

	    addressTextView.setText(place.getResults().get(position)
		    .getVicinity());

	    int last = place.getResults().get(position).getTypes().size();

	    String concatPlaceTypes = "";

	    for (int i = 0; i < last; i++) {
		String punct;
		if (i != (last - 1)) {
		    punct = ", ";
		} else {
		    punct = ".";
		}

		String currentType = place.getResults().get(position)
			.getTypes().get(i);
		if (currentType != null && currentType != "establishment") {
		    concatPlaceTypes = (concatPlaceTypes + currentType + punct);
		}
	    }

	    String finalPlaceTypes = concatPlaceTypes.replace('_', ' ');
	    TextView wordName = (TextView) view.findViewById(R.id.label_words);
	    wordName.setText(finalPlaceTypes);

	}

	return (view);
    }

}