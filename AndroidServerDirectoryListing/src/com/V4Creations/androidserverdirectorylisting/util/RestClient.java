package com.V4Creations.androidserverdirectorylisting.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RestClient {
	String TAG = "VTULifeRestClient";

	public static final String BASE_URL = "http://www.vtulife.com";
	public static String KEY_SUCCESS = "message", KEY_ERROR = "error";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void loadResource(String url,
			JsonHttpResponseHandler jsonHttpResponseHandler) {
		client.get(getAbsoluteUrl(url), new RequestParams(),
				jsonHttpResponseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		return BASE_URL + relativeUrl;
	}
}
