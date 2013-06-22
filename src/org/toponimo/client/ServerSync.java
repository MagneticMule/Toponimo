package org.toponimo.client;

import org.toponimo.client.models.webster.Word;
import org.toponimo.client.utils.http.ToponimoVolley;

import rest.WordRequest;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class ServerSync {

    private final RequestQueue mQueue;

    public ServerSync(RequestQueue queue) {
        mQueue = queue;

    }

  //  public Request<?> downloadWords(String lemma, Listener<Word> listener, ErrorListener errorListener) {
  //      return mQueue.add(new WordRequest(lemma, listener, errorListener));

  //  }
    
    public Request<?> downloadWord(String wordId, Listener<Word> listener, ErrorListener errorListener) {
        return mQueue.add(new WordRequest(wordId, listener, errorListener));

    }

    public Request downloadImages(String lemma) {
        return null;
    }

    public Request downloadLocations(String lat, String lng) {
        return null;
    }

    public Request uploadImages() {
        return null;
    }

    public void uploadWords() {

    }

}
