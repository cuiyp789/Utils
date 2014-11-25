/**********************************************************
 * Copyright © 2013-1014 深圳市美传网络科技有限公司版权所有
 * 创 建 人：yangbo
 * 创 建 日 期：2014-7-22 下午1:55:19
 * 版 本 号：
 * 修 改 人：
 * 描 述：
 * <p>
 *	
 * </p>
 **********************************************************/
package com.ztb.charger.download;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * <p>
 * 
 * </p>
 * 
 * @author yangbo
 * @date 2014-7-22
 * @version
 * @since
 */
public class NetworkUtil {
	final static String TAG = "NetworkUtil";
	
	private final static String IP_ZTB = "10.10.10";
	public final static String WIFI_ZTB = "RT5350_AP";

	/**
	 * 获取网络状态
	 * @return 1为wifi连接，2为2g网络，3为3g网络，-1为无网络连接
	 */
	public static int getNetworkerStatus(Context con) {
		ConnectivityManager conMan = (ConnectivityManager) con.getSystemService(
				Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conMan.getActiveNetworkInfo();
		if (null != info && info.isConnected()) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				switch (info.getSubtype()) {
				case 1:
				case 2:
				case 4:
					// 2G网络
					return 2;
				default:
					// 3G及其以上网络
					return 3;
				}
			} else {
				// wifi网络
				return 1;
			}
		} else {
			// 无网络
			return -1;
		}
	}

	public static boolean hasNetWork(Context con) {
		boolean netSataus = false;
		ConnectivityManager cwjManager = (ConnectivityManager) con.getApplicationContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		cwjManager.getActiveNetworkInfo();
		if (cwjManager.getActiveNetworkInfo() != null) {
			netSataus = cwjManager.getActiveNetworkInfo().isAvailable();
		}
		return netSataus;
	}

	public static boolean isWifiConnected(Context con){
		try{
			ConnectivityManager connManager = (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			return mWifi.isConnected();
		}catch(Exception e){
		}
		return false;
	}

	private static WifiInfo getWifiInfo(Context context) {
		WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		return wifiInfo;
	}

	/**
	 * 判断网络连接状态
	 * 
	 * @return 返回true为连接
	 */
	public static boolean isNetworkerConnect(Context con) {
		return getNetworkerStatus(con) != -1;
	}

	/** 进入网络设置 */
	public static void netWorkSetting(Context con) {
		String sdkVersion = android.os.Build.VERSION.SDK;
		Intent intent = null;
		if (Integer.valueOf(sdkVersion) > 10) {
			intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
		} else {
			intent = new Intent();
			ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings");
			intent.setComponent(comp);
			intent.setAction("android.intent.action.VIEW");
		}
		con.startActivity(intent);
	}

}
