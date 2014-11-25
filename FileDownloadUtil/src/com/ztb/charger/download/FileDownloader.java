/**********************************************************
 * Copyright © 2013-1014 深圳市美传网络科技有限公司版权所有
 * 创 建 人：Administrator
 * 创 建 日 期：2014-3-25 上午10:28:51
 * 版 本 号：
 * 修 改 人：
 * 描 述：
 * <p>
 *	
 * </p>
 **********************************************************/
package com.ztb.charger.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.provider.SyncStateContract.Constants;
import android.widget.Toast;

/**
 * <p>
 * 
 * </p>
 * 
 * @author yangbo
 * @date 2014-3-25
 * @version
 * @since
 */
public class FileDownloader {
	private final static String TAG = FileDownloader.class.getCanonicalName();
	private static FileDownloader instance;
	private List<FileStateListener> mListenerList;
	/**
	 * 排队的下载任务，等待下载的
	 */
	private BlockingQueue<NetFile> mDownloadQueue = new LinkedBlockingQueue<NetFile>();
	/**
	 * 排队的下载任务，正在下载的
	 */
	private BlockingQueue<NetFile> mDownloadingQueue = new LinkedBlockingQueue<NetFile>();
	private ReentrantLock mLock = new ReentrantLock();
	/**
	 * 如果是显式的暂停请求，这个map就会有值。
	 */
	private Map<String, Boolean> mPausedMap = new HashMap<String, Boolean>();

	/**
	 * 下载发生异常时，会暂停发生异常的任务，然后重新排队。但要把重试的次数给记下来。<br>
	 * 超过最多重试次数之后，就取消掉这个任务，不放到队列里面去排，直到手动的重试<br>
	 */
	private Map<String, Integer> mTryTimesMap = new HashMap<String, Integer>();

	private boolean mIsRunning;
	/**
	 * 发出暂停请求，但InputStream的read仍然可能长时间不返回，<br>
	 * 于是采用监听者模式的代码就会长时间不会被调用，下载按钮等的状态也就不会被刷新。<br>
	 * 为了解决这个问题，需要显式的调用close
	 */
	private Map<String, InputStream> mFileDownloadISMap = new HashMap<String, InputStream>();
	/**
	 * 下载发生异常时最多的重试次数
	 */
	private final static int MAX_TRY_TIMES = 50;

	private FileDownloader() {
		mListenerList = new ArrayList<FileStateListener>();
		// 从数据库中获取保存的未完成下载任务信息，并加入队列中
		mIsRunning = true;
	}

	public static FileDownloader getInstance() {
		if (instance == null)
			instance = new FileDownloader();
		return instance;
	}

	/**
	 * 要做到的几件事情：<br>
	 * 1.已经下载完成的触发安装<br>
	 * 2.下载过但还没有完成的，继续下载<br>
	 * 3.
	 * 
	 * @param info
	 * @throws InterruptedException
	 */
	public void download(NetFile info, Context context) {
		mLock.lock();
		for (NetFile tmp : mDownloadingQueue) {
			if (tmp.url.equals(info.url)) {
				tmp.state = DownloadState.downloading;
				info.state = DownloadState.downloading;
				mLock.unlock();
				notifyAllListeners(tmp, context);
				return;
			}
		}

		for (NetFile tmp : mDownloadQueue) {
			if (tmp.url.equals(info.url)) {
				tmp.state = DownloadState.inDownloadQueue;
				info.state = DownloadState.inDownloadQueue;
				mLock.unlock();
				notifyAllListeners(tmp, context);
				return;
			}
		}

		try {
			if (!mDownloadQueue.contains(info))
				mDownloadQueue.put(info);
			notifyAllListeners(info, context);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			mLock.unlock();
		}
	}

