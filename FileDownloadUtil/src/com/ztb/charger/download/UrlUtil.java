/**
 * FileDownloadUtil
 * com.ztb.charger.download
 * UrlUtil.java
 * 责任人:  
 * 创建/修改时间: 2014年11月20日-下午5:45:12
 * Copyright © 2013-2014 深圳掌通宝科技有限公司-版权所有
 */
package com.ztb.charger.download;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Administrator
 * 将url进行urlEncode，比如：如果下载一个中文件文件名的文件，不编码，就会抛出FileNotFoundException
 */
public class UrlUtil {
    public static String getEncodedUrl(String originalUrl) {
        URL url = null;
        try {
            url = new URL(originalUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String[] p = url.getFile().split(File.separator);
        String path = "";
        int count = 0;
        for (String name : p) {
            String a = null;
            try {
//                a = URLEncoder.encode(name, "UTF-8");
                a = android.net.Uri.encode(name, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (count == 0) {
                path += a;
            } else {
                path += File.separator + a;
            }
            count++;
        }
        String encodeUrl = String.format("%s://%s%s", url.getProtocol(), url.getAuthority(), path);
        return encodeUrl;
    }
}
