package com.bangtoven.screenshotobserver;

import java.io.File;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;

public class ScreenshotObserver extends FileObserver {
	private static final String TAG = "ScreenshotObserver"; 
	private static final String PATH = Environment.getExternalStorageDirectory().toString() + "/Pictures/Screenshots/";
	
	private OnScreenshotTakenListener mListener;
	private Context mContext;
	private String mLastTakenPath;
	
	
	public ScreenshotObserver(OnScreenshotTakenListener listener) {
		super(PATH, FileObserver.CLOSE_WRITE);
		mListener = listener;
	}
	
	public ScreenshotObserver(OnScreenshotTakenListener listener, Context context) {
		this(listener);
		mContext = context;
	}

	@Override
	public void onEvent(int event, String path) {
		Log.i(TAG, "Event:"+event+"\t"+path);
		
		if (path==null || event!=FileObserver.CLOSE_WRITE)
			Log.i(TAG, "Don't care.");
		else if (mLastTakenPath!=null && path.equalsIgnoreCase(mLastTakenPath))
			Log.i(TAG, "This event has been observed before.");
		else {
			mLastTakenPath = path;
			this.onScreenshotTaken();
		}
	}
	
	private File getLastTakenFile() {
		return new File(PATH+mLastTakenPath);
	}
	
	private void onScreenshotTaken() {
		File file = this.getLastTakenFile();
		mListener.onScreenshotTaken(Uri.fromFile(file));
		Log.i(TAG, "Send event to listener.");
	}
	
	public void start() {
		super.startWatching();
	}
	
	public void stop() {
		super.stopWatching();
	}
	

	
//	private Uri getLastScreenshotUri() {
//		File[] files = mScreenshotFolder.listFiles(new FileFilter() {			
//			public boolean accept(File file) {
//				return file.isFile();
//			}
//		});
//		long modifiedTime = Long.MIN_VALUE;
//		File lastScreenshot = null;
//		for (File file : files) {
//			if (file.lastModified() > modifiedTime) {
//				lastScreenshot = file;
//				modifiedTime = file.lastModified();
//			}
//		}
//		
//		return Uri.fromFile(lastScreenshot);
//	}
}