	/**
	 * 从队列中移除，并删除下载文件
	 * 
	 * @param info
	 */
	public void delete(NetFile info, Context context) {
		info.state = DownloadState.fileDeleted;
		info.progress = 0;
		mLock.lock();
		for (NetFile cu : mDownloadQueue) {
			if (cu.url.equals(info.url)) {
				cu.state = DownloadState.fileDeleted;
				mDownloadQueue.remove(cu);
				break;
			}
		}

		for (NetFile cu : mDownloadingQueue) {
			if (cu.url.equals(info.url)) {
				cu.state = DownloadState.fileDeleted;
				mDownloadingQueue.remove(cu);
				break;
			}
		}

		mLock.unlock();
		InputStream is = mFileDownloadISMap.get(info.url);
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mFileDownloadISMap.remove(info.url);

		deleteFile(info, context);
		notifyAllListeners(info, context);
	}

	public void deleteFile(NetFile info, Context context) {
		String downloadDirPath = Config.getInstance().downloadDir;
		File downloadDir = new File(downloadDirPath);
		for (File file : downloadDir.listFiles()) {
			if (file.getName().contains(info.url)) {
				file.delete();
			}
		}
	}

	public void startAll(final Context context) {
		for (int i = 0; i < ThreadPoolManager.DOWNLOADER_THREAD_SIZE; i++) {
			ThreadPoolManager.executeHttpTask(new Runnable() {
				@Override
				public void run() {
					processInLoop(context);
				}
			});
		}
		;
	}

