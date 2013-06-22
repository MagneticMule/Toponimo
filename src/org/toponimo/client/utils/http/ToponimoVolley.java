package org.toponimo.client.utils.http;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class ToponimoVolley {

	private static final String	TAG	= "ToponimoVolley";

	private static RequestQueue	mRequestQueue;
	private static ImageLoader	mImageLoader;

	public ToponimoVolley() throws IllegalAccessException {
		// no instances
		throw new IllegalAccessException(TAG + " cannot be instantiated");
	}

	public static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
		// mImageLoader = new ImageLoader(mRequestQueue, new
		// BitmapLruCache(MAX_IMAGE_CACHE_ENTIRES));
	}

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}


	public static ImageLoader getImageLoader() {
		if (mImageLoader != null) {
			return mImageLoader;
		} else {
			throw new IllegalStateException("ImageLoader not initialized");
		}
	}

}
