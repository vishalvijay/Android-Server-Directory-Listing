package com.V4Creations.androidserverdirectorylisting.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.V4Creations.androidserverdirectorylisting.R;
import com.V4Creations.androidserverdirectorylisting.model.ResourceItem;

public class DirectoryListingAdapter extends SupportArrayAdapter<ResourceItem> {

	public DirectoryListingAdapter(Context context) {
		super(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ResourceItem tempDirectoryItem = getItem(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = getLayoutInflater().inflate(
					R.layout.list_directory_item, null);
			viewHolder = new ViewHolder();
			viewHolder.nameTextView = (TextView) convertView
					.findViewById(R.id.nameTextView);
			viewHolder.sizeTextView = (TextView) convertView
					.findViewById(R.id.sizeTextView);
			viewHolder.dateTextView = (TextView) convertView
					.findViewById(R.id.dateTextView);
			viewHolder.iconImageView = (ImageView) convertView
					.findViewById(R.id.iconImageView);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ViewHolder) convertView.getTag();
		viewHolder.nameTextView.setText(tempDirectoryItem.name);
		viewHolder.sizeTextView.setText(tempDirectoryItem.size);
		viewHolder.dateTextView.setText(tempDirectoryItem.date);
		viewHolder.iconImageView.setBackgroundResource(tempDirectoryItem
				.getIcon());
		return convertView;
	}

	private static class ViewHolder {
		TextView nameTextView;
		TextView sizeTextView;
		TextView dateTextView;
		ImageView iconImageView;
	}
}
