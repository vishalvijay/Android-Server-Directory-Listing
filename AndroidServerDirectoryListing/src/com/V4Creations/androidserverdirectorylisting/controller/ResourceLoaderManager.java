package com.V4Creations.androidserverdirectorylisting.controller;

import java.util.ArrayList;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;

import com.V4Creations.androidserverdirectorylisting.R;
import com.V4Creations.androidserverdirectorylisting.model.ResourceItem;
import com.V4Creations.androidserverdirectorylisting.model.ResourceStackItem;
import com.V4Creations.androidserverdirectorylisting.model.interfaces.ResourceLoaderInterface;
import com.V4Creations.androidserverdirectorylisting.util.RestClient;
import com.V4Creations.androidserverdirectorylisting.util.SystemFeatureChecker;
import com.loopj.android.http.JsonHttpResponseHandler;

public class ResourceLoaderManager extends JsonHttpResponseHandler {
	String TAG = "ResourceLoaderManager";
	private static final String RESOURCE_MAIN = "/androidDirectoryListing.php";
	private final String SORT_URL = "?sort=date&order=asc";
	public static final String KEY_RESOURCES = "items", KEY_HREF = "href",
			KEY_NAME = "name", KEY_EXT = "ext", KEY_SIZE = "size",
			KEY_DATE = "date";

	private Stack<ResourceStackItem> mStack;
	private Context context;
	private ResourceLoaderInterface mResourceLoaderInterface;
	private int selectedIndex = -1;
	private boolean isReloadEnabled = false, isLoading = false;

	public ResourceLoaderManager(Context context,
			ResourceLoaderInterface resourceLoaderInterface) {
		mStack = new Stack<ResourceStackItem>();
		this.context = context;
		mResourceLoaderInterface = resourceLoaderInterface;
	}

	public void loadDirectory(String href, int selectedIndex) {
		isLoading = true;
		mResourceLoaderInterface.onStartLoading();
		if (SystemFeatureChecker.isInternetConnection(context)) {
			this.selectedIndex = selectedIndex;
			String newUrl = RESOURCE_MAIN + (href == null ? SORT_URL : href);
			RestClient.loadResource(newUrl, this);
		} else
			onFailure(
					405,
					new Throwable(context
							.getString(R.string.internet_not_available)),
					new JSONObject());
	}

	public void downloadFile(Activity activity, String href) {
		SystemFeatureChecker.downloadFile(activity, RestClient.BASE_URL
				+ RESOURCE_MAIN + href, false);
	}

	public boolean isGoBack() {
		return mStack.size() > 1 && !isReloading();
	}

	public boolean isReloading() {
		return isLoading;
	}

	public void goBack() {
		if (isGoBack()) {
			mResourceLoaderInterface.onStartLoading();
			mStack.pop();
			mResourceLoaderInterface.onLoadingSuccess(mStack.lastElement());
		}
	}

	public void reload() {
		String url = null;
		if (!mStack.isEmpty())
			url = mStack.lastElement().mUrl;
		isReloadEnabled = true;
		loadDirectory(url, -1);
	}

	@Override
	public void onSuccess(JSONObject response) {
		isLoading = false;
		ResourceStackItem resourceStackItem;
		if (!isReloadEnabled || mStack.isEmpty()) {
			ArrayList<ResourceItem> resourceItems = parseJson(response);
			String parentDirectoryName = null, parentDirectoryHref = null;
			if (selectedIndex != -1 && !mStack.isEmpty()) {
				ResourceItem resourceItem = mStack.lastElement().mResourceItems
						.get(selectedIndex);
				parentDirectoryName = resourceItem.name;
				parentDirectoryHref = resourceItem.href;
				selectedIndex = -1;
			}
			resourceStackItem = new ResourceStackItem(resourceItems,
					parentDirectoryName, parentDirectoryHref);
		} else {
			ResourceStackItem tempResourceStackItem = mStack.pop();
			resourceStackItem = new ResourceStackItem(parseJson(response),
					tempResourceStackItem.mDirName, tempResourceStackItem.mUrl);
		}
		isReloadEnabled = false;
		mStack.push(resourceStackItem);
		mResourceLoaderInterface.onLoadingSuccess(resourceStackItem);
	}

	@Override
	public void onFailure(int statusCode, Throwable e, JSONObject errorResponse) {
		isLoading = false;
		selectedIndex = -1;
		isReloadEnabled = false;
		String message = statusCode != 405 ? context
				.getString(R.string.default_connection_error) : e.getMessage();
		mResourceLoaderInterface.onLoadingFailure(message, e.getMessage() + "");
	}

	private ArrayList<ResourceItem> parseJson(JSONObject jsonObject) {
		ArrayList<ResourceItem> resourceItems = new ArrayList<ResourceItem>();
		JSONArray resourceItemJsonArray;
		try {
			resourceItemJsonArray = jsonObject.getJSONArray(KEY_RESOURCES);
			for (int i = 0; i < resourceItemJsonArray.length(); i++) {
				JSONObject item = resourceItemJsonArray.getJSONObject(i);
				ResourceItem resourceItem = new ResourceItem();
				resourceItem.href = item.getString(KEY_HREF);
				resourceItem.name = item.getString(KEY_NAME);
				resourceItem.size = item.getString(KEY_SIZE);
				resourceItem.date = item.getString(KEY_DATE);
				resourceItem.ext = item.getString(KEY_EXT);
				resourceItems.add(resourceItem);
			}
		} catch (JSONException e) {
		}
		return resourceItems;
	}
}
