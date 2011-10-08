package com.magneticmule.toponimo.client.ui.adapters;

import java.util.List;

import com.magneticmule.toponimo.client.R;
import com.magneticmule.toponimo.client.ToponimoApplication;
import com.magneticmule.toponimo.client.placestructure.Place;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlacesListAdapter extends ArrayAdapter<Place> {

	private static List<Place>	objects;
	private Context							context;

	ToponimoApplication					application;

	// used for list item background color

	public PlacesListAdapter(Context context, int textViewResourceId, List<Place> _objects) {
		super(context, textViewResourceId, _objects);
		this.context = context;
		PlacesListAdapter.objects = _objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.simplerow, null);
		}

		Place place = objects.get(position);
		if (place != null) {

			String placeName = place.getResults().get(position).getName();
			if (placeName != null) {
				TextView tvName = (TextView) view.findViewById(R.id.label);
				tvName.setText(place.getResults().get(position).getName());
			}

			TextView tvAddress = (TextView) view.findViewById(R.id.label_address);
			tvAddress.setText(place.getResults().get(position).getVicinity());

			Log.i("PlistAdapter", place.getResults().get(position).getName());

			int last = place.getResults().get(position).getTypes().size();

			String concatPlaceTypes = "";

			for (int i = 0; i < last; i++) {
				String punct;
				if (i != (last - 1)) {
					punct = ", ";
				} else {
					punct = ".";
				}

				String currentType = place.getResults().get(position).getTypes().get(i);
				if (currentType != null && currentType != "establishment") {
					concatPlaceTypes = (concatPlaceTypes + currentType + punct);
				}
			}

			String finalPlaceTypes = concatPlaceTypes.replace('_', ' ');

			
			TextView wordName = (TextView) view.findViewById(R.id.label_words);
			wordName.setText(finalPlaceTypes);
			// ImageView image = (ImageView) view.findViewById(R.id.icon);

			// String placeType = place.getResults().get(position).getTypes()
			// .get(0).toString();
			// defineIcon(placeType, image);

		}

		return (view);
	}

	@SuppressWarnings("unused")
	private void defineIcon(String returnedType, ImageView image) {
		if ((returnedType.equalsIgnoreCase("locality") || (returnedType.equalsIgnoreCase("sublocality")))) {
			image.setImageResource(R.drawable.geocode);
		} else if ((returnedType.equalsIgnoreCase("grocery_or_supermarket") || (returnedType.equalsIgnoreCase("store") || (returnedType
				.equalsIgnoreCase("electronics_store"))))) {
			image.setImageResource(R.drawable.shopping);
		} else if ((returnedType.equalsIgnoreCase("bar") || (returnedType.equalsIgnoreCase("night_club")))) {
			image.setImageResource(R.drawable.bar);
		} else if (returnedType.equalsIgnoreCase("establishment")) {
			image.setImageResource(R.drawable.genericbusiness);
		} else if (returnedType.equalsIgnoreCase("beauty_salon")) {
			image.setImageResource(R.drawable.barber);
		} else if (returnedType.equalsIgnoreCase("travel_agency")) {
			image.setImageResource(R.drawable.travelagent);
		} else if ((returnedType.equalsIgnoreCase("cafe") || (returnedType.equalsIgnoreCase("food") || (returnedType
				.equalsIgnoreCase("meal_delivery") || (returnedType.equalsIgnoreCase("restaurant")))))) {
			image.setImageResource(R.drawable.restaurant);
		} else if (returnedType.equalsIgnoreCase("lodging")) {
			image.setImageResource(R.drawable.lodging);
		} else if (returnedType.equalsIgnoreCase("car_dealer")) {
			image.setImageResource(R.drawable.cardealer);
		} else if (returnedType.equalsIgnoreCase("movie_theater")) {
			image.setImageResource(R.drawable.movies);
		}

	}

}