	public void stopAll() {
		mIsRunning = false;

		// 中断所有的下载任务
		// 把未完成的任务保存到数据库里

		for (InputStream is : mFileDownloadISMap.values()) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void pause(NetFile info, Context context) {
		mLock.lock();
		for (NetFile cu : mDownloadQueue) {
			if (cu.url.equals(info.url)) {
				cu.state = DownloadState.paused;
				mDownloadQueue.remove(cu);
				break;
			}
		}
		for (NetFile cu : mDownloadingQueue) {
			if (cu.url.equals(info.url)) {
				cu.state = DownloadState.paused;
				info.progress = cu.progress;
				// mDownloadingQueue.remove(cu);
				break;
			}
		}

		mLock.unlock();

		mPausedMap.put(info.url, true);
		mTryTimesMap.remove(info.url);
		mFileDownloadISMap.remove(info.url);

		InputStream is = mFileDownloadISMap.get(info.url);
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		info.state = DownloadState.paused;
		notifyAllListeners(info, context);
	}

	public void redownload(NetFile info, Context context) {
		// 为了界面上能最快得到反馈，这里先发通知回去
		info.progress = 0;
		info.state = DownloadState.inDownloadQueue;
		notifyAllListeners(info, context);

		// 关闭正在下载的流
		mPausedMap.put(info.url, true);
		mTryTimesMap.remove(info.url);
		InputStream is = mFileDownloadISMap.get(info.url);
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 并删除临时文件
		deleteFile(info, context);
		// 然后把info重新添加回下载队列当中
		download(info, context);
	}

	private void processInLoop(Context context) {
		while (mIsRunning) {
			NetFile info;
			try {
				info = mDownloadQueue.take();
				info.state = DownloadState.downloading;
				if (!mDownloadingQueue.contains(info))
					mDownloadingQueue.put(info);
			} catch (InterruptedException e) {
				e.printStackTrace();
				continue;
			}
			doDownload(info, context);
			mDownloadingQueue.remove(info);
			mFileDownloadISMap.remove(info.url);
		}
	}

	/**
	 * 获取下载临时文件目录 <br>
	 * 根据不同的下载状态进行不同处理:<br>
	 * 没启动过，直接启动<br>
	 * 暂停了，则从上次保存的那个进度继续下载<br>
	 * 
	 * @param info
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void doDownload(final NetFile info, Context context) {
		if (!mTryTimesMap.containsKey(info.url)) {
			mTryTimesMap.put(info.url, 1);
		}

		String downloadDirPath = Config.getInstance().downloadDir;
		File dir = new File(downloadDirPath);
		if (!dir.exists())
			dir.mkdirs();
		File tmpFile = new File(DownloadHelper.urlToLocalPath(info.url) + Config.getInstance().TMP_FILE_SUFFIX);

		int tryTimes = mTryTimesMap.containsKey(info.url) ? mTryTimesMap.get(info.url) : 0;
		boolean completed = false;
		tryLoop: for (; tryTimes < MAX_TRY_TIMES && !completed; tryTimes++) {
			RandomAccessFile raf = null;
			try {
				raf = downloadToTempFile(info, tmpFile, context);
				completed = true;
			} catch (Exception e) {
				if (info.state == DownloadState.fileDeleted) {
					mDownloadingQueue.remove(info);
					return;
				}

				if (mPausedMap.containsKey(info.url) && mPausedMap.get(info.url)) {
					// 暂停时会抛出异常，在这里就设置正确的状态
					info.state = DownloadState.paused;
				} else {
					// 非暂停操作导致的异常，这些才是真正的异常
					if (tryTimes <= 1) {// 第一次发生异常，则给予弹出框通知
						displayExceptionMessage(e, context);
					}

					if (!e.getClass().toString().contains("FileNotFoundException")// 并非下载文件不存在
							&& !e.getClass().toString().contains("MalformedURLException")// 不是url格式错误
							&& tryTimes < MAX_TRY_TIMES// 并且没有达到重试的上限
							&& NetworkUtil.hasNetWork(context)) {// 而且网络也没有断开，就可以重试下载
						try {
							Thread.sleep(20);
							continue tryLoop;
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						mTryTimesMap.put(info.url, tryTimes);
					} else {// 达到重试的上限了，不再重试下载
						info.state = DownloadState.paused;
						notifyAllListeners(info, context);
						break tryLoop;
					}
				}

				notifyAllListeners(info, context);
				continue tryLoop;
			} finally {
				if (raf != null) {
					try {
						raf.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				mPausedMap.remove(info.url);
			}
		}

		if (info.state != DownloadState.paused && completed) {
			mDownloadingQueue.remove(info);
			renameCompletedTempFile(info, downloadDirPath, tmpFile, context);
		}
	}

	private void renameCompletedTempFile(final NetFile info, String downloadDirPath, final File tmpFile,
			Context context) {
		File file = new File(DownloadHelper.urlToLocalPath(info.url) + Config.getInstance().TMP_FILE_SUFFIX);
		tmpFile.renameTo(file);

		info.progress = 100;
		info.state = DownloadState.downloadCompleted;

		notifyAllListeners(info, context);
	}

	private RandomAccessFile downloadToTempFile(final NetFile info, final File tmpFile, final Context context)
			throws FileNotFoundException, IOException, InterruptedException, MalformedURLException, ProtocolException {
		RandomAccessFile raf;
		long startPosition = tmpFile.length();
		raf = getRandomAccessFile(tmpFile, startPosition, context);
		InputStream is = createDownloadInputStream(info, startPosition);
		int buf = NetworkUtil.isWifiConnected(context) ? 10240 : 1024;
		final int progressStep = 1;
		byte[] buffer = new byte[buf];
		int len = 0;
		long process = startPosition;

		// 为了优化UI体验，还没有建立下载链接就发通知去刷新界面
		ThreadPoolManager.executeNormalTask(new Runnable() {

			@Override
			public void run() {
				long progress = tmpFile.length() < info.length ? tmpFile.length() * 100 / info.length
						: 100;
				info.progress = (int) progress;
				info.state = DownloadState.downloading;
				notifyAllListeners(info, context);
			}
		});

		final long[] prevProgress = { 0 };

		while ((len = is.read(buffer)) != -1) {

			if (info.state == DownloadState.paused) {
				break;
			}

			raf.write(buffer, 0, len);
			process += len;

			final long progress = process < info.length ? process * 100 / info.length : 100;

			if (progress > prevProgress[0] + progressStep) {// 按照1%的进度来通知刷新
				ThreadPoolManager.executeNormalTask(new Runnable() {

					@Override
					public void run() {
						info.progress = (int) progress;
						// info.setDownloadState(FileState.downloading);
						prevProgress[0] = progress;
						notifyAllListeners(info, context);
					}
				});

			}
		}
		return raf;
	}

	private InputStream createDownloadInputStream(final NetFile info, long startPosition)
			throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL(info.url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(Config.getInstance().TIME_OUT_DOWNLOAD_FILE_IN_MS);
		conn.setReadTimeout(Config.getInstance().TIME_OUT_DOWNLOAD_FILE_IN_MS);
		conn.setRequestMethod("GET");
		// 指定下载资源的起始位置
		conn.setRequestProperty("Range", "bytes=" + startPosition + "-");
		InputStream is = conn.getInputStream();
		long length = info.length;
		if (length <= 0) {
			length = conn.getContentLength();
			if (length > 0) {
				info.length = length;
			}
			System.out.println("length: " + length);
		}
		mFileDownloadISMap.put(info.url, is);

		return is;
	}

	private void displayExceptionMessage(final Exception e, final Context context) {
		Handler mainHandler = new Handler(Looper.getMainLooper());
		mainHandler.post(new Runnable() {
			@Override
			public void run() {
				String exception = e.getClass().toString();
				if (exception.contains("FileNotFoundException")) {
					Toast.makeText(context, "下载地址无效,请选择下载其它应用", Toast.LENGTH_SHORT).show();
				} else if (exception.contains("MalformedURLException")) {
					Toast.makeText(context, "下载地址无效,请选择下载其它应用", Toast.LENGTH_SHORT).show();
				} else if (exception.contains("ConnectTimeoutException")) {
					if (NetworkUtil.hasNetWork(context))
						Toast.makeText(context, "网络连接超时", Toast.LENGTH_SHORT).show();
				} else {
					if (NetworkUtil.hasNetWork(context))
						Toast.makeText(context, "网络异常,请稍后再试", Toast.LENGTH_SHORT).show();
				}
			}
		});
		e.printStackTrace();
	}

	private RandomAccessFile getRandomAccessFile(File fileOut, long startPosition, Context context)
			throws FileNotFoundException, IOException, InterruptedException {

		if (fileOut.getAbsoluteFile().toString().startsWith("/data/data/")) {
			String command1 = "chmod 777 " + context.getFilesDir().getAbsolutePath();
			Process proc = Runtime.getRuntime().exec(command1);
			proc.waitFor();
			String s = (new StringBuilder()).append("chmod 777 ").append(fileOut.getAbsolutePath()).toString();
			Runtime runtime;
			runtime = Runtime.getRuntime();
			Process p = runtime.exec(s);
			p.waitFor();
		}

		RandomAccessFile raf = new RandomAccessFile(fileOut, "rwd");
		raf.seek(startPosition);
		return raf;
	}

	public void notifyAllListeners(final NetFile info, Context context) {
		mLock.lock();
		for (FileStateListener listener : mListenerList) {
			listener.stateChanged(info, context);
		}

		mLock.unlock();
	}

	public void addStateListener(FileStateListener listener) {
		mLock.lock();
		mListenerList.add(listener);
		mLock.unlock();
	}

	public void removeStateListener(FileStateListener listener) {
		mLock.lock();
		mListenerList.remove(listener);
		mLock.unlock();
	}

	public List<NetFile> getDownloadingList(Context context) {
		if (mDownloadingQueue.isEmpty()) {
			mDownloadingQueue.addAll(FileDownloadInfoDAO.getInstance(context).queryForState(DownloadState.downloading));
		}

		List<NetFile> list = new ArrayList<NetFile>(mDownloadingQueue);
		list.addAll(mDownloadQueue);
		return list;
	}

	List<NetFile> getPausedFiles(Context context) {
		return FileDownloadInfoDAO.getInstance(context).queryForState(DownloadState.paused);
	}

	List<NetFile> getWaittingFiles(Context context) {
		return FileDownloadInfoDAO.getInstance(context).queryForState(DownloadState.inDownloadQueue);
	}
}
