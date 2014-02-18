package com.V4Creations.androidserverdirectorylisting.view.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.V4Creations.androidserverdirectorylisting.R;

public class AndroidServerDirectoryListingMainActivity extends FragmentActivity {
	String TAG = "AndroidServerDirectoryListingMainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
	}
}
