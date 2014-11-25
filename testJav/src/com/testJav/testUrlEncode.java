/**
 * testJav
 * testJav
 * testUrlEncode.java
 * 责任人:  
 * 创建/修改时间: 2014年11月12日-下午2:41:36
 * Copyright © 2013-2014 深圳掌通宝科技有限公司-版权所有
 */
package com.testJav;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Administrator
 *
 */
public class testUrlEncode {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try{
        String url1 = "http://10.10.10.200:8080/sdb1/AI+ VIDEO/好cyp.3gp";
        String url2 = getEncodedUrlPath(url1);
        String url3 = URLEncoder.encode(url1, "utf-8");
//        String url4 = android.net.Uri.encode(url1, "utf-8");
        System.out.println(url2);
        System.out.println(URIUtils.);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    static String getEncodedUrlPath(String originalUrl) {
        URL url = null;
        try {
            url = new URL(originalUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String sep = url.getPath().substring(0, 1);
        String[] p = url.getFile().split(sep);
        String path = "";
        int count = 0;
        for (String name : p) {
            String a = null;
            try {
                a = URLEncoder.encode(name, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (count == 0) {
                path += a;
            } else {
                path += (sep + a);
            }
            count++;
        }
        String encodeUrl = String.format("%s://%s%s", url.getProtocol(), url.getAuthority(), path);
        return encodeUrl;
    }
}
