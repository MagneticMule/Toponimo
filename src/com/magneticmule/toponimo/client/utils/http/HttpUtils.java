package com.magneticmule.toponimo.client.utils.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
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
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.magneticmule.toponimo.client.Constants;
import com.magneticmule.toponimo.client.utils.TwoReturnValues;

public class HttpUtils {

    public static final String TAG = "HttpUtils";

    /**
     * 
     * To convert the InputStream to String we use the Reader.read(char[]
     * buffer) method. We iterate until the Reader return -1 which means there's
     * no more data to read. We use the StringWriter class to produce the
     * string.
     * 
     * @param is
     * @return
     * @throws IOException
     */

    public static String convertStreamToString(InputStream is)
	    throws IOException {

	if (is != null) {
	    Writer writer = new StringWriter();
	    char[] buffer = new char[1024];
	    try {
		Reader reader = new BufferedReader(new InputStreamReader(is,
			"UTF-8"));
		int n;
		while ((n = reader.read(buffer)) != -1) {
		    writer.write(buffer, 0, n);
		}
	    } finally {
		is.close();
	    }
	    return writer.toString();
	} else {
	    return "";
	}
    }

    /**
     * 
     * @param s
     * @return
     */
    public String md5(String s) {
	try {
	    // Create MD5 Hash
	    MessageDigest digest = java.security.MessageDigest
		    .getInstance("MD5");
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
	ConnectivityManager cm = (ConnectivityManager) context
		.getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo netInfo = cm.getActiveNetworkInfo();
	if (netInfo != null && netInfo.isConnected()) {
	    return true;
	} else {
	    return false;
	}

    }

    /**
     * Passes the login credentials to the server.
     * 
     * @param userName
     * @param password
     * @param rememberMe
     * @param url
     * @return is
     */

    public static TwoReturnValues<Integer, String> authenticate(
	    DefaultHttpClient httpClient, String userName, String password,
	    String rememberMe, String url) {
	// DefaultHttpClient httpClient = new DefaultHttpClient();
	List<NameValuePair> userLogin = new ArrayList<NameValuePair>();
	TwoReturnValues<Integer, String> returnVals = null;
	InputStream is = null;
	String outputString = "";
	// userLogin.add(new BasicNameValuePair("UserLogin", null));
	userLogin.add(new BasicNameValuePair("UserLogin[username]", userName));
	userLogin.add(new BasicNameValuePair("UserLogin[password]", password));
	userLogin.add(new BasicNameValuePair("UserLogin[rememberme]",
		rememberMe));

	try {
	    HttpPost httpPost = new HttpPost(url);
	    UrlEncodedFormEntity entity = null;
	    try {
		entity = new UrlEncodedFormEntity(userLogin, "UTF-8");
		httpPost.setEntity(entity);
		HttpResponse response = httpClient.execute(httpPost);
		int statusCode = 0;
		try {
		    statusCode = response.getStatusLine().getStatusCode();
		    returnVals.firstVal = statusCode;
		} catch (NumberFormatException nfe) {

		}
		HttpEntity httpEntity = response.getEntity();
		if (statusCode == 200 && httpEntity != null) {
		    is = response.getEntity().getContent();
		    outputString = convertStreamToString(is);
		    returnVals.secondVal = outputString;
		    httpEntity.consumeContent();
		} else {
		    outputString = response.getStatusLine().getReasonPhrase();
		}
		Log.i("StatusCode", Integer.toString(statusCode));

	    } catch (ClientProtocolException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	} finally {
	}

	return returnVals;
    }

    /**
     * Construct a new HTTP GET request. This will predominately be used for
     * downloading JSON data from the Toponimo server.
     * 
     * @param url
     * @return
     */
    public static String getJSONData(DefaultHttpClient httpClient, String url) {
	URI uri;
	InputStream is = null;
	String outputString = "";
	try {
	    uri = new URI(url);
	    HttpGet httpGet = new HttpGet(uri);
	    httpGet.addHeader("Accept-Encoding", "gzip");
	    HttpResponse response = httpClient.execute(httpGet);
	    HttpEntity entity = response.getEntity();
	    if (entity != null) {
		is = entity.getContent();
		Header contentEncoding = response
			.getFirstHeader("Content-Encoding");
		if (contentEncoding != null
			&& contentEncoding.getValue().equalsIgnoreCase("gzip")) {
		    is = new GZIPInputStream(is);
		}
		outputString = convertStreamToString(is);
		// flush http contents
		entity.consumeContent();
	    }
	} catch (Exception e) {
	    Log.d(TAG, e.getMessage());
	    e.printStackTrace();
	} finally {
	}
	return outputString;

    }

    /**
     * Construct a new HTTP 'POST' request. This request will be used for
     * posting user generated words to the server and images.
     * 
     * @param httpClient
     * @param postParameters
     * @param uri
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void executeHttpPost(DefaultHttpClient httpClient,
	    List<NameValuePair> postParameters, String uri)
	    throws UnsupportedEncodingException, IOException {

	HttpResponse response;
	try {
	    HttpPost httpPost = new HttpPost(uri);
	    httpPost.addHeader("Accept-Encoding", "gzip");
	    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(
		    postParameters, "UTF-8");

	    httpPost.setEntity(entity);
	    response = httpClient.execute(httpPost);
	    HttpEntity httpEntity = response.getEntity();

	    // if the server returned anything other than 200 OK status
	    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		throw new IOException(response.getStatusLine()
			.getReasonPhrase());
	    }
	    if (httpEntity != null) {
		httpEntity.consumeContent();

	    }
	} finally {

	}
    }

    /**
     * Construct new multipart post request.
     * 
     * @param httpClient
     * @param postParameters
     * @param uri
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public static void executeHttpMultipartPost(DefaultHttpClient httpClient,
	    List<NameValuePair> postParameters, String uri, Uri fileName)
	    throws UnsupportedEncodingException, IOException {

	File file = new File(fileName.getPath());
	Log.d(TAG, fileName.getPath());

	HttpResponse response;

	try {
	    HttpPost httpPost = new HttpPost(uri);
	    httpPost.addHeader("Accept-Encoding", "gzip");

	    MultipartEntity entity = new MultipartEntity(
		    HttpMultipartMode.BROWSER_COMPATIBLE);

	    FileBody fileBody = new FileBody(file);

	    entity.addPart("userimage", fileBody);

	    // insert the name value pairs in to the form entity
	    for (int i = 0; i < postParameters.size(); i++) {
		entity.addPart(postParameters.get(i).getName(), new StringBody(
			postParameters.get(i).getValue()));
	    }

	    httpPost.setEntity(entity);
	    response = httpClient.execute(httpPost);

	    // if the server returned anything other than 200 OK status
	    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
		if (entity != null)
		    entity.consumeContent();
		String errorBody = EntityUtils.toString(response.getEntity());

		throw new IOException("Server returned: "
			+ response.getStatusLine().getStatusCode() + ": "
			+ response.getStatusLine().getReasonPhrase()
			+ errorBody);
	    }
	    if (entity != null) {
		entity.consumeContent();

	    }

	} finally {

	}
    }

    /**
     * Construct a new GET request. This method can be used for downloading
     * images from the Toponimo web server as well as other services such as
     * Google Maps.
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
	    Log.d("HttpUtils.class",
		    "Image read in"
			    + ((System.currentTimeMillis() - startTime / 1000) + " sec"));
	} catch (MalformedURLException e) {
	    Log.d("MalformedURLException",
		    "when trying to download image @ Webstransaction.class, StackTrace follows:");
	    e.printStackTrace();
	} catch (IOException e) {
	    Log.d("IOException",
		    " when trying to download image @ Webstransaction.class, StackTrace follows:");
	    e.printStackTrace();
	}

	return file;

    }

    public static final String GetHttpStatusMessage(int httpCode) {

	String message = Integer.toString(httpCode) + ": ";

	switch (httpCode) {
	case 200:
	    message += "OK";
	    break;
	case 400:
	    message += "Bad request";
	    break;
	case 401:
	    message += "Unauthroized";
	    break;
	case 402:
	    message += "Payment required";
	    break;
	case 404:
	    message += "Forbidden";
	    break;
	case 500:
	    message += "Internal server error";
	    break;
	case 501:
	    message += "Not implemented on server";
	    break;
	}

	return message;

    }

}
