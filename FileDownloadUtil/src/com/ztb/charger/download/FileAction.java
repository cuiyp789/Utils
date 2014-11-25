/**********************************************************
 * Copyright © 2013-1014 深圳市美传网络科技有限公司版权所有
 * 创 建 人：Administrator
 * 创 建 日 期：2014-3-25 上午10:22:09
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
 * @author yangbo
 * @date 2014-3-25
 * @version 
 * @since 
 */
public enum FileAction {
  /**
   * 点击继续或更新按钮时的状态
   */
  toDownload,
  /**
   * 点击重新下载按钮的请求
   */
  toRedownload,
  /**
   * 请求暂停下载任务
   */
  toPause,
  /**
   * 删除file的下载文件
   */
  toDelete
}