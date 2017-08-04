package com.thatsales.ImageLoding;

import android.content.Context;

import java.io.File;

/**
 * FileCache class used for get and create new file in internal storage of mobile
 */
public class FileCache {

	private File cacheDir;

    /**
     * constructor of this call create new file on given path
     * @param context
     */
	public FileCache(Context context) {

		// Find the dir to save cached images
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					"/Picture/ThatSales/ImgCache");
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
	}

    /**
     * getFile method used to get file from give path
     * @param url
     * @return
     */
	public File getFile(String url) {
		String filename = String.valueOf(url.hashCode());
		// String filename = URLEncoder.encode(url);
		File f = new File(cacheDir, filename);
		return f;
	}

    /**
     * clear method used to delete existing file
     */
	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}
}