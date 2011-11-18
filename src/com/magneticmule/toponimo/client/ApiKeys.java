package com.magneticmule.toponimo.client;

/**
 * 
 * Place to keep constant values such as API keys which should remain private.
 * 
 */

public class ApiKeys {

	public static final String	GOOGLE_API_KEY					= "AIzaSyARV2mbzLBlF697bCAa03UysnaF2FK2eec";

	public static final String	FACEBOOK_API_KEY				= "100575183364254";

	public static final String	FACEBOOK_API_SECRET			= "935c7758a9dd88b349b43ce42d31d20b";

	public static final String	DICTIONARY_BASE_ADDRESS	= "http://en.wiktionary.org/w/api.php?action=query&prop=revisions&titles=%s&rvprop=content&format=json%s";

	public static final String	DEFINITION_URL					= "http://www.api.toponimo.org/definition.php?word=";

	public static final String	DOWNLOAD_URL						= "http://api.toponimo.org?";

	public static final String	UPLOAD_URL							= "http://api.toponimo.org/upload.php/?";

	public static final String	LOGIN_URL								= "http://www.toponimo.org/toponimo/auth/login";

}
