package com.V4Creations.androidserverdirectorylisting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import System.JSONParser;
import System.SystemFeatureChecker;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

public class AndroidServerDirectoryListingMainActivity extends
		SherlockListActivity {
	String TAG = "AndroidServerDirectoryListingMainActivity";

	Stack<StackItem> stack;
	ListView lv;
	JSONObject json = null;

	boolean isForwardEnabled = false, isBackEnabled = false, isRefresh = true;

	final private int BACK_MENU = 0, REFRESH_MENU = 1, FORWARD_MENU = 2;

	ArrayList<HashMap<String, String>> itemList;

	private String directoryName = null;

	private String webUrl = "http://www.vtulife.com";
	private String pageUrl = "/androidDirectoryListing.php";
	private String url = pageUrl + "?sort=date&order=asc";

	private static final String TAG_ITEMS = "items";
	private static final String TAG_HREF = "href";
	private static final String TAG_NAME = "name";
	private static final String TAG_EXT = "ext";
	private static final String TAG_EXT_IMAGE = "ext_image";
	private static final String TAG_SIZE = "size";
	private static final String TAG_DATE = "date";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.android_server_directory_listing_main_layout);
		stack = new Stack<StackItem>();

		getSupportActionBar().setTitle("Directory Listing");
		itemList = new ArrayList<HashMap<String, String>>();
		new LoadDataFromServer().execute();

		lv = getListView();
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String ext = ((TextView) view.findViewById(R.id.extTextView))
						.getText().toString();
				String href = ((TextView) view.findViewById(R.id.hrefTextView))
						.getText().toString();
				if (ext.equals("dir")) {
					StackItem stackItem = new StackItem(json, url,
							directoryName);
					stack.push(stackItem);
					directoryName = ((TextView) view
							.findViewById(R.id.nameTextView)).getText()
							.toString();
					url = ((TextView) view.findViewById(R.id.hrefTextView))
							.getText().toString();
					new LoadDataFromServer().execute();
				} else {
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(webUrl + pageUrl + href));
					startActivity(i);
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		com.actionbarsherlock.view.MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(
				R.menu.android_server_directory_listing_main_layout, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (isForwardEnabled) {
			menu.getItem(FORWARD_MENU).setEnabled(true);
			menu.getItem(FORWARD_MENU).setIcon(R.drawable.forward);
		} else {
			menu.getItem(FORWARD_MENU).setEnabled(false);
			menu.getItem(FORWARD_MENU).setIcon(R.drawable.forward_desable);
		}
		if (!stack.isEmpty()) {
			menu.getItem(BACK_MENU).setEnabled(true);
			menu.getItem(BACK_MENU).setIcon(R.drawable.back);
		} else {
			menu.getItem(BACK_MENU).setEnabled(false);
			menu.getItem(BACK_MENU).setIcon(R.drawable.back_desable);
		}
		if (isRefresh)
			menu.getItem(REFRESH_MENU).setIcon(R.drawable.ic_refresh);
		else
			menu.getItem(REFRESH_MENU).setIcon(
					android.R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.menu_back:
			goBack();
			return true;
		case R.id.menu_refresh:
			if (isRefresh)
				new LoadDataFromServer().execute();
			else
				Toast.makeText(getApplicationContext(),
						"Sorry, can't stop this operation", Toast.LENGTH_SHORT)
						.show();
			return true;
		}
		return false;
	}

	private void removeListItems() {
		itemList.clear();
		ListAdapter adapter = new SimpleAdapter(
				AndroidServerDirectoryListingMainActivity.this, itemList,
				R.layout.list_item, null, null);
		setListAdapter(adapter);
	}

	private void goBack() {
		removeListItems();
		try {
			StackItem stackItem = stack.pop();
			json = stackItem.mJson;
			url = stackItem.mUrl;
			directoryName = stackItem.mDirName;
			JSONArray items = json.getJSONArray(TAG_ITEMS);

			for (int i = 0; i < items.length(); i++) {
				JSONObject item = items.getJSONObject(i);

				String href = item.getString(TAG_HREF);
				String name = item.getString(TAG_NAME);
				String size = item.getString(TAG_SIZE);
				String date = item.getString(TAG_DATE);
				String ext = item.getString(TAG_EXT);
				HashMap<String, String> map = new HashMap<String, String>();

				map.put(TAG_HREF, href);
				map.put(TAG_NAME, name);
				map.put(TAG_SIZE, size);
				map.put(TAG_DATE, date);
				map.put(TAG_EXT, ext);
				map.put(TAG_EXT_IMAGE, Integer.toString(selectImage(ext)));
				itemList.add(map);
			}
			ListAdapter adapter = new SimpleAdapter(
					AndroidServerDirectoryListingMainActivity.this, itemList,
					R.layout.list_item, new String[] { TAG_HREF, TAG_NAME,
							TAG_SIZE, TAG_DATE, TAG_EXT_IMAGE, TAG_EXT },
					new int[] { R.id.hrefTextView, R.id.nameTextView,
							R.id.sizeTextView, R.id.dateTextView,
							R.id.iconImageView, R.id.extTextView });
			setListAdapter(adapter);
		} catch (JSONException e) {
			Toast.makeText(getApplicationContext(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
		getSupportActionBar().setSubtitle(directoryName);
		supportInvalidateOptionsMenu();
	}

	private int selectImage(String ext) {
		if (ext.equals("dir"))
			return R.drawable.folder;
		else if (ext.equals("png") || ext.equals("jpeg") || ext.equals("jpg")
				|| ext.equals("bmp"))
			return R.drawable.jpeg;
		else if (ext.equals("gif"))
			return R.drawable.gif;
		else if (ext.equals("zip") || ext.equals("rar") || ext.equals("tar")
				|| ext.equals("7zip") || ext.equals("gz"))
			return R.drawable.archive;
		else if (ext.equals("exe"))
			return R.drawable.exe;
		else if (ext.equals("txt"))
			return R.drawable.txt;
		else if (ext.equals("htm") || ext.equals("html") || ext.equals("xml")
				|| ext.equals("php") || ext.equals("pl"))
			return R.drawable.html;
		else if (ext.equals("flv"))
			return R.drawable.flash;
		else if (ext.equals("swf"))
			return R.drawable.swf;
		else if (ext.equals("xls"))
			return R.drawable.xsl;
		else if (ext.equals("doc") || ext.equals("docx"))
			return R.drawable.doc;
		else if (ext.equals("pdf"))
			return R.drawable.pdf;
		else if (ext.equals("psd"))
			return R.drawable.psd;
		else if (ext.equals("rm"))
			return R.drawable.real;
		else if (ext.equals("mpeg") || ext.equals("mpg") || ext.equals("mov")
				|| ext.equals("avi") || ext.equals("mp4") || ext.equals("3gp")
				|| ext.equals("vob"))
			return R.drawable.video;
		else
			return R.drawable.unknown;
	}

	@Override
	public void onBackPressed() { // method for exit confirmation
		AlertDialog.Builder builder = new AlertDialog.Builder(
				AndroidServerDirectoryListingMainActivity.this);
		builder.setMessage("Are you sure ?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.setTitle("Exit");
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.show();

	}

	class LoadDataFromServer extends AsyncTask<String, String, String> {
		boolean isConnectionOk = true;
		String errorMessage = "";
		JSONParser jParser = new JSONParser();
		JSONArray items = null;

		@Override
		protected void onPreExecute() {
			removeListItems();
			isRefresh = false;
			supportInvalidateOptionsMenu();
			super.onPreExecute();
			getSupportActionBar().setSubtitle("Loading...");
			setSupportProgressBarIndeterminateVisibility(true);
		}

		protected String doInBackground(String... args) {
			if (SystemFeatureChecker
					.isInternetConnection(getApplicationContext())) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				try {
					json = jParser.makeHttpRequest(webUrl + url, "GET", params);
				} catch (Exception e1) {
					isConnectionOk = false;
					errorMessage = e1.getMessage();
					return null;
				}

				try {
					items = json.getJSONArray(TAG_ITEMS);
					if (items.length() == 0) {
						isConnectionOk = false;
						errorMessage = "Empty folder";
						return null;
					}
					for (int i = 0; i < items.length(); i++) {
						JSONObject item = items.getJSONObject(i);

						String href = item.getString(TAG_HREF);
						String name = item.getString(TAG_NAME);
						String size = item.getString(TAG_SIZE);
						String date = item.getString(TAG_DATE);
						String ext = item.getString(TAG_EXT);

						HashMap<String, String> map = new HashMap<String, String>();

						map.put(TAG_HREF, href);
						map.put(TAG_NAME, name);
						map.put(TAG_SIZE, size);
						map.put(TAG_DATE, date);
						map.put(TAG_EXT, ext);
						map.put(TAG_EXT_IMAGE,
								Integer.toString(selectImage(ext)));
						itemList.add(map);
					}
				} catch (JSONException e) {
					isConnectionOk = false;
					errorMessage = e.getMessage();
				}
			} else {
				isConnectionOk = false;
				errorMessage = "Sorry, internet connection is not available.";
			}
			return null;
		}

		protected void onPostExecute(String file_url) {
			isRefresh = true;
			setSupportProgressBarIndeterminateVisibility(false);
			getSupportActionBar().setSubtitle(null);
			if (isConnectionOk) {
				runOnUiThread(new Runnable() {
					public void run() {
						ListAdapter adapter = new SimpleAdapter(
								AndroidServerDirectoryListingMainActivity.this,
								itemList, R.layout.list_item, new String[] {
										TAG_HREF, TAG_NAME, TAG_SIZE, TAG_DATE,
										TAG_EXT_IMAGE, TAG_EXT }, new int[] {
										R.id.hrefTextView, R.id.nameTextView,
										R.id.sizeTextView, R.id.dateTextView,
										R.id.iconImageView, R.id.extTextView });
						setListAdapter(adapter);
					}
				});
			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						errorMessage, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_HORIZONTAL
						| Gravity.CENTER_VERTICAL, 0, 0);
				toast.show();
			}
			getSupportActionBar().setSubtitle(directoryName);
			supportInvalidateOptionsMenu();
		}

	}

	public class StackItem {
		public JSONObject mJson;
		public String mUrl;
		public String mDirName;

		public StackItem(JSONObject json, String url, String dirName) {
			mJson = json;
			mUrl = url;
			mDirName = dirName;
		}
	}
}
