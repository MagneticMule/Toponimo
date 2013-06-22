package rest;

import org.toponimo.client.ToponimoApplication;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;

public class PlaceRequest<T> extends Request<T> implements ISaveable {
	
	ToponimoApplication app;

	public PlaceRequest(int method, String url, ErrorListener listener) {
		super(method, url, listener);
		app = ToponimoApplication.getApp();
		
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		
		return null;
	}

	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
		
	}

	public int writeRestResultToDatabase() {
		
		return 0;
	}

	public int writeLocalResultToDatabase() {
		// TODO Auto-generated method stub
		return 0;
	}






}
