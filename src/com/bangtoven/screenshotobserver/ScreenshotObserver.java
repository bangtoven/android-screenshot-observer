package com.bangtoven.screenshotobserver;

import java.io.File;
import java.io.FileFilter;

import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

public class ScreenshotObserver {
	static final String TAG = "ScreenshotObserver"; 
	static final int INTERVAL = 1;
	
	private File mScreenshotFolder;
	private long mLastModified;
	
	private Handler mHandler;
	private Runnable mObserverThread;
	private boolean mIsRunning;
	
	private OnScreenshotTakenListener mListener;
	
	public ScreenshotObserver(OnScreenshotTakenListener listener) {
		mListener = listener;
		
		mScreenshotFolder = new File(Environment.getExternalStorageDirectory().toString() + "/Pictures/Screenshots");
		mLastModified = mScreenshotFolder.lastModified();
		
		mIsRunning = false;
		mHandler = new Handler();
		mObserverThread = new Runnable() {
			@Override 
			public void run() {
				if (mScreenshotFolder.lastModified() > mLastModified) {
					Log.i(TAG,"Screenshot Taken.");
					Uri uri = getLastScreenshotUri();
					mListener.onScreenshotTaken(uri);
					mIsRunning = false;
				}
				else if (mIsRunning) {
					Log.i(TAG,"Running");
					mHandler.postDelayed(mObserverThread, INTERVAL*1000);
				}
			}
		};
	}
	
	public boolean isRunning() {
		return mIsRunning;
	}
	
	public void start() {
		if (mIsRunning == false) {
			mIsRunning = true;
			mLastModified = mScreenshotFolder.lastModified();
			mHandler.post(mObserverThread);
		}
	}
	
	public void pause() {
		mIsRunning = false;
	}
	
	public void stop() {
		mHandler.removeCallbacks(mObserverThread);
	}
	
	private Uri getLastScreenshotUri() {
		File[] files = mScreenshotFolder.listFiles(new FileFilter() {			
			public boolean accept(File file) {
				return file.isFile();
			}
		});
		long modifiedTime = Long.MIN_VALUE;
		File lastScreenshot = null;
		for (File file : files) {
			if (file.lastModified() > modifiedTime) {
				lastScreenshot = file;
				modifiedTime = file.lastModified();
			}
		}
		
		return Uri.fromFile(lastScreenshot);
	}
}
