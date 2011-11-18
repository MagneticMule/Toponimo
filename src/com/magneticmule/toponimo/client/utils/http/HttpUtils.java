package com.magneticmule.toponimo.client.utils.http;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.magneticmule.toponimo.client.*;

public class HttpUtils {

	public static final String	TAG	= "HttpUtils";	;

	public String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static Boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		} else {
			return false;
		}

	}

/**
 * Passes the login credentials to the server. 
 * @param userName
 * @param password
 * @param rememberMe
 * @param url
 * @return is
 */

	public static InputStream authenticate(String userName, String password, String rememberMe, String url) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		List<NameValuePair> userLogin = new ArrayList<NameValuePair>();
		InputStream is = null;
		userLogin.add(new BasicNameValuePair("username", userName));
		userLogin.add(new BasicNameValuePair("password", password));
		userLogin.add(new BasicNameValuePair("rememberme", rememberMe));
		userLogin.add(new BasicNameValuePair("UserLogin", null));

		try {
			HttpPost httpPost = new HttpPost(url);
			UrlEncodedFormEntity entity = null;
			try {
				entity = new UrlEncodedFormEntity(userLogin, "UTF-8");
				httpPost.setEntity(entity);
				HttpResponse response = httpClient.execute(httpPost);				
				String statusCode = Integer.toString(response.getStatusLine().getStatusCode());
				Log.i("StatusCode", statusCode);
				is = response.getEntity().getContent();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return is;
	}

	/**
	 * Construct a new HTTP GET request. This will predominately be used for
	 * downloading JSON data from the Toponimo server.
	 * 
	 * @param url
	 * @return
	 */
	public static InputStream getJSONData(String url) {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		URI uri;
		InputStream is = null;
		try {
			uri = new URI(url);
			HttpPost httpPost = new HttpPost(uri);
			httpPost.addHeader("Accept-Encoding", "gzip");
			HttpResponse response = httpClient.execute(httpPost);
			is = response.getEntity().getContent();
			Header contentEncoding = response.getFirstHeader("Content-Encoding");
			if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
				is = new GZIPInputStream(is);
			}
		} catch (Exception e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return is;

	}

	/**
	 * Construct a new HTTP 'POST' request. This request will be used for posting
	 * user generated words to the server.
	 * 
	 * @param words
	 * @param pid
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static void executeHttpPost(List<NameValuePair> postParameters) throws UnsupportedEncodingException, IOException {
		String uri = ApiKeys.UPLOAD_URL;
		HttpClient httpClient = new DefaultHttpClient();
		try {
			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			HttpPost httpPost = new HttpPost(uri);
			httpPost.addHeader("Accept-Encoding", "gzip");
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(postParameters, "UTF-8");
			httpPost.setEntity(entity);
			String response = httpClient.execute(httpPost, responseHandler);
			Log.i(TAG, response);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	/**
	 * Construct a new GET request. This method can be used for downloading images
	 * from the Toponimo web server as well as other services such as Google Maps.
	 * 
	 * @param url
	 * @param filename
	 * @return Bitmap
	 */
	public static File getWebImage(String url, String filename) {
		final String PATH = Constants.LOCAL_IMAGE_STORE;
		String completedFilename = new String(PATH.concat(filename));
		File file = null;

		try {
			URL myFileUrl = new URL(url);
			file = new File(completedFilename);
			long startTime = System.currentTimeMillis();
			Log.d(TAG, "Starting Download");
			URLConnection urlConnect = myFileUrl.openConnection();

			// define input streams to read data from the url
			InputStream is = urlConnect.getInputStream();
			BufferedInputStream bufferedis = new BufferedInputStream(is);

			// read bytes to the buffer until finished
			ByteArrayBuffer byteBuffer = new ByteArrayBuffer(100);
			int current = 0;
			while ((current = bufferedis.read()) != -1) {
				byteBuffer.append((byte) current);
			}

			// convert byte array to string
			FileOutputStream fileOutStream = new FileOutputStream(file);
			fileOutStream.write(byteBuffer.toByteArray());
			fileOutStream.close();
			Log.d("WebDataHelper.class", "Image read in" + ((System.currentTimeMillis() - startTime / 1000) + " sec"));
		} catch (MalformedURLException e) {
			Log.d("MalformedURLException", "when trying to download image @ Webstransaction.class, StackTrace follows:");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("IOException", " when trying to download image @ Webstransaction.class, StackTrace follows:");
			e.printStackTrace();
		}

		return file;

	}

}