package rest;

import org.toponimo.client.models.webster.WebsterEntries;
import org.toponimo.client.models.webster.Word;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.google.gson.Gson;

public class WordRequest extends Request<Word> {
    
    private final Listener<Word> mListener;
    
    private final Gson mGson = new Gson();

    public WordRequest(String _wordId, Listener<Word> _listener, ErrorListener _errorListener) {
        super(Method.GET, _wordId, _errorListener);
        mListener = _listener;
    }

    @Override
    protected Response<Word> parseNetworkResponse(NetworkResponse response) {
        String json = new String(response.data);
        WebsterEntries entries = mGson.fromJson(json, WebsterEntries.class);
        
        return Response.success(entries.getWords().get(0), getCacheEntry());
    }

    @Override
    protected void deliverResponse(Word response) {
       mListener.onResponse(response);
        
    }



}
