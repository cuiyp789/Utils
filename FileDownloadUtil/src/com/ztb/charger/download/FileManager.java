/**********************************************************
 * Copyright © 2013-1014 深圳市美传网络科技有限公司版权所有
 * 创 建 人：yangbo
 * 创 建 日 期：2014-8-25 下午4:39:21
 * 版 本 号：
 * 修 改 人：
 * 描 述：
 * <p>
 *	
 * </p>
 **********************************************************/
package com.ztb.charger.download;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

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
public class FileManager {
  private final static String TAG = FileManager.class.getCanonicalName();
  private static FileManager instance;
  private FileDownloader mDownloader;

  /**
   * 因为安装和卸载的广播信息中都只包含状态信息和包名，需要一个map来保存完整的object。 <br>
   * 之所以静态，那是为了防止ManageActivity被回收之后，这个列表也被回收
   */
  private static Map<String, NetFile> mFileDownloadInfoMap = new HashMap<String, NetFile>();

  private ReentrantLock mLock;

  public FileManager() {
  }
  
  private FileManager(Context context) {
    mDownloader = FileDownloader.getInstance();
    mDownloader.startAll(context);
    mLock = new ReentrantLock();

    addStateListener(FileDownloadInfoDAO.getInstance(context));
  }

  public static FileManager getInstance(Context context) {
    if (instance == null)
      instance = new FileManager(context);
    return instance;
  }
  
  public void addStateListener(FileStateListener listener) {
    mDownloader.addStateListener(listener);
  }

  /**
   * 获取已下载完毕的file
   * 
   * @return
   */
  public List<NetFile> getDownloadCompletedFiles(Context context) {
    File downloadDir = new File(Config.getInstance().downloadDir);
    if (downloadDir.exists()) {
      List<NetFile> resultList = new ArrayList<NetFile>();
      for (File downloadedFile : downloadDir.listFiles()) {
        // 必然是packName___version___size这种格式
        if (!downloadedFile.exists())
          continue;
//        String[] nameParts = downloadedFile.getName().replace(".apk.tmp", "").replace(".apk", "").split("___");
        String name = downloadedFile.getName();
        String nameParts = name.substring(0, name.lastIndexOf("."));

        //BackupFileBean info = FileDownloadInfoDAO.getInstance(context).query(nameParts[0]);
        NetFile info = FileDownloadInfoDAO.getInstance(context).query(nameParts);
        if (info == null) {
          downloadedFile.deleteOnExit();
          continue;
        }

        if (info.state != DownloadState.downloadCompleted){
          continue;
        }
        info.progress = 100;
        resultList.add(info);
      }

      return resultList;
    } else {
      downloadDir.mkdirs();
      return new ArrayList<NetFile>();
    }
  }

  /**
   * 获取正在下载当中/暂停的文件
   * 
   * @return
   */
  public List<NetFile> getDownloadingFiles(Context context) {
    mLock.lock();
    List<NetFile> list = new ArrayList<NetFile>();
    List<NetFile> pausedList = mDownloader.getPausedFiles(context);
    List<NetFile> waitList = mDownloader.getWaittingFiles(context);
    List<NetFile> downloadingList = mDownloader.getDownloadingList(context);
    list.addAll(downloadingList);
    for (NetFile paused : pausedList) {
      boolean duplicated = false;
      for (NetFile downloading : downloadingList)
        if (downloading.equals(paused)) {
          duplicated = true;
          break;
        }
      if (!duplicated)
        list.add(paused);
    }
    for (NetFile wait : waitList) {
      wait.state = DownloadState.paused;
      boolean duplicated = false;
      for (NetFile downloading : downloadingList)
        if (downloading.equals(wait)) {
          duplicated = true;
          break;
        }
      if (!duplicated)
        list.add(wait);
    }

    mLock.unlock();
    return list;
  }

  public void removeStateListener(FileStateListener listener) {
    mDownloader.removeStateListener(listener);
  }

  /**
   * 在列表中点击升级、下载还是继续下载、安装，都要调用这个方法
   * 
   * @param info
   */
  public void setAction(final NetFile info, final FileAction action, final Context context) {
    ThreadPoolManager.executeNormalTask(new Runnable() {

      @Override
      public void run() {
        // 为了解决多线程操作时对同一个引用对象的修改导致下载处理逻辑出现混乱，
        // 需要讲对象进行克隆
//        BackupFileBean fileInfo = (BackupFileBean) Cloner.shallowClone(info);
        execAction(info, action, context);
      }
    });

  }

  public void pauseAllDownloadingTasks(Context context) {
    mLock.lock();
    List<NetFile> downloadingList = getDownloadingFiles(context);
    for (NetFile file : downloadingList) {
      setAction(file, FileAction.toPause, context);
    }
    mLock.unlock();
  }

  private void execAction(NetFile info, FileAction action, Context context) {
    switch (action) {
    case toDownload:
      info.state = DownloadState.inDownloadQueue;
      mDownloader.download(info, context);
      break;
    case toRedownload:
      mDownloader.redownload(info, context);
      break;
    case toPause:
      mDownloader.pause(info, context);
      break;
    case toDelete:
      mFileDownloadInfoMap.put(info.url, info);
      mDownloader.delete(info, context);
      break;
    default:
      break;
    }
  }

  public void setProgressState(NetFile file, Context context) {
    if (file == null)
      return;

    mLock.lock();
    for (NetFile info : getDownloadingFiles(context)) {
      if (info.equals(file)) {
        file.state = info.state;
        file.progress = info.progress;
        mLock.unlock();
        return;
      }
    }

    for (NetFile info : getDownloadCompletedFiles(context)) {
      if (info.equals(file)) {
        file.state = DownloadState.downloadCompleted;
        file.progress = 100;
        mLock.unlock();
        return;
      }
    }

    file.state = DownloadState.none;

    mLock.unlock();
  }

  public void setState(String fileName, int state, Context context) {
    Log.d(TAG, "setState(fileName:" + fileName + ",state:" + state + ")");

    if (fileName == null || TextUtils.isEmpty(fileName.trim()))
      return;

    switch (state) {
    case DownloadState.fileDeleted:
      NetFile deletedFile = mFileDownloadInfoMap.get(fileName);
      if (deletedFile == null)
        return;
      deletedFile.state = state;
      mDownloader.notifyAllListeners(deletedFile, context);
      mFileDownloadInfoMap.remove(fileName);
      break;

    default:
      break;
    }
  }

  /**
   * 当网络为非wifi时会给出确认对话框
   * 
   * @param info
   * @param activity
   */
  public void toDownload(NetFile info, final Activity activity, FileAction action, boolean checkNetwork) {
    if (info == null) {
      return;
    }
    if (checkNetwork && !NetworkUtil.isNetworkerConnect(activity)) {
      return;
    }
    if (action != null) {
      setAction(info, action, activity);
    } else {
      if (info.state != DownloadState.paused) {
        //Toast.makeText(activity, "已加入下载列队2", Toast.LENGTH_SHORT).show();
      }
      setAction(info, FileAction.toDownload, activity);
    }
  }

  public void toDownload(NetFile info, Activity activity) {
    toDownload(info, activity, null, true);
  }

  public void toDownload(NetFile info, Activity activity, FileAction action) {
    toDownload(info, activity, action, true);
  }

}