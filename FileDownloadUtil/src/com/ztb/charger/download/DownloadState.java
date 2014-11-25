/**********************************************************
 * Copyright © 2013-1014 深圳市美传网络科技有限公司版权所有
 * 创 建 人：yangbo
 * 创 建 日 期：2014-8-25 下午3:56:25
 * 版 本 号：
 * 修 改 人：
 * 描 述：
 * <p>
 *	
 * </p>
 **********************************************************/
package com.ztb.charger.download;

/**
 * <p>
 * 
 * </p>
 * 
 * @author yangbo
 * @date 2014-8-25
 * @version
 * @since
 */
public class DownloadState {
	public final static int none = 0;
	/**
	 * 界面上需要指导该file是可以下载的
	 */
	public final static int updateable = 1;
	/**
	 * 已经加入下载队列了，但还没有下载
	 */
	public final static int inDownloadQueue = 2;
	/**
	 * 界面上需要知道这个file在下载当中
	 */
	public final static int downloading = 3;
	/**
	 * 界面上点击了暂停按钮
	 */
	public final static int paused = 4;
	/**
	 * 已下载完毕
	 */
	public final static int downloadCompleted = 5;
	/**
	 * file文件已经被删除完毕
	 */
	public final static int fileDeleted = 6;
}
