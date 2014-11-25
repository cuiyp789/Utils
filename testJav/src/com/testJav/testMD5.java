/**
 * testJav
 * testJav
 * testBase64.java
 * 责任人:  
 * 创建/修改时间: 2014年11月14日-下午5:37:57
 * Copyright © 2013-2014 深圳掌通宝科技有限公司-版权所有
 */
package com.testJav;

import java.io.File;
import java.util.Base64;

import testJav.utils.Base64McUtil;
import testJav.utils.MD5Util;




/**
 * @author Administrator
 *
 */
public class testMD5 {
    public static void main(String[] args) {
        String url = "http://192.168.1.184:9000/install/app/20141120/1947aa5011140075ff710fdff9ffe99e/百度应用.apk";
        System.out.println(MD5Util.getMD5String(url));
        
        System.out.println("end of main()");
    }
}
