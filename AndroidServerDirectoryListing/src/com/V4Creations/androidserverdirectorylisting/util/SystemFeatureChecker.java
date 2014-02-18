package com.V4Creations.androidserverdirectorylisting.util;

import java.io.File;
import java.util.Calendar;

import com.V4Creations.androidserverdirectorylisting.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;

public class SystemFeatureChecker {

	public static boolean isInternetConnection(Context context) {
		ConnectivityManager cn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nf = cn.getActiveNetworkInfo();
		if (nf != null && nf.isConnected() == true)
			return true;
		else
			return false;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void downloadFile(Activity activity, String urlString,
			boolean isBrowserDownload) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD
				&& !isBrowserDownload) {
			DownloadManager dm = SystemFeatureChecker
					.getDownloadManager(activity);
			try {
				Utils.getDefaultRootFolder();
				String fileName = getFileNameFromGetURL(urlString);
				Request request = new Request(Uri.parse(urlString))
						.setDestinationInExternalPublicDir(
								Utils.DEFAULT_FOLDER, fileName)
						.setTitle(fileName)
						.setDescription(
								activity.getString(R.string.downloading));
				manageApiIssues(request);
				dm.enqueue(request);
			} catch (IllegalArgumentException e) {
				try {
					Request request = new Request(Uri.parse(urlString))
							.setDestinationInExternalPublicDir(
									Utils.DEFAULT_FOLDER,
									getFileNameFromURL(urlString))
							.setDescription(
									activity.getString(R.string.downloading));
					manageApiIssues(request);
					dm.enqueue(request);
				} catch (Exception ex) {
					openUrlInBrowser(activity, urlString);
				}
			}
		} else {
			openUrlInBrowser(activity, urlString);
		}
	}

	private static String getFileNameFromURL(String urlString) {
		try {
			return new File(urlString).getName();
		} catch (Exception ex) {

		}
		return Calendar.getInstance().getTimeInMillis() + "";
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static DownloadManager getDownloadManager(Context context) {
		return (DownloadManager) context
				.getSystemService(Context.DOWNLOAD_SERVICE);
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private static void manageApiIssues(Request request) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.HONEYCOMB_MR2) {
				request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
			} else {
				request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
			}
		} else
			request.setShowRunningNotification(true);
	}

	public static void openUrlInBrowser(Activity activity, String urlString) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(urlString));
		activity.startActivity(i);
	}

	private static String getFileNameFromGetURL(String urlString) {
		String[] fileNameStrings = urlString.split("download=");
		if (fileNameStrings.length < 2)
			throw new IllegalArgumentException("Invalid URL");
		return fileNameStrings[1].replace("+", "_");
	}
}
