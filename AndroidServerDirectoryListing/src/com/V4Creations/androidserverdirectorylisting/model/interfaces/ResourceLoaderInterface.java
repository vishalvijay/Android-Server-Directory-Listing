package com.V4Creations.androidserverdirectorylisting.model.interfaces;

import com.V4Creations.androidserverdirectorylisting.model.ResourceStackItem;

public interface ResourceLoaderInterface {
	public void onStartLoading();

	public void onLoadingSuccess(ResourceStackItem resourceStackItem);

	public void onLoadingFailure(String message, String trackMessage);
}
