package com.V4Creations.androidserverdirectorylisting.view.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.V4Creations.androidserverdirectorylisting.R;
import com.V4Creations.androidserverdirectorylisting.controller.ResourceLoaderManager;
import com.V4Creations.androidserverdirectorylisting.controller.adapter.DirectoryListingAdapter;
import com.V4Creations.androidserverdirectorylisting.model.ResourceItem;
import com.V4Creations.androidserverdirectorylisting.model.ResourceStackItem;
import com.V4Creations.androidserverdirectorylisting.model.interfaces.ResourceLoaderInterface;
import com.V4Creations.androidserverdirectorylisting.view.activity.AndroidServerDirectoryListingMainActivity;

public class DirectoryListingFragment extends ListFragment implements
		ResourceLoaderInterface {

	String TAG = "DirectoryListingFragment";
	private AndroidServerDirectoryListingMainActivity activity;
	private DirectoryListingAdapter mAdapter;
	private LinearLayout progressLinearLayout;
	private ProgressBar progressBar;
	private TextView progressTextView;
	private ResourceLoaderManager mResourceLoaderManager;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity = (AndroidServerDirectoryListingMainActivity) getActivity();
		return inflater.inflate(R.layout.fragemnt_directory, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initView();
		hideProgressLinearLayout();
		initResourceLoader();
	}

	private void initResourceLoader() {
		mResourceLoaderManager = new ResourceLoaderManager(activity, this);
		mResourceLoaderManager.loadDirectory(null, -1);
	}

	private void initView() {
		initListView();
		progressLinearLayout = (LinearLayout) getView().findViewById(
				R.id.progressLinearLayout);
		progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
		progressTextView = (TextView) getView().findViewById(
				R.id.progressTextView);
	}

	private void initListView() {
		mAdapter = new DirectoryListingAdapter(activity);
		setListAdapter(mAdapter);
	}

	@Override
	public void onListItemClick(ListView l, View view, int position, long id) {
		super.onListItemClick(l, view, position, id);
		ResourceItem resourceItem = mAdapter.getItem(position);
		if (resourceItem.ext.equals("dir")) {
			mResourceLoaderManager.loadDirectory(resourceItem.href, position);
		} else {
			mResourceLoaderManager.downloadFile(activity, resourceItem.href);
			showToast(R.string.downloading_started);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		MenuItem backMenuItem = menu.findItem(R.id.menu_back);
		MenuItem refreshMenuItem = menu.findItem(R.id.menu_refresh);
		if (mResourceLoaderManager != null && mResourceLoaderManager.isGoBack()) {
			backMenuItem.setEnabled(true);
			backMenuItem.setIcon(R.drawable.ic_action_previous_item);
		} else {
			backMenuItem.setEnabled(false);
			backMenuItem.setIcon(R.drawable.ic_action_previous_item_disabled);
		}
		if (mResourceLoaderManager != null
				&& !mResourceLoaderManager.isReloading()) {
			refreshMenuItem.setIcon(R.drawable.ic_action_refresh);
			refreshMenuItem.setEnabled(true);
		} else {
			refreshMenuItem.setIcon(R.drawable.ic_action_refresh_disabled);
			refreshMenuItem.setEnabled(false);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_back:
			mResourceLoaderManager.goBack();
			return true;
		case R.id.menu_refresh:
			mResourceLoaderManager.reload();
			return true;
		}
		return false;
	}

	private void hideProgressLinearLayout() {
		progressLinearLayout.setVisibility(View.GONE);
	}

	private void showProgressLinearLayout() {
		progressLinearLayout.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		progressTextView.setText(R.string.please_wait);
	}

	private void showReload() {
		progressLinearLayout.setVisibility(View.VISIBLE);
		progressBar.setVisibility(View.GONE);
		progressTextView.setText(R.string.please_reload);
	}

	@Override
	public void onStartLoading() {
		mAdapter.clear();
		showProgressLinearLayout();
		changeSubtitle(getString(R.string.loading));
	}

	private void changeSubtitle(String string) {
		activity.getActionBar().setSubtitle(string);
	}

	@Override
	public void onLoadingSuccess(ResourceStackItem resourceStackItem) {
		hideProgressLinearLayout();
		if (resourceStackItem.mResourceItems.size() != 0)
			mAdapter.supportAddAll(resourceStackItem.mResourceItems);
		else {
			showToast(R.string.empty_folder);
			if (mResourceLoaderManager.isGoBack())
				mResourceLoaderManager.goBack();
			else
				showReload();
		}
		changeSubtitle(resourceStackItem.mDirName);
		activity.invalidateOptionsMenu();
	}

	@Override
	public void onLoadingFailure(String message, String trackMessage) {
		showToast(message);
		changeSubtitle(null);
		showReload();
		activity.invalidateOptionsMenu();
	}

	private void showToast(String string) {
		Toast.makeText(activity, string, Toast.LENGTH_SHORT).show();
	}

	private void showToast(int resId) {
		showToast(getString(resId));
	}
}