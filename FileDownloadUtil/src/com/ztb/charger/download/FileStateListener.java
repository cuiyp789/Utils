/**********************************************************
 * Copyright © 2013-1014 深圳市美传网络科技有限公司版权所有
 * 创 建 人：yangbo
 * 创 建 日 期：2014-8-25 下午4:30:11
 * 版 本 号：
 * 修 改 人：
 * 描 述：
 * <p>
 *	
 * </p>
 **********************************************************/
package com.ztb.charger.download;

import android.content.Context;


public interface FileStateListener {
  
  /**
   * @param info
   * @param context
   */
  public void stateChanged(final NetFile info, Context context);
}
