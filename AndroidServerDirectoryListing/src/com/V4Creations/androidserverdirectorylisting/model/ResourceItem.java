package com.V4Creations.androidserverdirectorylisting.model;

import com.V4Creations.androidserverdirectorylisting.R;

public class ResourceItem {
	public String href;
	public String name;
	public String size;
	public String date;
	public String ext;

	public int getIcon() {
		if (ext.matches("(?i)dir"))
			return R.drawable.folder;
		else if (ext.matches("(?i)png") || ext.matches("(?i)jpeg")
				|| ext.matches("(?i)jpg") || ext.matches("(?i)bmp"))
			return R.drawable.jpeg;
		else if (ext.matches("(?i)gif"))
			return R.drawable.gif;
		else if (ext.matches("(?i)zip") || ext.matches("(?i)rar")
				|| ext.matches("(?i)tar") || ext.matches("(?i)7zip")
				|| ext.matches("(?i)gz"))
			return R.drawable.archive;
		else if (ext.matches("(?i)exe"))
			return R.drawable.exe;
		else if (ext.matches("(?i)txt"))
			return R.drawable.txt;
		else if (ext.matches("(?i)htm") || ext.matches("(?i)html")
				|| ext.matches("(?i)xml") || ext.matches("(?i)php")
				|| ext.matches("(?i)pl"))
			return R.drawable.html;
		else if (ext.matches("(?i)flv"))
			return R.drawable.flash;
		else if (ext.matches("(?i)swf"))
			return R.drawable.swf;
		else if (ext.matches("(?i)xls"))
			return R.drawable.xsl;
		else if (ext.matches("(?i)doc") || ext.matches("(?i)docx"))
			return R.drawable.doc;
		else if (ext.matches("(?i)ppt") || ext.matches("(?i)pptx"))
			return R.drawable.ppt;
		else if (ext.matches("(?i)pdf"))
			return R.drawable.pdf;
		else if (ext.matches("(?i)psd"))
			return R.drawable.psd;
		else if (ext.matches("(?i)rm"))
			return R.drawable.real;
		else if (ext.matches("(?i)mpeg") || ext.matches("(?i)mpg")
				|| ext.matches("(?i)mov") || ext.matches("(?i)avi")
				|| ext.matches("(?i)mp4") || ext.matches("(?i)3gp")
				|| ext.matches("(?i)vob"))
			return R.drawable.video;
		else
			return R.drawable.unknown;
	}

}
