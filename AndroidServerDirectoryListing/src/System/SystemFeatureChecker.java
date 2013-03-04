package System;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

public class SystemFeatureChecker {
	// static String TAG = "SystemFeatureChecker";

	public static boolean isInternetConnection(Context context) {
		ConnectivityManager cn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nf = cn.getActiveNetworkInfo();
		if (nf != null && nf.isConnected() == true)
			return true;
		else
			return false;
	}

	public static String getImei(Context context) {

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		return telephonyManager.getDeviceId();
	}

	public static int getDisplayWidth(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		return displaymetrics.widthPixels;
	}

	public static int getDisplayHeight(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		return displaymetrics.heightPixels;
	}

	public static int getAppVersionCode(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getAppVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return StringOperator.toFullNameFormate(model);
		} else {
			return StringOperator.toFullNameFormate(manufacturer) + " " + model;
		}
	}

	public static String getAndroidVersion() {
		return Build.VERSION.RELEASE;
	}
}
