package rest;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.toponimo.client.utils.http.HttpUtils;
import org.toponimo.client.utils.http.ToponimoVolley;
import org.toponimo.client.ApiKeys;
import org.toponimo.client.ToponimoApplication;
import org.toponimo.client.models.Place;
import org.toponimo.client.models.Results;

public class ToponimoApi {
	
	RequestQueue queue;

	public ToponimoApi() {
		queue = ToponimoVolley.getRequestQueue();
	}
	
	
	
	public void locationGet(Request request) {
		// Read cache from database
		
		
	}
	

	
	private Response.Listener<String> successListener() {
		return new Response.Listener<String>() {

			public void onResponse(String response) {
				Gson gson = new Gson();

			}

		};
	}

	private Response.ErrorListener errorListener() {
		return new Response.ErrorListener() {

			public void onErrorResponse(VolleyError error) {

			}
		};
	}
	
	

}
