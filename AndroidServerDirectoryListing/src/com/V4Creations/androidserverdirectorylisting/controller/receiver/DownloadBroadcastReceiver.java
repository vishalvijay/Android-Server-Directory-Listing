package com.V4Creations.androidserverdirectorylisting.controller.receiver;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import com.V4Creations.androidserverdirectorylisting.R;
import com.V4Creations.androidserverdirectorylisting.util.SystemFeatureChecker;
import com.V4Creations.androidserverdirectorylisting.util.Utils;
import com.V4Creations.androidserverdirectorylisting.view.activity.AndroidServerDirectoryListingMainActivity;

public class DownloadBroadcastReceiver extends BroadcastReceiver {

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)
				&& Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			Bundle extras = intent.getExtras();
			DownloadManager.Query q = new DownloadManager.Query();
			long id = extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID);
			q.setFilterById(id);
			Cursor c = SystemFeatureChecker.getDownloadManager(context)
					.query(q);
			if (c.moveToFirst()) {
				int status = c.getInt(c
						.getColumnIndex(DownloadManager.COLUMN_STATUS));
				String title = c.getString(c
						.getColumnIndex(DownloadManager.COLUMN_TITLE));
				String message;
				if (status == DownloadManager.STATUS_SUCCESSFUL)
					message = context.getString(R.string.download_complete);
				else
					message = context.getString(R.string.download_unsuccessful);
				Utils.showNotification(id, title, message,
						R.drawable.ic_action_download, context,
						AndroidServerDirectoryListingMainActivity.class);
			}
		}
	}
}