package com.ztb.charger.download;

import java.io.Serializable;

import com.ztb.charger.download.DownloadState;


public class NetFile implements Serializable {
	public String url;
	public long length;
	public int progress;
	public int state = DownloadState.none;
	
	//数据库列表
	public static final String COL_url = "url";
	public static final String COL_progress = "progress";
	public static final String COL_state = "state";
	public static final String TABLE = "table_file_download";

	@Override
	public boolean equals(Object o) {
	    if(o == null){
	        return false;
	    }
		if (o instanceof NetFile) {
			NetFile other = (NetFile) o;
			return url != null && url.equalsIgnoreCase(other.url);
		} else {
			return false;
		}
	}

}
