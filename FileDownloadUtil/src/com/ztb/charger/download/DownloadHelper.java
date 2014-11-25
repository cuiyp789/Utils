/**********************************************************
 * Copyright © 2013-1014 深圳市美传网络科技有限公司版权所有
 * 创 建 人：yangbo
 * 创 建 日 期：2014-8-25 下午4:27:57
 * 版 本 号：
 * 修 改 人：
 * 描 述：
 * <p>
 *	
 * </p>
 **********************************************************/
package com.ztb.charger.download;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>
 * 	
 * </p>
 * @author yangbo
 * @date 2014-8-25
 * @version 
 * @since 
 */
public class DownloadHelper {
    final static String TAG = "DownloadHelper"; 

    public interface DownloadListener {
        public void downloadComplete(File file);
    }
	
	/**
	 * 点击后的操作
	 * 
	 * @param activity
	 * @param file
	 */
	public static void options(Activity activity, NetFile file) {
		options(activity, file, true);
	}

	private static void options(Activity activity, NetFile file, boolean isShowDialog) {
		FileManager mFileManager = FileManager.getInstance(activity);
		switch (file.state) {
		case DownloadState.paused:
		case DownloadState.none:
		case DownloadState.updateable:
			toDownload(file, activity, isShowDialog);
			break;
		case DownloadState.downloading:
			mFileManager.setAction(file, FileAction.toPause, activity);
			break;
		case DownloadState.downloadCompleted:
			break;
		case DownloadState.inDownloadQueue:
			mFileManager.setAction(file, FileAction.toPause, activity);
			break;
		case DownloadState.fileDeleted:
			file.state = DownloadState.none;
			toDownload(file, activity, isShowDialog);
			break;
		default:
			break;
		}
	}

	private static void toDownload(NetFile info, Activity activity,
			boolean isShowDialog) {
		FileManager mFileManager = FileManager.getInstance(activity);
		if (isShowDialog) {
			mFileManager.toDownload(info, activity);
		} else {
			if (info.state != DownloadState.paused) {
				Toast.makeText(activity, "已加入下载列队", Toast.LENGTH_SHORT).show();
			}
			mFileManager.setAction(info, FileAction.toDownload, activity);
		}
	}

	/**
	 * 详情下载按钮没有图标
	 * @param info
	 */
	public static void changeButton(NetFile info, Context context, TextView mProgress, final DownloadListener listener) {
		switch (info.state) {
		case DownloadState.none:
			break;
		case DownloadState.downloading:
			int progress = info.progress / 3;
			System.out.println("progress: " + progress);
			mProgress.setText(progress + "%");
			break;
		case DownloadState.downloadCompleted:
			System.out.println("downloadCompleted...1...");
			if (null != listener) {
			    String filePath = urlToLocalPath(info.url);
			    File file = new File(filePath);
				listener.downloadComplete(file);
			}
			break;
		case DownloadState.paused:
			break;
		case DownloadState.inDownloadQueue:
			break;
		}
	}

	public static void toBatchDownload(List<NetFile> list, final Activity activity) {
		if (list == null || list.size() == 0) {
			return;
		}
		toDownloadList(list, activity);
	}

	private static void toDownloadList(List<NetFile> list, Context context) {
		if (list == null || list.size() == 0) {
			return;
		}
		FileManager mFileManager = FileManager.getInstance(context);
		Iterator<NetFile> iterator = list.iterator();
		while (iterator.hasNext()) {
			NetFile info = iterator.next();
			mFileManager.setAction(info, FileAction.toDownload, context);
		}
	}
	public static String urlToLocalPath(String url){
	    String dir = Config.getInstance().downloadDir;
        String filePath = dir + MD5Util.getMD5String(url);
        return filePath;
	}
}
